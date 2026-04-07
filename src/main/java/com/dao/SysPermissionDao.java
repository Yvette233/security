package com.dao;

import com.common.BasicDao;
import com.pojo.SysPermission;
import com.util.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SysPermissionDao extends BasicDao<SysPermission> {
    private static final Log log = LogFactory.getLog(SysPermissionDao.class);

    public List<SysPermission> getSysPermissionList(String name, String type, String method, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(name)) {
            propertyList.add(Triple.of("name", name, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(type)) {
            propertyList.add(Triple.of("type", type, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(method)) {
            propertyList.add(Triple.of("method", method, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", "String2Number", QueryType.Asc));

        CriteriaQuery<SysPermission> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }


}
