package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

/** Spigot 1.12.2 中僵尸的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.Zombie"})
public final class NovaZombie {

    private NovaZombie() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Zombie.class, "isBaby", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> zombie(arguments).isBaby()));
        builder.extension(Zombie.class, "setBaby", function -> function
                .param("baby", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    zombie(arguments).setBaby(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Zombie.class, "isVillager", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> zombie(arguments).isVillager()));
        builder.extension(Zombie.class, "setVillager", function -> function
                .param("villager", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    zombie(arguments).setVillager(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Zombie.class, "setVillagerProfession", function -> function
                .param("profession", Villager.Profession.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    zombie(arguments).setVillagerProfession(
                            NovaTypeSupport.argument(arguments, 1, Villager.Profession.class));
                    return null;
                }));
    }

    private static Zombie zombie(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Zombie.class);
    }
}
