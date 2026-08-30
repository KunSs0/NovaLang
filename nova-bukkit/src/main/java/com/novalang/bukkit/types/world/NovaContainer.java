package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.inventory.Inventory;

/** Container 与 Lockable 方块状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.Container", "org.bukkit.block.Lockable"})
final class NovaContainer {

    private NovaContainer() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Container.class, "inventory", function -> function.returns(Inventory.class)
                .invoke(arguments -> container(arguments).getInventory()));
        builder.extension(Container.class, "snapshotInventory", function -> function.returns(Inventory.class)
                .invoke(arguments -> container(arguments).getSnapshotInventory()));
        builder.extension(Lockable.class, "isLocked", function -> function.returns(Boolean.class)
                .invoke(arguments -> lockable(arguments).isLocked()));
        builder.extension(Lockable.class, "lock", function -> function.returns(String.class)
                .invoke(arguments -> lockable(arguments).getLock()));
        builder.extension(Lockable.class, "setLock", function -> function.param("key", String.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    lockable(arguments).setLock(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static Container container(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Container.class);
    }

    private static Lockable lockable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Lockable.class);
    }
}
