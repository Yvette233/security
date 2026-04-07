package com.controller;

import com.manager.SysGroupManager;
import com.pojo.SysGroup;
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
@RequestMapping("/sysGroup")
public class SysGroupController {

    public final static Log log = LogFactory.getLog(SysGroupController.class);

    @Autowired
    private SysGroupManager sysGroupManager;

    @GetMapping(value = "/getGroup")
    public Result getGroup(@RequestParam String id) throws Exception {
        return Result.ok(sysGroupManager.getById(id));
    }

    @PostMapping(value = "/getGroupList")
    public Result getGroupList(@RequestParam(required = false) String name, @RequestParam(required = false) String nameCH,
                               @RequestParam(required = false) String userIds, @RequestBody(required = false) Page page) throws Exception {
        return sysGroupManager.getGroupList(name, nameCH, userIds, page);
    }

    @PostMapping(value = "/saveOrUpdateGroup")
    public Result saveOrUpdateGroup(@RequestBody SysGroup sysGroup) throws Exception {
        sysGroupManager.saveOrUpdate(sysGroup);
        return Result.ok();
    }

    @DeleteMapping(value = "/deleteGroup/{id}")
    public Result deleteGroup(@PathVariable String id) throws Exception {
        sysGroupManager.deleteGroup(id);
        return Result.ok();
    }

    /********************************************关联关系****************************************************/
    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String groupIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(groupIds)) {
            return Result.error("请检查groupIds" + groupIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysGroupManager.saveAssociation(groupIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String groupIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(groupIds)) {
            return Result.error("请检查groupIds" + groupIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysGroupManager.deleteAssociation(groupIds, ids, associationObjectName));
    }
}
