package com.manager;

import com.dao.SysDeptUserDao;
import com.dao.SysGroupUserDao;
import com.dao.SysPositionUserDao;
import com.dao.SysUserDao;
import com.pojo.SysDeptUser;
import com.pojo.SysGroupUser;
import com.pojo.SysPositionUser;
import com.pojo.SysUser;
import com.util.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PublicManager {
    public final static Log log = LogFactory.getLog(PublicManager.class);

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysGroupUserDao sysGroupUserDao;
    @Resource
    private SysDeptUserDao sysDeptUserDao;
    @Resource
    private SysPositionUserDao sysPositionUserDao;

    /*******************************通用****************************************/

    public SysUser findSysUser(String user) {
        if (StringUtils.isEmpty(user)) {
            return null;
        }

        List<SysUser> resultList = new ArrayList<SysUser>();

        resultList = sysUserDao.findByProperty("id", user);
        if (ObjectUtils.isEmpty(resultList)) {
            resultList = sysUserDao.findByProperty("userName", user);
        }
        if (ObjectUtils.isEmpty(resultList)) {
            resultList = sysUserDao.findByProperty("userNameCH", user);
        }

        if (ObjectUtils.isEmpty(resultList)) {
            resultList = sysUserDao.findByProperty("cellphone", user);
        }
        if (ObjectUtils.isEmpty(resultList)) {
            resultList = sysUserDao.findByProperty("email", user);
        }

        if (ObjectUtils.isEmpty(resultList)) {
            return null;
        }

        return resultList.get(0);
    }


    public String getSysUserNameCH(String user) {
        SysUser sysUser = this.findSysUser(user);
        if (ObjectUtils.isEmpty(sysUser)) {
            return null;
        }
        return sysUser.getUserNameCH();
    }


    /***********************************************************************/


    public Set<String> getProposersByDept(String previousUserId) {
        Set<String> proposerSet = new HashSet<String>();
        List<SysDeptUser> sysDeptUserList = sysDeptUserDao.getSysDeptUserList(null, previousUserId, null);
        for (SysDeptUser sysDeptUser : sysDeptUserList) {
            List<SysDeptUser> deptUserList = sysDeptUserDao.getSysDeptUserList(sysDeptUser.getDeptId(), null, null);
            for (SysDeptUser deptUser : deptUserList) {
                if (!previousUserId.equals(deptUser.getUserId())) {
                    proposerSet.add(deptUser.getUserId());
                }
            }
        }
        return proposerSet;
    }


    public Set<String> getProposersByParentDept(String previousUserId) {
        Set<String> proposerSet = new HashSet<String>();
        List<SysDeptUser> sysDeptUserList = sysDeptUserDao.getSysDeptUserList(null, previousUserId, null);
        for (SysDeptUser sysDeptUser : sysDeptUserList) {
            List<SysDeptUser> parentDeptUserList = sysDeptUserDao.getSysDeptUserList(sysDeptUser.getSysDept().getParentId(), null, null);
            for (SysDeptUser parentDeptUser : parentDeptUserList) {
                if (!previousUserId.equals(parentDeptUser.getUserId())) {
                    proposerSet.add(parentDeptUser.getUserId());
                }
            }
        }
        return proposerSet;
    }

    public Set<String> getProposersByGroup(String previousUserId) {
        Set<String> proposerSet = new HashSet<String>();


        List<SysGroupUser> sysGroupUserList = sysGroupUserDao.getSysGroupUserList(null, previousUserId, null);
        for (SysGroupUser sysGroupUser : sysGroupUserList) {
            List<SysGroupUser> groupUserList = sysGroupUserDao.getSysGroupUserList(sysGroupUser.getGroupId(), null, null);
            for (SysGroupUser groupUser : groupUserList) {
                if (!previousUserId.equals(groupUser.getUserId())) {
                    proposerSet.add(groupUser.getUserId());
                }
            }
        }
        return proposerSet;
    }


    public Set<String> getProposersByPosition(String previousUserId) {
        Set<String> proposerSet = new HashSet<String>();
        List<SysPositionUser> sysPositionUserList = sysPositionUserDao.getSysPositionUserList(null, previousUserId, null);
        for (SysPositionUser sysPositionUser : sysPositionUserList) {
            List<SysPositionUser> positionUserList = sysPositionUserDao.getSysPositionUserList(sysPositionUser.getPositionId(), null, null);
            for (SysPositionUser positionUser : positionUserList) {
                if (!previousUserId.equals(positionUser.getUserId())) {
                    proposerSet.add(positionUser.getUserId());
                }
            }
        }
        return proposerSet;
    }


    public Set<String> getProposersByParentPosition(String previousUserId) {
        Set<String> proposerSet = new HashSet<String>();
        List<SysPositionUser> sysPositionUserList = sysPositionUserDao.getSysPositionUserList(null, previousUserId, null);
        for (SysPositionUser sysPositionUser : sysPositionUserList) {
            List<SysPositionUser> parentPositionUserList = sysPositionUserDao.getSysPositionUserList(sysPositionUser.getSysPosition().getParentId(), null, null);
            for (SysPositionUser parentPositionUser : parentPositionUserList) {
                if (!previousUserId.equals(parentPositionUser.getUserId())) {
                    proposerSet.add(parentPositionUser.getUserId());
                }
            }
        }
        return proposerSet;
    }

    /**
     * @return
     *********************************************************************/

    public Pair<List<SysUser>, Set<SysUser>> getUserList(String groupId, Page page) {
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

}
