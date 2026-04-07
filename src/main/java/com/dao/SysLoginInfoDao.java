package com.dao;

import com.common.BasicDao;
import com.pojo.SysLoginInfo;
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
public class SysLoginInfoDao extends BasicDao<SysLoginInfo> {
    private static final Log log = LogFactory.getLog(SysLoginInfoDao.class);


    public List<SysLoginInfo> getSysLoginInfoList(String userId, String remoteAddress, String remoteHost, String remotePort, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(remoteAddress)) {
            propertyList.add(Triple.of("remoteAddress", remoteAddress, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(remoteHost)) {
            propertyList.add(Triple.of("remoteHost", remoteHost, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(remotePort)) {
            propertyList.add(Triple.of("remotePort", remotePort, QueryType.Equal));
        }
        propertyList.add(Triple.of("createTime", null, QueryType.Asc));

        CriteriaQuery<SysLoginInfo> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }


}
