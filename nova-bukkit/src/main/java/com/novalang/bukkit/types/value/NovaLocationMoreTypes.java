package com.novalang.bukkit.types.value;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

@Requires(classes = {"org.bukkit.Location"})
public final class NovaLocationMoreTypes {

    private NovaLocationMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Location.class, "block", function -> function.returns(Block.class).invoke(arguments -> ((Location) arguments[0]).getBlock()));
        builder.extension(Location.class, "chunk", function -> function.returns(Chunk.class).invoke(arguments -> ((Location) arguments[0]).getChunk()));
        builder.extension(Location.class, "toString", function -> function.returns(String.class).invoke(arguments -> ((Location) arguments[0]).toString()));
    }
}
