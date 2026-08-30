package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Enderman;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 末影人的 Fluxon 函数别名。 */
public final class NovaEnderman {

    private NovaEnderman() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Enderman.class, "carriedMaterial", function -> function.returns(JavaTypeRef.javaType(MaterialData.class).nullable())
                .invoke(arguments -> enderman(arguments).getCarriedMaterial()));
        builder.extension(Enderman.class, "setCarriedMaterial", function -> function.param("material", JavaTypeRef.javaType(MaterialData.class).nullable()).returns(Void.TYPE)
                .invoke(arguments -> {
                    enderman(arguments).setCarriedMaterial(argument(arguments, 1, MaterialData.class));
                    return null;
                }));
    }

    private static Enderman enderman(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Enderman.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
