package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

/** Spigot 1.12.2 EvokerFangs 扩展。 */
@Requires(classes = {"org.bukkit.entity.EvokerFangs"})
public final class NovaEvokerFangs {

    private NovaEvokerFangs() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef owner = JavaTypeRef.javaType(LivingEntity.class).nullable();
        builder.extension(EvokerFangs.class, "owner", function -> function.returns(owner).invoke(arguments -> fangs(arguments).getOwner()));
        builder.extension(EvokerFangs.class, "setOwner", function -> function.param("owner", owner).returns(Void.TYPE).invoke(arguments -> {
            fangs(arguments).setOwner(NovaTypeSupport.argument(arguments, 1, LivingEntity.class));
            return null;
        }));
    }

    private static EvokerFangs fangs(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EvokerFangs.class);
    }
}
