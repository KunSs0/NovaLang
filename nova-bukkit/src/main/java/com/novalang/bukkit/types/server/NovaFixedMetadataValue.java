package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.metadata.FixedMetadataValue;

/** FixedMetadataValue 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.metadata.FixedMetadataValue"})
public final class NovaFixedMetadataValue {

    private NovaFixedMetadataValue() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FixedMetadataValue.class, "invalidate", function -> function
                .invoke(arguments -> {
                    value(arguments).invalidate();
                    return null;
                }));
        builder.extension(FixedMetadataValue.class, "value", function -> function
                .returns(JavaTypeRef.javaType(Object.class).nullable())
                .invoke(arguments -> value(arguments).value()));
    }

    private static FixedMetadataValue value(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FixedMetadataValue.class);
    }
}
