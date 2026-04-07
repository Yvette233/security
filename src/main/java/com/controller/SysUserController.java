package com.controller;

import com.manager.SysUserManager;
import com.pojo.SysUser;
import com.util.Page;
import com.util.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/6/30-20:59
 * creator：fangpengcheng
 */

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    public final static Log log = LogFactory.getLog(SysUserController.class);

    @Autowired
    private SysUserManager sysUserManager;

    @GetMapping(value = "/getUser")
    public Result getUser(@RequestParam String id) throws Exception {
        SysUser sysUser = sysUserManager.getById(id);
        sysUser.setPassword(null);
        return Result.ok(sysUser);
    }

    @PostMapping(value = "/getUserList")
    public Result getUserList(@RequestParam(required = false) String name, @RequestParam(required = false) String nameCH, @RequestParam(required = false) String type, @RequestParam(required = false) Long sex,
                              @RequestParam(required = false) String roleIds, @RequestParam(required = false) String deptIds, @RequestParam(required = false) String positionIds, @RequestParam(required = false) String groupIds,
                              @RequestBody(required = false) Page page) throws Exception {
        return sysUserManager.getUserList(name, nameCH, type, sex, roleIds, deptIds, positionIds, groupIds, page);
    }

    @PostMapping(value = "/saveOrUpdateUser")
    public Result saveOrUpdateUser(@RequestBody SysUser sysUser) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUserManager.saveOrUpdate(sysUser);
        return Result.ok();
    }

    @DeleteMapping(value = "/deleteUser/{id}")
    public Result deleteUser(@PathVariable String id) throws Exception {
        sysUserManager.deleteUser(id);
        return Result.ok();
    }

    /*************************************************************************/
    @GetMapping(value = "/getUsers")
    public Result getUsers(@RequestParam(required = false) String candidateGroups) {
        return Result.ok(sysUserManager.getUsers(candidateGroups));
    }

    @GetMapping(value = "/getTurnCandidateUsers/{taskId}/{turnType}")
    public Result getTurnCandidateUsers(@PathVariable String taskId, @PathVariable Boolean turnType) {
        return Result.ok(sysUserManager.getTurnCandidateUsers(taskId, turnType));
    }

    /********************************************关联关系****************************************************/


    @PostMapping("/getAssociations")
    @ResponseBody
    public Result getAssociations(@RequestParam(required = false) String id, @RequestBody(required = false) Page page) {
        return sysUserManager.getAssociations(id, page);
    }

    @PutMapping(value = "/saveAssociation")
    public Result saveAssociation(@RequestParam(required = false) String userIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(userIds)) {
            return Result.error("请检查userIds" + userIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysUserManager.saveAssociation(userIds, ids, associationObjectName));
    }

    @DeleteMapping(value = "/deleteAssociation")
    public Result deleteAssociation(@RequestParam(required = false) String userIds, @RequestParam(required = false) String ids, @RequestParam(required = false) String associationObjectName) throws Exception {
        if (!StringUtils.hasLength(userIds)) {
            return Result.error("请检查userIds" + userIds);
        } else if (!StringUtils.hasLength(ids)) {
            return Result.error("请检查ids" + ids);
        } else if (!StringUtils.hasLength(associationObjectName)) {
            return Result.error("请检查associationObjectName" + associationObjectName);
        }
        return Result.ok(sysUserManager.deleteAssociation(userIds, ids, associationObjectName));
    }

}
