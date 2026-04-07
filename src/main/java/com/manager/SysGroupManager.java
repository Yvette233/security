package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.BasicManager;
import com.dao.SysGroupDao;
import com.dao.SysGroupUserDao;
import com.pojo.SysGroup;
import com.pojo.SysGroupUser;
import com.util.Page;
import com.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SysGroupManager extends BasicManager<SysGroup> {
    public final static Log log = LogFactory.getLog(SysGroupManager.class);

    @Resource
    private SysGroupDao sysGroupDao;
    @Resource
    private SysGroupUserDao sysGroupUserDao;

    public void deleteGroup(String id) {
        sysGroupDao.deleteById(id);
    }

    public void saveOrUpdate(SysGroup sysGroup) {
        //找到数据库中旧组
        sysGroupDao.saveOrUpdate(sysGroup);
    }

    public SysGroup getById(String id) {
        return sysGroupDao.getById(id);
    }

    /*********************************************************************************/
    public Result getGroupList(String name, String nameCH, String userIds, Page page) {
        List<SysGroup> groupList = new ArrayList<SysGroup>();
        String message = "association";
        if (StringUtils.isNotEmpty(userIds) && hasEqualGroupsByUserIds(userIds)) {
            groupList = this.getSysGroupsByUserId(userIds.split(",")[0], page).getLeft();
        }

        if (CollectionUtils.isEmpty(groupList)) {
            message = "non-association";
            groupList = sysGroupDao.getSysGroupList(name, nameCH, page);
            if (CollectionUtils.isEmpty(groupList)) {
                return null;
            }
        }
        return Result.ok(message, this.addProperties(groupList), page);
    }

    public JSONArray addProperties(List<SysGroup> groupList) {
        JSONArray jsonArray = new JSONArray();
        if (!CollectionUtils.isEmpty(groupList)) {
            for (SysGroup group : groupList) {
//                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObject = (JSONObject) JSON.toJSON(group);
                jsonObject.put("label", group.getNameCH());
                jsonObject.put("value", group.getId());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /***********************************关联关系********************************************/
    private Boolean hasEqualGroupsByUserIds(String userIds) {
        Boolean hasEqualGroups = true;
        String[] userIdArray = userIds.split(",");
        List<SysGroupUser> objectList = this.sysGroupUserDao.getSysGroupUserList(null, userIdArray[0], null);

        if (userIdArray.length > 1) {
            for (int i = 1; i < userIdArray.length; i++) {
                List<SysGroupUser> list = this.sysGroupUserDao.getSysGroupUserList(null, userIdArray[i], null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysGroupUser::getGroupId))
                        .map(sysGroupUser -> sysGroupUser.getGroupId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysGroupUser::getGroupId))
                                .map(sysGroupUser -> sysGroupUser.getGroupId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualGroups = false;
                    break;
                }
            }
        }
        return hasEqualGroups;
    }

    //得到用户与组的关系
    public Pair<List<SysGroup>, Set<SysGroup>> getSysGroupsByUserId(String userId, Page page) {
        List<SysGroupUser> sysGroupUserList = sysGroupUserDao.getSysGroupUserList(null, userId, page);
        Set<SysGroup> sysGroupSet = new HashSet<>();
        List<SysGroup> sysGroupList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sysGroupUserList)) {
            sysGroupUserList.forEach(groupUser -> {
                sysGroupSet.add(groupUser.getSysGroup());
                sysGroupList.add(groupUser.getSysGroup());
            });
        }
        return Pair.of(sysGroupList, sysGroupSet);
    }

    /***********************************查看、修改关联关系********************************************/


    public String saveAssociation(String groupIds, String ids, String associationObjectName) {
        String label = "";
        String[] groupIdArray = groupIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < groupIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("组".equals(associationObjectName)) {
                    label += this.sysGroupUserDao.saveSysGroupUserAssociation(groupIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String groupIds, String ids, String associationObjectName) {
        String label = "";
        String[] groupIdArray = groupIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < groupIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("组".equals(associationObjectName)) {
                    label += this.sysGroupUserDao.deleteSysGroupUserAssociation(groupIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }
}
