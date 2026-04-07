package com.dao;

import com.common.BasicDao;
import com.pojo.SysPermissionRole;
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
public class SysPermissionRoleDao extends BasicDao<SysPermissionRole> {
    private static final Log log = LogFactory.getLog(SysPermissionRoleDao.class);

    public List<SysPermissionRole> getSysPermissionRoleList(String permissionId, String roleId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(permissionId)) {
            propertyList.add(Triple.of("permissionId", permissionId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(roleId)) {
            propertyList.add(Triple.of("roleId", roleId, QueryType.Equal));
        }
        propertyList.add(Triple.of("roleId", null, QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysPermissionRole> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysPermissionRoleAssociation(String permissionId, String roleId) {
        List<SysPermissionRole> sysPermissionRoleList = this.getSysPermissionRoleList(permissionId, roleId, null);
        if (CollectionUtils.isEmpty(sysPermissionRoleList)) {
            SysPermissionRole sysPermissionRole = new SysPermissionRole();
            sysPermissionRole.setRoleId(roleId);
            sysPermissionRole.setPermissionId(permissionId);
            sysPermissionRole.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysPermissionRole);
            return "权限" + permissionId + "与角色" + roleId + "关联保存成功";
        }
        return "权限" + permissionId + "已与角色" + roleId + "存在该关联关系";
    }

    public String deleteSysPermissionRoleAssociation(String permissionId, String roleId) {
        List<SysPermissionRole> sysPermissionRoleList = this.getSysPermissionRoleList(permissionId, roleId, null);
        if (!CollectionUtils.isEmpty(sysPermissionRoleList)) {
            this.delete(sysPermissionRoleList.get(0));
            return "权限" + permissionId + "与角色" + roleId + "关联删除成功";
        }
        return "权限" + permissionId + "与角色" + roleId + "不存在该关联关系";
    }
}
