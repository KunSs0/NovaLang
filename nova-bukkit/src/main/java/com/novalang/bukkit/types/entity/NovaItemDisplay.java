package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.Method;

/** 1.19.4+ ItemDisplay 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.ItemDisplay", "org.bukkit.entity.ItemDisplay$ItemDisplayTransform"}, methods = {"org.bukkit.entity.ItemDisplay#getItemStack", "org.bukkit.entity.ItemDisplay#setItemStack", "org.bukkit.entity.ItemDisplay#getItemDisplayTransform", "org.bukkit.entity.ItemDisplay#setItemDisplayTransform"})
public final class NovaItemDisplay {
    private static final String TYPE="org.bukkit.entity.ItemDisplay", TRANSFORM="org.bukkit.entity.ItemDisplay$ItemDisplayTransform";
    private NovaItemDisplay() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaEntityReflection.type(NovaItemDisplay.class,TYPE),x=NovaEntityReflection.type(NovaItemDisplay.class,TRANSFORM);
        Method item=NovaEntityReflection.method(t,"getItemStack"),setItem=NovaEntityReflection.method(t,"setItemStack",ItemStack.class),transform=NovaEntityReflection.method(t,"getItemDisplayTransform"),setTransform=NovaEntityReflection.method(t,"setItemDisplayTransform",x);
        b.extension(t,"itemStack",f->f.returns(ItemStack.class).invoke(a->NovaEntityReflection.invoke(item,a[0])));
        b.extension(t,"setItemStack",f->f.param("item",ItemStack.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setItem,a[0],a[1])));
        b.extension(t,"itemDisplayTransform",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaEntityReflection.invoke(transform,a[0])));
        b.extension(t,"setItemDisplayTransform",f->f.param("transform",JavaTypeRef.javaType(x)).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setTransform,a[0],a[1])));
        b.extension(t,"setItemDisplayTransform",f->f.param("transform",String.class).returns(Void.TYPE).invoke(a->setTransform(setTransform,x,a[0],(String)a[1])));
    }
    private static Object setTransform(Method method,Class<?> type,Object target,String name){Object value=NovaEntityReflection.enumValue(type,name);if(value!=null){return NovaEntityReflection.invoke(method,target,value);}return null;}
}
