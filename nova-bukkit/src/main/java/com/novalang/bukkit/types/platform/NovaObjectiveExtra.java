package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.scoreboard.Objective;

/** 计分板目标状态操作的可选编译期别名。 */
@Requires(classes = {"org.bukkit.scoreboard.Objective"})
public final class NovaObjectiveExtra {

    private NovaObjectiveExtra() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Objective.class, "isModifiable", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> objective(arguments).isModifiable()));
        builder.extension(Objective.class, "unregister", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    objective(arguments).unregister();
                    return null;
                }));
    }

    private static Objective objective(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Objective.class);
    }
}
