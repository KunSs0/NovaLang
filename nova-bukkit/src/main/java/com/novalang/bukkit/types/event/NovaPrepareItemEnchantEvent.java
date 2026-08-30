package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.enchantment.PrepareItemEnchantEvent"})
public final class NovaPrepareItemEnchantEvent {
    private NovaPrepareItemEnchantEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(PrepareItemEnchantEvent.class, "enchanter", f -> f.returns(Player.class).invoke(a -> e(a).getEnchanter()));
        b.extension(PrepareItemEnchantEvent.class, "enchantBlock", f -> f.returns(Block.class).invoke(a -> e(a).getEnchantBlock()));
        b.extension(PrepareItemEnchantEvent.class, "item", f -> f.returns(ItemStack.class).invoke(a -> e(a).getItem()));
        b.extension(PrepareItemEnchantEvent.class, "expLevelCostsOffered", f -> f.returns(int[].class).invoke(a -> e(a).getExpLevelCostsOffered()));
        b.extension(PrepareItemEnchantEvent.class, "offers", f -> f.returns(EnchantmentOffer[].class).invoke(a -> e(a).getOffers()));
        b.extension(PrepareItemEnchantEvent.class, "enchantmentBonus", f -> f.returns(Integer.class).invoke(a -> e(a).getEnchantmentBonus()));
    }
    private static PrepareItemEnchantEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, PrepareItemEnchantEvent.class); }
}
