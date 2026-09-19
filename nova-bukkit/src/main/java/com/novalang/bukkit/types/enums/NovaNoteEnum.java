package com.novalang.bukkit.types.enums;
import com.novalang.bukkit.Requires; import com.novalang.runtime.host.JavaTypes; import org.bukkit.Note;
@Requires(classes = {"org.bukkit.Note$Tone"}) public final class NovaNoteEnum { private NovaNoteEnum() { } public static void register(JavaTypes.Builder b) { NovaEnum.registerEnum(b,"noteTone",Note.Tone.class); } }
