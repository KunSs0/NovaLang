package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.util.Date;

@Requires(classes = {"org.bukkit.BanList"})
public final class NovaBanList {
    private NovaBanList() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableDate = JavaTypeRef.javaType(Date.class).nullable();
        builder.extension(BanList.class, "getBanEntry", function -> function.param("target", String.class)
                .returns(JavaTypeRef.javaType(BanEntry.class).nullable())
                .invoke(arguments -> list(arguments).getBanEntry(NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(BanList.class, "addBan", function -> function.param("target", String.class)
                .param("reason", nullableString)
                .param("expires", nullableDate)
                .param("source", nullableString)
                .returns(BanEntry.class)
                .invoke(arguments -> list(arguments).addBan(
                        NovaTypeSupport.argument(arguments, 1, String.class),
                        NovaTypeSupport.argument(arguments, 2, String.class),
                        NovaTypeSupport.argument(arguments, 3, Date.class),
                        NovaTypeSupport.argument(arguments, 4, String.class))));
        builder.extension(BanList.class, "banEntries", function -> function
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(BanEntry.class)))
                .invoke(arguments -> list(arguments).getBanEntries()));
        builder.extension(BanList.class, "isBanned", function -> function.param("target", String.class)
                .returns(Boolean.class)
                .invoke(arguments -> list(arguments).isBanned(NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(BanList.class, "pardon", function -> function.param("target", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    list(arguments).pardon(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static BanList list(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BanList.class);
    }
}
