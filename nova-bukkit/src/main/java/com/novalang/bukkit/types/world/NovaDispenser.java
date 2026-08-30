package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Dispenser;
import org.bukkit.projectiles.BlockProjectileSource;

@Requires(classes = {"org.bukkit.block.Dispenser"})
public final class NovaDispenser {
    private NovaDispenser() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Dispenser.class, "blockProjectileSource", function -> function
                .returns(JavaTypeRef.javaType(BlockProjectileSource.class).nullable())
                .invoke(arguments -> dispenser(arguments).getBlockProjectileSource()));
        builder.extension(Dispenser.class, "dispense", function -> function.returns(Boolean.class)
                .invoke(arguments -> dispenser(arguments).dispense()));
    }

    private static Dispenser dispenser(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Dispenser.class);
    }
}
