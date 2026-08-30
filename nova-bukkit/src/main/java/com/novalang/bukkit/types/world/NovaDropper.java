package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires; import com.novalang.bukkit.types.value.NovaTypeSupport; import com.novalang.runtime.host.JavaTypes; import org.bukkit.block.Dropper;
@Requires(classes = {"org.bukkit.block.Dropper"}) public final class NovaDropper { private NovaDropper() { } public static void register(JavaTypes.Builder b) { b.extension(Dropper.class,"drop",f->f.returns(Void.TYPE).invoke(a->{NovaTypeSupport.argument(a,0,Dropper.class).drop();return null;})); } }
