package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

/** 头颅物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.SkullMeta"})
@SuppressWarnings("deprecation")
public final class NovaSkullMeta {

    private NovaSkullMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableOfflinePlayer = JavaTypeRef.javaType(OfflinePlayer.class).nullable();
        builder.extension(SkullMeta.class, "hasOwner", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasOwner()));
        builder.extension(SkullMeta.class, "owner", function -> function
                .returns(nullableString)
                .invoke(arguments -> meta(arguments).getOwner()));
        builder.extension(SkullMeta.class, "setOwner", function -> function
                .param("owner", nullableString)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).setOwner(argument(arguments, 1, String.class))));
        builder.extension(SkullMeta.class, "owningPlayer", function -> function
                .returns(nullableOfflinePlayer)
                .invoke(arguments -> meta(arguments).getOwningPlayer()));
        builder.extension(SkullMeta.class, "setOwningPlayer", function -> function
                .param("owner", nullableOfflinePlayer)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).setOwningPlayer(argument(arguments, 1, OfflinePlayer.class))));
        builder.extension(SkullMeta.class, "clone", function -> function
                .returns(SkullMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static SkullMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SkullMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
