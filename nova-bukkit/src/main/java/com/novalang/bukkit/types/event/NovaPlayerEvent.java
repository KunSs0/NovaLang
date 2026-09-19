package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/** 常用玩家事件的 Spigot 1.12.2 别名。 */
public final class NovaPlayerEvent {
    private NovaPlayerEvent() { }

    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef nullableBlock = JavaTypeRef.javaType(Block.class).nullable();
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        b.extension(PlayerEvent.class, "player", f -> f.returns(Player.class).invoke(a -> playerEvent(a).getPlayer()));
        b.extension(PlayerJoinEvent.class, "joinMessage", f -> f.returns(String.class).invoke(a -> join(a).getJoinMessage()));
        b.extension(PlayerJoinEvent.class, "setJoinMessage", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { join(a).setJoinMessage(arg(a, 1, String.class)); return null; }));
        b.extension(PlayerQuitEvent.class, "quitMessage", f -> f.returns(String.class).invoke(a -> quit(a).getQuitMessage()));
        b.extension(PlayerQuitEvent.class, "setQuitMessage", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { quit(a).setQuitMessage(arg(a, 1, String.class)); return null; }));
        b.extension(PlayerCommandPreprocessEvent.class, "message", f -> f.returns(String.class).invoke(a -> command(a).getMessage()));
        b.extension(PlayerCommandPreprocessEvent.class, "setMessage", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { command(a).setMessage(arg(a, 1, String.class)); return null; }));
        b.extension(PlayerCommandPreprocessEvent.class, "recipients", f -> f.returns(java.util.Set.class).invoke(a -> command(a).getRecipients()));
        b.extension(PlayerInteractEvent.class, "action", f -> f.returns(Action.class).invoke(a -> interact(a).getAction()));
        b.extension(PlayerInteractEvent.class, "item", f -> f.returns(nullableItem).invoke(a -> interact(a).getItem()));
        b.extension(PlayerInteractEvent.class, "material", f -> f.returns(JavaTypeRef.javaType(org.bukkit.Material.class).nullable()).invoke(a -> interact(a).getMaterial()));
        b.extension(PlayerInteractEvent.class, "hasBlock", f -> f.returns(Boolean.class).invoke(a -> interact(a).hasBlock()));
        b.extension(PlayerInteractEvent.class, "hasItem", f -> f.returns(Boolean.class).invoke(a -> interact(a).hasItem()));
        b.extension(PlayerInteractEvent.class, "clickedBlock", f -> f.returns(nullableBlock).invoke(a -> interact(a).getClickedBlock()));
        b.extension(PlayerInteractEvent.class, "blockFace", f -> f.returns(org.bukkit.block.BlockFace.class).invoke(a -> interact(a).getBlockFace()));
        b.extension(PlayerInteractEvent.class, "hand", f -> f.returns(EquipmentSlot.class).invoke(a -> interact(a).getHand()));
        b.extension(PlayerMoveEvent.class, "from", f -> f.returns(Location.class).invoke(a -> move(a).getFrom()));
        b.extension(PlayerMoveEvent.class, "setFrom", f -> f.param("location", Location.class).returns(Void.TYPE).invoke(a -> { move(a).setFrom(arg(a, 1, Location.class)); return null; }));
        b.extension(PlayerMoveEvent.class, "to", f -> f.returns(nullableLocation).invoke(a -> move(a).getTo()));
        b.extension(PlayerMoveEvent.class, "setTo", f -> f.param("location", nullableLocation).returns(Void.TYPE).invoke(a -> { move(a).setTo(arg(a, 1, Location.class)); return null; }));
        b.extension(PlayerTeleportEvent.class, "cause", f -> f.returns(PlayerTeleportEvent.TeleportCause.class).invoke(a -> teleport(a).getCause()));
    }

    private static PlayerEvent playerEvent(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerEvent.class); }
    private static PlayerJoinEvent join(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerJoinEvent.class); }
    private static PlayerQuitEvent quit(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerQuitEvent.class); }
    private static PlayerCommandPreprocessEvent command(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerCommandPreprocessEvent.class); }
    private static PlayerInteractEvent interact(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerInteractEvent.class); }
    private static PlayerMoveEvent move(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerMoveEvent.class); }
    private static PlayerTeleportEvent teleport(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerTeleportEvent.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
