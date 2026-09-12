package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Method;
import java.util.Set;

/** 1.13+ RedstoneWire BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.RedstoneWire", "org.bukkit.block.data.type.RedstoneWire$Connection"}, methods = {
        "org.bukkit.block.data.type.RedstoneWire#getFace", "org.bukkit.block.data.type.RedstoneWire#setFace", "org.bukkit.block.data.type.RedstoneWire#getAllowedFaces"})
public final class NovaBlockRedstoneWire {
    private static final String REDSTONE_WIRE = "org.bukkit.block.data.type.RedstoneWire";
    private static final String CONNECTION = "org.bukkit.block.data.type.RedstoneWire$Connection";
    private NovaBlockRedstoneWire() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> wireType = NovaBlockDataReflection.type(NovaBlockRedstoneWire.class, REDSTONE_WIRE);
        Class<?> connectionType = NovaBlockDataReflection.type(NovaBlockRedstoneWire.class, CONNECTION);
        Method getFace = NovaBlockDataReflection.method(wireType, "getFace", BlockFace.class);
        Method setFace = NovaBlockDataReflection.method(wireType, "setFace", BlockFace.class, connectionType);
        Method getAllowedFaces = NovaBlockDataReflection.method(wireType, "getAllowedFaces");
        builder.extension(wireType, "getFace", f -> f.param("face", BlockFace.class).returns(JavaTypeRef.javaType(connectionType)).invoke(a -> NovaBlockDataReflection.invoke(getFace, a[0], a[1])));
        builder.extension(wireType, "getFace", f -> f.param("face", String.class).returns(JavaTypeRef.javaType(connectionType)).invoke(a -> {
            BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, (String) a[1]);
            return face == null ? null : NovaBlockDataReflection.invoke(getFace, a[0], face);
        }));
        registerSetFace(builder, wireType, connectionType, setFace, BlockFace.class, connectionType);
        builder.extension(wireType, "setFace", f -> f.param("face", String.class).param("connection", connectionType).returns(Void.TYPE).invoke(a -> {
            BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, (String) a[1]);
            if (face != null) { NovaBlockDataReflection.invoke(setFace, a[0], face, a[2]); }
            return null;
        }));
        builder.extension(wireType, "setFace", f -> f.param("face", BlockFace.class).param("connection", String.class).returns(Void.TYPE).invoke(a -> {
            Object connection = NovaBlockDataReflection.enumValue(connectionType, (String) a[2]);
            if (connection != null) { NovaBlockDataReflection.invoke(setFace, a[0], a[1], connection); }
            return null;
        }));
        builder.extension(wireType, "setFace", f -> f.param("face", String.class).param("connection", String.class).returns(Void.TYPE).invoke(a -> {
            BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, (String) a[1]);
            Object connection = NovaBlockDataReflection.enumValue(connectionType, (String) a[2]);
            if (face != null && connection != null) { NovaBlockDataReflection.invoke(setFace, a[0], face, connection); }
            return null;
        }));
        builder.extension(wireType, "allowedFaces", f -> f.returns(Set.class).invoke(a -> NovaBlockDataReflection.invoke(getAllowedFaces, a[0])));
    }
    private static void registerSetFace(JavaTypes.Builder builder, Class<?> wireType, Class<?> connectionType, Method setFace, Class<?> faceType, Class<?> ignoredConnectionType) {
        builder.extension(wireType, "setFace", f -> f.param("face", faceType).param("connection", connectionType).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setFace, a[0], a[1], a[2])));
    }
}
