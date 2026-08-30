package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

/** Spigot 1.12.2 metadata API 别名。 */
final class NovaMetadata {

    private NovaMetadata() {
    }

    @SuppressWarnings("unchecked")
    static void register(JavaTypes.Builder b) {
        b.extension(Metadatable.class, "setMetadata", f -> f.param("metadataKey", String.class).param("newMetadataValue", MetadataValue.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Metadatable.class).setMetadata(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, MetadataValue.class)); return null; }));
        b.extension(Metadatable.class, "getMetadata", f -> f.param("metadataKey", String.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(MetadataValue.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Metadatable.class).getMetadata(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Metadatable.class, "hasMetadata", f -> f.param("metadataKey", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Metadatable.class).hasMetadata(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Metadatable.class, "removeMetadata", f -> f.param("metadataKey", String.class).param("owningPlugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Metadatable.class).removeMetadata(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Plugin.class)); return null; }));
        b.extension(MetadataValue.class, "value", f -> f.returns(Object.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).value()));
        b.extension(MetadataValue.class, "asInt", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asInt()));
        b.extension(MetadataValue.class, "asFloat", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asFloat()));
        b.extension(MetadataValue.class, "asDouble", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asDouble()));
        b.extension(MetadataValue.class, "asLong", f -> f.returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asLong()));
        b.extension(MetadataValue.class, "asShort", f -> f.returns(Short.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asShort()));
        b.extension(MetadataValue.class, "asByte", f -> f.returns(Byte.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asByte()));
        b.extension(MetadataValue.class, "asBoolean", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asBoolean()));
        b.extension(MetadataValue.class, "asString", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).asString()));
        b.extension(MetadataValue.class, "owningPlugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataValue.class).getOwningPlugin()));
        b.extension(MetadataValue.class, "invalidate", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, MetadataValue.class).invalidate(); return null; }));
        b.extension(MetadataStore.class, "setMetadata", f -> f.param("subject", Object.class).param("metadataKey", String.class).param("newMetadataValue", MetadataValue.class).invoke(a -> { NovaTypeSupport.argument(a, 0, MetadataStore.class).setMetadata(NovaTypeSupport.argument(a, 1, Object.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, MetadataValue.class)); return null; }));
        b.extension(MetadataStore.class, "getMetadata", f -> f.param("subject", Object.class).param("metadataKey", String.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(MetadataValue.class))).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataStore.class).getMetadata(NovaTypeSupport.argument(a, 1, Object.class), NovaTypeSupport.argument(a, 2, String.class))));
        b.extension(MetadataStore.class, "hasMetadata", f -> f.param("subject", Object.class).param("metadataKey", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, MetadataStore.class).hasMetadata(NovaTypeSupport.argument(a, 1, Object.class), NovaTypeSupport.argument(a, 2, String.class))));
        b.extension(MetadataStore.class, "removeMetadata", f -> f.param("subject", Object.class).param("metadataKey", String.class).param("owningPlugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, MetadataStore.class).removeMetadata(NovaTypeSupport.argument(a, 1, Object.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, Plugin.class)); return null; }));
        b.extension(MetadataStore.class, "invalidateAll", f -> f.param("owningPlugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, MetadataStore.class).invalidateAll(NovaTypeSupport.argument(a, 1, Plugin.class)); return null; }));
    }
}
