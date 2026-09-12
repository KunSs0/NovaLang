package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.inventory.PaperInventoryReflection;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.EquipmentSlot;
import java.lang.reflect.Method;

/** 1.20.5+ EquipmentSlotGroup 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.EquipmentSlotGroup"}, methods = {"org.bukkit.inventory.EquipmentSlotGroup#test"})
public final class NovaEquipmentSlotGroup {
    private static final String TYPE = "org.bukkit.inventory.EquipmentSlotGroup";
    private NovaEquipmentSlotGroup() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperInventoryReflection.type(NovaEquipmentSlotGroup.class, TYPE);
        Method test = PaperInventoryReflection.method(type, "test", EquipmentSlot.class);
        Method toString = PaperInventoryReflection.method(type, "toString");
        builder.extension(type, "test", function -> function.param("slot", EquipmentSlot.class).returns(Boolean.class).invoke(arguments -> PaperInventoryReflection.invoke(test, arguments[0], arguments[1])));
        builder.extension(type, "toString", function -> function.returns(String.class).invoke(arguments -> PaperInventoryReflection.invoke(toString, arguments[0])));
    }
}
