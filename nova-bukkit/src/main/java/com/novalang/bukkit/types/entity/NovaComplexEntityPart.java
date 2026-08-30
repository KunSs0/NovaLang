package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
@Requires(classes = {"org.bukkit.entity.ComplexEntityPart"})
public final class NovaComplexEntityPart {
    private NovaComplexEntityPart() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ComplexEntityPart.class, "parent", function -> function.returns(ComplexLivingEntity.class).invoke(arguments -> event(arguments).getParent()));
    }
    private static ComplexEntityPart event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, ComplexEntityPart.class); }
}
