package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Method;

/** 1.20.5+ EquipmentSlotGroup 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.EquipmentSlotGroup"}, methods = {"org.bukkit.inventory.EquipmentSlotGroup#test"})
public final class NovaEquipmentSlotGroup {
    private static final String TYPE = "org.bukkit.inventory.EquipmentSlotGroup";
    private NovaEquipmentSlotGroup() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaEquipmentSlotGroup.class, TYPE);
        Method test = NovaInventoryReflection.method(type, "test", EquipmentSlot.class);
        Method toString = NovaInventoryReflection.method(type, "toString");
        builder.extension(type, "test", function -> function.param("slot", EquipmentSlot.class).returns(Boolean.class).invoke(arguments -> NovaInventoryReflection.invoke(test, arguments[0], arguments[1])));
        builder.extension(type, "toString", function -> function.returns(String.class).invoke(arguments -> NovaInventoryReflection.invoke(toString, arguments[0])));
    }
}
