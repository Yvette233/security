package com.controller;

import com.dao.SysUserDao;
import com.pojo.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BasicDaoTest {

    @Autowired
    SysUserDao sysUserDao;

    @Test
    public void getAllResult() {
        sysUserDao.getAllResult();
    }

    //没成功
    @Test
    public void findByProperty() {
        sysUserDao.findByProperty("state",1);
    }

    @Test
    public void get() {
        sysUserDao.getById("12");
    }

    @Test
    public void add() {
        SysUser sysUser = new SysUser();
        sysUser.setUuid("1111");
        sysUser.setState(222);
        sysUser.setUserName("test");
        sysUser.setUserNameCH("测试");
        sysUserDao.add(sysUser);
    }

    @Test
    public void update() {
        SysUser sysUser = new SysUser();
        sysUser.setUuid("444");
        sysUser.setState(222);
        sysUser.setUserNameCH("测试4444");
        sysUser.setUserName("test444");
        sysUserDao.saveOrUpdate(sysUser);
    }

    @Test
    public void delete() {
        List<SysUser> sysUsers = new ArrayList<SysUser>();
        sysUsers.add(sysUserDao.getById("444"));
        sysUserDao.delete(sysUsers);
//        sysUserDao.deleteById("1111");
    }

}
