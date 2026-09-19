package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;

/** Spigot 1.12.2 已点燃 TNT 的 Fluxon 函数别名。 */
public final class NovaTNTPrimed {

    private NovaTNTPrimed() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(TNTPrimed.class, "setFuseTicks", function -> function.param("ticks", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            tnt(arguments).setFuseTicks(argument(arguments, 1, Integer.class));
            return null;
        }));
        builder.extension(TNTPrimed.class, "fuseTicks", function -> function.returns(Integer.class)
                .invoke(arguments -> tnt(arguments).getFuseTicks()));
        builder.extension(TNTPrimed.class, "source", function -> function.returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> tnt(arguments).getSource()));
    }

    private static TNTPrimed tnt(Object[] arguments) {
        return argument(arguments, 0, TNTPrimed.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
