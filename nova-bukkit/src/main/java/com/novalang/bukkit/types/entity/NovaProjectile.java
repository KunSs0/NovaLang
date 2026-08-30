package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

/** Projectile 别名；Vehicle 在 1.12.2 只有 Entity 已覆盖的 velocity 函数。 */
final class NovaProjectile {

    private NovaProjectile() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef source = JavaTypeRef.javaType(ProjectileSource.class);
        builder.extension(Projectile.class, "shooter", f -> f.returns(source.nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Projectile.class).getShooter()));
        builder.extension(Projectile.class, "setShooter", f -> f.param("shooter", source.nullable()).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Projectile.class).setShooter(NovaTypeSupport.argument(a, 1, ProjectileSource.class)); return null; }));
        builder.extension(Projectile.class, "doesBounce", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Projectile.class).doesBounce()));
        builder.extension(Projectile.class, "setBounce", f -> f.param("bounce", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Projectile.class).setBounce(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
    }
}
