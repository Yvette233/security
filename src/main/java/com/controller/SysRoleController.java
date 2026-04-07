package com.controller;

import com.manager.SysRoleManager;
import com.pojo.SysRole;
import com.util.Page;
import com.util.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/6/30-20:59
 * creator：fangpengcheng
 */

@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    public final static Log log = LogFactory.getLog(SysRoleController.class);

    @Autowired
    private SysRoleManager sysRoleManager;

    @GetMapping(value = "/getRole")
    public Result getRole(@RequestParam String id) throws Exception {
        return Result.ok(sysRoleManager.getById(id));
    }

    @PostMapping(value = "/getRoleList")
    public Result getRoleList(@RequestParam(required = false) String name, @RequestParam(required = false) String nameCH,
                              @RequestParam(required = false) String userIds, @RequestParam(required = false) String permissionIds, @RequestParam(required = false) String menuIds,
                              @RequestBody(required = false) Page page) throws Exception {
        return sysRoleManager.getRoleList(name, nameCH, userIds, permissionIds, menuIds, page);
    }

    @PostMapping(value = "/saveOrUpdateRole")
    public Result saveOrUpdateRole(@RequestBody SysRole sysRole) throws Exception {
        sysRoleManager.saveOrUpdate(sysRole);
        return Result.ok();
    }

    @DeleteMapping(value = "/deleteRole/{id}")
    public Result deleteRole(@PathVariable String id) throws Exception {
        sysRoleManager.deleteRole(id);
        return Result.ok();
    }

    /********************************************关联关系****************************************************/

    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String roleIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(roleIds)) {
            return Result.error("请检查roleIds" + roleIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysRoleManager.saveAssociation(roleIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String roleIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(roleIds)) {
            return Result.error("请检查roleIds" + roleIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysRoleManager.deleteAssociation(roleIds, ids, associationObjectName));
    }
}
