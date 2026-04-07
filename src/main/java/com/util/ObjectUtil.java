package com.util;

import com.common.BasicModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 对对象进行的一些操作
 */
@Service
public class ObjectUtil {
    private final static Log log = LogFactory.getLog(ObjectUtil.class);

    /**
     * lqm todo 可以采用clone方法进行复制
     * <p>
     * 把对象的属性中不为null值从一个Copy到另外一个，如果返回对象是复杂类型就不要进行copy了呢，因为复杂类型还是不能自动sync的
     *
     * @param from
     * @param to
     * @return
     */
    public static Boolean copyAttributes(Object from, Object to) {
        return copyAttributes(from, to, false);
    }

    public static Boolean copyAttributes(Object from, Object to,
                                         Boolean includeNull) throws RuntimeException {
        if (from == null) {
            return true;
        }
        if (to != null && from.getClass() != to.getClass()) {
            // throw new RuntimeException("给出的两个类的类型不相同，不能进行赋值");
        }


        try {
            // 下面是反射机制
            // lqm todo
            // 这一部分应该考虑由clone取代
            Method[] methods = from.getClass().getMethods();
            String propertyName = null;
            Object returnValue = null;
            Method getMethod = null;
            Method setMethod = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().indexOf("get") == 0
                        && methods[i].getParameterTypes().length == 0) {
                    propertyName = methods[i].getName().substring(3);

                    try {
                        log.debug("from执行方法：" + methods[i].getName());
                        //lqm todo
                        //对 boolean函数的 is 方法支持不好
                        setMethod = to.getClass().getMethod(
                                "set" + propertyName,
                                methods[i].getReturnType());
                        if (setMethod == null)
                            continue;

                        returnValue = methods[i].invoke(from);
                        // todo
                        // 暂时还需要与dorado协调处理从界面传回null问题
                        // dorado需要告诉我们哪些字段产生了更新
                        // if(returnValue!=null)
                        // {
                        // String
                        // returnValueType=methods[i].getReturnType().getSimpleName();
                        // if ( !"Class".equals(propertyName)
                        // && ((returnValueType.equals("Long"))
                        // || (returnValueType.equalsIgnoreCase("String") )
                        // || (returnValueType.equalsIgnoreCase("Double") )
                        // || (returnValueType.equalsIgnoreCase("Integer") )
                        // || (returnValueType.equalsIgnoreCase("Float") )
                        // || (returnValueType.equalsIgnoreCase("Boolean") )
                        // || (returnValueType.equalsIgnoreCase("Date") )
                        // || (returnValueType.equalsIgnoreCase("Byte") )
                        // || (returnValueType.equalsIgnoreCase("Character") )
                        // || (returnValueType.equalsIgnoreCase("Number") )
                        // || (returnValueType.equalsIgnoreCase("Short")))) {
                        if (returnValue != null) {
                            if (!"Class".equals(propertyName)
                                    && ((returnValue instanceof Long)
                                    || (returnValue instanceof String)
                                    || (returnValue instanceof Double)
                                    || (returnValue instanceof Integer)
                                    || (returnValue instanceof Float)
                                    || (returnValue instanceof Boolean)
                                    || (returnValue instanceof Date)
                                    || (returnValue instanceof Byte)
                                    || (returnValue instanceof Character)
                                    || (returnValue instanceof Number) || (returnValue instanceof Short))) {

                                log.debug("to执行方法：" + "set" + propertyName);

                                setMethod.invoke(to, returnValue);
                            }
                        } else {
                            if (includeNull != null && includeNull)
                                setMethod.invoke(to, returnValue);

                        }
                        // }

                        // else if (returnValue instanceof BasicModel) {//
                        // 判断是否其中的所有属性都为空，是的话将不赋值给to变量
                        // getMethod = to.getClass().getMethod(
                        // "get" + propertyName);
                        // Object o = getMethod.invoke(to);
                        // boolean b = isAllPropertyNull(returnValue);
                        // if (b == false && o == null) {
                        // setMethod = to.getClass().getMethod(
                        // "set" + propertyName,
                        // methods[i].getReturnType());
                        // setMethod.invoke(to, returnValue);
                        // } else if (b == false && o != null) {
                        // copyAttributes(returnValue, o);
                        // }
                        // }
                    } catch (IllegalAccessException e) {
                        // e.printStackTrace();
                        log.debug("使用对象" + from.getClass().getName() + "的方法"
                                + methods[i].getName() + "错误" + "或者对象"
                                + to.getClass().getName() + "的方法set"
                                + propertyName + "错误！程序继续运行...");
                        continue;
                    } catch (InvocationTargetException e) {
                        // e.printStackTrace();
                        log.debug("使用对象" + from.getClass().getName() + "的方法"
                                + methods[i].getName() + "错误" + "或者对象"
                                + to.getClass().getName() + "的方法set"
                                + propertyName + "错误！程序继续运行...");
                        continue;
                    } catch (NoSuchMethodException e) {
                        // e.printStackTrace();
                        log.debug("找不到对象" + to.getClass().getName() + "的方法：set"
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

    public static boolean isAllPropertyNull(Object object) {
        if (object == null) {
            return true;
        }
        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().indexOf("get") == 0) {// 判断所有属性是否都为空
                String propertyName = methods[i].getName().substring(3);
                Object returnValue;
                try {
                    returnValue = methods[i].invoke(object);
                    if (returnValue != null
                            && !"Class".equals(propertyName)
                            && ((returnValue instanceof Long)
                            || (returnValue instanceof String)
                            || (returnValue instanceof Double)
                            || (returnValue instanceof Integer)
                            || (returnValue instanceof Float)
                            || (returnValue instanceof Boolean)
                            || (returnValue instanceof Date)
                            || (returnValue instanceof Byte)
                            || (returnValue instanceof Character)
                            || (returnValue instanceof Number) || (returnValue instanceof Short))) {
                        return false;
                    } else if (returnValue instanceof BasicModel) {
                        if (!isAllPropertyNull(returnValue)) {
                            return false;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    log.error("使用对象" + object.getClass().getName() + "的方法"
                            + methods[i].getName() + "错误！程序继续运行...");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    log.error("使用对象" + object.getClass().getName() + "的方法"
                            + methods[i].getName() + "错误！程序继续运行...");
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    log.error("使用对象" + object.getClass().getName() + "的方法"
                            + methods[i].getName() + "错误！程序继续运行...");
                    e.printStackTrace();
                }

            }
        }
        return true;
    }

    public static boolean copyAttribute(Object object, String attributeName,
                                        Object attributeValue, String attributeType) {
        if (object == null || attributeName == null) {
            return false;
        }
        if (attributeType == null) {
            attributeType = "string";
        }
        Class parameter;
        if ("date".equalsIgnoreCase(attributeType)) {
            parameter = Date.class;
        } else if ("long".equalsIgnoreCase(attributeType)
                || "integer".equalsIgnoreCase(attributeType)
                || "int".equalsIgnoreCase(attributeType)) {
            parameter = Long.class;
        } else if ("float".equalsIgnoreCase(attributeType)
                || "double".equalsIgnoreCase(attributeType)) {
            parameter = Double.class;
        } else {
            parameter = String.class;
        }
        String setMethodName = "set"
                + attributeName.substring(0, 1).toUpperCase()
                + attributeName.substring(1);
        try {
            Method method = object.getClass().getMethod(setMethodName,
                    parameter);
            method.invoke(object, attributeValue);
            log.debug("执行方法" + method + "，给属性" + attributeName + "赋值"
                    + attributeValue + "成功！");
        } catch (SecurityException e) {
            log.error("复制属性失败，系统将继续运行！");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            log.error("复制属性失败，系统将继续运行！");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log.error("复制属性失败，系统将继续运行！");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error("复制属性失败，系统将继续运行！");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            log.error("复制属性失败，系统将继续运行！");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获得一个对象所具有的属性
     *
     * @param className
     * @return
     */
    public static List<String> getClassAttribute(String className) {
        List<String> list = new ArrayList<String>();
        try {
            Class clazz = Class.forName(className);
            // 下面是反射机制
            Method[] methods = clazz.getMethods();
            String propertyName = null;
            Class returnValue = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().indexOf("get") == 0) {
                    propertyName = methods[i].getName().substring(3);
                    returnValue = methods[i].getReturnType();
                    if (returnValue != null
                            && !"Class".equals(propertyName)
                            && ((returnValue == Long.class)
                            || (returnValue == String.class)
                            || (returnValue == Double.class)
                            || (returnValue == Integer.class)
                            || (returnValue == Float.class)
                            || (returnValue == Boolean.class)
                            || (returnValue == Date.class)
                            || (returnValue == Byte.class)
                            || (returnValue == Character.class)
                            || (returnValue == Number.class) || (returnValue == Short.class))) {
                        list.add(propertyName.substring(0, 1).toLowerCase()
                                + propertyName.substring(1));
                    } else if (returnValue != null
                            && !"Class".equals(propertyName)
                            && ((returnValue.getSuperclass() == BasicModel.class))) {
                        // 下面是子对象
                        Method[] methods1 = returnValue.getMethods();
                        String propertyName1 = null;
                        Class returnValue1 = null;
                        for (int j = 0; j < methods1.length; j++) {
                            if (methods1[j].getName().indexOf("get") == 0) {
                                propertyName1 = methods1[j].getName()
                                        .substring(3);
                                returnValue1 = methods1[j].getReturnType();
                                if (returnValue1 != null
                                        && !"Class".equals(propertyName1)
                                        && ((returnValue1 == Long.class)
                                        || (returnValue1 == String.class)
                                        || (returnValue1 == Double.class)
                                        || (returnValue1 == Integer.class)
                                        || (returnValue1 == Float.class)
                                        || (returnValue1 == Boolean.class)
                                        || (returnValue1 == Date.class)
                                        || (returnValue1 == Byte.class)
                                        || (returnValue1 == Character.class)
                                        || (returnValue1 == Number.class) || (returnValue1 == Short.class))) {
                                    list.add(propertyName.substring(0, 1)
                                            .toLowerCase()
                                            + propertyName.substring(1)
                                            + "."
                                            + propertyName1.substring(0, 1)
                                            .toLowerCase()
                                            + propertyName1.substring(1));
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 实现一种类似的深度复制功能，两种对象必须类型完全一致 其中copyProperties还需要完善，需要支持这种定义 a.b.c等递归属性
     *
     * @return
     */
    public synchronized static Object copyObject(Object from, Object to,
                                                 String copyProperties) throws RuntimeException {

        if (copyProperties == null || copyProperties == "")
            copyAttributes(from, to);
        else
            copyProperties(from, to, copyProperties);
        /*
         * //by lqm 系统暂时不支持多层更新 将来如需要支持多层更新，可考虑将此层打开
         *
         * try { // 下面是反射机制 Method[] methods = from.getClass().getMethods();
         * String propertyName = null; Object doradoReturnValue = null; Method
         * setMethod = null; for (int i = 0; i < methods.length; i++) { if
         * (methods[i].getName().indexOf("get") == 0 &&
         * !"getNotNullExtendAttribute".equals(methods[i] .getName())) {
         * propertyName = methods[i].getName().substring(3); try {
         * log.debug("from执行方法：" + methods[i].getName()); doradoReturnValue =
         * methods[i].invoke(from); if (!"Class".equals(propertyName) &&
         * (doradoReturnValue instanceof BasicModel)) { Object
         * toReturnValue=methods[i].invoke(to);
         *
         * copyObject(doradoReturnValue,toReturnValue,copyProperties); } } catch
         * (IllegalAccessException e) { e.printStackTrace(); log.error("使用对象" +
         * from.getClass().getName() + "的方法" + methods[i].getName() + "错误" +
         * "或者对象" + to.getClass().getName() + "的方法set" + propertyName +
         * "错误！程序继续运行..."); continue; } catch (InvocationTargetException e) {
         * e.printStackTrace(); log.error("使用对象" + from.getClass().getName() +
         * "的方法" + methods[i].getName() + "错误" + "或者对象" +
         * to.getClass().getName() + "的方法set" + propertyName + "错误！程序继续运行...");
         * continue; }catch (Exception e) { e.printStackTrace(); } } } } catch
         * (RuntimeException e) { log.error("复制属性错误"); e.printStackTrace();
         * throw new RuntimeException(e.getMessage()); }
         */

        return to;

    }

    /**
     * 将source的属性properties拷贝到target
     *
     * @param source
     * @param target
     * @throws RuntimeException
     */
    public synchronized static void copyProperties(Object source,
                                                   Object target, String copyProperties) throws RuntimeException {
        String[] properties = copyProperties.split(",");

        if (properties.length > 0) {

            for (int i = 0; i < properties.length; i++) {
                String property = properties[i].trim();
                MethodCaller.copyProperty(source, property, target);
            }
        }
    }

    public static Object getValue(Object object, String name) {

        try {
            //获取当前对象（object）的类
            Class jClass = object.getClass();
//获取所需的属性值
            PropertyDescriptor pd = new PropertyDescriptor(name, jClass);
//获得读取属性值的方法
            Method getMethod = pd.getReadMethod();
//读取属性值
            Object o = getMethod.invoke(object);
////设置并转换数据类型
//		emp.setCreateId(Math.toIntExact((Long) o));
////调用set方法
//		emp.setIsDelete(0);
////插入添加的数据
//		empService.insert(emp);
            return o;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
