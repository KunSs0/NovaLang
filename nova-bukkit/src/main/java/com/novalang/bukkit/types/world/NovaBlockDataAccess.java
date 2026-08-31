package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 1.13+ BlockData 的 Block/Material 访问入口。 */
@Requires(
        classes = {"org.bukkit.block.data.BlockData"},
        methods = {
                "org.bukkit.block.Block#getBlockData",
                "org.bukkit.block.Block#setBlockData",
                "org.bukkit.Material#createBlockData"
        })
public final class NovaBlockDataAccess {

    private static final String BLOCK_DATA = "org.bukkit.block.data.BlockData";

    private NovaBlockDataAccess() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> blockDataType = type(BLOCK_DATA);
        Method getBlockData = method(Block.class, "getBlockData");
        Method setBlockData = method(Block.class, "setBlockData", blockDataType);
        Method setBlockDataWithPhysics = method(Block.class, "setBlockData", blockDataType, Boolean.TYPE);
        Method createBlockData = method(Material.class, "createBlockData");
        Method createBlockDataFromData = method(Material.class, "createBlockData", String.class);

        builder.extension(Block.class, "blockData", function -> function
                .returns(JavaTypeRef.javaType(blockDataType))
                .invoke(arguments -> invoke(getBlockData, block(arguments))));
        builder.extension(Block.class, "setBlockData", function -> function
                .param("data", blockDataType)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(setBlockData, block(arguments), argument(arguments, 1, blockDataType))));
        builder.extension(Block.class, "setBlockData", function -> function
                .param("data", blockDataType)
                .param("applyPhysics", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(setBlockDataWithPhysics, block(arguments),
                        argument(arguments, 1, blockDataType), argument(arguments, 2, Boolean.class))));
        builder.extension(Material.class, "createBlockData", function -> function
                .returns(JavaTypeRef.javaType(blockDataType))
                .invoke(arguments -> invoke(createBlockData, material(arguments))));
        builder.extension(Material.class, "createBlockData", function -> function
                .param("data", String.class)
                .returns(JavaTypeRef.javaType(blockDataType))
                .invoke(arguments -> invoke(createBlockDataFromData, material(arguments), argument(arguments, 1, String.class))));
    }

    private static Class<?> type(String name) {
        try {
            return Class.forName(name, false, NovaBlockDataAccess.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + name, exception);
        }
    }

    private static Method method(Class<?> targetType, String name, Class<?>... parameterTypes) {
        try {
            return targetType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 方法不存在: " + targetType.getName() + '#' + name, exception);
        }
    }

    private static Block block(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Block.class);
    }

    private static Material material(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Material.class);
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
