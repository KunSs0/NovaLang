package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;

/** 1.19.4+ Display.Billboard 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.Display$Billboard"})
public final class NovaDisplayBillboard {
    private static final String TYPE = "org.bukkit.entity.Display$Billboard";
    private NovaDisplayBillboard() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaDisplayBillboard.class, TYPE);
        PaperEntityReflection.registerEnum(builder, "displayBillboard", type);
    }
}
