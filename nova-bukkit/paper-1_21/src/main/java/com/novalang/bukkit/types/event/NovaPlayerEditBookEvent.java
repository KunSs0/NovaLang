package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
@Requires(classes = {"org.bukkit.event.player.PlayerEditBookEvent"})
public final class NovaPlayerEditBookEvent {
    private NovaPlayerEditBookEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(PlayerEditBookEvent.class, "previousBookMeta", f -> f.returns(BookMeta.class).invoke(a -> event(a).getPreviousBookMeta()));
        b.extension(PlayerEditBookEvent.class, "newBookMeta", f -> f.returns(BookMeta.class).invoke(a -> event(a).getNewBookMeta()));
        b.extension(PlayerEditBookEvent.class, "slot", f -> f.returns(Integer.class).invoke(a -> event(a).getSlot()));
        b.extension(PlayerEditBookEvent.class, "setNewBookMeta", f -> f.param("meta", BookMeta.class).returns(Void.TYPE).invoke(a -> { event(a).setNewBookMeta(NovaTypeSupport.argument(a, 1, BookMeta.class)); return null; }));
        b.extension(PlayerEditBookEvent.class, "isSigning", f -> f.returns(Boolean.class).invoke(a -> event(a).isSigning()));
        b.extension(PlayerEditBookEvent.class, "setSigning", f -> f.param("signing", Boolean.class).returns(Void.TYPE).invoke(a -> { event(a).setSigning(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
    }
    private static PlayerEditBookEvent event(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerEditBookEvent.class); }
}
