package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

import java.util.UUID;

@Requires(classes = {"org.bukkit.block.Skull"})
public final class NovaSkull {
    private NovaSkull() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullablePlayer = JavaTypeRef.javaType(OfflinePlayer.class).nullable();
        builder.extension(Skull.class, "hasOwner", function -> function.returns(Boolean.class)
                .invoke(arguments -> skull(arguments).hasOwner()));
        builder.extension(Skull.class, "owner", function -> function.returns(nullableString)
                .invoke(arguments -> skull(arguments).getOwner()));
        builder.extension(Skull.class, "setOwner", function -> function.param("owner", String.class)
                .returns(Boolean.class)
                .invoke(arguments -> skull(arguments).setOwner(NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(Skull.class, "owningPlayer", function -> function.returns(nullablePlayer)
                .invoke(arguments -> skull(arguments).getOwningPlayer()));
        builder.extension(Skull.class, "setOwningPlayer", function -> function.param("player", OfflinePlayer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    skull(arguments).setOwningPlayer(NovaTypeSupport.argument(arguments, 1, OfflinePlayer.class));
                    return null;
                }));
        builder.extension(Skull.class, "setOwningPlayer", function -> function.param("name", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    skull(arguments).setOwningPlayer(Bukkit.getOfflinePlayer(
                            NovaTypeSupport.argument(arguments, 1, String.class)));
                    return null;
                }));
        builder.extension(Skull.class, "setOwningPlayer", function -> function.param("uniqueId", UUID.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    skull(arguments).setOwningPlayer(Bukkit.getOfflinePlayer(
                            NovaTypeSupport.argument(arguments, 1, UUID.class)));
                    return null;
                }));
        builder.extension(Skull.class, "rotation", function -> function.returns(BlockFace.class)
                .invoke(arguments -> skull(arguments).getRotation()));
        builder.extension(Skull.class, "setRotation", function -> function.param("rotation", BlockFace.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    skull(arguments).setRotation(NovaTypeSupport.argument(arguments, 1, BlockFace.class));
                    return null;
                }));
        builder.extension(Skull.class, "setRotation", function -> function.param("rotation", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    BlockFace rotation = NovaTypeSupport.findEnum(BlockFace.class,
                            NovaTypeSupport.argument(arguments, 1, String.class));
                    if (rotation != null) {
                        skull(arguments).setRotation(rotation);
                    }
                    return null;
                }));
        builder.extension(Skull.class, "skullType", function -> function.returns(SkullType.class)
                .invoke(arguments -> skull(arguments).getSkullType()));
        builder.extension(Skull.class, "setSkullType", function -> function.param("type", SkullType.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    skull(arguments).setSkullType(NovaTypeSupport.argument(arguments, 1, SkullType.class));
                    return null;
                }));
        builder.extension(Skull.class, "setSkullType", function -> function.param("type", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    SkullType type = NovaTypeSupport.findEnum(SkullType.class,
                            NovaTypeSupport.argument(arguments, 1, String.class));
                    if (type != null) {
                        skull(arguments).setSkullType(type);
                    }
                    return null;
                }));
    }

    private static Skull skull(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Skull.class);
    }
}
