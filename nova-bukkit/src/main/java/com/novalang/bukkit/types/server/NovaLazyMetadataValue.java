package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.metadata.LazyMetadataValue;

/** LazyMetadataValue 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.metadata.LazyMetadataValue"})
public final class NovaLazyMetadataValue {

    private NovaLazyMetadataValue() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(LazyMetadataValue.class, "value", function -> function
                .returns(JavaTypeRef.javaType(Object.class).nullable())
                .invoke(arguments -> value(arguments).value()));
    }

    private static LazyMetadataValue value(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LazyMetadataValue.class);
    }
}
