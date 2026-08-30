package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/** 书籍物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.BookMeta"})
public final class NovaBookMeta {

    private NovaBookMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableGeneration = JavaTypeRef.javaType(BookMeta.Generation.class).nullable();
        JavaTypeRef pages = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class));
        builder.extension(BookMeta.class, "hasTitle", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasTitle()));
        builder.extension(BookMeta.class, "title", function -> function
                .returns(nullableString)
                .invoke(arguments -> meta(arguments).getTitle()));
        builder.extension(BookMeta.class, "setTitle", function -> function
                .param("title", nullableString)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).setTitle(argument(arguments, 1, String.class))));
        builder.extension(BookMeta.class, "hasAuthor", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasAuthor()));
        builder.extension(BookMeta.class, "author", function -> function
                .returns(nullableString)
                .invoke(arguments -> meta(arguments).getAuthor()));
        builder.extension(BookMeta.class, "setAuthor", function -> function
                .param("author", nullableString)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setAuthor(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(BookMeta.class, "hasGeneration", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasGeneration()));
        builder.extension(BookMeta.class, "generation", function -> function
                .returns(nullableGeneration)
                .invoke(arguments -> meta(arguments).getGeneration()));
        builder.extension(BookMeta.class, "setGeneration", function -> function
                .param("generation", nullableGeneration)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setGeneration(argument(arguments, 1, BookMeta.Generation.class));
                    return null;
                }));
        builder.extension(BookMeta.class, "hasPages", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasPages()));
        builder.extension(BookMeta.class, "getPage", function -> function
                .param("page", Integer.class)
                .returns(String.class)
                .invoke(arguments -> meta(arguments).getPage(argument(arguments, 1, Integer.class))));
        builder.extension(BookMeta.class, "setPage", function -> function
                .param("page", Integer.class)
                .param("content", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setPage(argument(arguments, 1, Integer.class), argument(arguments, 2, String.class));
                    return null;
                }));
        builder.extension(BookMeta.class, "pages", function -> function
                .returns(pages)
                .invoke(arguments -> meta(arguments).getPages()));
        builder.extension(BookMeta.class, "setPages", function -> function
                .param("pages", pages)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setPages(pages(arguments, 1));
                    return null;
                }));
        builder.extension(BookMeta.class, "pageCount", function -> function
                .returns(Integer.class)
                .invoke(arguments -> meta(arguments).getPageCount()));
        builder.extension(BookMeta.class, "clone", function -> function
                .returns(BookMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static BookMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BookMeta.class);
    }

    @SuppressWarnings("unchecked")
    private static List<String> pages(Object[] arguments, int index) {
        return (List<String>) NovaTypeSupport.argument(arguments, index, List.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
