package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 高版本 Bukkit BlockFace 增量 API。 */
@Requires(
        classes = {"org.bukkit.block.BlockFace"},
        methods = {
                "org.bukkit.block.BlockFace#getDirection",
                "org.bukkit.block.BlockFace#isCartesian"
        })
public final class NovaBlockFaceModern {

    private NovaBlockFaceModern() {
    }

    public static void register(JavaTypes.Builder builder) {
        Method getDirection = method("getDirection");
        Method isCartesian = method("isCartesian");
        builder.extension(BlockFace.class, "direction", function -> function
                .returns(Vector.class)
                .invoke(arguments -> invoke(getDirection, face(arguments))));
        builder.extension(BlockFace.class, "isCartesian", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> invoke(isCartesian, face(arguments))));
    }

    private static BlockFace face(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockFace.class);
    }

    private static Method method(String name) {
        try {
            return BlockFace.class.getMethod(name);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的方法不存在: " + name, exception);
        }
    }

    private static Object invoke(Method method, BlockFace target) {
        try {
            return method.invoke(target);
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
