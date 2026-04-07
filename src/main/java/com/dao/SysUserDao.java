package com.dao;

import com.common.BasicDao;
import com.pojo.SysUser;
import com.util.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class SysUserDao extends BasicDao<SysUser> {
    private static final Log log = LogFactory.getLog(SysUserDao.class);


    public List<SysUser> getSysUserList(String userName, String userNameCH, String type, Long sex, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(userName)) {
            propertyList.add(Triple.of("userName", userName, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(userNameCH)) {
            propertyList.add(Triple.of("userNameCH", userNameCH, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(type)) {
            propertyList.add(Triple.of("type", type, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(sex)) {
            propertyList.add(Triple.of("sex", sex, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", "String2Number", QueryType.Asc));

        CriteriaQuery<SysUser> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }


    public List<SysUser> getUserByConditions(String id, String name, Page page) {
        // TODO Auto-generated method stub
        List<SysUser> list = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();

            StringBuffer hql = new StringBuffer("from  "
                    + SysUser.class.getName() + " a where 1=1 ");

            if (StringUtils.isNotEmpty(id)) {
                hql.append(" and a.id like '%" + id + "%'");
            }

            if (StringUtils.isNotEmpty(name)) {
                hql.append(" and a.name like :name ");
            }

            Query query = session.createQuery(hql.toString());

            // 如果需要进行分页处理，则调用如下语句
            if (page != null) page.pagePro(session, query, hql.toString(), null);

            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SysUser> getUserByConditions2(String id, String name, Page page) {
        // TODO Auto-generated method stub
        List<SysUser> list = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();

            Map<String, Object> parameters = new HashMap<String, Object>();
            StringBuffer hql = new StringBuffer("from "
                    + SysUser.class.getName() + " a where 1=1 ");

            if (StringUtils.isNotEmpty(id)) {
                hql.append(" and a.id like :id ");
                parameters.put("id", "%" + id + "%");
            }

            if (StringUtils.isNotEmpty(name)) {
                hql.append(" and a.name like :name ");
                parameters.put("name", "%" + name + "%");
            }

            Query query = session.createQuery(hql.toString());

            parameters.forEach((key, value) -> {
                query.setParameter(key, value);
            });

            // 如果需要进行分页处理，则调用如下语句
            if (page != null)
                page.pagePro(session, query, hql.toString(), parameters);

            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SysUser> getUserByConditions3(String id, String name, Page page) {
        // TODO Auto-generated method stub
        List<SysUser> list = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();

            Map<Integer, Object> parameters = new HashMap<Integer, Object>();
            StringBuffer hql = new StringBuffer("from "
                    + SysUser.class.getName() + " a where 1=1 ");

            int num = 0;
            if (StringUtils.isNotEmpty(id)) {
                hql.append(" and a.id like ?" + String.valueOf(num) + " ");
                parameters.put(num, "%" + id + "%");
                num++;
            }

            if (StringUtils.isNotEmpty(name)) {
                hql.append(" and a.name like ?" + String.valueOf(num) + " ");
                parameters.put(num++, "%" + name + "%");
                num++;
            }

            Query query = session.createQuery(hql.toString());


            if (parameters != null && !parameters.isEmpty()) {
                parameters.forEach((key, value) -> {
                    query.setParameter(key, value);
                });
            }

            // 如果需要进行分页处理，则调用如下语句
            if (page != null)
                page.pagePro(session, query, hql.toString(), parameters);

            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<SysUser> login(String name, String password) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
            return null;
        }
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        propertyList.add(Triple.of("name", name, QueryType.Equal));
        propertyList.add(Triple.of("password", password, QueryType.Equal));
        CriteriaQuery<SysUser> criteriaQuery = this.getCriteriaQuery(propertyList, null);

        List<SysUser> list = this.getResult(criteriaQuery, null);

        return list;
    }

}
