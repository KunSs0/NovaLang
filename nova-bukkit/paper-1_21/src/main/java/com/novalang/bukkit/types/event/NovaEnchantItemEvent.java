package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.enchantment.EnchantItemEvent"})
public final class NovaEnchantItemEvent {
    private NovaEnchantItemEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(EnchantItemEvent.class, "enchanter", f -> f.returns(Player.class).invoke(a -> e(a).getEnchanter()));
        b.extension(EnchantItemEvent.class, "enchantBlock", f -> f.returns(Block.class).invoke(a -> e(a).getEnchantBlock()));
        b.extension(EnchantItemEvent.class, "item", f -> f.returns(ItemStack.class).invoke(a -> e(a).getItem()));
        b.extension(EnchantItemEvent.class, "expLevelCost", f -> f.returns(Integer.class).invoke(a -> e(a).getExpLevelCost()));
        b.extension(EnchantItemEvent.class, "setExpLevelCost", f -> f.param("cost", Integer.class).returns(Void.TYPE).invoke(a -> { e(a).setExpLevelCost(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        b.extension(EnchantItemEvent.class, "whichButton", f -> f.returns(Integer.class).invoke(a -> e(a).whichButton()));
    }
    private static EnchantItemEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, EnchantItemEvent.class); }
}
