package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Tripwire BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Tripwire"}, methods = {"org.bukkit.block.data.type.Tripwire#isDisarmed", "org.bukkit.block.data.type.Tripwire#setDisarmed"})
public final class NovaBlockTripwire {
    private NovaBlockTripwire() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockTripwire.class, "org.bukkit.block.data.type.Tripwire");
        Method isDisarmed = NovaBlockDataReflection.method(type, "isDisarmed");
        Method setDisarmed = NovaBlockDataReflection.method(type, "setDisarmed", Boolean.TYPE);
        builder.extension(type, "isDisarmed", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isDisarmed, a[0])));
        builder.extension(type, "setDisarmed", f -> f.param("disarmed", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setDisarmed, a[0], a[1])));
    }
}
