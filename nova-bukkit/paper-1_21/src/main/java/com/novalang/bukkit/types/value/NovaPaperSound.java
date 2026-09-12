package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import java.util.Locale;

/** Paper 1.21 Sound 注册器，使用 Bukkit 注册表解析音效。 */
public final class NovaPaperSound {

    private NovaPaperSound() {
    }

    /** 注册 Paper 1.21 的 sound 与 soundOrNull 全局函数。 */
    public static void register(JavaTypes.Builder builder) {
        builder.globalFunction("sound", function -> function
                .param("name", String.class)
                .returns(Sound.class)
                .invoke1(String.class, NovaPaperSound::requireSound));
        builder.globalFunction("soundOrNull", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(Sound.class).nullable())
                .invoke1(String.class, NovaPaperSound::findSound));
    }

    private static Sound requireSound(String value) {
        Sound sound = findSound(value);
        if (sound == null) {
            throw new IllegalArgumentException("音效不存在: " + value);
        }
        return sound;
    }

    private static Sound findSound(String value) {
        String normalized = value.trim().replace(' ', '_').toLowerCase(Locale.ROOT);
        NamespacedKey directKey = NamespacedKey.fromString(normalized);
        if (directKey != null) {
            Sound direct = Registry.SOUNDS.get(directKey);
            if (direct != null) {
                return direct;
            }
        }
        String enumName = normalized.replace('.', '_');
        for (Sound sound : Registry.SOUNDS) {
            String key = sound.getKey().getKey().replace('.', '_');
            if (key.equalsIgnoreCase(enumName) || sound.getKey().toString().equalsIgnoreCase(normalized)) {
                return sound;
            }
        }
        return null;
    }
}
