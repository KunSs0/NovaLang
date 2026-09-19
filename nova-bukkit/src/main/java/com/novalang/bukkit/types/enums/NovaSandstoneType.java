package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.SandstoneType;

/** Spigot 1.12.2 SandstoneType 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaSandstoneType {

    private NovaSandstoneType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SandstoneType.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) sandstoneType(arguments).getData()));
        builder.extension(SandstoneType.class, "getByData", function -> function
                .param("data", Integer.class)
                .returns(JavaTypeRef.javaType(SandstoneType.class).nullable())
                .invoke(arguments -> SandstoneType.getByData(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static SandstoneType sandstoneType(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SandstoneType.class);
    }
}
