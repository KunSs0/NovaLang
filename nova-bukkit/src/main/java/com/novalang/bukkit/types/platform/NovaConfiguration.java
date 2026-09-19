package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Bukkit Configuration/YAML/serialization 的 Fluxon 别名。 */
final class NovaConfiguration {

    private NovaConfiguration() {
    }

    static void register(JavaTypes.Builder b) {
        registerSection(b);
        registerConfiguration(b);
        registerFileConfiguration(b);
        b.extension(ConfigurationSerializable.class, "serialize",
                function -> function.returns(JavaTypeRef.mapOf(
                                JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class)))
                        .invoke(arguments -> argument(arguments, 0, ConfigurationSerializable.class).serialize()));
    }

    private static void registerSection(JavaTypes.Builder builder) {
        JavaTypeRef nullableObject = JavaTypeRef.javaType(Object.class).nullable();
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableSection = JavaTypeRef.javaType(ConfigurationSection.class).nullable();
        JavaTypeRef nullableVector = JavaTypeRef.javaType(Vector.class).nullable();
        JavaTypeRef nullableOfflinePlayer = JavaTypeRef.javaType(OfflinePlayer.class).nullable();
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef nullableColor = JavaTypeRef.javaType(Color.class).nullable();

        builder.extension(ConfigurationSection.class, "currentPath",
                function -> function.returns(nullableString).invoke(arguments -> section(arguments).getCurrentPath()));
        builder.extension(ConfigurationSection.class, "name",
                function -> function.returns(String.class).invoke(arguments -> section(arguments).getName()));
        builder.extension(ConfigurationSection.class, "root",
                function -> function.returns(JavaTypeRef.javaType(Configuration.class).nullable())
                        .invoke(arguments -> section(arguments).getRoot()));
        builder.extension(ConfigurationSection.class, "parent",
                function -> function.returns(nullableSection).invoke(arguments -> section(arguments).getParent()));
        builder.extension(ConfigurationSection.class, "getKeys",
                function -> function.param("deep", Boolean.class)
                        .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(String.class)))
                        .invoke(arguments -> section(arguments).getKeys(argument(arguments, 1, Boolean.class))));
        builder.extension(ConfigurationSection.class, "getValues",
                function -> function.param("deep", Boolean.class)
                        .returns(JavaTypeRef.mapOf(JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class)))
                        .invoke(arguments -> section(arguments).getValues(argument(arguments, 1, Boolean.class))));
        builder.extension(ConfigurationSection.class, "contains",
                function -> function.param("path", String.class).returns(Boolean.class)
                        .invoke(arguments -> section(arguments).contains(argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "contains",
                function -> function.param("path", String.class).param("ignoreDefault", Boolean.class)
                        .returns(Boolean.class).invoke(arguments -> section(arguments).contains(
                                argument(arguments, 1, String.class), argument(arguments, 2, Boolean.class))));
        builder.extension(ConfigurationSection.class, "isSet",
                function -> function.param("path", String.class).returns(Boolean.class)
                        .invoke(arguments -> section(arguments).isSet(argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "get",
                function -> function.param("path", String.class).returns(nullableObject)
                        .invoke(arguments -> section(arguments).get(argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "get",
                function -> function.param("path", String.class).param("default", nullableObject)
                        .returns(nullableObject).invoke(arguments -> section(arguments).get(
                                argument(arguments, 1, String.class), argument(arguments, 2, Object.class))));
        builder.extension(ConfigurationSection.class, "set",
                function -> function.param("path", String.class).param("value", nullableObject)
                        .invoke(arguments -> {
                            section(arguments).set(
                                    argument(arguments, 1, String.class), argument(arguments, 2, Object.class));
                            return null;
                        }));
        builder.extension(ConfigurationSection.class, "createSection",
                function -> function.param("path", String.class).returns(ConfigurationSection.class)
                        .invoke(arguments -> section(arguments).createSection(argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "createSection",
                function -> function.param("path", String.class).param("values", Map.class)
                        .returns(ConfigurationSection.class).invoke(arguments -> section(arguments).createSection(
                                argument(arguments, 1, String.class), argument(arguments, 2, Map.class))));

        registerScalar(builder, "getString", String.class, nullableString, 0);
        registerScalar(builder, "getInt", Integer.class, JavaTypeRef.javaType(Integer.class), 1);
        registerScalar(builder, "getBoolean", Boolean.class, JavaTypeRef.javaType(Boolean.class), 2);
        registerScalar(builder, "getDouble", Double.class, JavaTypeRef.javaType(Double.class), 3);
        registerScalar(builder, "getLong", Long.class, JavaTypeRef.javaType(Long.class), 4);

        registerList(builder, "getStringList", 0, JavaTypeRef.javaType(String.class));
        registerList(builder, "getIntegerList", 1, JavaTypeRef.javaType(Integer.class));
        registerList(builder, "getBooleanList", 2, JavaTypeRef.javaType(Boolean.class));
        registerList(builder, "getDoubleList", 3, JavaTypeRef.javaType(Double.class));
        registerList(builder, "getFloatList", 4, JavaTypeRef.javaType(Float.class));
        registerList(builder, "getLongList", 5, JavaTypeRef.javaType(Long.class));
        registerList(builder, "getByteList", 6, JavaTypeRef.javaType(Byte.class));
        registerList(builder, "getCharacterList", 7, JavaTypeRef.javaType(Character.class));
        registerList(builder, "getShortList", 8, JavaTypeRef.javaType(Short.class));

        registerValue(builder, "getVector", "isVector", Vector.class, nullableVector, 0);
        registerValue(builder, "getOfflinePlayer", "isOfflinePlayer",
                OfflinePlayer.class, nullableOfflinePlayer, 1);
        registerValue(builder, "getItemStack", "isItemStack", ItemStack.class, nullableItem, 2);
        registerValue(builder, "getColor", "isColor", Color.class, nullableColor, 3);

        builder.extension(ConfigurationSection.class, "getConfigurationSection",
                function -> function.param("path", String.class).returns(nullableSection)
                        .invoke(arguments -> section(arguments).getConfigurationSection(
                                argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "isConfigurationSection",
                function -> function.param("path", String.class).returns(Boolean.class)
                        .invoke(arguments -> section(arguments).isConfigurationSection(
                                argument(arguments, 1, String.class))));
        builder.extension(ConfigurationSection.class, "defaultSection",
                function -> function.returns(nullableSection)
                        .invoke(arguments -> section(arguments).getDefaultSection()));
        builder.extension(ConfigurationSection.class, "addDefault",
                function -> function.param("path", String.class).param("value", nullableObject)
                        .invoke(arguments -> {
                            section(arguments).addDefault(
                                    argument(arguments, 1, String.class), argument(arguments, 2, Object.class));
                            return null;
                        }));
    }

    private static void registerScalar(JavaTypes.Builder builder,
                                       String getterName,
                                       Class<?> defaultType,
                                       JavaTypeRef returnType,
                                       int scalar) {
        String predicateName = "is" + getterName.substring(3);
        builder.extension(ConfigurationSection.class, getterName,
                function -> function.param("path", String.class).returns(returnType)
                        .invoke(arguments -> getScalar(section(arguments),
                                argument(arguments, 1, String.class), null, scalar, false)));
        builder.extension(ConfigurationSection.class, getterName,
                function -> function.param("path", String.class).param("default", defaultType).returns(returnType)
                        .invoke(arguments -> getScalar(section(arguments), argument(arguments, 1, String.class),
                                arguments[2], scalar, true)));
        builder.extension(ConfigurationSection.class, predicateName,
                function -> function.param("path", String.class).returns(Boolean.class)
                        .invoke(arguments -> isScalar(section(arguments),
                                argument(arguments, 1, String.class), scalar)));
    }

    private static Object getScalar(ConfigurationSection section,
                                    String path,
                                    Object defaultValue,
                                    int scalar,
                                    boolean hasDefault) {
        switch (scalar) {
            case 0:
                return hasDefault ? section.getString(path, (String) defaultValue) : section.getString(path);
            case 1:
                return hasDefault ? section.getInt(path, (Integer) defaultValue) : section.getInt(path);
            case 2:
                return hasDefault ? section.getBoolean(path, (Boolean) defaultValue) : section.getBoolean(path);
            case 3:
                return hasDefault ? section.getDouble(path, (Double) defaultValue) : section.getDouble(path);
            default:
                return hasDefault ? section.getLong(path, (Long) defaultValue) : section.getLong(path);
        }
    }

    private static boolean isScalar(ConfigurationSection section, String path, int scalar) {
        switch (scalar) {
            case 0:
                return section.isString(path);
            case 1:
                return section.isInt(path);
            case 2:
                return section.isBoolean(path);
            case 3:
                return section.isDouble(path);
            default:
                return section.isLong(path);
        }
    }

    private static void registerList(JavaTypes.Builder builder,
                                     String name,
                                     int listType,
                                     JavaTypeRef elementType) {
        builder.extension(ConfigurationSection.class, name,
                function -> function.param("path", String.class).returns(JavaTypeRef.listOf(elementType))
                        .invoke(arguments -> getList(
                                section(arguments), argument(arguments, 1, String.class), listType)));
    }

    private static List<?> getList(ConfigurationSection section, String path, int listType) {
        switch (listType) {
            case 0:
                return section.getStringList(path);
            case 1:
                return section.getIntegerList(path);
            case 2:
                return section.getBooleanList(path);
            case 3:
                return section.getDoubleList(path);
            case 4:
                return section.getFloatList(path);
            case 5:
                return section.getLongList(path);
            case 6:
                return section.getByteList(path);
            case 7:
                return section.getCharacterList(path);
            default:
                return section.getShortList(path);
        }
    }

    private static <T> void registerValue(JavaTypes.Builder builder,
                                          String getterName,
                                          String predicateName,
                                          Class<T> valueClass,
                                          JavaTypeRef valueType,
                                          int valueKind) {
        builder.extension(ConfigurationSection.class, getterName,
                function -> function.param("path", String.class).returns(valueType)
                        .invoke(arguments -> getValue(section(arguments),
                                argument(arguments, 1, String.class), null, valueKind, false)));
        builder.extension(ConfigurationSection.class, getterName,
                function -> function.param("path", String.class).param("default", valueType).returns(valueType)
                        .invoke(arguments -> getValue(section(arguments), argument(arguments, 1, String.class),
                                argument(arguments, 2, valueClass), valueKind, true)));
        builder.extension(ConfigurationSection.class, predicateName,
                function -> function.param("path", String.class).returns(Boolean.class)
                        .invoke(arguments -> isValue(section(arguments),
                                argument(arguments, 1, String.class), valueKind)));
    }

    private static Object getValue(ConfigurationSection section,
                                   String path,
                                   Object defaultValue,
                                   int valueKind,
                                   boolean hasDefault) {
        switch (valueKind) {
            case 0:
                return hasDefault ? section.getVector(path, (Vector) defaultValue) : section.getVector(path);
            case 1:
                return hasDefault
                        ? section.getOfflinePlayer(path, (OfflinePlayer) defaultValue)
                        : section.getOfflinePlayer(path);
            case 2:
                return hasDefault ? section.getItemStack(path, (ItemStack) defaultValue) : section.getItemStack(path);
            default:
                return hasDefault ? section.getColor(path, (Color) defaultValue) : section.getColor(path);
        }
    }

    private static boolean isValue(ConfigurationSection section, String path, int valueKind) {
        switch (valueKind) {
            case 0:
                return section.isVector(path);
            case 1:
                return section.isOfflinePlayer(path);
            case 2:
                return section.isItemStack(path);
            default:
                return section.isColor(path);
        }
    }

    private static void registerConfiguration(JavaTypes.Builder builder) {
        JavaTypeRef nullableConfiguration = JavaTypeRef.javaType(Configuration.class).nullable();
        JavaTypeRef nullableObject = JavaTypeRef.javaType(Object.class).nullable();
        builder.extension(Configuration.class, "addDefault",
                function -> function.param("path", String.class).param("value", nullableObject)
                        .invoke(arguments -> {
                            configuration(arguments).addDefault(
                                    argument(arguments, 1, String.class), argument(arguments, 2, Object.class));
                            return null;
                        }));
        builder.extension(Configuration.class, "addDefaults",
                function -> function.param("defaults", Map.class).invoke(arguments -> {
                    configuration(arguments).addDefaults(argument(arguments, 1, Map.class));
                    return null;
                }));
        builder.extension(Configuration.class, "addDefaults",
                function -> function.param("defaults", Configuration.class).invoke(arguments -> {
                    configuration(arguments).addDefaults(argument(arguments, 1, Configuration.class));
                    return null;
                }));
        builder.extension(Configuration.class, "setDefaults",
                function -> function.param("defaults", nullableConfiguration).invoke(arguments -> {
                    configuration(arguments).setDefaults(argument(arguments, 1, Configuration.class));
                    return null;
                }));
        builder.extension(Configuration.class, "defaults",
                function -> function.returns(nullableConfiguration)
                        .invoke(arguments -> configuration(arguments).getDefaults()));
        builder.extension(Configuration.class, "options",
                function -> function.returns(ConfigurationOptions.class)
                        .invoke(arguments -> configuration(arguments).options()));

        builder.extension(ConfigurationOptions.class, "configuration",
                function -> function.returns(Configuration.class)
                        .invoke(arguments -> options(arguments).configuration()));
        builder.extension(ConfigurationOptions.class, "pathSeparator",
                function -> function.param("separator", Character.class).returns(ConfigurationOptions.class)
                        .invoke(arguments -> options(arguments).pathSeparator(
                                argument(arguments, 1, Character.class))));
        builder.extension(ConfigurationOptions.class, "copyDefaults",
                function -> function.returns(Boolean.class).invoke(arguments -> options(arguments).copyDefaults()));
        builder.extension(ConfigurationOptions.class, "copyDefaults",
                function -> function.param("enabled", Boolean.class).returns(ConfigurationOptions.class)
                        .invoke(arguments -> options(arguments).copyDefaults(
                                argument(arguments, 1, Boolean.class))));
    }

    private static void registerFileConfiguration(JavaTypes.Builder builder) {
        builder.extension(FileConfiguration.class, "save",
                function -> function.param("file", String.class).invoke(arguments -> {
                    fileConfiguration(arguments).save(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(FileConfiguration.class, "save",
                function -> function.param("file", File.class).invoke(arguments -> {
                    fileConfiguration(arguments).save(argument(arguments, 1, File.class));
                    return null;
                }));
        builder.extension(FileConfiguration.class, "saveToString",
                function -> function.returns(String.class)
                        .invoke(arguments -> fileConfiguration(arguments).saveToString()));
        builder.extension(FileConfiguration.class, "load",
                function -> function.param("file", String.class).invoke(arguments -> {
                    fileConfiguration(arguments).load(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(FileConfiguration.class, "load",
                function -> function.param("file", File.class).invoke(arguments -> {
                    fileConfiguration(arguments).load(argument(arguments, 1, File.class));
                    return null;
                }));
        builder.extension(FileConfiguration.class, "loadFromString",
                function -> function.param("contents", String.class).invoke(arguments -> {
                    fileConfiguration(arguments).loadFromString(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(FileConfiguration.class, "options",
                function -> function.returns(FileConfigurationOptions.class)
                        .invoke(arguments -> fileConfiguration(arguments).options()));

        builder.extension(FileConfigurationOptions.class, "configuration",
                function -> function.returns(FileConfiguration.class)
                        .invoke(arguments -> fileOptions(arguments).configuration()));
        builder.extension(FileConfigurationOptions.class, "copyDefaults",
                function -> function.param("enabled", Boolean.class).returns(FileConfigurationOptions.class)
                        .invoke(arguments -> fileOptions(arguments).copyDefaults(
                                argument(arguments, 1, Boolean.class))));
        builder.extension(FileConfigurationOptions.class, "pathSeparator",
                function -> function.param("separator", Character.class).returns(FileConfigurationOptions.class)
                        .invoke(arguments -> fileOptions(arguments).pathSeparator(
                                argument(arguments, 1, Character.class))));
        builder.extension(FileConfigurationOptions.class, "header",
                function -> function.returns(JavaTypeRef.javaType(String.class).nullable())
                        .invoke(arguments -> fileOptions(arguments).header()));
        builder.extension(FileConfigurationOptions.class, "header",
                function -> function.param("header", JavaTypeRef.javaType(String.class).nullable())
                        .returns(FileConfigurationOptions.class).invoke(arguments -> fileOptions(arguments).header(
                                argument(arguments, 1, String.class))));
        builder.extension(FileConfigurationOptions.class, "copyHeader",
                function -> function.returns(Boolean.class)
                        .invoke(arguments -> fileOptions(arguments).copyHeader()));
        builder.extension(FileConfigurationOptions.class, "copyHeader",
                function -> function.param("enabled", Boolean.class).returns(FileConfigurationOptions.class)
                        .invoke(arguments -> fileOptions(arguments).copyHeader(
                                argument(arguments, 1, Boolean.class))));
    }

    private static ConfigurationSection section(Object[] arguments) {
        return argument(arguments, 0, ConfigurationSection.class);
    }

    private static Configuration configuration(Object[] arguments) {
        return argument(arguments, 0, Configuration.class);
    }

    private static ConfigurationOptions options(Object[] arguments) {
        return argument(arguments, 0, ConfigurationOptions.class);
    }

    private static FileConfiguration fileConfiguration(Object[] arguments) {
        return argument(arguments, 0, FileConfiguration.class);
    }

    private static FileConfigurationOptions fileOptions(Object[] arguments) {
        return argument(arguments, 0, FileConfigurationOptions.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
