package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;

import java.util.List;

@Requires(classes = {"org.bukkit.block.Banner"})
public final class NovaBanner {
    private NovaBanner() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(Banner.class, "baseColor", f -> f.returns(DyeColor.class).invoke(a -> e(a).getBaseColor()));
        b.extension(Banner.class, "setBaseColor", f -> f.param("color", DyeColor.class).returns(Void.TYPE).invoke(a -> { e(a).setBaseColor(NovaTypeSupport.argument(a, 1, DyeColor.class)); return null; }));
        b.extension(Banner.class, "setBaseColor", f -> f.param("color", String.class).returns(Void.TYPE).invoke(a -> {
            DyeColor color = NovaTypeSupport.findEnum(DyeColor.class, NovaTypeSupport.argument(a, 1, String.class));
            if (color != null) {
                e(a).setBaseColor(color);
            }
            return null;
        }));
        b.extension(Banner.class, "patterns", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Pattern.class))).invoke(a -> e(a).getPatterns()));
        b.extension(Banner.class, "setPatterns", f -> f.param("patterns", List.class).returns(Void.TYPE).invoke(a -> {
            e(a).setPatterns(patterns(a));
            return null;
        }));
        b.extension(Banner.class, "addPattern", f -> f.param("pattern", Pattern.class).returns(Void.TYPE).invoke(a -> { e(a).addPattern(NovaTypeSupport.argument(a, 1, Pattern.class)); return null; }));
        b.extension(Banner.class, "getPattern", f -> f.param("index", Integer.class).returns(Pattern.class).invoke(a -> e(a).getPattern(NovaTypeSupport.argument(a, 1, Integer.class))));
        b.extension(Banner.class, "removePattern", f -> f.param("index", Integer.class).returns(Pattern.class).invoke(a -> e(a).removePattern(NovaTypeSupport.argument(a, 1, Integer.class))));
        b.extension(Banner.class, "setPattern", f -> f.param("index", Integer.class).param("pattern", Pattern.class).returns(Void.TYPE).invoke(a -> {
            e(a).setPattern(NovaTypeSupport.argument(a, 1, Integer.class), NovaTypeSupport.argument(a, 2, Pattern.class));
            return null;
        }));
        b.extension(Banner.class, "numberOfPatterns", f -> f.returns(Integer.class).invoke(a -> e(a).numberOfPatterns()));
    }

    @SuppressWarnings("unchecked")
    private static List<Pattern> patterns(Object[] arguments) {
        return (List<Pattern>) NovaTypeSupport.argument(arguments, 1, List.class);
    }

    private static Banner e(Object[] a) { return NovaTypeSupport.argument(a, 0, Banner.class); }
}
