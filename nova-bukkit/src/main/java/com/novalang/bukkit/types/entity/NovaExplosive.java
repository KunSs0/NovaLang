package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Explosive;
@Requires(classes = {"org.bukkit.entity.Explosive"})
public final class NovaExplosive {
    private NovaExplosive() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Explosive.class, "yield", function -> function.returns(Float.class).invoke(arguments -> event(arguments).getYield()));
        builder.extension(Explosive.class, "setYield", function -> function.param("yield", Float.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setYield(NovaTypeSupport.argument(arguments, 1, Float.class)); return null; }));
        builder.extension(Explosive.class, "incendiary", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isIncendiary()));
        builder.extension(Explosive.class, "setIncendiary", function -> function.param("incendiary", Boolean.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setIsIncendiary(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Explosive.class, "isIncendiary", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isIncendiary()));
        builder.extension(Explosive.class, "setIsIncendiary", function -> function.param("incendiary", Boolean.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setIsIncendiary(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
    }
    private static Explosive event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, Explosive.class); }
}
