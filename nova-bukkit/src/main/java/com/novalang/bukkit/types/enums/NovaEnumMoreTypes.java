package com.novalang.bukkit.types.enums;
import com.novalang.bukkit.NovaBukkitRegistrar; import com.novalang.runtime.host.JavaTypes;
/** Spigot 1.12.2 补充枚举注册器。 */
public final class NovaEnumMoreTypes { private NovaEnumMoreTypes() { } public static void register(JavaTypes.Builder b) { NovaBukkitRegistrar.register(b,NovaEnchantmentEnum.class,NovaEnchantmentEnum::register); NovaBukkitRegistrar.register(b,NovaNoteEnum.class,NovaNoteEnum::register); NovaBukkitRegistrar.register(b,NovaConversationEnum.class,NovaConversationEnum::register); } }
