package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Statistic;

/** Spigot 1.12.2 Statistic 的 Fluxon 函数别名。 */
public final class NovaStatistic {

    private NovaStatistic() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Statistic.class, "type", function -> function
                .returns(Statistic.Type.class)
                .invoke(arguments -> statistic(arguments).getType()));
        builder.extension(Statistic.class, "isSubstatistic", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> statistic(arguments).isSubstatistic()));
        builder.extension(Statistic.class, "isBlock", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> statistic(arguments).isBlock()));
    }

    private static Statistic statistic(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Statistic.class);
    }
}
