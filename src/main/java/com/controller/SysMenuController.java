package com.controller;

import com.manager.SysMenuManager;
import com.pojo.SysMenu;
import com.util.Page;
import com.util.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/6/30-20:59
 * creator：fangpengcheng
 */

@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    public final static Log log = LogFactory.getLog(SysMenuController.class);

    @Resource
    private SysMenuManager sysMenuManager;


    @PostMapping("/getMenuList")
    @ResponseBody
    public Result getMenuList(@RequestParam(required = false) String id, @RequestParam(required = false) String parentId,
                              @RequestParam(required = false) String roleIds, @RequestBody(required = false) Page page) {
        return sysMenuManager.getMenuList(id, parentId, roleIds, page);
    }

    @GetMapping(value = "/getMenu")
    public Result getMenu(@RequestParam String id) throws Exception {
        return Result.ok(sysMenuManager.getById(id));
    }

    @PostMapping(value = "/saveOrUpdateMenu")
    public Result saveOrUpdateMenu(@RequestBody SysMenu sysMenu) throws Exception {
        sysMenuManager.saveOrUpdate(sysMenu);
        return Result.ok();
    }

    @DeleteMapping(value = "/deleteMenu/{id}")
    public Result deleteMenu(@PathVariable String id) throws Exception {
        sysMenuManager.deleteMenu(id);
        return Result.ok();
    }
    //////////////////////////////////准备删除 fpc todo ////////////////////////////////////////////////////

    //目前是为了拼成查看的路由，包括所有的子菜单，上面getMenuList方法没有子菜单
    @GetMapping("/getMenusByRoleId")
    @ResponseBody
    public Result getMenusByRoleId(@RequestParam(value = "roleId", required = false) String roleId) {
        return Result.ok(sysMenuManager.getMenusByRoleId(roleId));
    }

    /********************************************关联关系****************************************************/
    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String menuIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(menuIds)) {
            return Result.error("请检查menuIds" + menuIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysMenuManager.saveAssociation(menuIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String menuIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(menuIds)) {
            return Result.error("请检查menuIds" + menuIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysMenuManager.deleteAssociation(menuIds, ids, associationObjectName));
    }
}
