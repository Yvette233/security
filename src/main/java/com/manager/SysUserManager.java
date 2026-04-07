package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.*;
import com.pojo.*;
import com.util.Page;
import com.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SysUserManager {
    public final static Log log = LogFactory.getLog(SysUserManager.class);

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysGroupUserDao sysGroupUserDao;
    @Resource
    private SysRoleUserDao sysRoleUserDao;
    @Resource
    private SysPositionUserDao sysPositionUserDao;
    @Resource
    private SysDeptUserDao sysDeptUserDao;

    public void deleteUser(String id) {
        sysUserDao.deleteById(id);
    }

    public void saveOrUpdate(SysUser sysUser) {
        //找到数据库中旧角色
        sysUserDao.saveOrUpdate(sysUser);
    }

    public SysUser getById(String id) {
        return sysUserDao.getById(id);
    }

    /*********************************************************************************/

    public Result getUserList(String name, String nameCH, String type, Long sex,
                              String roleIds, String deptIds, String positionIds, String groupIds, Page page) {
        List<SysUser> userList = new ArrayList<SysUser>();
        String message = "association";

        if (StringUtils.isNotEmpty(roleIds) && hasEqualUsersByRoleIds(roleIds)) {
            userList = this.getSysUsersByRoleId(roleIds.split(",")[0], page).getLeft();
        } else if (StringUtils.isNotEmpty(deptIds) && hasEqualUsersByDeptIds(deptIds)) {
            userList = this.getSysUsersByDeptId(deptIds.split(",")[0], page).getLeft();
        } else if (StringUtils.isNotEmpty(positionIds) && hasEqualUsersByPositionIds(positionIds)) {
            userList = this.getSysUsersByPositionId(positionIds.split(",")[0], page).getLeft();
        } else if (StringUtils.isNotEmpty(groupIds) && hasEqualUsersByGroupIds(groupIds)) {
            userList = this.getSysUsersByGroupId(groupIds.split(",")[0], page).getLeft();
        }

        if (CollectionUtils.isEmpty(userList)) {
            message = "non-association";
            userList = sysUserDao.getSysUserList(name, nameCH, type, sex, page);
            if (CollectionUtils.isEmpty(userList)) {
                return null;
            }
        }

        return Result.ok(message, (JSONArray) JSON.toJSON(userList), page);
    }

    /*********************************************************************************/


    public JSONArray getUsers(String candidateGroups) {
        List<SysUser> userList = null;
        if (ObjectUtils.isEmpty(candidateGroups)) {
            //获得全部人员信息
            userList = sysUserDao.getAllResult();
        }
        return this.switchJSONArray(userList);
    }


    //fpc todo
    public Result getTurnCandidateUsers(String taskId, Boolean turnType) {
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        if (task == null) {
//            return Result.error("任务不存在");
//        }
//
//        //如果是代办，则把所有用户都找出来
//        if (!turnType) {
//            List<SysUser> userList = this.sysUserDao.getAllResult();
//            if (StringUtils.isNotEmpty(task.getAssignee())) {
//                Iterator<SysUser> it = userList.iterator();
//                while (it.hasNext()) {
//                    SysUser sysUser = (SysUser) it.next();
//                    if (sysUser.getId().equals(task.getAssignee())) {
//                        it.remove();
//                        break;
//                    }
//                }
//            }
//            return Result.ok(this.switchJSONArray(userList));
//        }
//
//        // 获取当前模型
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//        // 根据任务节点id获取当前节点
//        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
//
//        List<SysUser> userList = new ArrayList<SysUser>();
//        if (flowElement instanceof UserTask) {
//            UserTask userTask = (UserTask) flowElement;
//            if (!ObjectUtils.isEmpty(userTask.getAssignee())) {
////                return Result.ok("没有可转办人员");
//            } else if (!CollectionUtils.isEmpty(userTask.getCandidateGroups())) {
////把候选组的用户取一遍
//                for (String groupId : userTask.getCandidateGroups()) {
//                    List<SysGroupUser> sysGroupUserList = sysGroupUserDao.getSysGroupUserList(groupId, null, null);
//                    sysGroupUserList.forEach(groupUser -> {
//                        if (StringUtils.isEmpty(task.getAssignee()) || (StringUtils.isNotEmpty(task.getAssignee()) && !task.getAssignee().equals(groupUser.getSysUser().getId()))) {
//                            userList.add(groupUser.getSysUser());
//                        }
//                    });
//                }
//            } else if (!CollectionUtils.isEmpty(userTask.getCandidateUsers())) {
//                for (String userId : userTask.getCandidateUsers()) {
//                    if (task.getAssignee() != null && task.getAssignee().equals(userId)) {
//                        continue;
//                    }
//                    SysUser sysUser = (SysUser) sysUserDao.getById(userId);
//                    if (!ObjectUtils.isEmpty(sysUser)) {
//                        userList.add(sysUser);
//                    }
//                }
//            }
//        }
//        return Result.ok(this.switchJSONArray(userList));
        return null;
    }


    public JSONArray switchJSONArray(List<SysUser> userList) {
        JSONArray jsonArray = new JSONArray();
        if (!CollectionUtils.isEmpty(userList)) {
            for (SysUser user : userList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("label", user.getUserNameCH());
                jsonObject.put("value", user.getId());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /***********************************安全验证springsecurity********************************************/

    public List<SysUser> getUserByDeptId(String deptId) {
        List<SysUser> sysUserList = sysUserDao.findByProperty("deptId", deptId);
        return sysUserList;
    }

    /***********************************判断对象内的所有对象关联到了相同的用户********************************************/
    private Boolean hasEqualUsersByRoleIds(String roleIds) {
        Boolean hasEqualUsers = true;
        String[] roleIdArray = roleIds.split(",");
        List<SysRoleUser> objectList = this.sysRoleUserDao.getSysRoleUserList(null, roleIdArray[0], null);

        if (roleIdArray.length > 1) {
            for (int i = 1; i < roleIdArray.length; i++) {
                List<SysRoleUser> list = this.sysRoleUserDao.getSysRoleUserList(null, roleIdArray[i], null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysRoleUser::getUserId))
                        .map(sysRoleUser -> sysRoleUser.getUserId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysRoleUser::getUserId))
                                .map(sysRoleUser -> sysRoleUser.getUserId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualUsers = false;
                    break;
                }
            }
        }
        return hasEqualUsers;
    }

    private Boolean hasEqualUsersByPositionIds(String positionIds) {
        Boolean hasEqualUsers = true;
        String[] positionIdArray = positionIds.split(",");
        List<SysPositionUser> objectList = this.sysPositionUserDao.getSysPositionUserList(positionIdArray[0], null, null);

        if (positionIdArray.length > 1) {
            for (int i = 1; i < positionIdArray.length; i++) {
                List<SysPositionUser> list = this.sysPositionUserDao.getSysPositionUserList(positionIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysPositionUser::getUserId))
                        .map(sysPositionUser -> sysPositionUser.getUserId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysPositionUser::getUserId))
                                .map(sysPositionUser -> sysPositionUser.getUserId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualUsers = false;
                    break;
                }
            }
        }
        return hasEqualUsers;
    }

    private Boolean hasEqualUsersByDeptIds(String deptIds) {
        Boolean hasEqualUsers = true;
        String[] deptIdArray = deptIds.split(",");
        List<SysDeptUser> objectList = this.sysDeptUserDao.getSysDeptUserList(deptIdArray[0], null, null);

        if (deptIdArray.length > 1) {
            for (int i = 1; i < deptIdArray.length; i++) {
                List<SysDeptUser> list = this.sysDeptUserDao.getSysDeptUserList(deptIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysDeptUser::getUserId))
                        .map(sysDeptUser -> sysDeptUser.getUserId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysDeptUser::getUserId))
                                .map(sysDeptUser -> sysDeptUser.getUserId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualUsers = false;
                    break;
                }
            }
        }
        return hasEqualUsers;
    }

    private Boolean hasEqualUsersByGroupIds(String groupIds) {
        Boolean hasEqualUsers = true;
        String[] groupIdArray = groupIds.split(",");
        List<SysGroupUser> objectList = this.sysGroupUserDao.getSysGroupUserList(groupIdArray[0], null, null);

        if (groupIdArray.length > 1) {
            for (int i = 1; i < groupIdArray.length; i++) {
                List<SysGroupUser> list = this.sysGroupUserDao.getSysGroupUserList(groupIdArray[i], null, null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysGroupUser::getUserId))
                        .map(sysGroupUser -> sysGroupUser.getUserId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysGroupUser::getUserId))
                                .map(sysGroupUser -> sysGroupUser.getUserId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualUsers = false;
                    break;
                }
            }
        }
        return hasEqualUsers;
    }

    /***********************************关联关系********************************************/


    //每个权限包含角色信息
    public Pair<List<SysUser>, Set<SysUser>> getSysUsersByRoleId(String roleId, Page page) {
        List<SysRoleUser> sysRoleUserList = this.sysRoleUserDao.getSysRoleUserList(null, roleId, page);
        List<SysUser> sysUserList = new ArrayList<SysUser>();
        Set<SysUser> sysUserSet = new HashSet<SysUser>();

        if (!CollectionUtils.isEmpty(sysRoleUserList)) {
            sysRoleUserList.forEach(sysRoleUser -> {
                sysUserList.add(sysRoleUser.getSysUser());
                sysUserSet.add(sysRoleUser.getSysUser());
            });
        }
        return Pair.of(sysUserList, sysUserSet);
    }

    //每个权限包含角色信息
    public Pair<List<SysUser>, Set<SysUser>> getSysUsersByPositionId(String positionId, Page page) {
        List<SysPositionUser> sysPositionUserList = this.sysPositionUserDao.getSysPositionUserList(positionId, null, page);
        List<SysUser> sysUserList = new ArrayList<SysUser>();
        Set<SysUser> sysUserSet = new HashSet<SysUser>();

        if (!CollectionUtils.isEmpty(sysPositionUserList)) {
            sysPositionUserList.forEach(sysPositionUser -> {
                sysUserList.add(sysPositionUser.getSysUser());
                sysUserSet.add(sysPositionUser.getSysUser());
            });
        }
        return Pair.of(sysUserList, sysUserSet);
    }

    //每个权限包含角色信息
    public Pair<List<SysUser>, Set<SysUser>> getSysUsersByDeptId(String deptId, Page page) {
        List<SysDeptUser> sysDeptUserList = this.sysDeptUserDao.getSysDeptUserList(deptId, null, page);
        List<SysUser> sysUserList = new ArrayList<SysUser>();
        Set<SysUser> sysUserSet = new HashSet<SysUser>();

        if (!CollectionUtils.isEmpty(sysDeptUserList)) {
            sysDeptUserList.forEach(sysDeptUser -> {
                sysUserList.add(sysDeptUser.getSysUser());
                sysUserSet.add(sysDeptUser.getSysUser());
            });
        }
        return Pair.of(sysUserList, sysUserSet);
    }

    //每个权限包含角色信息
    public Pair<List<SysUser>, Set<SysUser>> getSysUsersByGroupId(String groupId, Page page) {
        List<SysGroupUser> sysGroupUserList = this.sysGroupUserDao.getSysGroupUserList(groupId, null, page);
        List<SysUser> sysUserList = new ArrayList<SysUser>();
        Set<SysUser> sysUserSet = new HashSet<SysUser>();

        if (!CollectionUtils.isEmpty(sysGroupUserList)) {
            sysGroupUserList.forEach(sysGroupUser -> {
                sysUserList.add(sysGroupUser.getSysUser());
                sysUserSet.add(sysGroupUser.getSysUser());
            });
        }
        return Pair.of(sysUserList, sysUserSet);
    }

    /***********************************查看、修改关联关系********************************************/

    public Result getAssociations(String id, Page page) {
        JSONObject jsonObject = new JSONObject();


        return Result.ok("", jsonObject, page);
    }

    public String saveAssociation(String userIds, String ids, String associationObjectName) {
        String label = "";
        String[] userIdArray = userIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < userIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysRoleUserDao.saveSysRoleUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("职位".equals(associationObjectName)) {
                    label += this.sysPositionUserDao.saveSysPositionUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("部门".equals(associationObjectName)) {
                    label += this.sysDeptUserDao.saveSysDeptUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("组".equals(associationObjectName)) {
                    label += this.sysGroupUserDao.saveSysGroupUserAssociation(idArray[j], userIdArray[i]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String userIds, String ids, String associationObjectName) {
        String label = "";
        String[] userIdArray = userIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < userIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysRoleUserDao.deleteSysRoleUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("职位".equals(associationObjectName)) {
                    label += this.sysPositionUserDao.deleteSysPositionUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("部门".equals(associationObjectName)) {
                    label += this.sysDeptUserDao.deleteSysDeptUserAssociation(idArray[j], userIdArray[i]) + "\n";
                } else if ("组".equals(associationObjectName)) {
                    label += this.sysGroupUserDao.deleteSysGroupUserAssociation(idArray[j], userIdArray[i]) + "\n";
                }
            }
        }
        return label;
    }

}
