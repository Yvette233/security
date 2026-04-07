package com.common;

import com.util.IdentityGenerator;
import com.util.ObjectUtil;
import com.util.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings(value = {"unchecked"})
@Repository
@Transactional(rollbackFor = Exception.class)
public class BasicDao<T> extends HibernateDaoSupport {

    public final static Log log = LogFactory.getLog(BasicDao.class);

    //务必要有
    @Resource
    public void setSessionFacotry(SessionFactory sessionFacotry) {
        super.setSessionFactory(sessionFacotry);
    }

    /**********************************************************************/

    public enum QueryType {
        Equal, Like, Ge, Gt, Lt, Le, Asc, Desc
    }

    protected List<T> getResult(CriteriaQuery<T> criteriaQuery, Page page) {
        List<T> result = new ArrayList<T>();
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            TypedQuery<T> query = session.createQuery(criteriaQuery);
            if (page != null) {
                query
                        .setMaxResults(page.getPageSize())
                        .setFirstResult(page.getStartNum());
            }

            result = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param propertyList Triple<String,Object,String> 中第一个参数为属性名称，第二个为属性值，第三个为查询类型
     * @return
     */
    public CriteriaQuery<T> getCriteriaQuery(List<Triple<String, Object, QueryType>> propertyList, Page page) {
        CriteriaQuery<T> criteriaQuery = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            criteriaQuery = builder.createQuery((Class<T>) this.getClazz());
            Root<T> root = criteriaQuery.from((Class<T>) this.getClazz());
            criteriaQuery.select(root);

            ////////////////////////////// //////////////////////////////
            List<Predicate> predicatesList = new ArrayList<>();
            List<Order> orderList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(propertyList)) {
                Iterator it = propertyList.iterator();
                while (it.hasNext()) {
                    Triple<String, Object, String> property = (Triple<String, Object, String>) it.next();
                    if (QueryType.Equal.equals(property.getRight())) {
                        predicatesList.add(builder.equal(root.get(property.getLeft()), property.getMiddle()));
                    } else if (QueryType.Like.equals(property.getRight())) {
                        predicatesList.add(builder.like(root.get(property.getLeft()), "%" + (String) property.getMiddle() + "%"));
                    } else if (QueryType.Ge.equals(property.getRight())) {
                        predicatesList.add(builder.ge(root.get(property.getLeft()), (double) property.getMiddle()));
                    } else if (QueryType.Gt.equals(property.getRight())) {
                        predicatesList.add(builder.gt(root.get(property.getLeft()), (double) property.getMiddle()));
                    } else if (QueryType.Lt.equals(property.getRight())) {
                        predicatesList.add(builder.lt(root.get(property.getLeft()), (double) property.getMiddle()));
                    } else if (QueryType.Le.equals(property.getRight())) {
                        predicatesList.add(builder.le(root.get(property.getLeft()), (double) property.getMiddle()));
                    } else if (QueryType.Asc.equals(property.getRight())) {
                        if (!ObjectUtils.isEmpty(property.getMiddle()) && "String2Number".equals(property.getMiddle())) {
                            orderList.add(builder.asc(root.get(property.getLeft()).as(Integer.class)));
                        } else {
                            orderList.add(builder.asc(root.get(property.getLeft())));
                        }
                    } else if (QueryType.Desc.equals(property.getRight())) {
                        if (!ObjectUtils.isEmpty(property.getMiddle()) && "String2Number".equals(property.getMiddle())) {
                            orderList.add(builder.desc(root.get(property.getLeft()).as(Integer.class)));
                        } else {
                            orderList.add(builder.desc(root.get(property.getLeft())));
                        }
                    }

                }
            }
            Predicate[] finalPredicates = new Predicate[predicatesList.size()];
            predicatesList.toArray(finalPredicates);

            Order[] finalOrders = new Order[orderList.size()];
            orderList.toArray(finalOrders);

            ////////////////////////////查询sql/////////////////////////////////
            criteriaQuery.where(finalPredicates).orderBy(finalOrders);
            /////////////////////////计算总数sql/////////////////////////////////////
            if (page != null && Integer.valueOf(0).equals(page.getTotalCount())) {
                CriteriaQuery<Long> queryCount = builder.createQuery(Long.class);
                Root<T> entityRoot = queryCount.from((Class<T>) this.getClazz());
                queryCount.select(builder.count(entityRoot));
                queryCount.where(finalPredicates);
                Long totalCount = session.createQuery(queryCount).getSingleResult();
                page.setTotalCount(totalCount.intValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return criteriaQuery;
    }

    /*************************************查询*********************************************/

    public List<T> getAllResult(Class<T> clazz) {
        List<T> result = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery((Class<T>) clazz);
            Root<T> root = criteriaQuery.from((Class<T>) clazz);
//            criteriaQuery.select(root);

            TypedQuery<T> query = session.createQuery(criteriaQuery);
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<T> getAllResult() {
        return this.getAllResult((Class<T>) this.getClazz());
    }

    @Transactional(readOnly = false)
    public T get(Class<T> clazz, Serializable id) {
        // TODO Auto-generated method stub
        T t = null;
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            t = session.get(clazz, id);
            //根据ID查，(返回的是代理，不会立即访问数据库)。
            //t = session.load(clazz, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @Transactional(readOnly = false)
    public T getById(Serializable id) {
        return this.get((Class<T>) this.getClazz(), id);
    }

    @Transactional(readOnly = true)
    public List<T> findByProperty(String propertyName, Object value) {
        return this.findByProperty(propertyName, value, null, null);
    }

    @Transactional(readOnly = true)
    public List<T> findByProperty(String propertyName, Object value, String orderBy, Boolean isAsc) {
        List<Triple<String, Object, QueryType>> propertyList = new ArrayList<Triple<String, Object, QueryType>>();
        if (!ObjectUtils.isEmpty(propertyName)) {
            propertyList.add(Triple.of(propertyName, value, QueryType.Equal));
        }
        if (!ObjectUtils.isEmpty(orderBy)) {
            if (isAsc) {
                propertyList.add(Triple.of(orderBy, null, QueryType.Asc));
            } else {
                propertyList.add(Triple.of(orderBy, null, QueryType.Desc));
            }
        }

        CriteriaQuery<T> criteriaQuery = this.getCriteriaQuery(propertyList, null);
        return this.getResult(criteriaQuery, null);
    }

    /*************************************增加*********************************************/
    @Transactional(readOnly = false)
    public void add(T t) {
        // TODO Auto-generated method stub
        BasicModel basicModel = (BasicModel) t;
        if (basicModel.getUuid() == null) {
            basicModel.setUuid(IdentityGenerator.GetIdentityUUID());
        }
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            session.save(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    public void add(Collection<T> collection) {
        // TODO Auto-generated method stub
        for (T t : collection) {
            this.add(t);
        }
    }

    /*************************************删除*********************************************/
    @Transactional(readOnly = false)
    public void delete(T t) {
        // TODO Auto-generated method stub
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            // Session的delete()方法可以删除持久化对象和游离对象，而EntityManager的remove()方法只能删除持久化对象
//            session.remove(t);
            //实例可能被修改，需要先更新再删除
            session.update(t);
            session.delete(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    public void deleteById(Serializable id) {
        // TODO Auto-generated method stub
        if (id == null || "".equals(id)) {
            log.info("删除id为空");
        }
        try {
            T t = this.get((Class<T>) this.getClazz(), id);
            if (!ObjectUtils.isEmpty(t)) {
                this.delete(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    public void delete(Collection<T> collection) {
        // TODO Auto-generated method stub
        for (T t : collection) {
            this.delete(t);
        }
    }

    /*************************************修改*********************************************/

    // Boolean isImmediately
    @Transactional(readOnly = false)
    public void update(T t) {
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            session.update(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    public void saveOrUpdate(T t) {
        // TODO Auto-generated method stub
        if (t == null) {
            log.info("新增对象为空");
            return;
        }
        BasicModel basicModel = (BasicModel) t;
        if (basicModel.getUuid() == null || "".equals(basicModel.getUuid())) {
            this.add(t);
        }

        T oldObject = this.getById(basicModel.getUuid());
        if (!ObjectUtils.isEmpty(oldObject)) {
            ObjectUtil.copyAttributes(t, oldObject);
            this.update(oldObject);
        } else {
            this.add(t);
        }

    }


    @Transactional(readOnly = false)
    public void update(Collection<T> collection) {
        // TODO Auto-generated method stub
        for (T t : collection) {
            this.update(t);
        }
    }

    /*************************************其余*********************************************/

    //为了能够立即持久化数据，在增删改后调用该刷新方法
    @Transactional(readOnly = false)
    public void flush() {
        try {
            Session session = this.getSessionFactory().getCurrentSession();
//            session.getTransaction().begin();
            session.flush();
//            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getClazz() {
        Class<?> clazz = this.getClass();
        // 2得到子类对象的泛型父类类型
        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T>) types[0];
        return clazz;
    }

}
