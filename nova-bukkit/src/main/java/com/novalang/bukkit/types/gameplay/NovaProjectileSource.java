package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.projectiles.BlockProjectileSource;

/** Spigot 1.12.2 projectile-source aliases. */
final class NovaProjectileSource {

    private NovaProjectileSource() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(BlockProjectileSource.class, "block", f -> f.returns(Block.class).invoke(a -> NovaTypeSupport.argument(a, 0, BlockProjectileSource.class).getBlock()));
    }
}
