package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Art;
import org.bukkit.entity.Painting;
@Requires(classes = {"org.bukkit.entity.Painting"})
public final class NovaPainting {
    private NovaPainting() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Painting.class, "art", function -> function.returns(Art.class).invoke(arguments -> event(arguments).getArt()));
        builder.extension(Painting.class, "setArt", function -> function.param("art", Art.class).returns(Boolean.class).invoke(arguments -> event(arguments).setArt(NovaTypeSupport.argument(arguments, 1, Art.class))));
        builder.extension(Painting.class, "setArt", function -> function.param("art", Art.class).param("force", Boolean.class).returns(Boolean.class).invoke(arguments -> event(arguments).setArt(NovaTypeSupport.argument(arguments, 1, Art.class), NovaTypeSupport.argument(arguments, 2, Boolean.class))));
    }
    private static Painting event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, Painting.class); }
}
