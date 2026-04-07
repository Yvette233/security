package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.BasicManager;
import com.dao.SysPositionDao;
import com.dao.SysPositionUserDao;
import com.pojo.SysPosition;
import com.pojo.SysPositionUser;
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
public class SysPositionManager extends BasicManager<SysPosition> {
    public final static Log log = LogFactory.getLog(SysPositionManager.class);

    @Resource
    private SysPositionDao sysPositionDao;
    @Resource
    private SysPositionUserDao sysPositionUserDao;

    public void deletePosition(String id) {
        sysPositionDao.deleteById(id);
    }

    public void saveOrUpdate(SysPosition sysPosition) {
        //找到数据库中旧部门
        sysPositionDao.saveOrUpdate(sysPosition);
    }

    public SysPosition getById(String id) {
        return sysPositionDao.getById(id);
    }

    /*********************************************************************************/
    public Result getPositionList(String id, String parentId, String userIds, Page page) {
        //以后这里须要基于Page对数据进行截取，获取当前页面的数据 fpc
        List<SysPosition> currentPagePositionList = new ArrayList<SysPosition>();
        String message = "association";
        RecursionTree recursionTree = new RecursionTree<SysPosition>();

        if (StringUtils.isNotEmpty(userIds) && hasEqualPositionsByUserIds(userIds)) {
            currentPagePositionList.addAll(this.getSysPositionsByUserId(userIds.split(",")[0], page).getLeft());
        }


        if (CollectionUtils.isEmpty(currentPagePositionList)) {
            message = "non-association";
            //先把所有对象找出来
            List<SysPosition> allPositionList = sysPositionDao.getAllResult();

            //找到最上层级的部门
            if (!ObjectUtils.isEmpty(id) || !ObjectUtils.isEmpty(parentId)) {
                //筛选条件
                currentPagePositionList = sysPositionDao.getPositionList(id, parentId, null);
            } else {
                List<SysPosition> parentPositionList = recursionTree.getRootNodeList(allPositionList);
                this.getCurrentPageObjects(parentPositionList, page, currentPagePositionList);
            }

            //递归找到其所有子部门
            for (SysPosition parentPosition : currentPagePositionList) {
                recursionTree.buildChildTree(allPositionList, parentPosition);
            }
        }

        return Result.ok(message, (JSONArray) JSON.toJSON(currentPagePositionList), page);
    }

    /***********************************关联关系********************************************/

    private Boolean hasEqualPositionsByUserIds(String userIds) {
        Boolean hasEqualPositions = true;
        String[] userIdArray = userIds.split(",");
        List<SysPositionUser> objectList = this.sysPositionUserDao.getSysPositionUserList(null, userIdArray[0], null);

        if (userIdArray.length > 1) {
            for (int i = 1; i < userIdArray.length; i++) {
                List<SysPositionUser> list = this.sysPositionUserDao.getSysPositionUserList(null, userIdArray[i], null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysPositionUser::getPositionId))
                        .map(sysPositionUser -> sysPositionUser.getPositionId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysPositionUser::getPositionId))
                                .map(sysPositionUser -> sysPositionUser.getPositionId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualPositions = false;
                    break;
                }
            }
        }
        return hasEqualPositions;
    }

    //每个权限包含角色信息
    public Pair<List<SysPosition>, Set<SysPosition>> getSysPositionsByUserId(String userId, Page page) {
        List<SysPositionUser> sysPositionUserList = this.sysPositionUserDao.getSysPositionUserList(null, userId, page);
        List<SysPosition> sysPositionList = new ArrayList<SysPosition>();
        Set<SysPosition> sysPositionSet = new HashSet<SysPosition>();

        if (!CollectionUtils.isEmpty(sysPositionUserList)) {
            sysPositionUserList.forEach(sysPositionUser -> {
                sysPositionList.add(sysPositionUser.getSysPosition());
                sysPositionSet.add(sysPositionUser.getSysPosition());
            });
        }
        return Pair.of(sysPositionList, sysPositionSet);
    }

    /***********************************查看、修改关联关系 ********************************************/


    public String saveAssociation(String positionIds, String ids, String associationObjectName) {
        String label = "";
        String[] positionIdArray = positionIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < positionIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysPositionUserDao.saveSysPositionUserAssociation(positionIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String positionIds, String ids, String associationObjectName) {
        String label = "";
        String[] positionIdArray = positionIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < positionIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("用户".equals(associationObjectName)) {
                    label += this.sysPositionUserDao.deleteSysPositionUserAssociation(positionIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }
}
