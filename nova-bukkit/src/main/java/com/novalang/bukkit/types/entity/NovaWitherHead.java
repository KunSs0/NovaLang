package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
/** Wither.Head 的 Fluxon 枚举查询入口。 */
@Requires(classes={"org.bukkit.entity.Wither$Head"})
public final class NovaWitherHead { private NovaWitherHead(){} public static void register(JavaTypes.Builder b){NovaEntityReflection.registerEnum(b,"witherHead",NovaEntityReflection.type(NovaWitherHead.class,"org.bukkit.entity.Wither$Head"));}}
