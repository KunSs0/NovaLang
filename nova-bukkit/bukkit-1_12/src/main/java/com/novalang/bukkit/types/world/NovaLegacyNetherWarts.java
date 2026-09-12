package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NetherWartsState;
import org.bukkit.material.NetherWarts;

/** 旧版 NetherWarts 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.NetherWarts"})
final class NovaLegacyNetherWarts {

    private NovaLegacyNetherWarts() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(NetherWarts.class, "state", function -> function.returns(NetherWartsState.class).invoke(arguments -> warts(arguments).getState()));
        builder.extension(NetherWarts.class, "setState", function -> function.param("state", NetherWartsState.class).returns(Void.TYPE)
                .invoke(arguments -> { warts(arguments).setState(NovaTypeSupport.argument(arguments, 1, NetherWartsState.class)); return null; }));
        builder.extension(NetherWarts.class, "setState", function -> function.param("state", String.class).returns(Void.TYPE)
                .invoke(arguments -> { setNamedState(warts(arguments), NovaTypeSupport.argument(arguments, 1, String.class)); return null; }));
        builder.extension(NetherWarts.class, "toString", function -> function.returns(String.class).invoke(arguments -> warts(arguments).toString()));
        builder.extension(NetherWarts.class, "clone", function -> function.returns(NetherWarts.class).invoke(arguments -> warts(arguments).clone()));
    }

    private static void setNamedState(NetherWarts warts, String value) {
        NetherWartsState state = NovaTypeSupport.findEnum(NetherWartsState.class, value);
        if (state != null) {
            warts.setState(state);
        }
    }

    private static NetherWarts warts(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, NetherWarts.class);
    }
}
