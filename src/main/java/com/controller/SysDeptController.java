package com.controller;

import com.manager.SysDeptManager;
import com.pojo.SysDept;
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
@RequestMapping("/sysDept")
public class SysDeptController {

    public final static Log log = LogFactory.getLog(SysDeptController.class);

    @Autowired
    private SysDeptManager sysDeptManager;

    @PostMapping(value = "/getDeptList")
    public Result getDeptList(@RequestParam(required = false) String id, @RequestParam(required = false) String parentId,
                              @RequestParam(required = false) String userIds, @RequestBody(required = false) Page page) throws Exception {
        return sysDeptManager.getDeptList(id, parentId, userIds, page);
    }

    @GetMapping(value = "/getDept")
    public Result getDept(@RequestParam String id) throws Exception {
        return Result.ok(sysDeptManager.getById(id));
    }

    @PostMapping(value = "/saveOrUpdateDept")
    public Result saveOrUpdateDept(@RequestBody SysDept sysDept) throws Exception {
        sysDeptManager.saveOrUpdate(sysDept);
        return Result.ok();
    }

    @DeleteMapping(value = "/deleteDept/{id}")
    public Result deleteDept(@PathVariable String id) throws Exception {
        sysDeptManager.deleteDept(id);
        return Result.ok();
    }

    /********************************************关联关系****************************************************/
    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String deptIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(deptIds)) {
            return Result.error("请检查deptIds" + deptIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysDeptManager.saveAssociation(deptIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String deptIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(deptIds)) {
            return Result.error("请检查deptIds" + deptIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysDeptManager.deleteAssociation(deptIds, ids, associationObjectName));
    }
}
