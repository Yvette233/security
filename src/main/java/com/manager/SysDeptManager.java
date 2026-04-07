package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.BasicManager;
import com.dao.SysDeptDao;
import com.dao.SysDeptUserDao;
import com.pojo.SysDept;
import com.pojo.SysDeptUser;
import com.util.Page;
import com.util.RecursionTree;
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
public class SysDeptManager extends BasicManager<SysDept> {
    public final static Log log = LogFactory.getLog(SysDeptManager.class);

    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private SysDeptUserDao sysDeptUserDao;


    public void deleteDept(String id) {
        sysDeptDao.deleteById(id);
    }

    public void saveOrUpdate(SysDept sysDept) {
        //找到数据库中旧部门
        sysDeptDao.saveOrUpdate(sysDept);
    }

    public SysDept getById(String id) {
        return sysDeptDao.getById(id);
    }

    /*********************************************************************************/
    public Result getDeptList(String id, String parentId, String userIds, Page page) {
        //以后这里须要基于Page对数据进行截取，获取当前页面的数据 fpc todo
        List<SysDept> currentPageDeptList = new ArrayList<SysDept>();
        String message = "association";

        RecursionTree recursionTree = new RecursionTree<SysDept>();

        if (StringUtils.isNotEmpty(userIds) && hasEqualDeptsByUserIds(userIds)) {
            currentPageDeptList = this.getSysDeptsByUserId(userIds.split(",")[0], page).getLeft();
        }
        if (CollectionUtils.isEmpty(currentPageDeptList)) {
            message = "non-association";
            //先把所有对象找出来
            List<SysDept> allDeptList = sysDeptDao.getAllResult();

            //找到最上层级的部门
            if (!ObjectUtils.isEmpty(id) || !ObjectUtils.isEmpty(parentId)) {
                //筛选条件
                currentPageDeptList = sysDeptDao.getDeptList(id, parentId, page);
            } else {
                List<SysDept> parentDeptList = recursionTree.getRootNodeList(allDeptList);
                this.getCurrentPageObjects(parentDeptList, page, currentPageDeptList);
            }

            //递归找到其所有子部门
//        List<SysDept> allChildDeptList = new ArrayList<SysDept>();
            for (SysDept parentDept : currentPageDeptList) {
//            recursionTree.getAllChildren(deptList, parentDept, allChildDeptList);
//            //返回时不要落下该父部门
//            allChildDeptList.add(parentDept);
                recursionTree.buildChildTree(allDeptList, parentDept);
            }
        }


        JSONArray jsonArray2 = JSONArray.parseArray(JSONObject.toJSONString(currentPageDeptList));

        JSONArray jsonArray = (JSONArray) JSON.toJSON(currentPageDeptList);
        return Result.ok(message, jsonArray, page);
    }

    /***********************************关联关系********************************************/
    private Boolean hasEqualDeptsByUserIds(String userIds) {
        Boolean hasEqualUsers = true;
        //判断这些用户是否所属相同的部门
        String[] userIdArray = userIds.split(",");
        List<SysDeptUser> objectList = this.sysDeptUserDao.getSysDeptUserList(null, userIdArray[0], null);

        if (userIdArray.length > 1) {
            for (int i = 1; i < userIdArray.length; i++) {
                List<SysDeptUser> list = this.sysDeptUserDao.getSysDeptUserList(null, userIdArray[i], null);
//                   isEqual= !list.retainAll(currentPageDeptList);//移除操作
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysDeptUser::getDeptId).reversed())
                        .map(sysDeptUser -> {
                            return sysDeptUser.getDeptId();
                        }).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysDeptUser::getDeptId).reversed())
                                .map(sysDeptUser -> sysDeptUser.getDeptId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualUsers = false;
                    break;
                }
            }
        }
        return hasEqualUsers;
    }

    //每个权限包含角色信息
    public Pair<List<SysDept>, Set<SysDept>> getSysDeptsByUserId(String userId, Page page) {
        List<SysDeptUser> sysDeptUserList = this.sysDeptUserDao.getSysDeptUserList(null, userId, page);
        List<SysDept> sysDeptList = new ArrayList<SysDept>();
        Set<SysDept> sysDeptSet = new HashSet<SysDept>();

        if (!CollectionUtils.isEmpty(sysDeptUserList)) {
            sysDeptUserList.forEach(sysDeptUser -> {
                sysDeptList.add(sysDeptUser.getSysDept());
                sysDeptSet.add(sysDeptUser.getSysDept());
            });
        }
        return Pair.of(sysDeptList, sysDeptSet);
    }

    /***********************************查看、修改关联关系********************************************/


    public String saveAssociation(String deptIds, String ids, String associationObjectName) {
        String label = "";
        String[] deptIdArray = deptIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < deptIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysDeptUserDao.saveSysDeptUserAssociation(deptIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String deptIds, String ids, String associationObjectName) {
        String label = "";
        String[] deptIdArray = deptIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < deptIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysDeptUserDao.deleteSysDeptUserAssociation(deptIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }
}
