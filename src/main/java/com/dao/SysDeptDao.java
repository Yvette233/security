package com.dao;

import com.common.BasicDao;
import com.pojo.SysDept;
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
public class SysDeptDao extends BasicDao<SysDept> {
    private static final Log log = LogFactory.getLog(SysDeptDao.class);

    public List<SysDept> getDeptList(String id, String parentId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(id)) {
            propertyList.add(Triple.of("id", id, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(parentId)) {
            propertyList.add(Triple.of("parentId", parentId, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysDept> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);

    }

}
