package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
@Requires(classes = {"org.bukkit.entity.EnderDragonPart"})
public final class NovaEnderDragonPart {
    private NovaEnderDragonPart() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnderDragonPart.class, "parent", function -> function.returns(EnderDragon.class).invoke(arguments -> event(arguments).getParent()));
    }
    private static EnderDragonPart event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, EnderDragonPart.class); }
}
