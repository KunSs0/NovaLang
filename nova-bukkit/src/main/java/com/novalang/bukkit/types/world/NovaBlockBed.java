package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Bed BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Bed", "org.bukkit.block.data.type.Bed$Part"}, methods = {
        "org.bukkit.block.data.type.Bed#getPart", "org.bukkit.block.data.type.Bed#setPart", "org.bukkit.block.data.type.Bed#isOccupied"})
public final class NovaBlockBed {
    private static final String BED = "org.bukkit.block.data.type.Bed";
    private static final String PART = "org.bukkit.block.data.type.Bed$Part";
    private NovaBlockBed() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> bedType = NovaBlockDataReflection.type(NovaBlockBed.class, BED);
        Class<?> partType = NovaBlockDataReflection.type(NovaBlockBed.class, PART);
        Method getPart = NovaBlockDataReflection.method(bedType, "getPart");
        Method setPart = NovaBlockDataReflection.method(bedType, "setPart", partType);
        Method isOccupied = NovaBlockDataReflection.method(bedType, "isOccupied");
        builder.extension(bedType, "part", function -> function.returns(JavaTypeRef.javaType(partType)).invoke(arguments -> NovaBlockDataReflection.invoke(getPart, arguments[0])));
        builder.extension(bedType, "setPart", function -> function.param("part", partType).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setPart, arguments[0], arguments[1])));
        builder.extension(bedType, "setPart", function -> function.param("part", String.class).returns(Void.TYPE).invoke(arguments -> {
            Object part = NovaBlockDataReflection.enumValue(partType, (String) arguments[1]);
            if (part != null) {
                NovaBlockDataReflection.invoke(setPart, arguments[0], part);
            }
            return null;
        }));
        builder.extension(bedType, "isOccupied", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isOccupied, arguments[0])));
    }
}
