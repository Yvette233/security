package com.dao;

import com.common.BasicDao;
import com.pojo.SysPosition;
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
public class SysPositionDao extends BasicDao<SysPosition> {
    private static final Log log = LogFactory.getLog(SysPositionDao.class);

    public List<SysPosition> getPositionList(String id, String parentId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(id)) {
            propertyList.add(Triple.of("id", id, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(parentId)) {
            propertyList.add(Triple.of("parentId", parentId, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysPosition> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

}
