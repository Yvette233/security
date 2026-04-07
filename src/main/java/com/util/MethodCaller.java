package com.util;

import com.common.BasicModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 调用setter和getter方法的常用类
 */
public class MethodCaller {

    private final static Log log = LogFactory.getLog(MethodCaller.class);

    private static final char SPLITTER = '.';

    private synchronized static String initialUpper(String string) {
        String temp = string.substring(0, 1).toUpperCase()
                + string.substring(1);
        return temp;
    }

    /**
     * 调用obj对象的Get方法，获取返回的对象。
     *
     * @param obj：目标对象
     * @param property：所要调用的属性，例如要调用getName方法，这里的property写成name即可!
     * @return
     * @throws RuntimeException
     */
    public synchronized static Object callGetMethod(Object obj, String property)
            throws RuntimeException {
        Method method = null;
        Object result = null;
        try {
            method = obj.getClass().getMethod("get" + initialUpper(property),
                    null);
            result = method.invoke(obj, null);
        } catch (SecurityException e) {
            throw new RuntimeException("SecurityException:"
                    + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("NoSuchMethodException:"
                    + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("IllegalArgumentException:"
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException:"
                    + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("InvocationTargetException:"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 获取obj对象的Get方法所声明的返回对象的类型。
     *
     * @param obj：目标对象
     * @param property：所要调用的属性，例如想要知道getName方法所声明的返回类型，这里的property写成name即可!
     * @return
     * @throws RuntimeException
     */
    public synchronized static Class getGetMethodReturnType(Object obj,
                                                            String property) throws RuntimeException {
        Method method = null;
        Class result = null;
        try {
            method = obj.getClass().getMethod("get" + initialUpper(property),
                    null);
            result = method.getReturnType();
        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }


    /**
     * 根据属性名称获得它的get方法。
     *
     * @param obj：目标对象
     * @param property：所要调用的属性，例如想要知道getName方法所声明的返回类型，这里的property写成name即可!
     * @return
     * @throws RuntimeException
     */
    public synchronized static Method getGetMethod(Object obj, String property)
            throws RuntimeException {
        Method method = null;
        try {
            String str = initialUpper(property);
            Class c = (obj instanceof Class) ? (Class) obj : obj.getClass();
            method = c.getMethod("get" + str,
                    null);


        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        return method;
    }

    /**
     * 根据属性名称获得它的set方法。
     *
     * @param obj：目标对象
     * @param property：所要调用的属性，例如想要知道getName方法所声明的返回类型，这里的property写成name即可!
     * @return
     * @throws RuntimeException
     */
    public synchronized static Method getSetMethod(Object obj, String property)
            throws RuntimeException {
        Method method = null;
        try {
            String str = initialUpper(property);
            Class c = (obj instanceof Class) ? (Class) obj : obj.getClass();
            method = c.getMethod("get" + str,
                    null);
            Class result = method.getReturnType();
            method = c.getMethod("set" + str,
                    result);

        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        return method;
    }

    /**
     * 调用目标对象target的Set方法，将object对象设为target的相应属性property
     *
     * @param target：目标对象
     * @param property：所要设置的属性，例如想要设置setName方法，这里的property写成name即可!
     * @param object：所要设置的对象
     * @throws RuntimeException
     */
    public synchronized static void callSetMethod(Object target,
                                                  String property, Object object) throws RuntimeException {
        Method method = null;
        String result = null;
        Class myCls = null;
        myCls = getGetMethodReturnType(target, property);
        if (myCls == null) {
            myCls = object.getClass();
        }
        try {
            method = target.getClass().getMethod(
                    "set" + initialUpper(property), myCls);
            method.invoke(target, object);
        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 调用obj对象的Get方法所返回对象的Get方法，获取返回的对象。
     *
     * @param obj：目标对象。
     * @param propertyOfProperty：例如想要调用Task对象的getDept方法所返回的Dept对象的getId方法，propertyOfProperty写成dept.id即可.
     * @return
     * @throws RuntimeException
     */
    public synchronized static Object callGetPropertyMethod(Object obj,
                                                            String propertyOfProperty) throws RuntimeException {
        String[] properties = StringUtils.split(propertyOfProperty, SPLITTER);
        Method method = null;
        Object result = null;
        Object objProperty = null;
        try {
            if (properties.length == 2) {
                method = obj.getClass().getMethod(
                        "get" + initialUpper(properties[0]), null);
                objProperty = method.invoke(obj, null);
                if (objProperty != null) {
                    method = objProperty.getClass().getMethod(
                            "get" + initialUpper(properties[1]), null);
                    result = method.invoke(objProperty, null);
                }
            } else {
                result = "ERROR:属性定义错误!" + " OBJECT:" + obj.getClass()
                        + " PROPERTY:" + propertyOfProperty;
                throw new RuntimeException((String) result);
            }

        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }


    /**
     * 将source的属性property拷贝到target
     *
     * @param source
     * @param property
     * @param target
     * @throws RuntimeException
     */
    public synchronized static void copyProperty(Object source,
                                                 String property, Object target) throws RuntimeException {
        if (property.indexOf(".") >= 0) {
            throw new RuntimeException("该方法不支持多层对象属性复制！");
        }
        Object temp = callGetMethod(source, property);
        callSetMethod(target, property, temp);
    }

    /**
     * 把对象的关联对象从一个Copy到另外一个
     *
     * @param from
     * @param to
     * @return
     */
    public static Boolean copyObjects(Object from, Object to)
            throws RuntimeException {
        if (from == null) {
            return true;
        }
        if (to != null && from.getClass() != to.getClass()) {
            // throw new RuntimeException("给出的两个类的类型不相同，不能进行赋值");
        }
        try {
            // 下面是反射机制
            Method[] methods = from.getClass().getMethods();
            String propertyName = null;
            Object returnValue = null;
            Method setMethod = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().indexOf("get") == 0
                        && !"getNotNullExtendAttribute".equals(methods[i]
                        .getName())) {
                    propertyName = methods[i].getName().substring(3);
                    try {
                        log.debug("from执行方法：" + methods[i].getName());
                        returnValue = methods[i].invoke(from);
                        log.debug("to执行方法：" + "set" + propertyName);
                        if (!"Class".equals(propertyName)
                                && (returnValue instanceof BasicModel)) {
                            setMethod = to.getClass().getMethod(
                                    "set" + propertyName,
                                    methods[i].getReturnType());
                            setMethod.invoke(to, returnValue);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        log.error("使用对象" + from.getClass().getName() + "的方法"
                                + methods[i].getName() + "错误" + "或者对象"
                                + to.getClass().getName() + "的方法set"
                                + propertyName + "错误！程序继续运行...");
                        continue;
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        log.error("使用对象" + from.getClass().getName() + "的方法"
                                + methods[i].getName() + "错误" + "或者对象"
                                + to.getClass().getName() + "的方法set"
                                + propertyName + "错误！程序继续运行...");
                        continue;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        log.error("找不到对象" + to.getClass().getName() + "的方法：set"
                                + propertyName + "！程序继续运行...");
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("复制属性错误");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

}
