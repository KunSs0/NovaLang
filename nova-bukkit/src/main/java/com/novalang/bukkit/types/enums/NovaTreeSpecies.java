package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.TreeSpecies;

/** Spigot 1.12.2 TreeSpecies 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaTreeSpecies {

    private NovaTreeSpecies() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(TreeSpecies.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) treeSpecies(arguments).getData()));
        builder.extension(TreeSpecies.class, "getByData", function -> function
                .param("data", Integer.class)
                .returns(JavaTypeRef.javaType(TreeSpecies.class).nullable())
                .invoke(arguments -> TreeSpecies.getByData(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static TreeSpecies treeSpecies(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, TreeSpecies.class);
    }
}
