package com.dao;

import com.common.BasicDao;
import com.pojo.SysDeptUser;
import com.util.IdentityGenerator;
import com.util.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SysDeptUserDao extends BasicDao<SysDeptUser> {
    private static final Log log = LogFactory.getLog(SysDeptUserDao.class);

    public List<SysDeptUser> getSysDeptUserList(String deptId, String userId, Page page) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (ObjectUtils.isNotEmpty(deptId)) {
            propertyList.add(Triple.of("deptId", deptId, QueryType.Equal));
        }
        if (ObjectUtils.isNotEmpty(userId)) {
            propertyList.add(Triple.of("userId", userId, QueryType.Equal));
        }
        propertyList.add(Triple.of("deptId", null, QueryType.Asc));
        propertyList.add(Triple.of("id", null, QueryType.Asc));

        CriteriaQuery<SysDeptUser> criteriaQuery = this.getCriteriaQuery(propertyList, page);

        return this.getResult(criteriaQuery, page);
    }

    public String saveSysDeptUserAssociation(String deptId, String userId) {
        List<SysDeptUser> sysDeptUserList = this.getSysDeptUserList(deptId, userId, null);
        if (CollectionUtils.isEmpty(sysDeptUserList)) {
            SysDeptUser sysDeptUser = new SysDeptUser();
            sysDeptUser.setDeptId(deptId);
            sysDeptUser.setUserId(userId);
            sysDeptUser.setId(String.valueOf(IdentityGenerator.GetIdentityLong()));
            this.add(sysDeptUser);
            return "用户" + userId + "与部门" + deptId + "关联保存成功";
        }
        return "用户" + userId + "已与部门" + deptId + "存在该关联关系";
    }

    public String deleteSysDeptUserAssociation(String deptId, String userId) {
        List<SysDeptUser> sysDeptUserList = this.getSysDeptUserList(deptId, userId, null);
        if (!CollectionUtils.isEmpty(sysDeptUserList)) {
            this.delete(sysDeptUserList.get(0));
            return "用户" + userId + "与部门" + deptId + "关联删除成功";
        }
        return "用户" + userId + "与部门" + deptId + "不存在该关联关系";
    }
}
