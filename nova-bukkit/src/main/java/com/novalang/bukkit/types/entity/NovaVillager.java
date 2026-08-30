package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Villager;

/** 1.12 村民职业、职业分支与绿宝石财富的可选编译期别名。 */
@Requires(classes = {"org.bukkit.entity.Villager"})
public final class NovaVillager {

    private NovaVillager() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableCareer = JavaTypeRef.javaType(Villager.Career.class).nullable();
        builder.extension(Villager.class, "profession", function -> function
                .returns(Villager.Profession.class)
                .invoke(arguments -> villager(arguments).getProfession()));
        builder.extension(Villager.class, "setProfession", function -> function
                .param("profession", Villager.Profession.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    villager(arguments).setProfession(argument(arguments, 1, Villager.Profession.class));
                    return null;
                }));
        builder.extension(Villager.class, "career", function -> function
                .returns(nullableCareer)
                .invoke(arguments -> villager(arguments).getCareer()));
        builder.extension(Villager.class, "setCareer", function -> function
                .param("career", nullableCareer)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    villager(arguments).setCareer(argument(arguments, 1, Villager.Career.class));
                    return null;
                }));
        builder.extension(Villager.class, "setCareer", function -> function
                .param("career", nullableCareer)
                .param("resetTrades", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    villager(arguments).setCareer(
                            argument(arguments, 1, Villager.Career.class),
                            argument(arguments, 2, Boolean.class));
                    return null;
                }));
        builder.extension(Villager.class, "riches", function -> function
                .returns(Integer.class)
                .invoke(arguments -> villager(arguments).getRiches()));
        builder.extension(Villager.class, "setRiches", function -> function
                .param("riches", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    villager(arguments).setRiches(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static Villager villager(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Villager.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
