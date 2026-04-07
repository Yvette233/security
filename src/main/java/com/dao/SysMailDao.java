package com.dao;

import com.common.BasicDao;
import com.pojo.SysMail;
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
public class SysMailDao extends BasicDao<SysMail> {
    private static final Log log = LogFactory.getLog(SysMailDao.class);

    public List<SysMail> getMailList(String userId, String mailName, Boolean isDefault, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(mailName)) {
            propertyList.add(Triple.of("mailName", mailName, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(isDefault)) {
            propertyList.add(Triple.of("isDefault", isDefault, QueryType.Equal));
        }
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysMail> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);

    }

}
