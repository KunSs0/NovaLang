package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/** 1.13+ BlockData 的 Fluxon 通用函数契约。 */
@Requires(
        classes = {
                "org.bukkit.block.data.BlockData",
                "org.bukkit.SoundGroup",
                "org.bukkit.block.BlockSupport",
                "org.bukkit.block.structure.Mirror",
                "org.bukkit.block.structure.StructureRotation"
        },
        methods = {
                "org.bukkit.block.data.BlockData#getMaterial",
                "org.bukkit.block.data.BlockData#getAsString",
                "org.bukkit.block.data.BlockData#merge",
                "org.bukkit.block.data.BlockData#matches",
                "org.bukkit.block.data.BlockData#clone",
                "org.bukkit.block.data.BlockData#getSoundGroup",
                "org.bukkit.block.data.BlockData#getLightEmission",
                "org.bukkit.block.data.BlockData#isOccluding",
                "org.bukkit.block.data.BlockData#requiresCorrectToolForDrops",
                "org.bukkit.block.data.BlockData#isPreferredTool",
                "org.bukkit.block.data.BlockData#getPistonMoveReaction",
                "org.bukkit.block.data.BlockData#isSupported",
                "org.bukkit.block.data.BlockData#isFaceSturdy",
                "org.bukkit.block.data.BlockData#getMapColor",
                "org.bukkit.block.data.BlockData#getPlacementMaterial",
                "org.bukkit.block.data.BlockData#rotate",
                "org.bukkit.block.data.BlockData#mirror",
                "org.bukkit.block.data.BlockData#copyTo"
        })
public final class NovaBlockData {

    private static final String BLOCK_DATA = "org.bukkit.block.data.BlockData";
    private static final String SOUND_GROUP = "org.bukkit.SoundGroup";
    private static final String BLOCK_SUPPORT = "org.bukkit.block.BlockSupport";
    private static final String MIRROR = "org.bukkit.block.structure.Mirror";
    private static final String STRUCTURE_ROTATION = "org.bukkit.block.structure.StructureRotation";

    private NovaBlockData() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> blockDataType = type(BLOCK_DATA);
        Class<?> soundGroupType = type(SOUND_GROUP);
        Class<?> blockSupportType = type(BLOCK_SUPPORT);
        Class<?> mirrorType = type(MIRROR);
        Class<?> structureRotationType = type(STRUCTURE_ROTATION);
        Method getMaterial = method(blockDataType, "getMaterial");
        Method getAsString = method(blockDataType, "getAsString");
        Method getAsStringWithHideUnspecified = method(blockDataType, "getAsString", Boolean.TYPE);
        Method merge = method(blockDataType, "merge", blockDataType);
        Method matches = method(blockDataType, "matches", blockDataType);
        Method clone = method(blockDataType, "clone");
        Method getSoundGroup = method(blockDataType, "getSoundGroup");
        Method getLightEmission = method(blockDataType, "getLightEmission");
        Method isOccluding = method(blockDataType, "isOccluding");
        Method requiresCorrectToolForDrops = method(blockDataType, "requiresCorrectToolForDrops");
        Method isPreferredTool = method(blockDataType, "isPreferredTool", ItemStack.class);
        Method getPistonMoveReaction = method(blockDataType, "getPistonMoveReaction");
        Method isSupportedWithBlock = method(blockDataType, "isSupported", Block.class);
        Method isSupportedWithLocation = method(blockDataType, "isSupported", Location.class);
        Method isFaceSturdy = method(blockDataType, "isFaceSturdy", BlockFace.class, blockSupportType);
        Method getMapColor = method(blockDataType, "getMapColor");
        Method getPlacementMaterial = method(blockDataType, "getPlacementMaterial");
        Method rotate = method(blockDataType, "rotate", structureRotationType);
        Method mirror = method(blockDataType, "mirror", mirrorType);
        Method copyTo = method(blockDataType, "copyTo", blockDataType);

