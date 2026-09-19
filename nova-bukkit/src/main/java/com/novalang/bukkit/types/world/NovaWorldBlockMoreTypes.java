package com.novalang.bukkit.types.world;
import com.novalang.runtime.host.JavaTypes; import com.novalang.bukkit.types.value.NovaLocationMoreTypes;
/** World、区块、方块与位置的补充值对象聚合器。 */
public final class NovaWorldBlockMoreTypes { private NovaWorldBlockMoreTypes() { } public static void register(JavaTypes.Builder b) { NovaLocationMoreTypes.register(b); } }
