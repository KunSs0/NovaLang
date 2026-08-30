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
        NovaBukkitRegistrar.register(builder, NovaEntityType.class, NovaEntityType::register);
        NovaBukkitRegistrar.register(builder, NovaArt.class, NovaArt::register);
        NovaBukkitRegistrar.register(builder, NovaPainting.class, NovaPainting::register);
        NovaBukkitRegistrar.register(builder, NovaExplosive.class, NovaExplosive::register);
        NovaBukkitRegistrar.register(builder, NovaComplexEntityPart.class, NovaComplexEntityPart::register);
        NovaBukkitRegistrar.register(builder, NovaEnderDragonPart.class, NovaEnderDragonPart::register);
        NovaBukkitRegistrar.register(builder, NovaAreaEffectCloud.class, NovaAreaEffectCloud::register);
        NovaBukkitRegistrar.register(builder, NovaEnderDragon.class, NovaEnderDragon::register);
        NovaBukkitRegistrar.register(builder, NovaEvoker.class, NovaEvoker::register);
        NovaBukkitRegistrar.register(builder, NovaLlama.class, NovaLlama::register);
        NovaBukkitRegistrar.register(builder, NovaParrot.class, NovaParrot::register);
        NovaBukkitRegistrar.register(builder, NovaBat.class, NovaBat::register);
        NovaBukkitRegistrar.register(builder, NovaWolf.class, NovaWolf::register);
        NovaBukkitRegistrar.register(builder, NovaSheep.class, NovaSheep::register);
        NovaBukkitRegistrar.register(builder, NovaRabbit.class, NovaRabbit::register);
        NovaBukkitRegistrar.register(builder, NovaOcelot.class, NovaOcelot::register);
        NovaBukkitRegistrar.register(builder, NovaEnderman.class, NovaEnderman::register);
        NovaBukkitRegistrar.register(builder, NovaZombieVillager.class, NovaZombieVillager::register);
        NovaBukkitRegistrar.register(builder, NovaTNTPrimed.class, NovaTNTPrimed::register);
        NovaBukkitRegistrar.register(builder, NovaWitherSkull.class, NovaWitherSkull::register);
        NovaBukkitRegistrar.register(builder, NovaEnderSignal.class, NovaEnderSignal::register);
        NovaBukkitRegistrar.register(builder, NovaEntityProjectileSource.class, NovaEntityProjectileSource::register);
    }
}
