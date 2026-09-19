package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Tameable;

/** Spigot 1.12.2 中 Ageable、AnimalTamer、Tameable 的别名。 */
public final class NovaEntityCommon {
    private NovaEntityCommon() { }

    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableTamer = JavaTypeRef.javaType(AnimalTamer.class).nullable();
        b.extension(Ageable.class, "age", f -> f.returns(Integer.class).invoke(a -> ageable(a).getAge()));
        b.extension(Ageable.class, "setAge", f -> f.param("age", Integer.class).returns(Void.TYPE).invoke(a -> { ageable(a).setAge(arg(a, 1, Integer.class)); return null; }));
        b.extension(Ageable.class, "ageLock", f -> f.returns(Boolean.class).invoke(a -> ageable(a).getAgeLock()));
        b.extension(Ageable.class, "setAgeLock", f -> f.param("locked", Boolean.class).returns(Void.TYPE).invoke(a -> { ageable(a).setAgeLock(arg(a, 1, Boolean.class)); return null; }));
        b.extension(Ageable.class, "setBaby", f -> f.returns(Void.TYPE).invoke(a -> { ageable(a).setBaby(); return null; }));
        b.extension(Ageable.class, "setAdult", f -> f.returns(Void.TYPE).invoke(a -> { ageable(a).setAdult(); return null; }));
        b.extension(Ageable.class, "isAdult", f -> f.returns(Boolean.class).invoke(a -> ageable(a).isAdult()));
        b.extension(Ageable.class, "canBreed", f -> f.returns(Boolean.class).invoke(a -> ageable(a).canBreed()));
        b.extension(Ageable.class, "setBreed", f -> f.param("breed", Boolean.class).returns(Void.TYPE).invoke(a -> { ageable(a).setBreed(arg(a, 1, Boolean.class)); return null; }));
        b.extension(AnimalTamer.class, "name", f -> f.returns(String.class).invoke(a -> tamer(a).getName()));
        b.extension(AnimalTamer.class, "uniqueId", f -> f.returns(java.util.UUID.class).invoke(a -> tamer(a).getUniqueId()));
        b.extension(Tameable.class, "isTamed", f -> f.returns(Boolean.class).invoke(a -> tameable(a).isTamed()));
        b.extension(Tameable.class, "setTamed", f -> f.param("tamed", Boolean.class).returns(Void.TYPE).invoke(a -> { tameable(a).setTamed(arg(a, 1, Boolean.class)); return null; }));
        b.extension(Tameable.class, "owner", f -> f.returns(nullableTamer).invoke(a -> tameable(a).getOwner()));
        b.extension(Tameable.class, "setOwner", f -> f.param("owner", nullableTamer).returns(Void.TYPE).invoke(a -> { tameable(a).setOwner(arg(a, 1, AnimalTamer.class)); return null; }));
    }

    private static Ageable ageable(Object[] a) { return NovaTypeSupport.argument(a, 0, Ageable.class); }
    private static AnimalTamer tamer(Object[] a) { return NovaTypeSupport.argument(a, 0, AnimalTamer.class); }
    private static Tameable tameable(Object[] a) { return NovaTypeSupport.argument(a, 0, Tameable.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
