package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/** 玩家桶事件基础类型的 Spigot 1.12.2 别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerBucketEvent"})
public final class NovaPlayerBucketEvent {
    private NovaPlayerBucketEvent() { }
    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        b.extension(PlayerBucketEvent.class, "bucket", f -> f.returns(Material.class).invoke(a -> event(a).getBucket()));
        b.extension(PlayerBucketEvent.class, "itemStack", f -> f.returns(nullableItem).invoke(a -> event(a).getItemStack()));
        b.extension(PlayerBucketEvent.class, "setItemStack", f -> f.param("itemStack", nullableItem).returns(Void.TYPE).invoke(a -> { event(a).setItemStack(NovaTypeSupport.argument(a, 1, ItemStack.class)); return null; }));
        b.extension(PlayerBucketEvent.class, "blockClicked", f -> f.returns(Block.class).invoke(a -> event(a).getBlockClicked()));
        b.extension(PlayerBucketEvent.class, "blockFace", f -> f.returns(BlockFace.class).invoke(a -> event(a).getBlockFace()));
    }
    private static PlayerBucketEvent event(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerBucketEvent.class); }
}
