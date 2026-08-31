package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ Bamboo BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Bamboo", "org.bukkit.block.data.type.Bamboo$Leaves"}, methods = {
        "org.bukkit.block.data.type.Bamboo#getLeaves", "org.bukkit.block.data.type.Bamboo#setLeaves"})
public final class NovaBlockBamboo {
    private static final String BAMBOO = "org.bukkit.block.data.type.Bamboo";
    private static final String LEAVES = "org.bukkit.block.data.type.Bamboo$Leaves";
    private NovaBlockBamboo() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> bambooType = NovaBlockDataReflection.type(NovaBlockBamboo.class, BAMBOO);
        Class<?> leavesType = NovaBlockDataReflection.type(NovaBlockBamboo.class, LEAVES);
        Method getLeaves = NovaBlockDataReflection.method(bambooType, "getLeaves");
        Method setLeaves = NovaBlockDataReflection.method(bambooType, "setLeaves", leavesType);
        builder.extension(bambooType, "leaves", function -> function.returns(JavaTypeRef.javaType(leavesType)).invoke(arguments -> NovaBlockDataReflection.invoke(getLeaves, arguments[0])));
        builder.extension(bambooType, "setLeaves", function -> function.param("leaves", leavesType).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setLeaves, arguments[0], arguments[1])));
        builder.extension(bambooType, "setLeaves", function -> function.param("leaves", String.class).returns(Void.TYPE).invoke(arguments -> {
            Object leaves = NovaBlockDataReflection.enumValue(leavesType, (String) arguments[1]);
            if (leaves != null) {
                NovaBlockDataReflection.invoke(setLeaves, arguments[0], leaves);
            }
            return null;
        }));
    }
}
