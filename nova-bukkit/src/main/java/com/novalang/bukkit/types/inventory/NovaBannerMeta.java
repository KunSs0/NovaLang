package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.List;

/** 旗帜物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.BannerMeta"})
public final class NovaBannerMeta {

    private NovaBannerMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef patterns = JavaTypeRef.listOf(JavaTypeRef.javaType(Pattern.class));
        builder.extension(BannerMeta.class, "patterns", function -> function
                .returns(patterns)
                .invoke(arguments -> meta(arguments).getPatterns()));
        builder.extension(BannerMeta.class, "setPatterns", function -> function
                .param("patterns", patterns)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setPatterns(patterns(arguments, 1));
                    return null;
                }));
        builder.extension(BannerMeta.class, "addPattern", function -> function
                .param("pattern", Pattern.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).addPattern(argument(arguments, 1, Pattern.class));
                    return null;
                }));
        builder.extension(BannerMeta.class, "getPattern", function -> function
                .param("index", Integer.class)
                .returns(Pattern.class)
                .invoke(arguments -> meta(arguments).getPattern(argument(arguments, 1, Integer.class))));
        builder.extension(BannerMeta.class, "removePattern", function -> function
                .param("index", Integer.class)
                .returns(Pattern.class)
                .invoke(arguments -> meta(arguments).removePattern(argument(arguments, 1, Integer.class))));
        builder.extension(BannerMeta.class, "setPattern", function -> function
                .param("index", Integer.class)
                .param("pattern", Pattern.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setPattern(argument(arguments, 1, Integer.class), argument(arguments, 2, Pattern.class));
                    return null;
                }));
        builder.extension(BannerMeta.class, "numberOfPatterns", function -> function
                .returns(Integer.class)
                .invoke(arguments -> meta(arguments).numberOfPatterns()));
    }

    private static BannerMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BannerMeta.class);
    }

    @SuppressWarnings("unchecked")
    private static List<Pattern> patterns(Object[] arguments, int index) {
        return (List<Pattern>) NovaTypeSupport.argument(arguments, index, List.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
