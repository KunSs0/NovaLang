package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.13+ Dispenser BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Dispenser"}, methods = {"org.bukkit.block.data.type.Dispenser#isTriggered", "org.bukkit.block.data.type.Dispenser#setTriggered"})
public final class NovaBlockDispenser {
    private NovaBlockDispenser() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockDispenser.class, "org.bukkit.block.data.type.Dispenser");
        Method isTriggered = NovaBlockDataReflection.method(type, "isTriggered"); Method setTriggered = NovaBlockDataReflection.method(type, "setTriggered", Boolean.TYPE);
        builder.extension(type, "isTriggered", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isTriggered, a[0])));
        builder.extension(type, "setTriggered", f -> f.param("triggered", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setTriggered, a[0], a[1])));
    }
}
