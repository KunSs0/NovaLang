package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
@Requires(classes = {"org.bukkit.entity.EntityType"})
public final class NovaEntityType {
    private NovaEntityType() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityType.class, "name", function -> function.returns(String.class).invoke(arguments -> event(arguments).name()));
        builder.extension(EntityType.class, "entityName", function -> function.returns(String.class).invoke(arguments -> event(arguments).getName()));
        builder.extension(EntityType.class, "entityClass", function -> function.returns(JavaTypeRef.javaType(Class.class).nullable()).invoke(arguments -> event(arguments).getEntityClass()));
        builder.extension(EntityType.class, "typeId", function -> function.returns(Short.class).invoke(arguments -> event(arguments).getTypeId()));
        builder.extension(EntityType.class, "isSpawnable", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isSpawnable()));
        builder.extension(EntityType.class, "isAlive", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isAlive()));
        builder.extension(EntityType.class, "fromName", function -> function.param("name", String.class).returns(JavaTypeRef.javaType(EntityType.class).nullable()).invoke(arguments -> EntityType.fromName(NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(EntityType.class, "fromId", function -> function.param("id", Integer.class).returns(JavaTypeRef.javaType(EntityType.class).nullable()).invoke(arguments -> EntityType.fromId(NovaTypeSupport.argument(arguments, 1, Integer.class))));
    }
    private static EntityType event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, EntityType.class); }
}
