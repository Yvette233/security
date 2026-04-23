package com.manager;

import com.dao.SysLoginInfoDao;
import com.dao.SysRoleUserDao;
import com.dao.SysUserDao;
import com.pojo.*;
import com.util.JwtUtils;
import com.util.RequestUtils;
import com.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class LoginManager {
    public final static Log log = LogFactory.getLog(LoginManager.class);

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysRoleUserDao sysRoleUserDao;
    @Resource
    private SysLoginInfoDao sysLoginInfoDao;
    @Resource
    private SysPositionManager sysPositionManager;
    @Resource
    private SysDeptManager sysDeptManager;
    @Resource
    private SysRoleManager sysRoleManager;
    @Resource
    private SysGroupManager sysGroupManager;
    @Resource
    private PublicManager publicManager;
    @Resource
    private SysPermissionManager sysPermissionManager;

    /***********************************登陆时得到token********************************************/
    public SysUser updateSysUserAtLogin(String user, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //通过CustomUserDetailService返回的SysUser
        SysUser userDetails = (SysUser) authentication.getPrincipal();

        //activiti中调用taskRuntime.task(taskId)，需要先通过通过Principal::getName获取登录用户是否属于分派任务得候选人或候选组？
        //fpc todo 可以改为taskService...
//        userDetails.setName(userDetails.getId());

        String token = JwtUtils.createToken(user);
        userDetails.setToken(token);

        SysUser sysUser = this.publicManager.findSysUser(user);
        userDetails.setLoginRoleId(this.getLoginRoleId(sysUser.getId()));

        this.updateToken(user, userDetails.getLoginRoleId(), token);

        this.saveLoginInfo(RequestUtils.getRequest(), sysUser, user, userDetails.getLoginRoleId(), SysLoginInfo.TYPE_LOGIN, SysLoginInfo.STATE_SUCCESS);

        for (SysRole sysRole : userDetails.getSysRoleSet()) {
            if ("ACTIVITI_USER".equals(sysRole.getId())) {
                userDetails.getSysRoleSet().remove(sysRole);
                break;
            }
        }

        return userDetails;
    }

    private String getLoginRoleId(String userId) {
        List<SysRoleUser> sysRoleUserList = this.sysRoleUserDao.getSysRoleUserList(userId, null, null);
        if (!CollectionUtils.isEmpty(sysRoleUserList)) {
            return sysRoleUserList.get(0).getRoleId();
        }
        return null;
    }


    public void updateToken(String user, String loginRoleId, String token) {
        SysUser sysUser = this.publicManager.findSysUser(user);
        sysUser.setToken(token);
        sysUser.setUpdateTime(new Date());
        sysUser.setLoginRoleId(loginRoleId);
        sysUserDao.update(sysUser);
        sysUserDao.flush();
    }

    /**
     * @param request  请求路径
     * @param sysUser  登录用户
     * @param userName 登录时使用的用户
     * @param roleId   登录角色
     * @param state    登陆状态
     * @param type     类型
     */
    public void saveLoginInfo(HttpServletRequest request, SysUser sysUser, String userName, String roleId, Integer state, Integer type) {
        SysLoginInfo sysLoginInfo = new SysLoginInfo();
        sysLoginInfo.setLocalAddr(request.getLocalAddr());
        sysLoginInfo.setRequestURL(request.getRequestURL().toString());
        sysLoginInfo.setRemoteHost(request.getRemoteHost());
        sysLoginInfo.setQueryString(request.getQueryString());
        sysLoginInfo.setRemotePort(String.valueOf(request.getRemotePort()));
        sysLoginInfo.setRemoteAddr(request.getRemoteAddr());
        sysLoginInfo.setLocalName(request.getLocalName());
        sysLoginInfo.setPathInfo(request.getPathInfo());
        sysLoginInfo.setRequestURI(request.getRequestURI());

        if (!ObjectUtils.isEmpty(sysUser)) {
            sysLoginInfo.setUserId(sysUser.getId());
        }
        if (!StringUtils.isEmpty(userName)) {
            sysLoginInfo.setUserName(userName);
        }
        if (!StringUtils.isEmpty(roleId)) {
            sysLoginInfo.setRoleId(roleId);
        }
        sysLoginInfo.setState(state);
        sysLoginInfo.setType(type);
        sysLoginInfo.setCreateTime(new Date());
        sysLoginInfoDao.add(sysLoginInfo);
    }


    /***********************************退出时，删除用户Token********************************************/
    public Result deleteToken(String user) {
        SysUser sysUser = this.publicManager.findSysUser(user);
        try {
            sysUser.setToken(null);
            sysUser.setLoginRoleId(null);
            sysUser.setUpdateTime(null);
            sysUserDao.update(sysUser);
            return Result.ok("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败，原因：" + e.getMessage());
        }

    }

    /***********************************切换用户角色********************************************/

    public void updateLoginRoleId(String user, String loginRoleId) {
        SysUser sysUser = this.publicManager.findSysUser(user);
        sysUser.setLoginRoleId(loginRoleId);
        this.sysUserDao.update(sysUser);
    }

    /***********************************权限认证时********************************************/

    public SysUser findByToken(String token) {
        List<SysUser> sysUserList = sysUserDao.findByProperty("token", token);
        if (ObjectUtils.isNotEmpty(sysUserList)) {
            SysUser sysUser = sysUserList.get(0);

            this.setSimpleGrantedAuthority(sysUser);
            return sysUser;
        }
        return null;
    }

    //登录界面和查看路由都用到了
    public void setSimpleGrantedAuthority(SysUser sysUser) {
        //记录用户、角色和权限的关系
        Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) sysUser.getAuthorities();

        //同时拉取该用户所拥有的角色,同时记录每种角色所拥有的用户
        Set<SysRole> sysRoleSet = this.sysRoleManager.getSysRolesByUserId(sysUser.getId(), null).getRight();
        if (!CollectionUtils.isEmpty(sysRoleSet)) {
            sysRoleSet.forEach(sysRole -> {
                Set<SysPermission> sysPermissionSet = this.sysPermissionManager.getPermissionsByRoleId(sysRole.getId(), null).getRight();
                sysRole.setSysPermissionSet(sysPermissionSet);
            });
        }


        //为了保证业务流程方法能够执行，该角色的设置至关重要
        //使用activiti7的ProcessRuntime或TaskRuntime必须要有 ACTIVITI_USER 角色
        SysRole newRole = new SysRole();
        newRole.setId("ACTIVITI_USER");
        newRole.setName("使用ACTIVITI务必要加入的角色");
        sysRoleSet.add(newRole);

        sysRoleSet.stream().forEach(sysRole -> authorities.add(new SimpleGrantedAuthority("ROLE_" + sysRole.getId())));
        sysUser.setSysRoleSet(sysRoleSet);

        //记录用户与组的关系
        Set<SysGroup> sysGroupSet = this.sysGroupManager.getSysGroupsByUserId(sysUser.getId(), null).getRight();
        sysGroupSet.stream().forEach(group -> authorities.add(new SimpleGrantedAuthority("GROUP_" + group.getId())));
        sysUser.setSysGroupSet(sysGroupSet);

        //记录用户与职位的关系
        sysUser.setSysPositionSet(sysPositionManager.getSysPositionsByUserId(sysUser.getId(), null).getRight());

        //记录用户与部门的关系
        sysUser.setSysDeptSet(sysDeptManager.getSysDeptsByUserId(sysUser.getId(), null).getRight());
    }


}
