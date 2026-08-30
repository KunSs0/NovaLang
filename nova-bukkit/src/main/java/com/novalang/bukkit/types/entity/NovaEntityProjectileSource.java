package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

@Requires(classes = {"org.bukkit.projectiles.ProjectileSource"})
public final class NovaEntityProjectileSource {
    private NovaEntityProjectileSource() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ProjectileSource.class, "launchProjectile", function -> function
                .param("projectile", Class.class)
                .returns(Projectile.class)
                .invoke(arguments -> source(arguments).launchProjectile(
                        projectileClass(arguments))));
        builder.extension(ProjectileSource.class, "launchProjectile", function -> function
                .param("projectile", Class.class)
                .param("velocity", Vector.class)
                .returns(Projectile.class)
                .invoke(arguments -> source(arguments).launchProjectile(
                        projectileClass(arguments),
                        NovaTypeSupport.argument(arguments, 2, Vector.class))));
    }

    private static ProjectileSource source(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ProjectileSource.class);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Projectile> projectileClass(Object[] arguments) {
        return (Class<? extends Projectile>) NovaTypeSupport.argument(arguments, 1, Class.class);
    }
}
