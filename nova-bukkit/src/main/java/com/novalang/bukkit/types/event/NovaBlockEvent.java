package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/** 常用方块事件的 Spigot 1.12.2 别名。 */
public final class NovaBlockEvent {
    private NovaBlockEvent() { }

    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableBlock = JavaTypeRef.javaType(Block.class).nullable();
        b.extension(BlockEvent.class, "block", f -> f.returns(Block.class).invoke(a -> blockEvent(a).getBlock()));
        b.extension(BlockBreakEvent.class, "player", f -> f.returns(Player.class).invoke(a -> breakEvent(a).getPlayer()));
        b.extension(BlockBreakEvent.class, "dropItems", f -> f.returns(Boolean.class).invoke(a -> breakEvent(a).isDropItems()));
        b.extension(BlockBreakEvent.class, "setDropItems", f -> f.param("drop", Boolean.class).returns(Void.TYPE).invoke(a -> { breakEvent(a).setDropItems(arg(a, 1, Boolean.class)); return null; }));
        b.extension(BlockPlaceEvent.class, "player", f -> f.returns(Player.class).invoke(a -> place(a).getPlayer()));
        b.extension(BlockPlaceEvent.class, "blockPlaced", f -> f.returns(Block.class).invoke(a -> place(a).getBlockPlaced()));
        b.extension(BlockPlaceEvent.class, "blockReplacedState", f -> f.returns(BlockState.class).invoke(a -> place(a).getBlockReplacedState()));
        b.extension(BlockPlaceEvent.class, "blockAgainst", f -> f.returns(Block.class).invoke(a -> place(a).getBlockAgainst()));
        b.extension(BlockPlaceEvent.class, "itemInHand", f -> f.returns(ItemStack.class).invoke(a -> place(a).getItemInHand()));
        b.extension(BlockPlaceEvent.class, "hand", f -> f.returns(EquipmentSlot.class).invoke(a -> place(a).getHand()));
        b.extension(BlockPlaceEvent.class, "canBuild", f -> f.returns(Boolean.class).invoke(a -> place(a).canBuild()));
        b.extension(BlockPlaceEvent.class, "setBuild", f -> f.param("canBuild", Boolean.class).returns(Void.TYPE).invoke(a -> { place(a).setBuild(arg(a, 1, Boolean.class)); return null; }));
        b.extension(BlockFromToEvent.class, "face", f -> f.returns(BlockFace.class).invoke(a -> fromTo(a).getFace()));
        b.extension(BlockFromToEvent.class, "toBlock", f -> f.returns(Block.class).invoke(a -> fromTo(a).getToBlock()));
        b.extension(BlockRedstoneEvent.class, "oldCurrent", f -> f.returns(Integer.class).invoke(a -> redstone(a).getOldCurrent()));
        b.extension(BlockRedstoneEvent.class, "newCurrent", f -> f.returns(Integer.class).invoke(a -> redstone(a).getNewCurrent()));
        b.extension(BlockRedstoneEvent.class, "setNewCurrent", f -> f.param("current", Integer.class).returns(Void.TYPE).invoke(a -> { redstone(a).setNewCurrent(arg(a, 1, Integer.class)); return null; }));
    }

    private static BlockEvent blockEvent(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockEvent.class); }
    private static BlockBreakEvent breakEvent(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockBreakEvent.class); }
    private static BlockPlaceEvent place(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockPlaceEvent.class); }
    private static BlockFromToEvent fromTo(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockFromToEvent.class); }
    private static BlockRedstoneEvent redstone(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockRedstoneEvent.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
