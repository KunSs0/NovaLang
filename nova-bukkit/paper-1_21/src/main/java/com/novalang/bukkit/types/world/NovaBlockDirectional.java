package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 1.13+ Directional BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Directional"},
        methods = {
                "org.bukkit.block.data.Directional#getFacing",
                "org.bukkit.block.data.Directional#setFacing",
                "org.bukkit.block.data.Directional#getFaces"
        })
public final class NovaBlockDirectional {

    private static final String DIRECTIONAL = "org.bukkit.block.data.Directional";

    private NovaBlockDirectional() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> directionalType = type();
        Method getFacing = method(directionalType, "getFacing");
        Method setFacing = method(directionalType, "setFacing", BlockFace.class);
        Method getFaces = method(directionalType, "getFaces");
        builder.extension(directionalType, "facing", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> invoke(getFacing, target(arguments))));
        builder.extension(directionalType, "setFacing", function -> function
                .param("face", BlockFace.class)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(setFacing, target(arguments), argument(arguments, 1, BlockFace.class))));
        builder.extension(directionalType, "setFacing", function -> function
                .param("face", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, argument(arguments, 1, String.class));
                    if (face != null) {
                        invoke(setFacing, target(arguments), face);
                    }
                    return null;
                }));
        builder.extension(directionalType, "faces", function -> function
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(BlockFace.class)))
                .invoke(arguments -> invoke(getFaces, target(arguments))));
    }

    private static Class<?> type() {
        try {
            return Class.forName(DIRECTIONAL, false, NovaBlockDirectional.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + DIRECTIONAL, exception);
        }
    }

    private static Method method(Class<?> targetType, String name, Class<?>... parameterTypes) {
        try {
            return targetType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 方法不存在: " + targetType.getName() + '#' + name, exception);
        }
    }

    private static Object target(Object[] arguments) {
        return arguments[0];
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }

    private static Object invoke(Method method, Object target, Object... parameters) {
        try {
            return method.invoke(target, parameters);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("无法调用 Bukkit 方法: " + method, exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Bukkit 方法执行失败: " + method, cause);
        }
    }
}
