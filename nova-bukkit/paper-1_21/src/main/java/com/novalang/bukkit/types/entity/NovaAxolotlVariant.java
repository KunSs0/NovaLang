package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;

/** 1.17+ Axolotl.Variant 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.Axolotl$Variant"})
public final class NovaAxolotlVariant {
    private static final String TYPE = "org.bukkit.entity.Axolotl$Variant";
    private NovaAxolotlVariant() { }
    public static void register(JavaTypes.Builder builder) {
        PaperEntityReflection.registerEnum(builder, "axolotlVariant", PaperEntityReflection.type(NovaAxolotlVariant.class, TYPE));
    }
}
