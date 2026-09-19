package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/** Paper 1.21.x 的 Block/Material 与 BlockData 访问入口。 */
public final class NovaBlockDataAccess {
    private NovaBlockDataAccess() {
    }

    /** 注册 BlockData 的读取、写入与创建函数。 */
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Block.class, "blockData", f -> f.returns(JavaTypeRef.javaType(BlockData.class))
                .invoke(a -> block(a).getBlockData()));
        builder.extension(Block.class, "setBlockData", f -> f.param("data", BlockData.class).returns(Void.TYPE)
                .invoke(a -> { block(a).setBlockData(argument(a, 1, BlockData.class)); return null; }));
        builder.extension(Block.class, "setBlockData", f -> f.param("data", BlockData.class)
                .param("applyPhysics", Boolean.class).returns(Void.TYPE)
                .invoke(a -> { block(a).setBlockData(argument(a, 1, BlockData.class), argument(a, 2, Boolean.class)); return null; }));
        builder.extension(Material.class, "createBlockData", f -> f.returns(JavaTypeRef.javaType(BlockData.class))
                .invoke(a -> material(a).createBlockData()));
        builder.extension(Material.class, "createBlockData", f -> f.param("data", String.class)
                .returns(JavaTypeRef.javaType(BlockData.class))
                .invoke(a -> material(a).createBlockData(argument(a, 1, String.class))));
    }

    /** 取得扩展调用中的方块参数。 */
    private static Block block(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Block.class);
    }

    /** 取得扩展调用中的材质参数。 */
    private static Material material(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Material.class);
    }

    /** 取得扩展调用中的指定类型参数。 */
    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
