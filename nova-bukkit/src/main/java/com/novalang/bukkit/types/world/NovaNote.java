package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Note;

/** Spigot 1.12.2 音符值对象的 Fluxon 函数别名。 */
public final class NovaNote {

    private NovaNote() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Note.class, "sharped", function -> function.returns(Note.class)
                .invoke(arguments -> note(arguments).sharped()));
        builder.extension(Note.class, "flattened", function -> function.returns(Note.class)
                .invoke(arguments -> note(arguments).flattened()));
        builder.extension(Note.class, "id", function -> function.returns(Integer.class)
                .invoke(arguments -> (int) note(arguments).getId()));
        builder.extension(Note.class, "octave", function -> function.returns(Integer.class)
                .invoke(arguments -> note(arguments).getOctave()));
        builder.extension(Note.class, "tone", function -> function.returns(Note.Tone.class)
                .invoke(arguments -> note(arguments).getTone()));
        builder.extension(Note.class, "isSharped", function -> function.returns(Boolean.class)
                .invoke(arguments -> note(arguments).isSharped()));
    }

    private static Note note(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Note.class);
    }
}
