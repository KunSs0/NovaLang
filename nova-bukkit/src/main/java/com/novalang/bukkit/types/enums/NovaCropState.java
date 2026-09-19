package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.CropState;

/** Spigot 1.12.2 CropState 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaCropState {

    private NovaCropState() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CropState.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) cropState(arguments).getData()));
        builder.extension(CropState.class, "getByData", function -> function
                .param("data", Integer.class)
                .returns(JavaTypeRef.javaType(CropState.class).nullable())
                .invoke(arguments -> CropState.getByData(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static CropState cropState(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CropState.class);
    }
}
