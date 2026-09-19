package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/** 皮革护甲物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.LeatherArmorMeta"})
public final class NovaLeatherArmorMeta {

    private NovaLeatherArmorMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableColor = JavaTypeRef.javaType(Color.class).nullable();
        builder.extension(LeatherArmorMeta.class, "color", function -> function
                .returns(Color.class)
                .invoke(arguments -> meta(arguments).getColor()));
        builder.extension(LeatherArmorMeta.class, "setColor", function -> function
                .param("color", nullableColor)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setColor(argument(arguments, 1, Color.class));
                    return null;
                }));
        builder.extension(LeatherArmorMeta.class, "clone", function -> function
                .returns(LeatherArmorMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static LeatherArmorMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LeatherArmorMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
