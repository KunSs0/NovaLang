package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ChestedHorse;
@Requires(classes = {"org.bukkit.entity.ChestedHorse"})
public final class NovaChestedHorse {
    private NovaChestedHorse() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ChestedHorse.class, "isCarryingChest", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isCarryingChest()));
        builder.extension(ChestedHorse.class, "setCarryingChest", function -> function.param("chest", Boolean.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setCarryingChest(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
    }
    private static ChestedHorse event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, ChestedHorse.class); }
}
