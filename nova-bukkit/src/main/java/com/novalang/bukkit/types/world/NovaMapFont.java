package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapFont;

/** MapFont 及 CharacterSprite 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.map.MapFont", "org.bukkit.map.MapFont$CharacterSprite"})
final class NovaMapFont {

    private NovaMapFont() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(MapFont.class, "setChar", function -> function
                .param("character", String.class)
                .param("sprite", MapFont.CharacterSprite.class)
                .invoke(arguments -> {
                    String character = argument(arguments, 1, String.class);
                    if (character.isEmpty()) {
                        throw new IllegalArgumentException("字符不能为空");
                    }
                    font(arguments).setChar(character.charAt(0), argument(arguments, 2, MapFont.CharacterSprite.class));
                    return null;
                }));
        builder.extension(MapFont.class, "getChar", function -> function
                .param("character", String.class)
                .returns(JavaTypeRef.javaType(MapFont.CharacterSprite.class).nullable())
                .invoke(arguments -> {
                    String character = argument(arguments, 1, String.class);
                    if (character.isEmpty()) {
                        return null;
                    }
                    return font(arguments).getChar(character.charAt(0));
                }));
        builder.extension(MapFont.class, "getWidth", function -> function
                .param("text", String.class).returns(Integer.class)
                .invoke(arguments -> font(arguments).getWidth(argument(arguments, 1, String.class))));
        builder.extension(MapFont.class, "height", function -> function
                .returns(Integer.class).invoke(arguments -> font(arguments).getHeight()));
        builder.extension(MapFont.class, "isValid", function -> function
                .param("text", String.class).returns(Boolean.class)
                .invoke(arguments -> font(arguments).isValid(argument(arguments, 1, String.class))));
        builder.extension(MapFont.CharacterSprite.class, "get", function -> function
                .param("row", Integer.class).param("column", Integer.class).returns(Boolean.class)
                .invoke(arguments -> sprite(arguments).get(argument(arguments, 1, Integer.class), argument(arguments, 2, Integer.class))));
        builder.extension(MapFont.CharacterSprite.class, "width", function -> function
                .returns(Integer.class).invoke(arguments -> sprite(arguments).getWidth()));
    }

    private static MapFont font(Object[] arguments) { return argument(arguments, 0, MapFont.class); }
    private static MapFont.CharacterSprite sprite(Object[] arguments) { return argument(arguments, 0, MapFont.CharacterSprite.class); }
    private static <T> T argument(Object[] arguments, int index, Class<T> type) { return NovaTypeSupport.argument(arguments, index, type); }
}
