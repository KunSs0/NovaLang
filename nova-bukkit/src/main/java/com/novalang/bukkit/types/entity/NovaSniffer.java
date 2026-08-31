package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import java.lang.reflect.Method;
import java.util.Collection;

/** 1.20+ Sniffer 及 State 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Sniffer", "org.bukkit.entity.Sniffer$State"}, methods = {"org.bukkit.entity.Sniffer#getExploredLocations", "org.bukkit.entity.Sniffer#removeExploredLocation", "org.bukkit.entity.Sniffer#addExploredLocation", "org.bukkit.entity.Sniffer#getState", "org.bukkit.entity.Sniffer#setState", "org.bukkit.entity.Sniffer#findPossibleDigLocation", "org.bukkit.entity.Sniffer#canDig"})
public final class NovaSniffer {
    private static final String TYPE = "org.bukkit.entity.Sniffer";
    private static final String STATE = "org.bukkit.entity.Sniffer$State";
    private NovaSniffer() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaSniffer.class, TYPE); Class<?> state = NovaEntityReflection.type(NovaSniffer.class, STATE);
        Method explored = NovaEntityReflection.method(type, "getExploredLocations"); Method remove = NovaEntityReflection.method(type, "removeExploredLocation", Location.class); Method add = NovaEntityReflection.method(type, "addExploredLocation", Location.class);
        Method getState = NovaEntityReflection.method(type, "getState"); Method setState = NovaEntityReflection.method(type, "setState", state); Method find = NovaEntityReflection.method(type, "findPossibleDigLocation"); Method canDig = NovaEntityReflection.method(type, "canDig");
        builder.extension(type, "exploredLocations", f -> f.returns(Collection.class).invoke(a -> NovaEntityReflection.invoke(explored, a[0])));
        builder.extension(type, "removeExploredLocation", f -> f.param("location", Location.class).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(remove, a[0], a[1])));
        builder.extension(type, "addExploredLocation", f -> f.param("location", Location.class).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(add, a[0], a[1])));
        builder.extension(type, "state", f -> f.returns(JavaTypeRef.javaType(state)).invoke(a -> NovaEntityReflection.invoke(getState, a[0])));
        builder.extension(type, "setState", f -> f.param("state", JavaTypeRef.javaType(state)).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setState, a[0], a[1])));
        builder.extension(type, "setState", f -> f.param("state", String.class).returns(Void.TYPE).invoke(a -> setState(setState, state, a[0], (String) a[1])));
        builder.extension(type, "findPossibleDigLocation", f -> f.returns(JavaTypeRef.javaType(Location.class).nullable()).invoke(a -> NovaEntityReflection.invoke(find, a[0])));
        builder.extension(type, "canDig", f -> f.returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(canDig, a[0])));
    }
    private static Object setState(Method method, Class<?> state, Object target, String name) { Object value = NovaEntityReflection.enumValue(state, name); if (value != null) { return NovaEntityReflection.invoke(method, target, value); } return null; }
}
