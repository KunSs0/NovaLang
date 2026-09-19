package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/** Fluxon 为 1.12.2 Block 提供的字符串枚举便捷别名。 */
@Requires(classes = {"org.bukkit.block.Block", "org.bukkit.block.BlockFace", "org.bukkit.Material"})
public final class NovaBlockStringTypes {

    private NovaBlockStringTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableBlock = JavaTypeRef.javaType(Block.class).nullable();
        builder.extension(Block.class, "getRelative", f -> f.param("face", String.class).returns(nullableBlock).invoke(a -> {
            BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, NovaTypeSupport.argument(a, 1, String.class));
            if (face == null) {
                return null;
            }
            return NovaTypeSupport.argument(a, 0, Block.class).getRelative(face);
        }));
        builder.extension(Block.class, "getRelative", f -> f.param("face", String.class).param("distance", Integer.class).returns(nullableBlock).invoke(a -> {
            BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, NovaTypeSupport.argument(a, 1, String.class));
            if (face == null) {
                return null;
            }
            return NovaTypeSupport.argument(a, 0, Block.class).getRelative(face, NovaTypeSupport.argument(a, 2, Integer.class));
        }));
        builder.extension(Block.class, "setType", f -> f.param("type", String.class).returns(Void.TYPE).invoke(a -> {
            Material material = NovaTypeSupport.findEnum(Material.class, NovaTypeSupport.argument(a, 1, String.class));
            if (material != null) {
                NovaTypeSupport.argument(a, 0, Block.class).setType(material);
            }
            return null;
        }));
        builder.extension(Block.class, "setType", f -> f.param("type", String.class).param("applyPhysics", Boolean.class).returns(Void.TYPE).invoke(a -> {
            Material material = NovaTypeSupport.findEnum(Material.class, NovaTypeSupport.argument(a, 1, String.class));
            if (material != null) {
                NovaTypeSupport.argument(a, 0, Block.class).setType(material, NovaTypeSupport.argument(a, 2, Boolean.class));
            }
            return null;
        }));
    }
}
