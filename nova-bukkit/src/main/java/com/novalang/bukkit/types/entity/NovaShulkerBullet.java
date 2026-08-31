package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ShulkerBullet;

/** Spigot 1.12.2 ShulkerBullet 扩展。 */
@Requires(classes = {"org.bukkit.entity.ShulkerBullet"})
public final class NovaShulkerBullet {

    private NovaShulkerBullet() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef target = JavaTypeRef.javaType(Entity.class).nullable();
        builder.extension(ShulkerBullet.class, "target", function -> function.returns(target).invoke(arguments -> bullet(arguments).getTarget()));
        builder.extension(ShulkerBullet.class, "setTarget", function -> function.param("target", target).returns(Void.TYPE).invoke(arguments -> {
            bullet(arguments).setTarget(NovaTypeSupport.argument(arguments, 1, Entity.class));
            return null;
        }));
    }

    private static ShulkerBullet bullet(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ShulkerBullet.class);
    }
}
