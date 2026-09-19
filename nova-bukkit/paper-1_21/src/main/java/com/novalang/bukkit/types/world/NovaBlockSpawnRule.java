package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;
import java.util.Map;

/** 1.21+ SpawnRule 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.spawner.SpawnRule"}, methods = {
        "org.bukkit.block.spawner.SpawnRule#getMinBlockLight",
        "org.bukkit.block.spawner.SpawnRule#setMinBlockLight",
        "org.bukkit.block.spawner.SpawnRule#getMaxBlockLight",
        "org.bukkit.block.spawner.SpawnRule#setMaxBlockLight",
        "org.bukkit.block.spawner.SpawnRule#getMinSkyLight",
        "org.bukkit.block.spawner.SpawnRule#setMinSkyLight",
        "org.bukkit.block.spawner.SpawnRule#getMaxSkyLight",
        "org.bukkit.block.spawner.SpawnRule#setMaxSkyLight",
        "org.bukkit.block.spawner.SpawnRule#clone",
        "org.bukkit.block.spawner.SpawnRule#deserialize",
        "org.bukkit.block.spawner.SpawnRule#serialize"})
public final class NovaBlockSpawnRule {

    private static final String SPAWN_RULE = "org.bukkit.block.spawner.SpawnRule";

    private NovaBlockSpawnRule() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> ruleType = NovaBlockDataReflection.type(NovaBlockSpawnRule.class, SPAWN_RULE);
        Method getMinBlockLight = NovaBlockDataReflection.method(ruleType, "getMinBlockLight");
        Method setMinBlockLight = NovaBlockDataReflection.method(ruleType, "setMinBlockLight", Integer.TYPE);
        Method getMaxBlockLight = NovaBlockDataReflection.method(ruleType, "getMaxBlockLight");
        Method setMaxBlockLight = NovaBlockDataReflection.method(ruleType, "setMaxBlockLight", Integer.TYPE);
        Method getMinSkyLight = NovaBlockDataReflection.method(ruleType, "getMinSkyLight");
        Method setMinSkyLight = NovaBlockDataReflection.method(ruleType, "setMinSkyLight", Integer.TYPE);
        Method getMaxSkyLight = NovaBlockDataReflection.method(ruleType, "getMaxSkyLight");
        Method setMaxSkyLight = NovaBlockDataReflection.method(ruleType, "setMaxSkyLight", Integer.TYPE);
        Method clone = NovaBlockDataReflection.method(ruleType, "clone");
        Method deserialize = NovaBlockDataReflection.method(ruleType, "deserialize", Map.class);
        Method serialize = NovaBlockDataReflection.method(ruleType, "serialize");
        JavaTypeRef rule = JavaTypeRef.javaType(ruleType);
        JavaTypeRef serialized = JavaTypeRef.mapOf(JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class));

        registerLightRange(builder, ruleType, "minBlockLight", "setMinBlockLight", getMinBlockLight, setMinBlockLight);
        registerLightRange(builder, ruleType, "maxBlockLight", "setMaxBlockLight", getMaxBlockLight, setMaxBlockLight);
        registerLightRange(builder, ruleType, "minSkyLight", "setMinSkyLight", getMinSkyLight, setMinSkyLight);
        registerLightRange(builder, ruleType, "maxSkyLight", "setMaxSkyLight", getMaxSkyLight, setMaxSkyLight);
        builder.extension(ruleType, "clone", function -> function.returns(rule)
                .invoke(arguments -> NovaBlockDataReflection.invoke(clone, arguments[0])));
        builder.extension(ruleType, "deserialize", function -> function.param("data", serialized).returns(rule)
                .invoke(arguments -> NovaBlockDataReflection.invoke(deserialize, null, arguments[1])));
        builder.extension(ruleType, "serialize", function -> function.returns(serialized)
                .invoke(arguments -> NovaBlockDataReflection.invoke(serialize, arguments[0])));
    }

    private static void registerLightRange(JavaTypes.Builder builder, Class<?> ruleType, String getterName,
                                           String setterName, Method getter, Method setter) {
        builder.extension(ruleType, getterName, function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getter, arguments[0])));
        builder.extension(ruleType, setterName, function -> function.param("value", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setter, arguments[0], arguments[1])));
    }
}
