package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;

/** 1.13+ TropicalFish.Pattern 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.TropicalFish$Pattern"})
public final class NovaTropicalFishPattern {
    private static final String TYPE = "org.bukkit.entity.TropicalFish$Pattern";
    private NovaTropicalFishPattern() { }
    public static void register(JavaTypes.Builder builder) {
        PaperEntityReflection.registerEnum(builder, "tropicalFishPattern", PaperEntityReflection.type(NovaTropicalFishPattern.class, TYPE));
    }
}
