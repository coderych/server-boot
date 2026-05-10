package com.coderych.commons.core.util.object;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.TypeUtil;
import com.coderych.commons.core.util.collection.ArrayUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * 类工具类，扩展 Hutool {@link ClassUtil}，增加泛型参数解析。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtils extends ClassUtil {

    public static String getShortName(Class<?> type) {
        return type == null ? null : type.getSimpleName();
    }

    public static Class<?>[] getGenericParameterTypes(Class<?> clazz) {
        final Type[] typeArguments = TypeUtil.getTypeArguments(clazz);
        if (ArrayUtils.isEmpty(typeArguments)) {
            return new Class[0];
        }
        final Class<?>[] classes = new Class[typeArguments.length];
        for (int i = 0; i < typeArguments.length; i++) {
            classes[i] = TypeUtil.getClass(typeArguments[i]);
        }
        return classes;
    }
}
