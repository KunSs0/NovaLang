package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.WorldType;

/** Spigot 1.12.2 WorldType 的 Fluxon 函数别名。 */
public final class NovaWorldType {

    private NovaWorldType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldType.class, "name", function -> function
                .returns(String.class)
                .invoke(arguments -> worldType(arguments).getName()));
        builder.extension(WorldType.class, "getByName", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(WorldType.class).nullable())
                .invoke(arguments -> WorldType.getByName(
                        NovaTypeSupport.argument(arguments, 1, String.class))));
    }

    private static WorldType worldType(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, WorldType.class);
    }
}
