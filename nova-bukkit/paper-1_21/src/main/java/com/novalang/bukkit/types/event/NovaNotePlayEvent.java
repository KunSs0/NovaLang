package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.event.block.NotePlayEvent;

/** 音符盒播放事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.NotePlayEvent"})
public final class NovaNotePlayEvent {

    private NovaNotePlayEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(NotePlayEvent.class, "instrument", function -> function
                .returns(Instrument.class)
                .invoke(arguments -> event(arguments).getInstrument()));
        builder.extension(NotePlayEvent.class, "note", function -> function
                .returns(Note.class)
                .invoke(arguments -> event(arguments).getNote()));
        builder.extension(NotePlayEvent.class, "setInstrument", function -> function
                .param("instrument", Instrument.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setInstrument(argument(arguments, 1, Instrument.class));
                    return null;
                }));
        builder.extension(NotePlayEvent.class, "setNote", function -> function
                .param("note", Note.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setNote(argument(arguments, 1, Note.class));
                    return null;
                }));
    }

    private static NotePlayEvent event(Object[] arguments) {
        return argument(arguments, 0, NotePlayEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
