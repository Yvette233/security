package com.dao;

import com.common.BasicDao;
import com.pojo.SysMenuRole;
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
public class SysMenuRoleDao extends BasicDao<SysMenuRole> {
    private static final Log log = LogFactory.getLog(SysMenuRoleDao.class);

    public List<SysMenuRole> getSysMenuRoleList(String menuId, String roleId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(menuId)) {
            propertyList.add(Triple.of("menuId", menuId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(roleId)) {
            propertyList.add(Triple.of("roleId", roleId, QueryType.Equal));
        }
        propertyList.add(Triple.of("menuId", "String2Number", QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysMenuRole> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysMenuRoleAssociation(String menuId, String roleId) {
        List<SysMenuRole> sysMenuRoleList = this.getSysMenuRoleList(menuId, roleId, null);
        if (CollectionUtils.isEmpty(sysMenuRoleList)) {
            SysMenuRole sysMenuRole = new SysMenuRole();
            sysMenuRole.setRoleId(roleId);
            sysMenuRole.setMenuId(menuId);
            sysMenuRole.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysMenuRole);
            return "菜单" + menuId + "与角色" + roleId + "关联保存成功";
        }
        return "菜单" + menuId + "已与角色" + roleId + "存在该关联关系";
    }

    public String deleteSysMenuRoleAssociation(String menuId, String roleId) {
        List<SysMenuRole> sysMenuRoleList = this.getSysMenuRoleList(menuId, roleId, null);
        if (!CollectionUtils.isEmpty(sysMenuRoleList)) {
            this.delete(sysMenuRoleList.get(0));
            return "菜单" + menuId + "与角色" + roleId + "关联删除成功";
        }
        return "菜单" + menuId + "与角色" + roleId + "不存在该关联关系";
    }
}
