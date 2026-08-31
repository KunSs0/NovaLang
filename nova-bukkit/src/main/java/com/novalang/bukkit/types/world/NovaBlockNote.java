package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.lang.reflect.Method;

/** 1.13+ NoteBlock BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.NoteBlock"}, methods = {
        "org.bukkit.block.data.type.NoteBlock#getInstrument",
        "org.bukkit.block.data.type.NoteBlock#setInstrument",
        "org.bukkit.block.data.type.NoteBlock#getNote",
        "org.bukkit.block.data.type.NoteBlock#setNote"})
public final class NovaBlockNote {

    private static final String NOTE_BLOCK = "org.bukkit.block.data.type.NoteBlock";

    private NovaBlockNote() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> noteBlockType = NovaBlockDataReflection.type(NovaBlockNote.class, NOTE_BLOCK);
        Method getInstrument = NovaBlockDataReflection.method(noteBlockType, "getInstrument");
        Method setInstrument = NovaBlockDataReflection.method(noteBlockType, "setInstrument", Instrument.class);
        Method getNote = NovaBlockDataReflection.method(noteBlockType, "getNote");
        Method setNote = NovaBlockDataReflection.method(noteBlockType, "setNote", Note.class);

        builder.extension(noteBlockType, "instrument", function -> function
                .returns(Instrument.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getInstrument, arguments[0])));
        builder.extension(noteBlockType, "setInstrument", function -> function
                .param("instrument", Instrument.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setInstrument, arguments[0], arguments[1])));
        builder.extension(noteBlockType, "note", function -> function
                .returns(Note.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getNote, arguments[0])));
        builder.extension(noteBlockType, "setNote", function -> function
                .param("note", Note.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setNote, arguments[0], arguments[1])));
        builder.extension(noteBlockType, "setNote", function -> function
                .param("note", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Note note = parseNote((String) arguments[1]);
                    if (note != null) {
                        NovaBlockDataReflection.invoke(setNote, arguments[0], note);
                    }
                    return null;
                }));
    }

    private static Note parseNote(String value) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        try {
            return new Note(Integer.parseInt(text));
        } catch (IllegalArgumentException ignored) {
            // 继续按音名解析。
        }
        if (text.length() < 2) {
            return null;
        }
        char name = Character.toUpperCase(text.charAt(0));
        Note.Tone tone;
        try {
            tone = Note.Tone.valueOf(String.valueOf(name));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        int index = 1;
        boolean sharp = false;
        boolean flat = false;
        if (index < text.length()) {
            char accidental = text.charAt(index);
            if (accidental == '#') {
                sharp = true;
                index++;
            } else if (accidental == 'b' || accidental == 'B') {
                flat = true;
                index++;
            }
        }
        int octave = 0;
        if (index < text.length()) {
            try {
                octave = Integer.parseInt(text.substring(index));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        try {
            if (sharp) {
                return Note.sharp(octave, tone);
            }
            if (flat) {
                return Note.flat(octave, tone);
            }
            return Note.natural(octave, tone);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
