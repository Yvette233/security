package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.BasicManager;
import com.dao.SysMenuRoleDao;
import com.dao.SysPermissionRoleDao;
import com.dao.SysRoleDao;
import com.dao.SysRoleUserDao;
import com.pojo.SysMenuRole;
import com.pojo.SysPermissionRole;
import com.pojo.SysRole;
import com.pojo.SysRoleUser;
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
public class SysRoleManager extends BasicManager<SysRole> {
    public final static Log log = LogFactory.getLog(SysRoleManager.class);

    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    private SysRoleUserDao sysRoleUserDao;
    @Resource
    private SysPermissionRoleDao sysPermissionRoleDao;
    @Resource
    private SysMenuRoleDao sysMenuRoleDao;

    public void deleteRole(String id) {
        sysRoleDao.deleteById(id);
    }

    public void saveOrUpdate(SysRole sysRole) {
        //找到数据库中旧角色
        sysRoleDao.saveOrUpdate(sysRole);
    }

    public SysRole getById(String id) {
        return sysRoleDao.getById(id);
    }

    /*********************************************************************************/

    public Result getRoleList(String name, String nameCH, String userIds, String permissionIds, String menuIds, Page page) {
        List<SysRole> roleList = new ArrayList<SysRole>();
        String message = "association";

        if (StringUtils.isNotEmpty(userIds) && hasEqualRolesByUserIds(userIds)) {
            roleList = this.getSysRolesByUserId(userIds.split(",")[0], page).getLeft();
        } else if (StringUtils.isNotEmpty(permissionIds) && hasEqualRolesByPermissionIds(permissionIds)) {
            roleList = this.getSysRolesByPermissionId(permissionIds.split(",")[0], page).getLeft();
        } else if (StringUtils.isNotEmpty(menuIds) && hasEqualRolesByMenuIds(menuIds)) {
            roleList = this.getSysRolesByMenuId(menuIds.split(",")[0], page).getLeft();
        }

        if (CollectionUtils.isEmpty(roleList)) {
            message = "non-association";
            roleList = sysRoleDao.getSysRoleList(name, nameCH, null, page);
            if (CollectionUtils.isEmpty(roleList)) {
                return null;
            }
        }


        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }

