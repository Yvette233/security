package com.dao;

import com.common.BasicDao;
import com.pojo.SysGroup;
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
public class SysGroupDao extends BasicDao<SysGroup> {
    private static final Log log = LogFactory.getLog(SysGroupDao.class);

    public List<SysGroup> getSysGroupList(String name, String nameCH, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(name)) {
            propertyList.add(Triple.of("name", name, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(nameCH)) {
            propertyList.add(Triple.of("nameCH", nameCH, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysGroup> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

}
