package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Tripwire;

/** 旧版 Tripwire 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Tripwire"})
final class NovaLegacyTripwire {

    private NovaLegacyTripwire() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Tripwire.class, "isActivated", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> tripwire(arguments).isActivated()));
        builder.extension(Tripwire.class, "setActivated", function -> function
                .param("activated", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    tripwire(arguments).setActivated(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Tripwire.class, "isObjectTriggering", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> tripwire(arguments).isObjectTriggering()));
        builder.extension(Tripwire.class, "setObjectTriggering", function -> function
                .param("triggering", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    tripwire(arguments).setObjectTriggering(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Tripwire.class, "clone", function -> function
                .returns(Tripwire.class)
                .invoke(arguments -> tripwire(arguments).clone()));
        builder.extension(Tripwire.class, "toString", function -> function
                .returns(String.class)
                .invoke(arguments -> tripwire(arguments).toString()));
    }

    private static Tripwire tripwire(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Tripwire.class);
    }
}
