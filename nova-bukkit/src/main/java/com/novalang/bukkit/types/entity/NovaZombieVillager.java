package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

/** Spigot 1.12.2 僵尸村民的 Fluxon 函数别名。 */
public final class NovaZombieVillager {

    private NovaZombieVillager() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ZombieVillager.class, "villagerProfession", function -> function.returns(Villager.Profession.class)
                .invoke(arguments -> villager(arguments).getVillagerProfession()));
        builder.extension(ZombieVillager.class, "setVillagerProfession", function -> function.param("profession", Villager.Profession.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    villager(arguments).setVillagerProfession(argument(arguments, 1, Villager.Profession.class));
                    return null;
                }));
    }

    private static ZombieVillager villager(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ZombieVillager.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
