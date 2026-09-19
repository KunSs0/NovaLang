package com.novalang.bukkit.types.value;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Sound;

/** Bukkit 1.12 Sound 注册器，使用 1.12 枚举 API 解析音效。 */
public final class NovaBukkit112Sound {

    private NovaBukkit112Sound() {
    }

    /** 注册 Bukkit 1.12 的 sound 与 soundOrNull 全局函数。 */
    public static void register(JavaTypes.Builder builder) {
        builder.globalFunction("sound", function -> function
                .param("name", String.class)
                .returns(Sound.class)
                .invoke1(String.class, NovaBukkit112Sound::requireSound));
        builder.globalFunction("soundOrNull", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(Sound.class).nullable())
                .invoke1(String.class, NovaBukkit112Sound::findSound));
    }

    private static Sound requireSound(String value) {
        Sound sound = findSound(value);
        if (sound == null) {
            throw new IllegalArgumentException("音效不存在: " + value);
        }
        return sound;
    }

    private static Sound findSound(String value) {
        return NovaTypeSupport.findEnum(Sound.class, value);
    }
}
