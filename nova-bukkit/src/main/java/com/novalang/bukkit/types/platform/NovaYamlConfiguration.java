package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import java.io.File;
import java.io.Reader;

/** Spigot 1.12.2 YAML 配置的 Fluxon 专用别名与协变返回类型。 */
@Requires(classes = {
        "org.bukkit.configuration.file.YamlConfiguration",
        "org.bukkit.configuration.file.YamlConfigurationOptions"
})
public final class NovaYamlConfiguration {

    private NovaYamlConfiguration() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(YamlConfiguration.class, "loadConfiguration", function -> function
                .param("file", File.class)
                .returns(YamlConfiguration.class)
                .invoke(arguments -> YamlConfiguration.loadConfiguration(
                        NovaTypeSupport.argument(arguments, 1, File.class))));
        builder.extension(YamlConfiguration.class, "loadConfiguration", function -> function
                .param("reader", Reader.class)
                .returns(YamlConfiguration.class)
                .invoke(arguments -> YamlConfiguration.loadConfiguration(
                        NovaTypeSupport.argument(arguments, 1, Reader.class))));
        builder.extension(YamlConfiguration.class, "options", function -> function
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> yamlConfiguration(arguments).options()));

        builder.extension(YamlConfigurationOptions.class, "configuration", function -> function
                .returns(YamlConfiguration.class)
                .invoke(arguments -> options(arguments).configuration()));
        builder.extension(YamlConfigurationOptions.class, "copyDefaults", function -> function
                .param("enabled", Boolean.class)
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> options(arguments).copyDefaults(
                        NovaTypeSupport.argument(arguments, 1, Boolean.class))));
        builder.extension(YamlConfigurationOptions.class, "pathSeparator", function -> function
                .param("separator", Integer.class)
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> options(arguments).pathSeparator((char) NovaTypeSupport
                        .argument(arguments, 1, Integer.class).intValue())));
        builder.extension(YamlConfigurationOptions.class, "header", function -> function
                .param("header", String.class)
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> options(arguments).header(
                        NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(YamlConfigurationOptions.class, "copyHeader", function -> function
                .param("enabled", Boolean.class)
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> options(arguments).copyHeader(
                        NovaTypeSupport.argument(arguments, 1, Boolean.class))));
        builder.extension(YamlConfigurationOptions.class, "indent", function -> function
                .returns(Integer.class)
                .invoke(arguments -> options(arguments).indent()));
        builder.extension(YamlConfigurationOptions.class, "indent", function -> function
                .param("indent", Integer.class)
                .returns(YamlConfigurationOptions.class)
                .invoke(arguments -> options(arguments).indent(
                        NovaTypeSupport.argument(arguments, 1, Integer.class))));
    }

    private static YamlConfiguration yamlConfiguration(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, YamlConfiguration.class);
    }

    private static YamlConfigurationOptions options(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, YamlConfigurationOptions.class);
    }
}
