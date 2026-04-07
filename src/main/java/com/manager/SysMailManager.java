package com.manager;


import com.dao.SysMailDao;
import com.pojo.SysMail;
import com.util.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class SysMailManager {
    public final static Log log = LogFactory.getLog(SysMailManager.class);

    @Resource
    private SysMailDao sysMailDao;


    /*********************************************************************************/

    public SysMail getSysMailByUserId(String userID, Page page) {
        List<SysMail> userList = new ArrayList<SysMail>();

        userList = this.sysMailDao.getMailList(userID, null, null, null);
        if (!CollectionUtils.isEmpty(userList)) {
            return userList.get(0);
        }
        return null;
    }


}
