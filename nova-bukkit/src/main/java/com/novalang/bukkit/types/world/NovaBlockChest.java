package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Chest BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.type.Chest", "org.bukkit.block.data.type.Chest$Type"},
        methods = {
                "org.bukkit.block.data.type.Chest#getType",
                "org.bukkit.block.data.type.Chest#setType"
        })
public final class NovaBlockChest {

    private static final String CHEST = "org.bukkit.block.data.type.Chest";
    private static final String TYPE = "org.bukkit.block.data.type.Chest$Type";

    private NovaBlockChest() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> chestType = NovaBlockDataReflection.type(NovaBlockChest.class, CHEST);
        Class<?> typeType = NovaBlockDataReflection.type(NovaBlockChest.class, TYPE);
        Method getType = NovaBlockDataReflection.method(chestType, "getType");
        Method setType = NovaBlockDataReflection.method(chestType, "setType", typeType);
        builder.extension(chestType, "type", function -> function.returns(JavaTypeRef.javaType(typeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getType, arguments[0])));
        builder.extension(chestType, "setType", function -> function
                .param("type", typeType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setType, arguments[0], arguments[1])));
        builder.extension(chestType, "setType", function -> function
                .param("type", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object type = NovaBlockDataReflection.enumValue(typeType, (String) arguments[1]);
                    if (type != null) {
                        NovaBlockDataReflection.invoke(setType, arguments[0], type);
                    }
                    return null;
                }));
    }
}
