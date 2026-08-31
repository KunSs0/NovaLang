package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Ocelot;

/** Spigot 1.12.2 Ocelot.Type 枚举别名。 */
@Requires(classes = {"org.bukkit.entity.Ocelot$Type"})
@SuppressWarnings("deprecation")
public final class NovaOcelotType {

    private NovaOcelotType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Ocelot.Type.class, "id", function -> function.returns(Integer.class).invoke(arguments -> type(arguments).getId()));
        builder.extension(Ocelot.Type.class, "getType", function -> function.param("id", Integer.class).returns(JavaTypeRef.javaType(Ocelot.Type.class).nullable()).invoke(arguments -> Ocelot.Type.getType(NovaTypeSupport.argument(arguments, 1, Integer.class))));
    }

    private static Ocelot.Type type(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Ocelot.Type.class);
    }
}
