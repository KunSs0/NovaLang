package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;

/** Spigot 1.12.2 ComplexLivingEntity 扩展。 */
@Requires(classes = {"org.bukkit.entity.ComplexLivingEntity"})
public final class NovaComplexLivingEntity {

    private NovaComplexLivingEntity() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef parts = JavaTypeRef.setOf(JavaTypeRef.javaType(ComplexEntityPart.class));
        builder.extension(ComplexLivingEntity.class, "parts", function -> function.returns(parts).invoke(arguments -> entity(arguments).getParts()));
    }

    private static ComplexLivingEntity entity(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ComplexLivingEntity.class);
    }
}
