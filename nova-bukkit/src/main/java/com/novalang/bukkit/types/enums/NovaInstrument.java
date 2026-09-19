package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Instrument;

/** Spigot 1.12.2 Instrument 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaInstrument {

    private NovaInstrument() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Instrument.class, "type", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) instrument(arguments).getType()));
    }

    private static Instrument instrument(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Instrument.class);
    }
}
