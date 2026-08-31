package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.CoalType;

/** Spigot 1.12.2 CoalType 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaCoalType {

    private NovaCoalType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CoalType.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) coalType(arguments).getData()));
        builder.extension(CoalType.class, "getByData", function -> function
                .param("data", Integer.class)
                .returns(JavaTypeRef.javaType(CoalType.class).nullable())
                .invoke(arguments -> CoalType.getByData(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static CoalType coalType(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CoalType.class);
    }
}
