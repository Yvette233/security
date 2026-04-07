package com.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.util.IdentityGenerator;
import com.util.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.ibatis.reflection.ArrayUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional(rollbackFor = Exception.class)
public class JpaDao extends BasicDao {

    /**********************************************找出数据库中是否存在表单********************************************************/

    /**
     * 获取所有表格
     *
     * @return
     * @throws SQLException
     */
    public List<String> getAllTableNames() {
        List<String> list = new ArrayList<String>();
//        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//        Session session = sessionFactory.openSession();
        Session session = this.getSessionFactory().getCurrentSession();
        if (session != null) {
            list = session.doReturningWork(
                    connection -> {
                        List<String> resultList = new ArrayList<>();
                        ResultSet rs;
                        String username = connection.getMetaData().getUserName();
                        try {
                            rs = connection.getMetaData().getTables(connection.getCatalog(), username, null, null);
                            while (rs.next()) {
                                String table = rs.getObject(3).toString();
                                resultList.add(table);
                            }
                        } catch (SQLException e) {
                            //不存在
                            return resultList;
                        }
                        rs.close();
                        return resultList;
                    }
            );
//            session.close();
        }
        return list;
    }

    /**
     * 验证表是否存在
     * @param tableName 表名
     * @return （false：不存在，true：存在）
     */
    public boolean checkTableNameExist(String tableName) {
        List<String> tableNameList = getAllTableNames();
        if (tableNameList == null || tableNameList.size() <= 0) {
            return false;
        }
        return tableNameList.contains(String.join("_", "TAB", tableName.toUpperCase())) ? true : false;
    }

    /**
     * 验证表是否存在
     * @param tableNameList 需要判断的表名列表
     * @return
     */
    public List<String> getExistTableNameList(List<String> tableNameList) {
        List<String> exsitableNameList = getAllTableNames();
        if (exsitableNameList == null || exsitableNameList.size() <= 0) {
            return new ArrayList<>();
        }
        return exsitableNameList.stream().filter(x -> tableNameList.contains(x.toUpperCase())).collect(Collectors.toList());
    }

    /******************************************************************************************************/

    /**
     *
     * @param tableName
     * @param variableMap
     */
    public void insert(String tableName,String id,String processInstanceId, Map<String, Object> variableMap) {
        Integer version =  this.getMaxVersion(tableName,processInstanceId);

        String sqlNames = "id,processInstanceId,version,";
        String sqlValues = ":id,:processInstanceId,:version,";
        Iterator<Map.Entry<String, Object>> it = variableMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if("formName".equalsIgnoreCase(entry.getKey())){
                continue;
            }

            sqlNames += entry.getKey() + ",";
            sqlValues += ":" + entry.getKey() + ",";
        }
        sqlNames = sqlNames.substring(0, sqlNames.length() - 1);
        sqlValues = sqlValues.substring(0, sqlValues.length() - 1);

        //        String sql = "INSERT INTO Person (name, id) VALUES (?, ?)"
        String sqlString = "INSERT INTO " + String.join("_", "TAB", tableName) + " (" + sqlNames + ") VALUES (" + sqlValues + ")";
        Session session = this.getSessionFactory().getCurrentSession();
        Query query = session.createSQLQuery(sqlString);

        query.setParameter("id", id);
        query.setParameter("processInstanceId", processInstanceId);
        query.setParameter("version", version);
        it = variableMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if("formName".equalsIgnoreCase(entry.getKey())){
                continue;
            }
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.executeUpdate();
    }

    public Integer getMaxVersion(String tableName,String processInstanceId) {
        String querySql ="SELECT ID,VERSION FROM " +String.join("_", "TAB", tableName)  +" WHERE PROCESSINSTANCEID='" + processInstanceId+ "' ORDER BY VERSION DESC";

        Session session = this.getSessionFactory().getCurrentSession();
        Query query = session.createSQLQuery(querySql);
        List<Object[]> list=query.getResultList();//后面操作一样

        Integer version = CollectionUtils.isEmpty(list)?0:Integer.parseInt(((BigDecimal)list.get(0)[1]).toString());
        return version;
    }
}
