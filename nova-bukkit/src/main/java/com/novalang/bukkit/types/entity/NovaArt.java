package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Art;
@Requires(classes = {"org.bukkit.Art"})
public final class NovaArt {
    private NovaArt() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Art.class, "blockWidth", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getBlockWidth()));
        builder.extension(Art.class, "blockHeight", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getBlockHeight()));
        builder.extension(Art.class, "id", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getId()));
        builder.extension(Art.class, "getById", function -> function.param("id", Integer.class).returns(JavaTypeRef.javaType(Art.class).nullable()).invoke(arguments -> Art.getById(NovaTypeSupport.argument(arguments, 1, Integer.class))));
        builder.extension(Art.class, "getByName", function -> function.param("name", String.class).returns(JavaTypeRef.javaType(Art.class).nullable()).invoke(arguments -> Art.getByName(NovaTypeSupport.argument(arguments, 1, String.class))));
    }
    private static Art event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, Art.class); }
}