        return Result.ok(message, (JSONArray) JSON.toJSON(roleList), page);
    }

    /***********************************关联关系********************************************/

    private Boolean hasEqualRolesByPermissionIds(String permissionIds) {
        Boolean hasEqualRoles = true;
        String[] permissionIdArray = permissionIds.split(",");
        List<SysPermissionRole> objectList = this.sysPermissionRoleDao.getSysPermissionRoleList(permissionIdArray[0], null, null);

        if (permissionIdArray.length > 1) {
            for (int i = 1; i < permissionIdArray.length; i++) {
                List<SysPermissionRole> list = this.sysPermissionRoleDao.getSysPermissionRoleList(permissionIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysPermissionRole::getRoleId))
                        .map(sysPermissionRole -> sysPermissionRole.getRoleId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysPermissionRole::getRoleId))
                                .map(sysPermissionRole -> sysPermissionRole.getRoleId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualRoles = false;
                    break;
                }
            }
        }
        return hasEqualRoles;
    }

    private Boolean hasEqualRolesByUserIds(String userIds) {
        Boolean hasEqualRoles = true;
        String[] userIdArray = userIds.split(",");
        List<SysRoleUser> objectList = this.sysRoleUserDao.getSysRoleUserList(userIdArray[0], null, null);

        if (userIdArray.length > 1) {
            for (int i = 1; i < userIdArray.length; i++) {
                List<SysRoleUser> list = this.sysRoleUserDao.getSysRoleUserList(userIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysRoleUser::getRoleId))
                        .map(sysRoleUser -> sysRoleUser.getRoleId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysRoleUser::getRoleId))
                                .map(sysRoleUser -> sysRoleUser.getRoleId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualRoles = false;
                    break;
                }
            }
        }
        return hasEqualRoles;
    }

    private Boolean hasEqualRolesByMenuIds(String menuIds) {
        Boolean hasEqualRoles = true;
        String[] menuIdArray = menuIds.split(",");
        List<SysMenuRole> objectList = this.sysMenuRoleDao.getSysMenuRoleList(menuIdArray[0], null, null);

        if (menuIdArray.length > 1) {
            for (int i = 1; i < menuIdArray.length; i++) {
                List<SysMenuRole> list = this.sysMenuRoleDao.getSysMenuRoleList(menuIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysMenuRole::getRoleId))
                        .map(sysMenuRole -> sysMenuRole.getRoleId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysMenuRole::getRoleId))
                                .map(sysMenuRole -> sysMenuRole.getRoleId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualRoles = false;
                    break;
                }
            }
        }
        return hasEqualRoles;
    }

    /***********************************关联关系********************************************/

    //每个权限包含角色信息
    public Pair<List<SysRole>, Set<SysRole>> getSysRolesByPermissionId(String permissionId, Page page) {
        List<SysPermissionRole> sysPermissionRoleList = sysPermissionRoleDao.getSysPermissionRoleList(permissionId, null, page);
        List<SysRole> sysRoleList = new ArrayList<SysRole>();
        Set<SysRole> sysRoleSet = new HashSet<SysRole>();

        if (!CollectionUtils.isEmpty(sysPermissionRoleList)) {
            sysPermissionRoleList.forEach(permissionRole -> {
                sysRoleList.add(permissionRole.getSysRole());
                sysRoleSet.add(permissionRole.getSysRole());
            });
        }
        return Pair.of(sysRoleList, sysRoleSet);
    }

    //每个权限包含角色信息
    public Pair<List<SysRole>, Set<SysRole>> getSysRolesByUserId(String userId, Page page) {
        List<SysRoleUser> sysRoleUserList = this.sysRoleUserDao.getSysRoleUserList(userId, null, page);
        List<SysRole> sysRoleList = new ArrayList<SysRole>();
        Set<SysRole> sysRoleSet = new HashSet<SysRole>();

        if (!CollectionUtils.isEmpty(sysRoleUserList)) {
            sysRoleUserList.forEach(sysRoleUser -> {
                sysRoleList.add(sysRoleUser.getSysRole());
                sysRoleSet.add(sysRoleUser.getSysRole());
            });
        }
        return Pair.of(sysRoleList, sysRoleSet);
    }

    //每个权限包含角色信息
    public Pair<List<SysRole>, Set<SysRole>> getSysRolesByMenuId(String menuId, Page page) {
        List<SysMenuRole> sysMenuRoleList = this.sysMenuRoleDao.getSysMenuRoleList(menuId, null, page);
        List<SysRole> sysRoleList = new ArrayList<SysRole>();
        Set<SysRole> sysRoleSet = new HashSet<SysRole>();

        if (!CollectionUtils.isEmpty(sysMenuRoleList)) {
            sysMenuRoleList.forEach(sysMenuRole -> {
                sysRoleList.add(sysMenuRole.getSysRole());
                sysRoleSet.add(sysMenuRole.getSysRole());
            });
        }
        return Pair.of(sysRoleList, sysRoleSet);
    }

    /***********************************查看、修改关联关系
     * @return********************************************/


    public String saveAssociation(String roleIds, String ids, String associationObjectName) {
        String label = "";
        String[] roleIdArray = roleIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < roleIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysRoleUserDao.saveSysRoleUserAssociation(roleIdArray[i], idArray[j]) + "\n";
                } else if ("菜单".equals(associationObjectName)) {
                    label += this.sysMenuRoleDao.saveSysMenuRoleAssociation(idArray[j], roleIdArray[i]) + "\n";
                } else if ("权限".equals(associationObjectName)) {
                    label += this.sysPermissionRoleDao.saveSysPermissionRoleAssociation(idArray[j], roleIdArray[i]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String roleIds, String ids, String associationObjectName) {
        String label = "";
        String[] roleIdArray = roleIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < roleIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysRoleUserDao.deleteSysRoleUserAssociation(roleIdArray[i], idArray[j]) + "\n";
                } else if ("菜单".equals(associationObjectName)) {
                    label += this.sysMenuRoleDao.deleteSysMenuRoleAssociation(idArray[j], roleIdArray[i]) + "\n";
                } else if ("权限".equals(associationObjectName)) {
                    label += this.sysPermissionRoleDao.deleteSysPermissionRoleAssociation(idArray[j], roleIdArray[i]) + "\n";
                }
            }
        }
        return label;
    }
}
