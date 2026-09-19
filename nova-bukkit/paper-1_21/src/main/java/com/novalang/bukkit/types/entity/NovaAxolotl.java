package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.17+ Axolotl 及 Variant 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Axolotl", "org.bukkit.entity.Axolotl$Variant"}, methods = {"org.bukkit.entity.Axolotl#isPlayingDead", "org.bukkit.entity.Axolotl#setPlayingDead", "org.bukkit.entity.Axolotl#getVariant", "org.bukkit.entity.Axolotl#setVariant"})
public final class NovaAxolotl {
    private static final String TYPE = "org.bukkit.entity.Axolotl";
    private static final String VARIANT = "org.bukkit.entity.Axolotl$Variant";
    private NovaAxolotl() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaAxolotl.class, TYPE); Class<?> variant = PaperEntityReflection.type(NovaAxolotl.class, VARIANT);
        Method playingDead = PaperEntityReflection.method(type, "isPlayingDead"); Method setPlayingDead = PaperEntityReflection.method(type, "setPlayingDead", Boolean.TYPE); Method getVariant = PaperEntityReflection.method(type, "getVariant"); Method setVariant = PaperEntityReflection.method(type, "setVariant", variant); JavaTypeRef variantRef = JavaTypeRef.javaType(variant);
        builder.extension(type, "isPlayingDead", function -> function.returns(Boolean.class).invoke(arguments -> PaperEntityReflection.invoke(playingDead, arguments[0])));
        builder.extension(type, "setPlayingDead", function -> function.param("value", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setPlayingDead, arguments[0], arguments[1])));
        builder.extension(type, "variant", function -> function.returns(variantRef).invoke(arguments -> PaperEntityReflection.invoke(getVariant, arguments[0])));
        builder.extension(type, "setVariant", function -> function.param("variant", variantRef).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setVariant, arguments[0], arguments[1])));
        builder.extension(type, "setVariant", function -> function.param("variant", String.class).returns(Void.TYPE).invoke(arguments -> setVariant(setVariant, variant, arguments[0], (String) arguments[1])));
    }
    private static Object setVariant(Method setter, Class<?> variant, Object target, String name) { Object value = PaperEntityReflection.enumValue(variant, name); if (value == null) { return null; } return PaperEntityReflection.invoke(setter, target, value); }
}
