package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Sittable;

/** Spigot 1.12.2 Sittable 扩展。 */
@Requires(classes = {"org.bukkit.entity.Sittable"})
public final class NovaSittable {

    private NovaSittable() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Sittable.class, "isSitting", function -> function.returns(Boolean.class).invoke(arguments -> sittable(arguments).isSitting()));
        builder.extension(Sittable.class, "setSitting", function -> function.param("sitting", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            sittable(arguments).setSitting(NovaTypeSupport.argument(arguments, 1, Boolean.class));
            return null;
        }));
    }

    private static Sittable sittable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sittable.class);
    }
}
