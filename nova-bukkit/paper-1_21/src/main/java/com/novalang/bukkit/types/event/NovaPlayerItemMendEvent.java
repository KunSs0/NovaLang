package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.player.PlayerItemMendEvent"})
public final class NovaPlayerItemMendEvent {

    private NovaPlayerItemMendEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerItemMendEvent.class, "item", function -> function.returns(ItemStack.class).invoke(arguments -> event(arguments).getItem()));
        builder.extension(PlayerItemMendEvent.class, "experienceOrb", function -> function.returns(ExperienceOrb.class).invoke(arguments -> event(arguments).getExperienceOrb()));
        builder.extension(PlayerItemMendEvent.class, "repairAmount", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getRepairAmount()));
        builder.extension(PlayerItemMendEvent.class, "setRepairAmount", function -> function.param("amount", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setRepairAmount(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
    }

    private static PlayerItemMendEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerItemMendEvent.class);
    }
}
