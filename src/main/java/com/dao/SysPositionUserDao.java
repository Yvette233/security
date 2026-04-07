package com.dao;

import com.common.BasicDao;
import com.pojo.SysPositionUser;
import com.util.IdentityGenerator;
import com.util.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SysPositionUserDao extends BasicDao<SysPositionUser> {
    private static final Log log = LogFactory.getLog(SysPositionUserDao.class);

    public List<SysPositionUser> getSysPositionUserList(String positionId, String userId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(positionId)) {
            propertyList.add(Triple.of("positionId", positionId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        propertyList.add(Triple.of("positionId", null, QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysPositionUser> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysPositionUserAssociation(String positionId, String userId) {
        List<SysPositionUser> SysPositionUserList = this.getSysPositionUserList(positionId, userId, null);
        if (CollectionUtils.isEmpty(SysPositionUserList)) {
            SysPositionUser sysPositionUser = new SysPositionUser();
            sysPositionUser.setPositionId(positionId);
            sysPositionUser.setUserId(userId);
            sysPositionUser.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysPositionUser);
            return "用户" + userId + "与职位" + positionId + "关联保存成功";
        }
        return "用户" + userId + "已与职位" + positionId + "存在该关联关系";
    }

    public String deleteSysPositionUserAssociation(String positionId, String userId) {
        List<SysPositionUser> SysPositionUserList = this.getSysPositionUserList(positionId, userId, null);
        if (!CollectionUtils.isEmpty(SysPositionUserList)) {
            this.delete(SysPositionUserList.get(0));
            return "用户" + userId + "与职位" + positionId + "关联删除成功";
        }
        return "用户" + userId + "与职位" + positionId + "不存在该关联关系";
    }
}
