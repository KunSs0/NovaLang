package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires; import com.novalang.bukkit.types.value.NovaTypeSupport; import com.novalang.runtime.host.JavaTypes; import org.bukkit.DyeColor; import org.bukkit.block.ShulkerBox;
@Requires(classes = {"org.bukkit.block.ShulkerBox"}) public final class NovaShulkerBox { private NovaShulkerBox() { } public static void register(JavaTypes.Builder b) { b.extension(ShulkerBox.class,"color",f->f.returns(DyeColor.class).invoke(a->NovaTypeSupport.argument(a,0,ShulkerBox.class).getColor())); } }
