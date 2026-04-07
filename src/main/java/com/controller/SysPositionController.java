package com.controller;

import com.manager.SysPositionManager;
import com.pojo.SysPosition;
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
@RequestMapping("/sysPosition")
public class SysPositionController {

    public final static Log log = LogFactory.getLog(SysPositionController.class);

    @Autowired
    private SysPositionManager sysPositionManager;

    @PostMapping(value = "/getPositionList")
    public Result getPositionList(@RequestParam(required = false) String id, @RequestParam(required = false) String parentId,
                                  @RequestParam(required = false) String userIds, @RequestBody(required = false) Page page) throws Exception {
        return sysPositionManager.getPositionList(id, parentId, userIds, page);
    }

    @GetMapping(value = "/getPosition")
    public Result getPosition(@RequestParam String id) throws Exception {
        return Result.ok(sysPositionManager.getById(id));
    }

    @PostMapping(value = "/saveOrUpdatePosition")
    public Result saveOrUpdatePosition(@RequestBody SysPosition sysPosition) throws Exception {
        sysPositionManager.saveOrUpdate(sysPosition);
        return Result.ok();
    }

    @DeleteMapping(value = "/deletePosition/{id}")
    public Result deletePosition(@PathVariable String id) throws Exception {
        sysPositionManager.deletePosition(id);
        return Result.ok();
    }

    /********************************************关联关系****************************************************/
    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String positionIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(positionIds)) {
            return Result.error("请检查positionIds" + positionIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysPositionManager.saveAssociation(positionIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String positionIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(positionIds)) {
            return Result.error("请检查positionIds" + positionIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysPositionManager.deleteAssociation(positionIds, ids, associationObjectName));
    }
}
