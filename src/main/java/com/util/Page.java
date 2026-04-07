package com.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.util.ObjectUtils;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C),Tsinghua university create time：2021/9/2-14:37 creator：fangpengcheng
 */
@MappedSuperclass
public class Page {

    private static final Log log = LogFactory.getLog(Page.class);

    /*当前页码*/
    @Transient
    private Integer currentPage;
    /*每页显示条数*/
    @Transient
    private Integer pageSize;
    /*当前条件下的总记录数*/
    @Transient
    private Integer totalCount = 0;
    /*总页数*/
    @Transient
    private Integer totalPage = 1;
    /*开始行号*/
    @Transient
    private Integer startNum = 0;

    /*查询的结果集*/
    @Transient
    private List<?> list;

    /*******************************************************************************/
//    public Page() {
//        new Page(currentPage, pageSize);
//    }
    public Page(Integer currentPage, Integer pageSize) {
        this.currentPage = (ObjectUtils.isEmpty(currentPage) || currentPage <= 0) ? 1 : currentPage;
        this.pageSize = (ObjectUtils.isEmpty(pageSize) || pageSize <= 0) ? 10 : pageSize;
        /*计算开始行号*/
        this.startNum = (this.currentPage - 1) * this.pageSize;

    }

//    public void pagePro(Criteria crit) {
//        //计算总数
//        crit.setProjection(Projections.rowCount());
//        this.totalCount = ((Long) crit.uniqueResult()).intValue();
//        //设置总页数
//        this.totalPage = (this.totalCount - 1) / this.pageSize + 1;
//
//
//        //单表查询的话只需要加这行
//        crit.setProjection(null);
//
//        //多表的话需要加这行,不加c.list将返回List<Object>
//        crit.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
//
//        //指定从那个对象开始查询，参数的索引位置是从0开始的，
//        crit.setFirstResult(this.startNum);
//        //分页时，一次最多产寻的对象数
//        crit.setMaxResults(this.pageSize);
//    }


    public void pagePro(Session session, Query query, String hql, Map<?, Object> parameters) {

        //计算记录总数
        this.getCount(session, hql, parameters);

        //设置总页数
        this.totalPage = (this.totalCount - 1) / this.pageSize + 1;

        //指定从那个对象开始查询，参数的索引位置是从0开始的，
        query.setFirstResult(this.startNum);
        //分页时，一次最多产寻的对象数
        query.setMaxResults(this.pageSize);
    }


    /**
     * 获取条数信息
     */
    public void getCount(Session session, String hql, Map<?, Object> parameters) {

        String str = new String();
        str = "select count(*) ";
        //判断hql是不是select or SELECT开始的
        if (hql.toLowerCase().trim().indexOf("select") == 0) {
            //如果是select 语句开始的，则在str中添加select count(*) from( hql )
            //获取from开始的字串
            str += hql.substring(hql.toLowerCase().indexOf("from"));
            System.out.println("是select 开始的hql " + str);
        } else {
            //如果是一个PO from CPO,则只是添加select count(*) 即可
            str += hql;
            System.out.println("不是select 开始的hql " + str);
        }
        System.out.println("=====================" + str);

        Query queryCount = session.createQuery(str);
        if (parameters != null && !parameters.isEmpty()) {
            //获取paramenters中的key的class，用于判断key的类型
            Class<? extends Object> keyClass = null;
            Set<?> keySet = parameters.keySet();
            for (Object k : keySet) {
                keyClass = k.getClass();
                break;//只需要判断第一个元素
            }


            for (Map.Entry<?, Object> entry : parameters.entrySet()) {
                String mapKey = (entry.getKey()).toString();
                Object mapValue = entry.getValue();
                if (keyClass.equals(Integer.class)) { //Integer类型
                    queryCount.setParameter(Integer.valueOf(mapKey), mapValue);
                } else if (keyClass.equals(String.class)) { //Integer类型
                    queryCount.setParameter(mapKey, mapValue);
                }
            }
        }

        //设置总记录数
        this.totalCount = Integer.parseInt(queryCount.uniqueResult().toString());
    }

    /*******************************************************************************/

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        //设置总页数
        this.totalPage = (this.totalCount - 1) / this.pageSize + 1;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getStartNum() {
        return startNum;
    }

    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
