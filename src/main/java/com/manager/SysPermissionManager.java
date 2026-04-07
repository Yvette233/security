package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.BasicManager;
import com.dao.SysPermissionDao;
import com.dao.SysPermissionRoleDao;
import com.pojo.SysPermission;
import com.pojo.SysPermissionRole;
import com.pojo.SysRole;
import com.util.Page;
import com.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SysPermissionManager extends BasicManager<SysPermission> {
    public final static Log log = LogFactory.getLog(SysPermissionManager.class);

    @Resource
    private SysPermissionDao sysPermissionDao;
    @Resource
    private SysPermissionRoleDao sysPermissionRoleDao;
    @Resource
    private SysRoleManager sysRoleManager;

    public void deletePermission(String id) {
        sysPermissionDao.deleteById(id);
    }

    public void saveOrUpdate(SysPermission sysPermission) {
        //找到数据库中旧角色
        sysPermissionDao.saveOrUpdate(sysPermission);
    }

    public SysPermission getById(String id) {
        return sysPermissionDao.getById(id);
    }

    /*********************************************************************************/

    public Result getPermissionList(String name, String type, String method, String roleIds, Page page) {
        List<SysPermission> permissionList = new ArrayList<SysPermission>();
        String message = "association";
        if (StringUtils.isNotEmpty(roleIds) && hasEqualPermissionsByRoleIds(roleIds)) {
            permissionList = this.getPermissionsByRoleId(roleIds.split(",")[0], page).getLeft();
        }

        if (CollectionUtils.isEmpty(permissionList)) {
            message = "non-association";
            permissionList = sysPermissionDao.getSysPermissionList(name, type, method, page);
            if (CollectionUtils.isEmpty(permissionList)) {
                return null;
            }
        }

        return Result.ok(message, (JSONArray) JSON.toJSON(permissionList), page);
    }

    /****************************************登录*****************************************/

    ///得到所有权限，每个权限包含角色信息
    public List<SysPermission> getAllPermissions() {
        List<SysPermission> sysPermissionList = sysPermissionDao.getAllResult();

        if (!CollectionUtils.isEmpty(sysPermissionList)) {
            sysPermissionList.forEach(sysPermission -> {
                Set<SysRole> sysRoleSet = this.sysRoleManager.getSysRolesByPermissionId(sysPermission.getId(), null).getRight();
                sysPermission.setSysRoleSet(sysRoleSet);
            });
        }
        return sysPermissionList;
    }

    /***********************************关联关系********************************************/
    private Boolean hasEqualPermissionsByRoleIds(String roleIds) {
        Boolean hasEqualPermissions = true;
        String[] roleIdArray = roleIds.split(",");
        List<SysPermissionRole> objectList = this.sysPermissionRoleDao.getSysPermissionRoleList(null, roleIdArray[0], null);

        if (roleIdArray.length > 1) {
            for (int i = 1; i < roleIdArray.length; i++) {
                List<SysPermissionRole> list = this.sysPermissionRoleDao.getSysPermissionRoleList(null, roleIdArray[i], null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysPermissionRole::getPermissionId))
                        .map(sysPermissionRole -> sysPermissionRole.getPermissionId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysPermissionRole::getPermissionId))
                                .map(sysPermissionRole -> sysPermissionRole.getPermissionId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualPermissions = false;
                    break;
                }
            }
        }
        return hasEqualPermissions;
    }

    //记录每种角色所拥有的用户
    public Pair<List<SysPermission>, Set<SysPermission>> getPermissionsByRoleId(String roleId, Page page) {
        List<SysPermissionRole> sysPermissionRoleList = sysPermissionRoleDao.getSysPermissionRoleList(null, roleId, null);
        List<SysPermission> sysPermissionList = new ArrayList<>();
        Set<SysPermission> sysPermissionSet = new HashSet<>();

        if (!CollectionUtils.isEmpty(sysPermissionRoleList)) {
            sysPermissionRoleList.forEach(permissionRole -> {
                sysPermissionList.add(permissionRole.getSysPermission());
                sysPermissionSet.add(permissionRole.getSysPermission());
            });
        }
        return Pair.of(sysPermissionList, sysPermissionSet);
    }

    /***********************************查看、修改关联关系 ********************************************/


    public String saveAssociation(String permissionIds, String ids, String associationObjectName) {
        String label = "";
        String[] permissionIdArray = permissionIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < permissionIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysPermissionRoleDao.saveSysPermissionRoleAssociation(permissionIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String permissionIds, String ids, String associationObjectName) {
        String label = "";
        String[] permissionIdArray = permissionIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < permissionIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysPermissionRoleDao.deleteSysPermissionRoleAssociation(permissionIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }
}
