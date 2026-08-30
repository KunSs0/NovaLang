package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires; import com.novalang.bukkit.types.value.NovaTypeSupport; import com.novalang.runtime.host.JavaTypes; import org.bukkit.block.Chest; import org.bukkit.inventory.Inventory;
@Requires(classes = {"org.bukkit.block.Chest"}) public final class NovaChest { private NovaChest() { } public static void register(JavaTypes.Builder b) { b.extension(Chest.class,"blockInventory",f->f.returns(Inventory.class).invoke(a->NovaTypeSupport.argument(a,0,Chest.class).getBlockInventory())); } }
