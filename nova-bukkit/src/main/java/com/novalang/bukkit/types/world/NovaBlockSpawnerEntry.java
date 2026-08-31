package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.21+ SpawnerEntry 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.block.spawner.SpawnerEntry",
        "org.bukkit.block.spawner.SpawnerEntry$Equipment",
        "org.bukkit.block.spawner.SpawnRule",
        "org.bukkit.entity.EntitySnapshot"}, methods = {
        "org.bukkit.block.spawner.SpawnerEntry#getSnapshot",
        "org.bukkit.block.spawner.SpawnerEntry#setSnapshot",
        "org.bukkit.block.spawner.SpawnerEntry#getSpawnWeight",
        "org.bukkit.block.spawner.SpawnerEntry#setSpawnWeight",
        "org.bukkit.block.spawner.SpawnerEntry#getSpawnRule",
        "org.bukkit.block.spawner.SpawnerEntry#setSpawnRule",
        "org.bukkit.block.spawner.SpawnerEntry#getEquipment",
        "org.bukkit.block.spawner.SpawnerEntry#setEquipment"})
public final class NovaBlockSpawnerEntry {

    private static final String SPAWNER_ENTRY = "org.bukkit.block.spawner.SpawnerEntry";
    private static final String EQUIPMENT = "org.bukkit.block.spawner.SpawnerEntry$Equipment";
    private static final String SPAWN_RULE = "org.bukkit.block.spawner.SpawnRule";
    private static final String ENTITY_SNAPSHOT = "org.bukkit.entity.EntitySnapshot";

    private NovaBlockSpawnerEntry() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> entryType = NovaBlockDataReflection.type(NovaBlockSpawnerEntry.class, SPAWNER_ENTRY);
        Class<?> equipmentType = NovaBlockDataReflection.type(NovaBlockSpawnerEntry.class, EQUIPMENT);
        Class<?> spawnRuleType = NovaBlockDataReflection.type(NovaBlockSpawnerEntry.class, SPAWN_RULE);
        Class<?> snapshotType = NovaBlockDataReflection.type(NovaBlockSpawnerEntry.class, ENTITY_SNAPSHOT);
        Method getSnapshot = NovaBlockDataReflection.method(entryType, "getSnapshot");
        Method setSnapshot = NovaBlockDataReflection.method(entryType, "setSnapshot", snapshotType);
        Method getSpawnWeight = NovaBlockDataReflection.method(entryType, "getSpawnWeight");
        Method setSpawnWeight = NovaBlockDataReflection.method(entryType, "setSpawnWeight", Integer.TYPE);
        Method getSpawnRule = NovaBlockDataReflection.method(entryType, "getSpawnRule");
        Method setSpawnRule = NovaBlockDataReflection.method(entryType, "setSpawnRule", spawnRuleType);
        Method getEquipment = NovaBlockDataReflection.method(entryType, "getEquipment");
        Method setEquipment = NovaBlockDataReflection.method(entryType, "setEquipment", equipmentType);

        builder.extension(entryType, "snapshot", function -> function.returns(JavaTypeRef.javaType(snapshotType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getSnapshot, arguments[0])));
        builder.extension(entryType, "setSnapshot", function -> function.param("snapshot", snapshotType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setSnapshot, arguments[0], arguments[1])));
        builder.extension(entryType, "spawnWeight", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getSpawnWeight, arguments[0])));
        builder.extension(entryType, "setSpawnWeight", function -> function.param("weight", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setSpawnWeight, arguments[0], arguments[1])));
        builder.extension(entryType, "spawnRule", function -> function.returns(JavaTypeRef.javaType(spawnRuleType).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getSpawnRule, arguments[0])));
        builder.extension(entryType, "setSpawnRule", function -> function.param("rule", JavaTypeRef.javaType(spawnRuleType).nullable()).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setSpawnRule, arguments[0], arguments[1])));
        builder.extension(entryType, "equipment", function -> function.returns(JavaTypeRef.javaType(equipmentType).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getEquipment, arguments[0])));
        builder.extension(entryType, "setEquipment", function -> function.param("equipment", JavaTypeRef.javaType(equipmentType).nullable()).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setEquipment, arguments[0], arguments[1])));
    }
}
