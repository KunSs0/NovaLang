package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

/** Bukkit World 全局查询入口。 */
final class BukkitWorldJavaTypes {

    private BukkitWorldJavaTypes() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableWorld = JavaTypeRef.javaType(World.class).nullable();
        builder.globalFunction("world", function -> function
                .param("id", UUID.class)
                .returns(nullableWorld)
                .invoke1(UUID.class, Bukkit::getWorld));
        builder.globalFunction("world", function -> function
                .param("name", String.class)
                .returns(nullableWorld)
                .invoke1(String.class, Bukkit::getWorld));
        builder.globalFunction("worlds", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(World.class)))
                .invoke0(Bukkit::getWorlds));
    }
}
