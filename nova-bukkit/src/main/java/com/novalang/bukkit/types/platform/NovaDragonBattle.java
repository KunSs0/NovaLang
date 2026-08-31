package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

/** 1.13+ DragonBattle 的 Fluxon 兼容契约。 */
@Requires(
        classes = {
                "org.bukkit.boss.DragonBattle",
                "org.bukkit.boss.DragonBattle$RespawnPhase",
                "org.bukkit.entity.EnderCrystal"
        },
        methods = {
                "org.bukkit.boss.DragonBattle#getEnderDragon",
                "org.bukkit.boss.DragonBattle#getBossBar",
                "org.bukkit.boss.DragonBattle#getEndPortalLocation",
                "org.bukkit.boss.DragonBattle#generateEndPortal",
                "org.bukkit.boss.DragonBattle#hasBeenPreviouslyKilled",
                "org.bukkit.boss.DragonBattle#setPreviouslyKilled",
                "org.bukkit.boss.DragonBattle#initiateRespawn",
                "org.bukkit.boss.DragonBattle#getRespawnPhase",
                "org.bukkit.boss.DragonBattle#setRespawnPhase",
                "org.bukkit.boss.DragonBattle#resetCrystals"
        })
public final class NovaDragonBattle {

    private static final String DRAGON_BATTLE = "org.bukkit.boss.DragonBattle";
    private static final String RESPAWN_PHASE = "org.bukkit.boss.DragonBattle$RespawnPhase";

    private NovaDragonBattle() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> dragonBattleType = type(DRAGON_BATTLE);
        Class<?> respawnPhaseType = type(RESPAWN_PHASE);
        Method getEnderDragon = method(dragonBattleType, "getEnderDragon");
        Method getBossBar = method(dragonBattleType, "getBossBar");
        Method getEndPortalLocation = method(dragonBattleType, "getEndPortalLocation");
        Method generateEndPortal = method(dragonBattleType, "generateEndPortal", Boolean.TYPE);
        Method hasBeenPreviouslyKilled = method(dragonBattleType, "hasBeenPreviouslyKilled");
        Method setPreviouslyKilled = method(dragonBattleType, "setPreviouslyKilled", Boolean.TYPE);
        Method initiateRespawn = method(dragonBattleType, "initiateRespawn");
        Method initiateRespawnWithCrystals = method(dragonBattleType, "initiateRespawn", Collection.class);
        Method getRespawnPhase = method(dragonBattleType, "getRespawnPhase");
        Method setRespawnPhase = method(dragonBattleType, "setRespawnPhase", respawnPhaseType);
        Method resetCrystals = method(dragonBattleType, "resetCrystals");

        builder.extension(dragonBattleType, "enderDragon", function -> function
                .returns(JavaTypeRef.javaType(EnderDragon.class).nullable())
                .invoke(arguments -> invoke(getEnderDragon, target(arguments))));
        builder.extension(dragonBattleType, "bossBar", function -> function
                .returns(JavaTypeRef.javaType(BossBar.class).nullable())
                .invoke(arguments -> invoke(getBossBar, target(arguments))));
        builder.extension(dragonBattleType, "endPortalLocation", function -> function
                .returns(JavaTypeRef.javaType(Location.class).nullable())
                .invoke(arguments -> invoke(getEndPortalLocation, target(arguments))));
        builder.extension(dragonBattleType, "generateEndPortal", function -> function
                .param("withPortals", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(generateEndPortal, target(arguments), argument(arguments, 1, Boolean.class))));
        builder.extension(dragonBattleType, "hasBeenPreviouslyKilled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> invoke(hasBeenPreviouslyKilled, target(arguments))));
        builder.extension(dragonBattleType, "setPreviouslyKilled", function -> function
                .param("previouslyKilled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(setPreviouslyKilled, target(arguments), argument(arguments, 1, Boolean.class))));
        builder.extension(dragonBattleType, "initiateRespawn", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(initiateRespawn, target(arguments))));
        builder.extension(dragonBattleType, "initiateRespawn", function -> function
                .param("crystals", Collection.class)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(initiateRespawnWithCrystals, target(arguments), argument(arguments, 1, Collection.class))));
        builder.extension(dragonBattleType, "respawnPhase", function -> function
                .returns(JavaTypeRef.javaType(respawnPhaseType))
                .invoke(arguments -> invoke(getRespawnPhase, target(arguments))));
        builder.extension(dragonBattleType, "setRespawnPhase", function -> function
                .param("phase", respawnPhaseType)
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(setRespawnPhase, target(arguments), argument(arguments, 1, respawnPhaseType))));
        builder.extension(dragonBattleType, "setRespawnPhase", function -> function
                .param("phase", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Object phase = enumValue(respawnPhaseType, argument(arguments, 1, String.class));
                    if (phase != null) {
                        invoke(setRespawnPhase, target(arguments), phase);
                    }
                    return null;
                }));
        builder.extension(dragonBattleType, "resetCrystals", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> invoke(resetCrystals, target(arguments))));
    }

    private static Class<?> type(String name) {
        try {
            return Class.forName(name, false, NovaDragonBattle.class.getClassLoader());
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
