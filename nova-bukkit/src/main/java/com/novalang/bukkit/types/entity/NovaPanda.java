package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.14+ Panda 及 Gene 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Panda", "org.bukkit.entity.Panda$Gene"}, methods = {"org.bukkit.entity.Panda#getMainGene", "org.bukkit.entity.Panda#setMainGene", "org.bukkit.entity.Panda#getHiddenGene", "org.bukkit.entity.Panda#setHiddenGene", "org.bukkit.entity.Panda#isRolling", "org.bukkit.entity.Panda#setRolling", "org.bukkit.entity.Panda#isSneezing", "org.bukkit.entity.Panda#setSneezing", "org.bukkit.entity.Panda#isOnBack", "org.bukkit.entity.Panda#setOnBack", "org.bukkit.entity.Panda#isEating", "org.bukkit.entity.Panda#setEating", "org.bukkit.entity.Panda#isScared", "org.bukkit.entity.Panda#getUnhappyTicks", "org.bukkit.entity.Panda$Gene#isRecessive"})
public final class NovaPanda {
    private static final String TYPE="org.bukkit.entity.Panda", GENE="org.bukkit.entity.Panda$Gene";
    private NovaPanda() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaEntityReflection.type(NovaPanda.class,TYPE),g=NovaEntityReflection.type(NovaPanda.class,GENE);
        Method main=NovaEntityReflection.method(t,"getMainGene"),setMain=NovaEntityReflection.method(t,"setMainGene",g),hidden=NovaEntityReflection.method(t,"getHiddenGene"),setHidden=NovaEntityReflection.method(t,"setHiddenGene",g),rolling=NovaEntityReflection.method(t,"isRolling"),setRolling=NovaEntityReflection.method(t,"setRolling",Boolean.TYPE),sneezing=NovaEntityReflection.method(t,"isSneezing"),setSneezing=NovaEntityReflection.method(t,"setSneezing",Boolean.TYPE),back=NovaEntityReflection.method(t,"isOnBack"),setBack=NovaEntityReflection.method(t,"setOnBack",Boolean.TYPE),eating=NovaEntityReflection.method(t,"isEating"),setEating=NovaEntityReflection.method(t,"setEating",Boolean.TYPE),scared=NovaEntityReflection.method(t,"isScared"),unhappy=NovaEntityReflection.method(t,"getUnhappyTicks"),recessive=NovaEntityReflection.method(g,"isRecessive");
        JavaTypeRef gene=JavaTypeRef.javaType(g);
        b.extension(t,"mainGene",f->f.returns(gene).invoke(a->NovaEntityReflection.invoke(main,a[0]))); b.extension(t,"setMainGene",f->f.param("gene",gene).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setMain,a[0],a[1])));
        b.extension(t,"hiddenGene",f->f.returns(gene).invoke(a->NovaEntityReflection.invoke(hidden,a[0]))); b.extension(t,"setHiddenGene",f->f.param("gene",gene).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setHidden,a[0],a[1])));
        b.extension(t,"isRolling",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(rolling,a[0]))); b.extension(t,"setRolling",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setRolling,a[0],a[1])));
        b.extension(t,"isSneezing",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(sneezing,a[0]))); b.extension(t,"setSneezing",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setSneezing,a[0],a[1])));
        b.extension(t,"isOnBack",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(back,a[0]))); b.extension(t,"setOnBack",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setBack,a[0],a[1])));
        b.extension(t,"isEating",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(eating,a[0]))); b.extension(t,"setEating",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setEating,a[0],a[1])));
        b.extension(t,"isScared",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(scared,a[0]))); b.extension(t,"unhappyTicks",f->f.returns(Integer.class).invoke(a->NovaEntityReflection.invoke(unhappy,a[0])));
        b.extension(g,"isRecessive",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(recessive,a[0])));
    }
}
