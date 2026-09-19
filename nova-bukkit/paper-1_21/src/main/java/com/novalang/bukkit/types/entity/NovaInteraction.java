package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

/** 1.19.4+ Interaction 及 PreviousInteraction 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Interaction", "org.bukkit.entity.Interaction$PreviousInteraction"}, methods = {"org.bukkit.entity.Interaction#getInteractionWidth", "org.bukkit.entity.Interaction#setInteractionWidth", "org.bukkit.entity.Interaction#getInteractionHeight", "org.bukkit.entity.Interaction#setInteractionHeight", "org.bukkit.entity.Interaction#isResponsive", "org.bukkit.entity.Interaction#setResponsive", "org.bukkit.entity.Interaction#getLastAttack", "org.bukkit.entity.Interaction#getLastInteraction", "org.bukkit.entity.Interaction$PreviousInteraction#getPlayer", "org.bukkit.entity.Interaction$PreviousInteraction#getTimestamp"})
public final class NovaInteraction {
    private static final String TYPE = "org.bukkit.entity.Interaction";
    private static final String PREVIOUS_INTERACTION = "org.bukkit.entity.Interaction$PreviousInteraction";
    private NovaInteraction() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaInteraction.class, TYPE);
        Class<?> previousInteraction = PaperEntityReflection.type(NovaInteraction.class, PREVIOUS_INTERACTION);
        Method getWidth = PaperEntityReflection.method(type, "getInteractionWidth"); Method setWidth = PaperEntityReflection.method(type, "setInteractionWidth", Float.TYPE);
        Method getHeight = PaperEntityReflection.method(type, "getInteractionHeight"); Method setHeight = PaperEntityReflection.method(type, "setInteractionHeight", Float.TYPE);
        Method isResponsive = PaperEntityReflection.method(type, "isResponsive"); Method setResponsive = PaperEntityReflection.method(type, "setResponsive", Boolean.TYPE);
        Method getLastAttack = PaperEntityReflection.method(type, "getLastAttack"); Method getLastInteraction = PaperEntityReflection.method(type, "getLastInteraction");
        builder.extension(type, "interactionWidth", function -> function.returns(Float.class).invoke(arguments -> PaperEntityReflection.invoke(getWidth, arguments[0])));
        builder.extension(type, "setInteractionWidth", function -> function.param("width", Float.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setWidth, arguments[0], arguments[1])));
        builder.extension(type, "interactionHeight", function -> function.returns(Float.class).invoke(arguments -> PaperEntityReflection.invoke(getHeight, arguments[0])));
        builder.extension(type, "setInteractionHeight", function -> function.param("height", Float.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setHeight, arguments[0], arguments[1])));
        builder.extension(type, "isResponsive", function -> function.returns(Boolean.class).invoke(arguments -> PaperEntityReflection.invoke(isResponsive, arguments[0])));
        builder.extension(type, "setResponsive", function -> function.param("responsive", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setResponsive, arguments[0], arguments[1])));
        builder.extension(type, "lastAttack", function -> function.returns(JavaTypeRef.javaType(previousInteraction).nullable()).invoke(arguments -> PaperEntityReflection.invoke(getLastAttack, arguments[0])));
        builder.extension(type, "lastInteraction", function -> function.returns(JavaTypeRef.javaType(previousInteraction).nullable()).invoke(arguments -> PaperEntityReflection.invoke(getLastInteraction, arguments[0])));
        registerPreviousInteraction(builder, previousInteraction);
    }
    private static void registerPreviousInteraction(JavaTypes.Builder builder, Class<?> type) {
        Method getPlayer = PaperEntityReflection.method(type, "getPlayer"); Method getTimestamp = PaperEntityReflection.method(type, "getTimestamp");
        builder.extension(type, "player", function -> function.returns(Player.class).invoke(arguments -> PaperEntityReflection.invoke(getPlayer, arguments[0])));
        builder.extension(type, "timestamp", function -> function.returns(Long.class).invoke(arguments -> PaperEntityReflection.invoke(getTimestamp, arguments[0])));
    }
}
