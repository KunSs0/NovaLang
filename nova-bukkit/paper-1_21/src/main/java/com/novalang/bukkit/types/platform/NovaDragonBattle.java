package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Paper 1.21.x DragonBattle 扩展。 */
public final class NovaDragonBattle {

    private NovaDragonBattle() {
    }

    /** 注册 DragonBattle 的直接类型扩展。 */
    public static void register(JavaTypes.Builder builder) {
        builder.extension(DragonBattle.class, "enderDragon", f -> f
                .returns(JavaTypeRef.javaType(EnderDragon.class).nullable())
                .invoke(a -> battle(a).getEnderDragon()));
        builder.extension(DragonBattle.class, "bossBar", f -> f
                .returns(JavaTypeRef.javaType(BossBar.class).nullable())
                .invoke(a -> battle(a).getBossBar()));
        builder.extension(DragonBattle.class, "endPortalLocation", f -> f
                .returns(JavaTypeRef.javaType(Location.class).nullable())
                .invoke(a -> battle(a).getEndPortalLocation()));
        builder.extension(DragonBattle.class, "generateEndPortal", f -> f
                .param("withPortals", Boolean.class).returns(Void.TYPE)
                .invoke(a -> { battle(a).generateEndPortal(argument(a, 1, Boolean.class)); return null; }));
        builder.extension(DragonBattle.class, "hasBeenPreviouslyKilled", f -> f
                .returns(Boolean.class).invoke(a -> battle(a).hasBeenPreviouslyKilled()));
        builder.extension(DragonBattle.class, "setPreviouslyKilled", f -> f
                .param("previouslyKilled", Boolean.class).returns(Void.TYPE)
                .invoke(a -> { battle(a).setPreviouslyKilled(argument(a, 1, Boolean.class)); return null; }));
        builder.extension(DragonBattle.class, "initiateRespawn", f -> f
                .returns(Void.TYPE).invoke(a -> { battle(a).initiateRespawn(); return null; }));
        builder.extension(DragonBattle.class, "initiateRespawn", f -> f
                .param("crystals", JavaTypeRef.listOf(JavaTypeRef.javaType(EnderCrystal.class)))
                .returns(Boolean.class)
                .invoke(arguments -> battle(arguments).initiateRespawn(crystals(arguments))));
        builder.extension(DragonBattle.class, "respawnPhase", f -> f
                .returns(JavaTypeRef.javaType(DragonBattle.RespawnPhase.class))
                .invoke(a -> battle(a).getRespawnPhase()));
        builder.extension(DragonBattle.class, "setRespawnPhase", f -> f
                .param("phase", DragonBattle.RespawnPhase.class).returns(Void.TYPE)
                .invoke(a -> { battle(a).setRespawnPhase(argument(a, 1, DragonBattle.RespawnPhase.class)); return null; }));
        builder.extension(DragonBattle.class, "setRespawnPhase", f -> f
                .param("phase", String.class).returns(Void.TYPE)
                .invoke(a -> {
                    DragonBattle.RespawnPhase phase = NovaTypeSupport.findEnum(
                            DragonBattle.RespawnPhase.class, argument(a, 1, String.class));
                    if (phase != null) {
                        battle(a).setRespawnPhase(phase);
                    }
                    return null;
                }));
        builder.extension(DragonBattle.class, "resetCrystals", f -> f
                .returns(Void.TYPE).invoke(a -> { battle(a).resetCrystals(); return null; }));
    }

    /** 取得扩展调用中的 DragonBattle 参数。 */
    private static DragonBattle battle(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, DragonBattle.class);
    }

    /** 取得扩展调用中的指定类型参数。 */
    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }

    /**
     * 将脚本参数转换为 DragonBattle 所需的 EnderCrystal 集合。
     *
     * @param arguments 扩展调用参数
     * @return 类型安全的水晶集合
     */
    private static Collection<EnderCrystal> crystals(Object[] arguments) {
        Object value = NovaTypeSupport.argument(arguments, 1, Object.class);
        if (!(value instanceof Collection<?>)) {
            throw new IllegalArgumentException("crystals must be a collection");
        }
        Collection<?> values = (Collection<?>) value;
        List<EnderCrystal> crystals = new ArrayList<EnderCrystal>();
        for (Object item : values) {
            crystals.add(EnderCrystal.class.cast(item));
        }
        return crystals;
    }
}
