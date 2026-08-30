package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.List;

/** Spigot 1.12.2 知识之书元数据的 Fluxon 函数别名。 */
public final class NovaKnowledgeBookMeta {

    private NovaKnowledgeBookMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef keys = JavaTypeRef.listOf(JavaTypeRef.javaType(NamespacedKey.class));
        builder.extension(KnowledgeBookMeta.class, "hasRecipes", function -> function.returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasRecipes()));
        builder.extension(KnowledgeBookMeta.class, "recipes", function -> function.returns(keys)
                .invoke(arguments -> meta(arguments).getRecipes()));
        builder.extension(KnowledgeBookMeta.class, "setRecipes", function -> function.param("recipes", keys).returns(Void.TYPE).invoke(arguments -> {
            meta(arguments).setRecipes(keys(arguments, 1));
            return null;
        }));
        builder.extension(KnowledgeBookMeta.class, "addRecipe", function -> function.param("recipe", NamespacedKey.class).returns(Void.TYPE).invoke(arguments -> {
            meta(arguments).addRecipe(argument(arguments, 1, NamespacedKey.class));
            return null;
        }));
    }

    private static KnowledgeBookMeta meta(Object[] arguments) {
        return argument(arguments, 0, KnowledgeBookMeta.class);
    }

    @SuppressWarnings("unchecked")
    private static List<NamespacedKey> keys(Object[] arguments, int index) {
        return (List<NamespacedKey>) arguments[index];
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
