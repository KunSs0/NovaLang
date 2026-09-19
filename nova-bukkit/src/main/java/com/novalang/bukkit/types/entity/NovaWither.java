package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.entity.LivingEntity;
/** Wither 头部目标与无敌帧的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.entity.Wither","org.bukkit.entity.Wither$Head"},methods={"org.bukkit.entity.Wither#setTarget","org.bukkit.entity.Wither#getTarget","org.bukkit.entity.Wither#getInvulnerabilityTicks","org.bukkit.entity.Wither#setInvulnerabilityTicks"})
public final class NovaWither {
    private NovaWither() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaEntityReflection.type(NovaWither.class,"org.bukkit.entity.Wither"); Class<?> h=NovaEntityReflection.type(NovaWither.class,"org.bukkit.entity.Wither$Head");
        Method setDefault=NovaEntityReflection.method(t,"setTarget",LivingEntity.class),setHead=NovaEntityReflection.method(t,"setTarget",h,LivingEntity.class),getHead=NovaEntityReflection.method(t,"getTarget",h),getTicks=NovaEntityReflection.method(t,"getInvulnerabilityTicks"),setTicks=NovaEntityReflection.method(t,"setInvulnerabilityTicks",Integer.TYPE); JavaTypeRef head=JavaTypeRef.javaType(h);
        b.extension(t,"setTarget",f->f.param("target",LivingEntity.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setDefault,a[0],a[1])));
        b.extension(t,"setTarget",f->f.param("head",head).param("target",LivingEntity.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setHead,a[0],a[1],a[2])));
        b.extension(t,"setTarget",f->f.param("head",String.class).param("target",LivingEntity.class).returns(Void.TYPE).invoke(a->setHead(setHead,h,a[0],(String)a[1],a[2])));
        b.extension(t,"getTarget",f->f.param("head",head).returns(JavaTypeRef.javaType(LivingEntity.class).nullable()).invoke(a->NovaEntityReflection.invoke(getHead,a[0],a[1])));
        b.extension(t,"getTarget",f->f.param("head",String.class).returns(JavaTypeRef.javaType(LivingEntity.class).nullable()).invoke(a->getHead(getHead,h,a[0],(String)a[1])));
        b.extension(t,"invulnerabilityTicks",f->f.returns(Integer.class).invoke(a->NovaEntityReflection.invoke(getTicks,a[0])));
        b.extension(t,"setInvulnerabilityTicks",f->f.param("ticks",Integer.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setTicks,a[0],a[1])));
    }
    private static Object setHead(Method method,Class<?> head,Object target,String name,Object entity){Object value=NovaEntityReflection.enumValue(head,name);if(value==null)return null;return NovaEntityReflection.invoke(method,target,value,entity);}
    private static Object getHead(Method method,Class<?> head,Object target,String name){Object value=NovaEntityReflection.enumValue(head,name);if(value==null)return null;return NovaEntityReflection.invoke(method,target,value);}
}
