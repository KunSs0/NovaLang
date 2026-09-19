package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

/** 1.19.4+ ItemDisplay 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.ItemDisplay", "org.bukkit.entity.ItemDisplay$ItemDisplayTransform"}, methods = {"org.bukkit.entity.ItemDisplay#getItemStack", "org.bukkit.entity.ItemDisplay#setItemStack", "org.bukkit.entity.ItemDisplay#getItemDisplayTransform", "org.bukkit.entity.ItemDisplay#setItemDisplayTransform"})
public final class NovaItemDisplay {
    private static final String TYPE = "org.bukkit.entity.ItemDisplay";
    private static final String TRANSFORM = "org.bukkit.entity.ItemDisplay$ItemDisplayTransform";
    private NovaItemDisplay() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaItemDisplay.class, TYPE); Class<?> transform = PaperEntityReflection.type(NovaItemDisplay.class, TRANSFORM); Method item = PaperEntityReflection.method(type, "getItemStack"); Method setItem = PaperEntityReflection.method(type, "setItemStack", ItemStack.class); Method getTransform = PaperEntityReflection.method(type, "getItemDisplayTransform"); Method setTransform = PaperEntityReflection.method(type, "setItemDisplayTransform", transform);
        builder.extension(type, "itemStack", function -> function.returns(ItemStack.class).invoke(arguments -> PaperEntityReflection.invoke(item, arguments[0]))); builder.extension(type, "setItemStack", function -> function.param("item", ItemStack.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setItem, arguments[0], arguments[1]))); builder.extension(type, "itemDisplayTransform", function -> function.returns(JavaTypeRef.javaType(transform)).invoke(arguments -> PaperEntityReflection.invoke(getTransform, arguments[0]))); builder.extension(type, "setItemDisplayTransform", function -> function.param("transform", JavaTypeRef.javaType(transform)).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setTransform, arguments[0], arguments[1]))); builder.extension(type, "setItemDisplayTransform", function -> function.param("transform", String.class).returns(Void.TYPE).invoke(arguments -> setTransform(setTransform, transform, arguments[0], (String) arguments[1])));
    }
    private static Object setTransform(Method method, Class<?> type, Object target, String name) { Object value = PaperEntityReflection.enumValue(type, name); if (value != null) { return PaperEntityReflection.invoke(method, target, value); } return null; }
}
