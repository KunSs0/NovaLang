package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Note;

/** Spigot 1.12.2 Note.Tone 的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.Note$Tone"})
public final class NovaNoteTone {

    private NovaNoteTone() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Note.Tone.class, "getId", function -> function
                .param("sharped", Boolean.class)
                .returns(Integer.class)
                .invoke(arguments -> (int) tone(arguments).getId(
                        NovaTypeSupport.argument(arguments, 1, Boolean.class))));
        builder.extension(Note.Tone.class, "isSharpable", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> tone(arguments).isSharpable()));
        builder.extension(Note.Tone.class, "isSharped", function -> function
                .param("id", Integer.class)
                .returns(Boolean.class)
                .invoke(arguments -> tone(arguments).isSharped(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
        builder.extension(Note.Tone.class, "getById", function -> function
                .param("id", Integer.class)
                .returns(JavaTypeRef.javaType(Note.Tone.class).nullable())
                .invoke(arguments -> Note.Tone.getById(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static Note.Tone tone(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Note.Tone.class);
    }
}
