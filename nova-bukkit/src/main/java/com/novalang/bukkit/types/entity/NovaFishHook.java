package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.FishHook;
@Requires(classes = {"org.bukkit.entity.FishHook"})
public final class NovaFishHook {
    private NovaFishHook() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(FishHook.class, "biteChance", function -> function.returns(Double.class).invoke(arguments -> event(arguments).getBiteChance()));
        builder.extension(FishHook.class, "setBiteChance", function -> function.param("chance", Double.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setBiteChance(NovaTypeSupport.argument(arguments, 1, Double.class)); return null; }));
    }
    private static FishHook event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, FishHook.class); }
}
