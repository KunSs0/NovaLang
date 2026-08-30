package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.BanEntry;

import java.util.Date;

@Requires(classes = {"org.bukkit.BanEntry"})
public final class NovaBanEntry {
    private NovaBanEntry() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableDate = JavaTypeRef.javaType(Date.class).nullable();
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        builder.extension(BanEntry.class, "target", function -> function.returns(String.class)
                .invoke(arguments -> entry(arguments).getTarget()));
        builder.extension(BanEntry.class, "created", function -> function.returns(Date.class)
                .invoke(arguments -> entry(arguments).getCreated()));
        builder.extension(BanEntry.class, "setCreated", function -> function.param("created", Date.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entry(arguments).setCreated(NovaTypeSupport.argument(arguments, 1, Date.class));
                    return null;
                }));
        builder.extension(BanEntry.class, "source", function -> function.returns(String.class)
                .invoke(arguments -> entry(arguments).getSource()));
        builder.extension(BanEntry.class, "setSource", function -> function.param("source", nullableString)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entry(arguments).setSource(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(BanEntry.class, "expiration", function -> function.returns(nullableDate)
                .invoke(arguments -> entry(arguments).getExpiration()));
        builder.extension(BanEntry.class, "setExpiration", function -> function.param("expiration", nullableDate)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entry(arguments).setExpiration(NovaTypeSupport.argument(arguments, 1, Date.class));
                    return null;
                }));
        builder.extension(BanEntry.class, "reason", function -> function.returns(nullableString)
                .invoke(arguments -> entry(arguments).getReason()));
        builder.extension(BanEntry.class, "setReason", function -> function.param("reason", nullableString)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entry(arguments).setReason(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(BanEntry.class, "save", function -> function.returns(Void.TYPE)
                .invoke(arguments -> {
                    entry(arguments).save();
                    return null;
                }));
    }

    private static BanEntry entry(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BanEntry.class);
    }
}
