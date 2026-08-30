package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中补充的实体值对象类型。 */
public final class NovaEntityMoreTypes {
    private NovaEntityMoreTypes() { }
    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaAbstractHorse.class, NovaAbstractHorse::register);
        NovaBukkitRegistrar.register(builder, NovaFishHook.class, NovaFishHook::register);
        NovaBukkitRegistrar.register(builder, NovaChestedHorse.class, NovaChestedHorse::register);
        NovaBukkitRegistrar.register(builder, NovaExplosive.class, NovaExplosive::register);
        NovaBukkitRegistrar.register(builder, NovaComplexEntityPart.class, NovaComplexEntityPart::register);
        NovaBukkitRegistrar.register(builder, NovaEnderDragonPart.class, NovaEnderDragonPart::register);
        NovaBukkitRegistrar.register(builder, NovaEntityProjectileSource.class, NovaEntityProjectileSource::register);
    }
}
