package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.AnimalTamer;
import java.lang.reflect.Method;

/** 1.14+ Fox 及 Type 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Fox", "org.bukkit.entity.Fox$Type"}, methods = {"org.bukkit.entity.Fox#getFoxType", "org.bukkit.entity.Fox#setFoxType", "org.bukkit.entity.Fox#isCrouching", "org.bukkit.entity.Fox#setCrouching", "org.bukkit.entity.Fox#setSleeping", "org.bukkit.entity.Fox#getFirstTrustedPlayer", "org.bukkit.entity.Fox#setFirstTrustedPlayer", "org.bukkit.entity.Fox#getSecondTrustedPlayer", "org.bukkit.entity.Fox#setSecondTrustedPlayer", "org.bukkit.entity.Fox#isFaceplanted"})
public final class NovaFox {
    private static final String TYPE="org.bukkit.entity.Fox", FOX_TYPE="org.bukkit.entity.Fox$Type";
    private NovaFox() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaEntityReflection.type(NovaFox.class,TYPE),ft=NovaEntityReflection.type(NovaFox.class,FOX_TYPE);
        Method type=NovaEntityReflection.method(t,"getFoxType"),setType=NovaEntityReflection.method(t,"setFoxType",ft),crouching=NovaEntityReflection.method(t,"isCrouching"),setCrouching=NovaEntityReflection.method(t,"setCrouching",Boolean.TYPE),setSleeping=NovaEntityReflection.method(t,"setSleeping",Boolean.TYPE),first=NovaEntityReflection.method(t,"getFirstTrustedPlayer"),setFirst=NovaEntityReflection.method(t,"setFirstTrustedPlayer",AnimalTamer.class),second=NovaEntityReflection.method(t,"getSecondTrustedPlayer"),setSecond=NovaEntityReflection.method(t,"setSecondTrustedPlayer",AnimalTamer.class),faceplanted=NovaEntityReflection.method(t,"isFaceplanted");
        JavaTypeRef nullableTamer=JavaTypeRef.javaType(AnimalTamer.class).nullable();
        b.extension(t,"foxType",f->f.returns(JavaTypeRef.javaType(ft)).invoke(a->NovaEntityReflection.invoke(type,a[0])));
        b.extension(t,"setFoxType",f->f.param("type",JavaTypeRef.javaType(ft)).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setType,a[0],a[1])));
        b.extension(t,"setFoxType",f->f.param("type",String.class).returns(Void.TYPE).invoke(a->setType(setType,ft,a[0],(String)a[1])));
        b.extension(t,"isCrouching",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(crouching,a[0]))); b.extension(t,"setCrouching",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setCrouching,a[0],a[1]))); b.extension(t,"setSleeping",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setSleeping,a[0],a[1])));
        b.extension(t,"firstTrustedPlayer",f->f.returns(nullableTamer).invoke(a->NovaEntityReflection.invoke(first,a[0]))); b.extension(t,"setFirstTrustedPlayer",f->f.param("player",nullableTamer).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setFirst,a[0],a[1])));
        b.extension(t,"secondTrustedPlayer",f->f.returns(nullableTamer).invoke(a->NovaEntityReflection.invoke(second,a[0]))); b.extension(t,"setSecondTrustedPlayer",f->f.param("player",nullableTamer).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setSecond,a[0],a[1])));
        b.extension(t,"isFaceplanted",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(faceplanted,a[0])));
    }
    private static Object setType(Method method,Class<?> type,Object target,String name) { Object value=NovaEntityReflection.enumValue(type,name); if(value!=null){return NovaEntityReflection.invoke(method,target,value);} return null; }
}
