package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import java.io.File;
import java.util.Map;
import java.util.Set;

/** Bukkit Configuration/YAML/serialization 的 Fluxon 别名。 */
final class NovaConfiguration {

    private NovaConfiguration() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(ConfigurationSection.class, "currentPath", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getCurrentPath()));
        b.extension(ConfigurationSection.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getName()));
        b.extension(ConfigurationSection.class, "root", f -> f.returns(JavaTypeRef.javaType(Configuration.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getRoot()));
        b.extension(ConfigurationSection.class, "parent", f -> f.returns(JavaTypeRef.javaType(ConfigurationSection.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getParent()));
        b.extension(ConfigurationSection.class, "getKeys", f -> f.param("deep", Boolean.class).returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getKeys(NovaTypeSupport.argument(a, 1, Boolean.class))));
        b.extension(ConfigurationSection.class, "contains", f -> f.param("path", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).contains(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "isSet", f -> f.param("path", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).isSet(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "get", f -> f.param("path", String.class).returns(Object.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).get(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "set", f -> f.param("path", String.class).param("value", Object.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ConfigurationSection.class).set(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Object.class)); return null; }));
        b.extension(ConfigurationSection.class, "createSection", f -> f.param("path", String.class).returns(ConfigurationSection.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).createSection(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getString", f -> f.param("path", String.class).returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getString(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getInt", f -> f.param("path", String.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getInt(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getBoolean", f -> f.param("path", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getBoolean(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getDouble", f -> f.param("path", String.class).returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getDouble(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getLong", f -> f.param("path", String.class).returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getLong(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(ConfigurationSection.class, "getStringList", f -> f.param("path", String.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSection.class).getStringList(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Configuration.class, "defaults", f -> f.returns(JavaTypeRef.javaType(Configuration.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Configuration.class).getDefaults()));
        b.extension(Configuration.class, "options", f -> f.returns(ConfigurationOptions.class).invoke(a -> NovaTypeSupport.argument(a, 0, Configuration.class).options()));
        b.extension(FileConfiguration.class, "saveToString", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, FileConfiguration.class).saveToString()));
        b.extension(ConfigurationSerializable.class, "serialize", f -> f.returns(Map.class).invoke(a -> NovaTypeSupport.argument(a, 0, ConfigurationSerializable.class).serialize()));
    }
}
