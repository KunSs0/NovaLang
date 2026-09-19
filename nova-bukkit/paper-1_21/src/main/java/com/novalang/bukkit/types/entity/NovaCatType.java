package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;

/** 1.14+ Cat.Type 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.Cat$Type"})
public final class NovaCatType {
    private static final String TYPE = "org.bukkit.entity.Cat$Type";
    private NovaCatType() { }
    public static void register(JavaTypes.Builder builder) {
        PaperEntityReflection.registerEnum(builder, "catType", PaperEntityReflection.type(NovaCatType.class, TYPE));
    }
}
