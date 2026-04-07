package com.dao;

import com.common.BasicDao;
import com.pojo.SysGroupUser;
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
public class SysGroupUserDao extends BasicDao<SysGroupUser> {
    private static final Log log = LogFactory.getLog(SysGroupUserDao.class);

    public List<SysGroupUser> getSysGroupUserList(String groupId, String userId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(groupId)) {
            propertyList.add(Triple.of("groupId", groupId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        propertyList.add(Triple.of("groupId", null, QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysGroupUser> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysGroupUserAssociation(String groupId, String userId) {
        List<SysGroupUser> sysGroupUserList = this.getSysGroupUserList(groupId, userId, null);
        if (CollectionUtils.isEmpty(sysGroupUserList)) {
            SysGroupUser sysGroupUser = new SysGroupUser();
            sysGroupUser.setGroupId(groupId);
            sysGroupUser.setUserId(userId);
            sysGroupUser.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysGroupUser);
            return "用户" + userId + "与组" + groupId + "关联保存成功";
        }
        return "用户" + userId + "已与组" + groupId + "存在该关联关系";
    }

    public String deleteSysGroupUserAssociation(String groupId, String userId) {
        List<SysGroupUser> sysGroupUserList = this.getSysGroupUserList(groupId, userId, null);
        if (!CollectionUtils.isEmpty(sysGroupUserList)) {
            this.delete(sysGroupUserList.get(0));
            return "用户" + userId + "与组" + groupId + "关联删除成功";
        }
        return "用户" + userId + "与组" + groupId + "不存在该关联关系";
    }
}
