package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;

/** Spigot 1.12.2 方块结构与旗帜相关枚举全局函数。 */
@Requires(classes = {
        "org.bukkit.block.banner.PatternType",
        "org.bukkit.block.structure.Mirror",
        "org.bukkit.block.structure.StructureRotation",
        "org.bukkit.block.structure.UsageMode"
})
public final class NovaWorldInventoryEnum {
    private NovaWorldInventoryEnum() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "patternType", PatternType.class);
        NovaEnum.registerEnum(builder, "mirror", Mirror.class);
        NovaEnum.registerEnum(builder, "structureRotation", StructureRotation.class);
        NovaEnum.registerEnum(builder, "usageMode", UsageMode.class);
    }
}
