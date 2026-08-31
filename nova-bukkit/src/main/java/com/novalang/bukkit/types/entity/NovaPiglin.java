package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import java.util.Set;
import org.bukkit.Material;

/** 1.16+ Piglin 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Piglin"},
        methods = {
                "org.bukkit.entity.Piglin#isAbleToHunt",
                "org.bukkit.entity.Piglin#setIsAbleToHunt",
                "org.bukkit.entity.Piglin#addBarterMaterial",
                "org.bukkit.entity.Piglin#removeBarterMaterial",
                "org.bukkit.entity.Piglin#addMaterialOfInterest",
                "org.bukkit.entity.Piglin#removeMaterialOfInterest",
                "org.bukkit.entity.Piglin#getInterestList",
                "org.bukkit.entity.Piglin#getBarterList"
        })
public final class NovaPiglin {

    private static final String TYPE = "org.bukkit.entity.Piglin";

    private NovaPiglin() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaPiglin.class, TYPE);
        Method ableToHunt = NovaEntityReflection.method(type, "isAbleToHunt");
        Method setAbleToHunt = NovaEntityReflection.method(type, "setIsAbleToHunt", Boolean.TYPE);
        Method addBarter = NovaEntityReflection.method(type, "addBarterMaterial", Material.class);
        Method removeBarter = NovaEntityReflection.method(type, "removeBarterMaterial", Material.class);
        Method addInterest = NovaEntityReflection.method(type, "addMaterialOfInterest", Material.class);
        Method removeInterest = NovaEntityReflection.method(type, "removeMaterialOfInterest", Material.class);
        Method interestList = NovaEntityReflection.method(type, "getInterestList");
        Method barterList = NovaEntityReflection.method(type, "getBarterList");
        builder.extension(type, "isAbleToHunt", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(ableToHunt, arguments[0])));
        builder.extension(type, "setIsAbleToHunt", function -> function.param("value", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setAbleToHunt, arguments[0], arguments[1])));
        materialMutation(builder, type, "addBarterMaterial", addBarter);
        materialMutation(builder, type, "removeBarterMaterial", removeBarter);
        materialMutation(builder, type, "addMaterialOfInterest", addInterest);
        materialMutation(builder, type, "removeMaterialOfInterest", removeInterest);
        builder.extension(type, "interestList", function -> function.returns(Set.class)
                .invoke(arguments -> NovaEntityReflection.invoke(interestList, arguments[0])));
        builder.extension(type, "barterList", function -> function.returns(Set.class)
                .invoke(arguments -> NovaEntityReflection.invoke(barterList, arguments[0])));
    }

    private static void materialMutation(JavaTypes.Builder builder, Class<?> type, String name, Method method) {
        builder.extension(type, name, function -> function.param("material", Material.class).returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(method, arguments[0], arguments[1])));
    }
}
