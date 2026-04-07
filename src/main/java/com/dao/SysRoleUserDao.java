package com.dao;

import com.common.BasicDao;
import com.pojo.SysRoleUser;
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
public class SysRoleUserDao extends BasicDao<SysRoleUser> {
    private static final Log log = LogFactory.getLog(SysRoleUserDao.class);

    public List<SysRoleUser> getSysRoleUserList(String userId, String roleId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(roleId)) {
            propertyList.add(Triple.of("roleId", roleId, QueryType.Equal));
        }
        propertyList.add(Triple.of("roleId", null, QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysRoleUser> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysRoleUserAssociation(String roleId, String userId) {
        List<SysRoleUser> sysRoleUserList = this.getSysRoleUserList(userId, roleId, null);
        if (CollectionUtils.isEmpty(sysRoleUserList)) {
            SysRoleUser sysRoleUser = new SysRoleUser();
            sysRoleUser.setRoleId(roleId);
            sysRoleUser.setUserId(userId);
            sysRoleUser.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysRoleUser);
            return "用户" + userId + "与角色" + roleId + "关联保存成功";
        }
        return "用户" + userId + "已与角色" + roleId + "存在该关联关系";
    }

    public String deleteSysRoleUserAssociation(String roleId, String userId) {
        List<SysRoleUser> sysRoleUserList = this.getSysRoleUserList(userId, roleId, null);
        if (!CollectionUtils.isEmpty(sysRoleUserList)) {
            this.delete(sysRoleUserList.get(0));
            return "用户" + userId + "与角色" + roleId + "关联删除成功";
        }
        return "用户" + userId + "与角色" + roleId + "不存在该关联关系";
    }
}
