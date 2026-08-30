package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.AbstractHorseInventory;

@Requires(classes = {"org.bukkit.entity.AbstractHorse"})
public final class NovaAbstractHorse {
    private NovaAbstractHorse() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(AbstractHorse.class, "variant", function -> function.returns(Horse.Variant.class).invoke(arguments -> event(arguments).getVariant()));
        builder.extension(AbstractHorse.class, "setVariant", function -> function.param("variant", Horse.Variant.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setVariant(NovaTypeSupport.argument(arguments, 1, Horse.Variant.class)); return null; }));
        builder.extension(AbstractHorse.class, "domestication", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getDomestication()));
        builder.extension(AbstractHorse.class, "setDomestication", function -> function.param("level", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setDomestication(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(AbstractHorse.class, "maxDomestication", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getMaxDomestication()));
        builder.extension(AbstractHorse.class, "setMaxDomestication", function -> function.param("level", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setMaxDomestication(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(AbstractHorse.class, "jumpStrength", function -> function.returns(Double.class).invoke(arguments -> event(arguments).getJumpStrength()));
        builder.extension(AbstractHorse.class, "setJumpStrength", function -> function.param("strength", Double.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setJumpStrength(NovaTypeSupport.argument(arguments, 1, Double.class)); return null; }));
        builder.extension(AbstractHorse.class, "inventory", function -> function.returns(AbstractHorseInventory.class).invoke(arguments -> event(arguments).getInventory()));
    }
    private static AbstractHorse event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, AbstractHorse.class);
    }
}