        builder.extension(blockDataType, "material", function -> function.returns(Material.class)
                .invoke(arguments -> invoke(getMaterial, target(arguments))));
        builder.extension(blockDataType, "asString", function -> function.returns(String.class)
                .invoke(arguments -> invoke(getAsString, target(arguments))));
        builder.extension(blockDataType, "getAsString", function -> function
                .param("hideUnspecified", Boolean.class).returns(String.class)
                .invoke(arguments -> invoke(getAsStringWithHideUnspecified, target(arguments), argument(arguments, 1, Boolean.class))));
        builder.extension(blockDataType, "merge", function -> function
                .param("data", blockDataType).returns(JavaTypeRef.javaType(blockDataType))
                .invoke(arguments -> invoke(merge, target(arguments), argument(arguments, 1, blockDataType))));
        builder.extension(blockDataType, "matches", function -> function
                .param("data", blockDataType).returns(Boolean.class)
                .invoke(arguments -> invoke(matches, target(arguments), argument(arguments, 1, blockDataType))));
        builder.extension(blockDataType, "clone", function -> function.returns(JavaTypeRef.javaType(blockDataType))
                .invoke(arguments -> invoke(clone, target(arguments))));
        builder.extension(blockDataType, "soundGroup", function -> function.returns(JavaTypeRef.javaType(soundGroupType))
                .invoke(arguments -> invoke(getSoundGroup, target(arguments))));
        builder.extension(blockDataType, "lightEmission", function -> function.returns(Integer.class)
                .invoke(arguments -> invoke(getLightEmission, target(arguments))));
        builder.extension(blockDataType, "isOccluding", function -> function.returns(Boolean.class)
                .invoke(arguments -> invoke(isOccluding, target(arguments))));
        builder.extension(blockDataType, "requiresCorrectToolForDrops", function -> function.returns(Boolean.class)
                .invoke(arguments -> invoke(requiresCorrectToolForDrops, target(arguments))));
        builder.extension(blockDataType, "isPreferredTool", function -> function
                .param("tool", ItemStack.class).returns(Boolean.class)
                .invoke(arguments -> invoke(isPreferredTool, target(arguments), argument(arguments, 1, ItemStack.class))));
        builder.extension(blockDataType, "pistonMoveReaction", function -> function.returns(PistonMoveReaction.class)
                .invoke(arguments -> invoke(getPistonMoveReaction, target(arguments))));
        builder.extension(blockDataType, "isSupported", function -> function
                .param("block", Block.class).returns(Boolean.class)
                .invoke(arguments -> invoke(isSupportedWithBlock, target(arguments), argument(arguments, 1, Block.class))));
        builder.extension(blockDataType, "isSupported", function -> function
                .param("location", Location.class).returns(Boolean.class)
                .invoke(arguments -> invoke(isSupportedWithLocation, target(arguments), argument(arguments, 1, Location.class))));
        builder.extension(blockDataType, "isFaceSturdy", function -> function
                .param("face", BlockFace.class).param("support", blockSupportType).returns(Boolean.class)
                .invoke(arguments -> invoke(isFaceSturdy, target(arguments), argument(arguments, 1, BlockFace.class), argument(arguments, 2, blockSupportType))));
        builder.extension(blockDataType, "isFaceSturdy", function -> function
                .param("face", String.class).param("support", blockSupportType).returns(Boolean.class)
                .invoke(arguments -> isFaceSturdy(isFaceSturdy, target(arguments), blockSupportType,
                        NovaTypeSupport.findEnum(BlockFace.class, argument(arguments, 1, String.class)), argument(arguments, 2, blockSupportType))));
        builder.extension(blockDataType, "isFaceSturdy", function -> function
                .param("face", BlockFace.class).param("support", String.class).returns(Boolean.class)
                .invoke(arguments -> isFaceSturdy(isFaceSturdy, target(arguments), blockSupportType,
                        argument(arguments, 1, BlockFace.class), enumValue(blockSupportType, argument(arguments, 2, String.class)))));
        builder.extension(blockDataType, "isFaceSturdy", function -> function
                .param("face", String.class).param("support", String.class).returns(Boolean.class)
                .invoke(arguments -> isFaceSturdy(isFaceSturdy, target(arguments), blockSupportType,
                        NovaTypeSupport.findEnum(BlockFace.class, argument(arguments, 1, String.class)),
                        enumValue(blockSupportType, argument(arguments, 2, String.class)))));
        builder.extension(blockDataType, "mapColor", function -> function.returns(Color.class)
                .invoke(arguments -> invoke(getMapColor, target(arguments))));
        builder.extension(blockDataType, "placementMaterial", function -> function.returns(Material.class)
                .invoke(arguments -> invoke(getPlacementMaterial, target(arguments))));
        builder.extension(blockDataType, "rotate", function -> function
                .param("rotation", structureRotationType).returns(Void.TYPE)
                .invoke(arguments -> invoke(rotate, target(arguments), argument(arguments, 1, structureRotationType))));
        builder.extension(blockDataType, "mirror", function -> function
                .param("mirror", mirrorType).returns(Void.TYPE)
                .invoke(arguments -> invoke(mirror, target(arguments), argument(arguments, 1, mirrorType))));
        builder.extension(blockDataType, "copyTo", function -> function
                .param("other", blockDataType).returns(Void.TYPE)
                .invoke(arguments -> invoke(copyTo, target(arguments), argument(arguments, 1, blockDataType))));
    }

    private static boolean isFaceSturdy(Method method, Object target, Class<?> supportType, BlockFace face, Object support) {
        if (face == null || support == null || !supportType.isInstance(support)) {
            return false;
        }
        return (Boolean) invoke(method, target, face, support);
    }

    private static Class<?> type(String name) {
        try {
            return Class.forName(name, false, NovaBlockData.class.getClassLoader());
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

    private static Object target(Object[] arguments) {
        return arguments[0];
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }

    private static Object enumValue(Class<?> enumType, String value) {
        if (value == null || !enumType.isEnum()) {
            return null;
        }
        String normalized = value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT);
        Object[] constants = enumType.getEnumConstants();
        for (Object constant : constants) {
            if (((Enum<?>) constant).name().equals(normalized)) {
                return constant;
            }
        }
        return null;
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
