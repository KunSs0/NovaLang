package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/** Spigot 1.12.2 实体装备栏及各槽位掉落率别名。 */
final class NovaEntityEquipment {

    private NovaEntityEquipment() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef items = JavaTypeRef.javaType(ItemStack[].class);

        registerItemSlot(builder, "itemInMainHand", "setItemInMainHand", item, 0);
        registerItemSlot(builder, "itemInOffHand", "setItemInOffHand", item, 1);
        registerItemSlot(builder, "itemInHand", "setItemInHand", item, 2);
        registerItemSlot(builder, "helmet", "setHelmet", item, 3);
        registerItemSlot(builder, "chestplate", "setChestplate", item, 4);
        registerItemSlot(builder, "leggings", "setLeggings", item, 5);
        registerItemSlot(builder, "boots", "setBoots", item, 6);

        builder.extension(EntityEquipment.class, "armorContents",
                function -> function.returns(items).invoke(arguments -> equipment(arguments).getArmorContents()));
        builder.extension(EntityEquipment.class, "setArmorContents",
                function -> function.param("items", items).invoke(arguments -> {
                    equipment(arguments).setArmorContents(argument(arguments, 1, ItemStack[].class));
                    return null;
                }));
        builder.extension(EntityEquipment.class, "clear", function -> function.invoke(arguments -> {
            equipment(arguments).clear();
            return null;
        }));

        registerDropChance(builder, "itemInHandDropChance", "setItemInHandDropChance", 0);
        registerDropChance(builder, "itemInMainHandDropChance", "setItemInMainHandDropChance", 1);
        registerDropChance(builder, "itemInOffHandDropChance", "setItemInOffHandDropChance", 2);
        registerDropChance(builder, "helmetDropChance", "setHelmetDropChance", 3);
        registerDropChance(builder, "chestplateDropChance", "setChestplateDropChance", 4);
        registerDropChance(builder, "leggingsDropChance", "setLeggingsDropChance", 5);
        registerDropChance(builder, "bootsDropChance", "setBootsDropChance", 6);

        builder.extension(EntityEquipment.class, "holder",
                function -> function.returns(Entity.class).invoke(arguments -> equipment(arguments).getHolder()));
    }

    private static void registerItemSlot(JavaTypes.Builder builder,
                                         String getterName,
                                         String setterName,
                                         JavaTypeRef itemType,
                                         int slot) {
        builder.extension(EntityEquipment.class, getterName,
                function -> function.returns(itemType).invoke(arguments -> getItem(equipment(arguments), slot)));
        builder.extension(EntityEquipment.class, setterName,
                function -> function.param("item", itemType).invoke(arguments -> {
                    setItem(equipment(arguments), slot, argument(arguments, 1, ItemStack.class));
                    return null;
                }));
    }

    private static ItemStack getItem(EntityEquipment equipment, int slot) {
        switch (slot) {
            case 0:
                return equipment.getItemInMainHand();
            case 1:
                return equipment.getItemInOffHand();
            case 2:
                return equipment.getItemInHand();
            case 3:
                return equipment.getHelmet();
            case 4:
                return equipment.getChestplate();
            case 5:
                return equipment.getLeggings();
            default:
                return equipment.getBoots();
        }
    }

    private static void setItem(EntityEquipment equipment, int slot, ItemStack item) {
        switch (slot) {
            case 0:
                equipment.setItemInMainHand(item);
                return;
            case 1:
                equipment.setItemInOffHand(item);
                return;
            case 2:
                equipment.setItemInHand(item);
                return;
            case 3:
                equipment.setHelmet(item);
                return;
            case 4:
                equipment.setChestplate(item);
                return;
            case 5:
                equipment.setLeggings(item);
                return;
            default:
                equipment.setBoots(item);
        }
    }

    private static void registerDropChance(JavaTypes.Builder builder,
                                           String getterName,
                                           String setterName,
                                           int slot) {
        builder.extension(EntityEquipment.class, getterName,
                function -> function.returns(Float.class).invoke(arguments -> getDropChance(equipment(arguments), slot)));
        builder.extension(EntityEquipment.class, setterName,
                function -> function.param("chance", Float.class).invoke(arguments -> {
                    setDropChance(equipment(arguments), slot, argument(arguments, 1, Float.class));
                    return null;
                }));
    }

    private static float getDropChance(EntityEquipment equipment, int slot) {
        switch (slot) {
            case 0:
                return equipment.getItemInHandDropChance();
            case 1:
                return equipment.getItemInMainHandDropChance();
            case 2:
                return equipment.getItemInOffHandDropChance();
            case 3:
                return equipment.getHelmetDropChance();
            case 4:
                return equipment.getChestplateDropChance();
            case 5:
                return equipment.getLeggingsDropChance();
            default:
                return equipment.getBootsDropChance();
        }
    }

    private static void setDropChance(EntityEquipment equipment, int slot, float chance) {
        switch (slot) {
            case 0:
                equipment.setItemInHandDropChance(chance);
                return;
            case 1:
                equipment.setItemInMainHandDropChance(chance);
                return;
            case 2:
                equipment.setItemInOffHandDropChance(chance);
                return;
            case 3:
                equipment.setHelmetDropChance(chance);
                return;
            case 4:
                equipment.setChestplateDropChance(chance);
                return;
            case 5:
                equipment.setLeggingsDropChance(chance);
                return;
            default:
                equipment.setBootsDropChance(chance);
        }
    }

    private static EntityEquipment equipment(Object[] arguments) {
        return argument(arguments, 0, EntityEquipment.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
