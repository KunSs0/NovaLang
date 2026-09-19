package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.21+ SpawnerEntry.Equipment 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.block.spawner.SpawnerEntry$Equipment",
        "org.bukkit.loot.LootTable"}, methods = {
        "org.bukkit.block.spawner.SpawnerEntry$Equipment#getEquipmentLootTable",
        "org.bukkit.block.spawner.SpawnerEntry$Equipment#setEquipmentLootTable"})
public final class NovaBlockSpawnerEquipment {

    private static final String EQUIPMENT = "org.bukkit.block.spawner.SpawnerEntry$Equipment";
    private static final String LOOT_TABLE = "org.bukkit.loot.LootTable";

    private NovaBlockSpawnerEquipment() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> equipmentType = NovaBlockDataReflection.type(NovaBlockSpawnerEquipment.class, EQUIPMENT);
        Class<?> lootTableType = NovaBlockDataReflection.type(NovaBlockSpawnerEquipment.class, LOOT_TABLE);
        Method getEquipmentLootTable = NovaBlockDataReflection.method(equipmentType, "getEquipmentLootTable");
        Method setEquipmentLootTable = NovaBlockDataReflection.method(equipmentType, "setEquipmentLootTable", lootTableType);

        builder.extension(equipmentType, "equipmentLootTable", function -> function
                .returns(JavaTypeRef.javaType(lootTableType).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getEquipmentLootTable, arguments[0])));
        builder.extension(equipmentType, "setEquipmentLootTable", function -> function
                .param("lootTable", JavaTypeRef.javaType(lootTableType).nullable())
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setEquipmentLootTable, arguments[0], arguments[1])));
    }
}
