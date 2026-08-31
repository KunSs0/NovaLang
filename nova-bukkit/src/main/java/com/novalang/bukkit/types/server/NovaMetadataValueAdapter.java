package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

/** MetadataValueAdapter 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.metadata.MetadataValueAdapter"})
public final class NovaMetadataValueAdapter {

    private NovaMetadataValueAdapter() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MetadataValueAdapter.class, "owningPlugin", function -> function
                .returns(Plugin.class)
                .invoke(arguments -> value(arguments).getOwningPlugin()));
        builder.extension(MetadataValueAdapter.class, "asInt", function -> function
                .returns(Integer.class).invoke(arguments -> value(arguments).asInt()));
        builder.extension(MetadataValueAdapter.class, "asFloat", function -> function
                .returns(Float.class).invoke(arguments -> value(arguments).asFloat()));
        builder.extension(MetadataValueAdapter.class, "asDouble", function -> function
                .returns(Double.class).invoke(arguments -> value(arguments).asDouble()));
        builder.extension(MetadataValueAdapter.class, "asLong", function -> function
                .returns(Long.class).invoke(arguments -> value(arguments).asLong()));
        builder.extension(MetadataValueAdapter.class, "asShort", function -> function
                .returns(Short.class).invoke(arguments -> value(arguments).asShort()));
        builder.extension(MetadataValueAdapter.class, "asByte", function -> function
                .returns(Byte.class).invoke(arguments -> value(arguments).asByte()));
        builder.extension(MetadataValueAdapter.class, "asBoolean", function -> function
                .returns(Boolean.class).invoke(arguments -> value(arguments).asBoolean()));
        builder.extension(MetadataValueAdapter.class, "asString", function -> function
                .returns(String.class).invoke(arguments -> value(arguments).asString()));
    }

    private static MetadataValueAdapter value(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MetadataValueAdapter.class);
    }
}
