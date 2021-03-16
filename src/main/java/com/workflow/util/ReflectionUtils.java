package com.workflow.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 *
 * @author 26223
 * @time 2016年10月13日
 * @email lukw@eastcom-sw.com
 */
@Slf4j
public class ReflectionUtils {

    private static final DateFormat DATE_INSTANCE = DateFormat.getDateInstance();

    public static Class<?> getGenderClass(final Class<?> clazz) {

        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (0 >= params.length) {
            return Object.class;
        }
        if (!(params[0] instanceof Class)) {
            log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class<?>) params[0];
    }

    /**
     * 获取指定实例 指定属性的值
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getter(Object object, String fieldName) {

        Field field = getField(object, fieldName);
        try {
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.info("getter", e);
        }
        return null;
    }

    /**
     * 设定指定实例 指定属性的值
     *
     * @param object
     * @param fieldName
     * @param value
     */
    public static void setter(Object object, String fieldName, Object value) {

        Field field = getField(object, fieldName);
        try {
            if (field != null) {
                field.setAccessible(true);
                field.set(object, value);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.info("setter", e);
        }
    }

    /**
     * 获取指定实例 指定属性
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static Field getField(Object object, String fieldName) {

        List<Field> list = getFields(object, true);
        if (!list.isEmpty()) {
            for (Field field : list) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取 object 属性
     *
     * @param object
     * @param isSuper 是否包含父类属性 true包含,false不包含 默认包含
     * @return
     */
    public static List<Field> getFields(Object object, boolean isSuper) {

        if (isSuper) {
            return getFields(object);
        }
        Class<?> clazz = getClass(object);
        List<Field> list = new ArrayList<>();
        getFields(clazz, list);
        return list;
    }

    private static void getFields(Class<?> clazz, List<Field> list) {

        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                list.add(field);
            }
        }
    }

    /**
     * 获取object 属性 不包含父类属性
     *
     * @return
     */
    public static List<Field> getFields(Object object) {

        Class<?> clazz = getClass(object);
        List<Field> list = new ArrayList<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            getFields(clazz, list);
        }
        return list;
    }

    /**
     * 获取class
     *
     * @param object
     * @return
     */
    private static Class<?> getClass(Object object) {
        Class<?> clazz = object.getClass();
        if (object instanceof Class<?>) {
            clazz = (Class<?>) object;
        }
        return clazz;
    }

    /**
     * bean 转 map
     *
     * @param object
     * @return
     */
    public static Map<String, Object> beanToMap(Object object) {

        List<Field> fields = getFields(object, true);
        return beanToMap(object, fields);
    }

    /**
     * bean 转 map
     *
     * @param object
     * @param isSuper 是否包含父类属性 默认包含
     * @return
     */
    public static Map<String, Object> beanToMap(Object object, boolean isSuper) {

        List<Field> fields = getFields(object, isSuper);
        return beanToMap(object, fields);
    }

    private static Map<String, Object> beanToMap(Object object, List<Field> fields) {

        Map<String, Object> map = Maps.newHashMap();
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                String fieldName = field.getName();
                map.put(fieldName, getter(object, fieldName));
            }
        }
        return map;
    }

    /**
     * 获取 object 的所有方法 包含 父类的方法
     *
     * @param object
     * @return
     */
    public static List<Method> getMethods(Object object) {

        Class<?> clazz = getClass(object);
        List<Method> list = Lists.newArrayList();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            getMethods(list, clazz);
        }
        return list;
    }

    /**
     * 获取 object 的所有方法 包含 父类的方法
     *
     * @param object
     * @param isSuper 是否 包含父类方法 true 包含 false 不包含
     * @return
     */
    public static List<Method> getMethods(Object object, boolean isSuper) {

        if (isSuper) {
            return getMethods(object);
        }
        Class<?> clazz = getClass(object);
        List<Method> list = Lists.newArrayList();
        getMethods(list, clazz);
        return list;
    }

    private static void getMethods(List<Method> list, Class<?> clazz) {

        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            list.addAll(Arrays.asList(methods));
        }
    }

    /**
     * bean 复制
     *
     * @param object 源bean
     * @param target 目标bean
     */
    public static void copyBean(Object object, Object target) {

        List<Field> tFs = getFields(target);
        List<Field> oFs = getFields(object);
        for (Field of : oFs) {
            for (Field tf : tFs) {
                if (of.getName().equals(tf.getName())) {
                    Object value = getter(object, of.getName());
                    setter(target, tf.getName(), value);
                }
            }
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getDeclaredField(Class<?> cls, String fileName) {
        while (!cls.equals(Object.class)) {
            try {
                return cls.getDeclaredField(fileName);
            } catch (Exception e) {
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    public static Object convert(Object value, Class<?> toClass) {
        if (value == null) {
            return null;
        }
        if (toClass.isInstance(value)) {
            return value;
        }
        if ((Date.class.equals(toClass)) && (value instanceof String)) {
            try {
                DATE_INSTANCE.parse((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        try {
            return convert2(value, toClass);
        } catch (Exception e) {
            return value;
        }
    }

    private static Object convert2(Object value, Class<?> toClass) {
        Converter converter = ConvertUtils.lookup(toClass);
        if (converter == null) {
            converter = ConvertUtils.lookup(String.class);
        }
        return converter.convert(toClass, value);
    }
}
