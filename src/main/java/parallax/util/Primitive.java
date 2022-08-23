package parallax.util;

import java.util.HashMap;
import java.util.Map;

public class Primitive {
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();
    static {
         primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
         primitiveWrapperMap.put(Byte.TYPE, Byte.class);
         primitiveWrapperMap.put(Character.TYPE, Character.class);
         primitiveWrapperMap.put(Short.TYPE, Short.class);
         primitiveWrapperMap.put(Integer.TYPE, Integer.class);
         primitiveWrapperMap.put(Long.TYPE, Long.class);
         primitiveWrapperMap.put(Double.TYPE, Double.class);
         primitiveWrapperMap.put(Float.TYPE, Float.class);
         primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || primitiveWrapperMap.containsKey(type);
    }
    
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    
}
