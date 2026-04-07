package com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.BasicManager;
import com.dao.SysMenuDao;
import com.dao.SysMenuRoleDao;
import com.pojo.SysMenu;
import com.pojo.SysMenuRole;
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
public class SysMenuManager extends BasicManager<SysMenu> {
    public final static Log log = LogFactory.getLog(SysMenuManager.class);

    @Resource
    private SysMenuDao sysMenuDao;
    @Resource
    private SysMenuRoleDao sysMenuRoleDao;


    public void deleteMenu(String id) {
        sysMenuDao.deleteById(id);
    }

    public void saveOrUpdate(SysMenu sysMenu) {
        //找到数据库中旧部门
        sysMenuDao.saveOrUpdate(sysMenu);
    }

    public SysMenu getById(String id) {
        return sysMenuDao.getById(id);
    }

    /*********************************************************************************/


    //拼成json
    public List<SysMenu> getMenuList(String roleId) {
        List<SysMenu> menuList = new ArrayList<SysMenu>();
        if (!ObjectUtils.isEmpty(roleId)) {
            menuList = this.getSysMenusByRoleId(roleId, null).getLeft();
        } else {
            //如果角色为空，则把所有通用路径展开
//            menuList = this.sysMenuDao.getAllResult();
            menuList = this.sysMenuDao.findByProperty("parentId", "1", "id", true);
        }

        return menuList;
    }

    //去掉无用属性
    private void deleteProperty(JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonData = (JSONObject) jsonArray.get(i);//得到对象中的第i条记录
            jsonData.remove("uuid");
            jsonData.remove("id");
            jsonData.remove("parentId");

            JSONArray jsonChildren = (JSONArray) jsonData.get("children");
            if (jsonChildren != null && jsonChildren.size() != 0 && !jsonChildren.isEmpty()) {
                this.deleteProperty(jsonChildren);
            } else {
                jsonData.remove("children");
            }
        }

    }

    public Result getMenuList(String id, String parentId, String roleIds, Page page) {
        //以后这里须要基于Page对数据进行截取，获取当前页面的数据
        List<SysMenu> currentPageMenuList = new ArrayList<SysMenu>();
        RecursionTree recursionTree = new RecursionTree<SysMenu>();
        String message = "association";

        if (StringUtils.isNotEmpty(roleIds) && hasEqualMenusByRoleIds(roleIds)) {
            currentPageMenuList.addAll(this.getSysMenusByRoleId(roleIds.split(",")[0], null).getLeft());
        }

        if (CollectionUtils.isEmpty(currentPageMenuList)) {
            message = "non-association";
            //先把所有对象找出来
            List<SysMenu> allMenuList = sysMenuDao.getAllResult();

            //找到最上层级的部门
            List<SysMenu> parentMenuList;
            if (!ObjectUtils.isEmpty(id) || !ObjectUtils.isEmpty(parentId)) {
                //筛选条件
                currentPageMenuList = sysMenuDao.getMenuList(id, parentId, page);
            } else {
                parentMenuList = recursionTree.getRootNodeList(allMenuList);
                this.getCurrentPageObjects(parentMenuList, page, currentPageMenuList);
            }

            //递归找到其所有子页面部门
            for (SysMenu parentMenu : currentPageMenuList) {
                recursionTree.buildChildTree(allMenuList, parentMenu);
            }
        }

        return Result.ok(message, (JSONArray) JSON.toJSON(currentPageMenuList), page);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //拼成json
    public String getMenusByRoleId(String roleId) {
        List<SysMenu> menuList = this.getMenuList(roleId);
        RecursionTree recursionTree = new RecursionTree<SysMenu>();
        menuList = recursionTree.builTree(menuList);
        //
        JSONArray jsonArray = (JSONArray) JSON.toJSON(menuList);
//        this.deleteProperty(jsonArray);
        /*转为json看看效果*/
        String jsonOutput = jsonArray.toString();//JSON.toJSON(menuList).toString();//JSON.toJSONString(menuList);
        //使得meta能够变成json
        jsonOutput = jsonOutput.replaceAll("\"\\{", "{").replaceAll("\\}\"", "}").replaceAll("\'", "\"");
//        jsonOutput = jsonOutput.replaceAll("\\\\\'","\"");
        return jsonOutput;
    }

    /***********************************关联关系********************************************/
    private Boolean hasEqualMenusByRoleIds(String roleIds) {
        Boolean hasEqualMenus = true;
        String[] roleIdArray = roleIds.split(",");
        List<SysMenuRole> objectList = this.sysMenuRoleDao.getSysMenuRoleList(null, roleIdArray[0], null);

        if (roleIdArray.length > 1) {
            for (int i = 1; i < roleIdArray.length; i++) {
                List<SysMenuRole> list = this.sysMenuRoleDao.getSysMenuRoleList(null, roleIdArray[i], null);
                Boolean isEqual = list.stream().sorted(Comparator.comparing(SysMenuRole::getMenuId))
                        .map(sysMenuRole -> sysMenuRole.getMenuId()).collect(Collectors.joining(","))
                        .equals(objectList.stream().sorted(Comparator.comparing(SysMenuRole::getMenuId))
                                .map(sysMenuRole -> sysMenuRole.getMenuId()).collect(Collectors.joining(",")));
                if (isEqual == false) {
                    hasEqualMenus = false;
                    break;
                }
            }
        }
        return hasEqualMenus;
    }

    //每个权限包含角色信息
    public Pair<List<SysMenu>, Set<SysMenu>> getSysMenusByRoleId(String roleId, Page page) {
        List<SysMenuRole> sysMenuRoleList = this.sysMenuRoleDao.getSysMenuRoleList(null, roleId, page);
        List<SysMenu> sysMenuList = new ArrayList<SysMenu>();
        Set<SysMenu> sysMenuSet = new HashSet<SysMenu>();

        if (!CollectionUtils.isEmpty(sysMenuRoleList)) {
            sysMenuRoleList.forEach(sysMenuRole -> {
                sysMenuList.add(sysMenuRole.getSysMenu());
                sysMenuSet.add(sysMenuRole.getSysMenu());
            });
        }
        return Pair.of(sysMenuList, sysMenuSet);
    }

    /***********************************查看、修改关联关系********************************************/


    public String saveAssociation(String menuIds, String ids, String associationObjectName) {
        String label = "";
        String[] menuIdArray = menuIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < menuIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysMenuRoleDao.saveSysMenuRoleAssociation(menuIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }

    public String deleteAssociation(String menuIds, String ids, String associationObjectName) {
        String label = "";
        String[] menuIdArray = menuIds.split(",");
        String[] idArray = ids.split(",");

        for (int i = 0; i < menuIdArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if ("角色".equals(associationObjectName)) {
                    label += this.sysMenuRoleDao.deleteSysMenuRoleAssociation(menuIdArray[i], idArray[j]) + "\n";
                }
            }
        }
        return label;
    }
}
