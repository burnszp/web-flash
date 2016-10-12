package cn.enilu.flash.core.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 *
 * @author enilu(eniluzt@qq.com)
 *
 */
public final class Reflects {
    public static class ClassAnnotation<A extends Annotation> {
        private final Class<?> klass;
        private final A annotation;

        public ClassAnnotation(Class<?> klass, A annotation) {
            this.klass = klass;
            this.annotation = annotation;
        }

        public Class<?> getKlass() {
            return klass;
        }

        public A getAnnotation() {
            return annotation;
        }
    }

    private Reflects() {
    }

    /**
     * 获取类型中的所有属性信息
     * @param klass
     * @param recursive 是否获取父类属性信息
     * @return
     */
    public static List<Field> getFields(Class<?> klass, boolean recursive) {
        return getFields(klass, null, recursive);
    }

    public static List<Field> getFields(Class<?> klass, Class<? extends Annotation> annotationClass, boolean recursive) {
        Map<String, Field> fields = new LinkedHashMap<>();

        ArrayList<Class<?>> classList = Lists.newArrayList();
        classList.add(klass);
        if (recursive) {
            Class<?> current = klass.getSuperclass();
            while (current != null) {
                classList.add(current);
                current = current.getSuperclass();
            }
        }

        for (int i = classList.size() - 1; i >= 0; i--) {
            Class<?> current = classList.get(i);

            for (Field field : current.getDeclaredFields()) {
                if (annotationClass == null) {
                    fields.put(field.getName(), field);
                    continue;
                }

                if (field.getAnnotation(annotationClass) != null) {
                    fields.put(field.getName(), field);
                }
            }
        }

        return new ArrayList<>(fields.values());
    }

    public static boolean isAnnotationPresent(Class<?> klass,
                                              Class<? extends Annotation> annotationClass) {
        Class<?> current = klass;
        while (current != null) {
            if (current.isAnnotationPresent(annotationClass)) {
                return true;
            }
            current = current.getSuperclass();
        }

        return false;
    }

    /**
     * 获取指定类型的注解信息
     * @param klass
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> ClassAnnotation<A> getAnnotation(
            Class<?> klass, Class<A> annotationClass) {
        Class<?> current = klass;
        while (current != null) {
            if (current.isAnnotationPresent(annotationClass)) {
                A annotation = current.getAnnotation(annotationClass);
                return new ClassAnnotation<A>(current, annotation);
            }
            current = current.getSuperclass();
        }

        return null;
    }

    /**
     * 获取指定名称的属性信息
     * @param cc
     * @param name
     * @return
     */
    public static  Field getField(Class<?> cc,String name) {
        while (null != cc && cc != Object.class) {
            try {
                return cc.getDeclaredField(name);
            }
            catch (NoSuchFieldException e) {
                cc = cc.getSuperclass();
            }
        }
      return null;
    }

    /**
     * 获取集合属性中的参数类型，集合必须声明为范型
     * @param field  集合属性
     * @return 参数类型
     */
    public static Class getParameterizedType(Field field){
        Type type = field.getGenericType();
        if(type==null){
            return null;
        }
        if(type instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) type;
            Class genericClazz = (Class)pt.getActualTypeArguments()[0];
            return genericClazz;
        }
        return null;

    }
}
