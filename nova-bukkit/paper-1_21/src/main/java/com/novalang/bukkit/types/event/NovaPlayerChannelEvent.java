package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires; import com.novalang.bukkit.types.value.NovaTypeSupport; import com.novalang.runtime.host.JavaTypes; import org.bukkit.event.player.PlayerChannelEvent;
@Requires(classes = {"org.bukkit.event.player.PlayerChannelEvent"}) public final class NovaPlayerChannelEvent { private NovaPlayerChannelEvent() { } public static void register(JavaTypes.Builder b){b.extension(PlayerChannelEvent.class,"channel",f->f.returns(String.class).invoke(a->NovaTypeSupport.argument(a,0,PlayerChannelEvent.class).getChannel()));} }
