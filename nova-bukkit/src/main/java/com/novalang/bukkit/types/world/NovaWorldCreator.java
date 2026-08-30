package com.novalang.bukkit.types.world;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;

/** WorldCreator 的全局构造与静态查询入口。 */
public final class NovaWorldCreator {

    private NovaWorldCreator() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.globalFunction("worldCreator", function -> function
                .param("name", String.class)
                .returns(WorldCreator.class)
                .invoke1(String.class, WorldCreator::name));
        builder.globalFunction("worldGeneratorForName", function -> function
                .param("world", String.class)
                .param("name", String.class)
                .param("output", CommandSender.class)
                .returns(JavaTypeRef.javaType(ChunkGenerator.class).nullable())
                .invoke(arguments -> WorldCreator.getGeneratorForName(
                        (String) arguments[0],
                        (String) arguments[1],
                        (CommandSender) arguments[2])));
    }
}
