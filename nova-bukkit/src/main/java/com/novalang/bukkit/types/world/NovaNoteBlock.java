package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Note;
import org.bukkit.block.NoteBlock;

/** Spigot 1.12.2 音符盒方块状态的 Fluxon 函数别名。 */
public final class NovaNoteBlock {

    private NovaNoteBlock() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(NoteBlock.class, "note", function -> function.returns(Note.class)
                .invoke(arguments -> noteBlock(arguments).getNote()));
        builder.extension(NoteBlock.class, "setNote", function -> function.param("note", Note.class).returns(Void.TYPE).invoke(arguments -> {
            noteBlock(arguments).setNote(argument(arguments, 1, Note.class));
            return null;
        }));
    }

    private static NoteBlock noteBlock(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, NoteBlock.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
