package com.common;

import com.util.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class BasicManager<T> {
    public final static Log log = LogFactory.getLog(BasicManager.class);


    @Resource
    protected BasicDao<T> basicDao;
    @Autowired
    Environment environment;

    public static <T> T deepClone(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            ois.close();
            return (T) object;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用迭代器可，正确无误的删除
     *
     * @param list
     * @param element
     * @return
     */
    public static List iteratorRemove(List list, Object element) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object cur = iterator.next();
            if (cur.equals(element)) {
                // 注意！！！这里时Iterator.remove()!!!而不是list.remove()!!!
                iterator.remove();
            }
        }
        return list;
    }


//	public BasicDao<T> getBasicDao() {
//		return basicDao;
//	}
//
//	@Autowired
//	public void setBasicDao(BasicDao<T> basicDao) {
//		this.basicDao = basicDao;
//	}
//
//
//	public void add(T t) {
//		// TODO Auto-generated method stub
//		basicDao.add(t);
//	}
//
//
//
//	public void update(T t)  {
//		// TODO Auto-generated method stub
//		basicDao.update(t);
//	}
//
//
//	public void delete(T t) {
//		// TODO Auto-generated method stub
//		basicDao.delete(t);
//	}
//
//
//	public void save(T t) {
//		// TODO Auto-generated method stub
//		BasicModel basicModel = (BasicModel) t;
//		if (basicModel.getObjectState() == BasicModel.OBJECTSTATE_TOBEADDED) {
//			this.add(t);
//		}else if (basicModel.getObjectState() == BasicModel.OBJECTSTATE_TOBEDELETED) {
//			this.delete(t);
//		}else if (basicModel.getObjectState() == BasicModel.OBJECTSTATE_TOBEUPDATED) {
//			this.update(t);
//		}
//	}
//
//	public T getById(Serializable id) {
//		// TODO Auto-generated method stub
//		return basicDao.getById(id);
//	}
//
//
//	public void add(Collection<T> collection) {
//		for (T t : collection) {
//			basicDao.add(t);
//		};
//	}
//
//
//	public void update(Collection<T> collection) {
//		for (T t : collection) {
//			basicDao.update(t);
//		};
//	}
//
//
//	public void delete(Collection<T> collection) {
//		for (T t : collection) {
//			basicDao.delete(t);
//		};
//	}
//
//
//	public void save(Collection<T> collection) {
//		// TODO Auto-generated method stub
//		for (T t : collection) {
//			basicDao.save(t);
//		};
//	}
//
//
//	public List<T> findByProperty(String propertyName, Object value) {
//		// TODO Auto-generated method stub
//		return basicDao.findByProperty(propertyName,value);
//	}
//
//	public List<T> findByProperty(String propertyName, Object value, String orderBy, boolean isAsc) {
//		// TODO Auto-generated method stub
//		return basicDao.findByProperty(propertyName,value,orderBy,isAsc);
//	}
//
//
//	public void saveOrUpdate(T t) {
//		// TODO Auto-generated method stub
//		BasicModel basicModel = (BasicModel) t;
//		//在数据库中查看是否有当前记录
//		T t_database = basicDao.getById(basicModel.getUuid());
//		if(t_database==null || "".equals(t_database)){
//			basicModel.setObjectState(BasicModel.OBJECTSTATE_TOBEADDED);
//			this.basicDao.add(t);
//		}else{
//			basicModel.setObjectState(BasicModel.OBJECTSTATE_TOBEUPDATED);
//			ObjectUtil.copyAttributes(t,t_database);
//			this.basicDao.update(t_database);
//		}
//	}

    public <T> T selectOne(List<T> tList) {
        if (CollectionUtils.isEmpty(tList)) {
            return null;
        }
        return tList.get(0);
    }

//	@Value("${local.server.port}")
//	private String port ;
//	@LocalServerPort
//	private String port ;

    public String getPort() {
        return environment.getProperty("local.server.port");
    }


    public Object getValue(T object, String propertyName) {

        if (ObjectUtils.isEmpty(object) || ObjectUtils.isEmpty(propertyName)) {
            return null;
        }

        // 下面是反射机制
        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().indexOf("get") == 0
                    && methods[i].getParameterTypes().length == 0) {
                String name = methods[i].getName().substring(3);
                if (ObjectUtils.isEmpty(propertyName) || !propertyName.equalsIgnoreCase(name))
                    continue;
                try {
                    log.debug("from执行方法：" + methods[i].getName());
                    return methods[i].invoke(object);

                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug("使用对象" + object.getClass().getName() + "的方法"
                            + methods[i].getName() + "错误" + "或者对象"
                            + object.getClass().getName() + "的方法set"
                            + name + "错误！程序继续运行...");
                }
            }
        }
        return null;
    }

    public Boolean setValue(T object, String propertyName, Object propertyValue) {
        if (ObjectUtils.isEmpty(object) || ObjectUtils.isEmpty(propertyName)) {
            return false;
        }

        // 下面是反射机制
        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().indexOf("get") == 0
                    && methods[i].getParameterTypes().length == 0) {
                String name = methods[i].getName().substring(3);
                if (ObjectUtils.isEmpty(propertyName) || !propertyName.equalsIgnoreCase(name))
                    continue;

                try {
                    log.debug("object执行方法：" + methods[i].getName());
                    //对 boolean函数的 is 方法支持不好
                    Method setMethod = object.getClass().getMethod(
                            "set" + name,
                            methods[i].getReturnType());
                    if (setMethod == null)
                        continue;

                    log.debug("set" + propertyName + "值为" + propertyValue);
                    setMethod.invoke(object, propertyValue);
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug("使用对象" + object.getClass().getName() + "的方法"
                            + methods[i].getName() + "错误" + "或者对象"
                            + object.getClass().getName() + "的方法set"
                            + name + "错误！程序继续运行...");
                }
            }
        }
        return false;
    }

    public void getCurrentPageObjects(Collection<T> allObject, Page page, Collection<T> currentPageObjects) {
        int num = 0;
        for (T t : allObject) {
            if (num < page.getStartNum())
                continue;
            if (num >= page.getStartNum() + page.getPageSize())
                break;
            currentPageObjects.add(t);
            num++;
        }
        //设置总数为最顶层对象的数量
        page.setTotalCount(allObject.size());
    }

    public String getIds(List<T> list) {
        String ids = "";
        for (T object : list) {
            ids += this.getValue(object, "id") + ",";
        }
        ;
        ids.substring(0, ids.length() - 1);
        return ids;
    }

}
