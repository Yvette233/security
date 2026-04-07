package com.controller;

import com.manager.SysPermissionManager;
import com.pojo.SysPermission;
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
@RequestMapping("/sysPermission")
public class SysPermissionController {

    public final static Log log = LogFactory.getLog(SysPermissionController.class);

    @Autowired
    private SysPermissionManager sysPermissionManager;

    @GetMapping(value = "/getPermission")
    public Result getPermission(@RequestParam String id) throws Exception {
        return Result.ok(sysPermissionManager.getById(id));
    }

    @PostMapping(value = "/getPermissionList")
    public Result getPermissionList(@RequestParam(required = false) String name, @RequestParam(required = false) String type, @RequestParam(required = false) String method,
                                    @RequestParam(required = false) String roleIds, @RequestBody(required = false) Page page) throws Exception {
        return sysPermissionManager.getPermissionList(name, type, method, roleIds, page);
    }

    @PostMapping(value = "/saveOrUpdatePermission")
    public Result saveOrUpdatePermission(@RequestBody SysPermission sysPermission) throws Exception {
        sysPermissionManager.saveOrUpdate(sysPermission);
        return Result.ok();
    }

    @DeleteMapping(value = "/deletePermission/{id}")
    public Result deletePermission(@PathVariable String id) throws Exception {
        sysPermissionManager.deletePermission(id);
        return Result.ok();
    }

    /********************************************关联关系****************************************************/

    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String permissionIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(permissionIds)) {
            return Result.error("请检查permissionIds" + permissionIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysPermissionManager.saveAssociation(permissionIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String permissionIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(permissionIds)) {
            return Result.error("请检查permissionIds" + permissionIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysPermissionManager.deleteAssociation(permissionIds, ids, associationObjectName));
    }
}
