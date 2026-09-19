package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.meta.components.FoodComponent;

/**
 * Paper 1.21.8 {@link FoodComponent} 的 NovaLang 扩展。
 *
 * <p>Paper 1.21.8 只提供营养值、饱和度和无条件进食三个成员，因此这里严格按该版本
 * 的公开接口注册，不提前引用后续版本才存在的效果相关类型。</p>
 */
public final class NovaFoodComponent {

    private NovaFoodComponent() {
    }

    /**
     * 注册 Paper 1.21.8 FoodComponent 的读取和修改方法。
     *
     * @param builder JavaTypes 构建器
     */
    public static void register(JavaTypes.Builder builder) {
        builder.extension(FoodComponent.class, "nutrition", function -> function
                .returns(Integer.class)
                .invoke(arguments -> food(arguments).getNutrition()));
        builder.extension(FoodComponent.class, "setNutrition", function -> function
                .param("nutrition", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    food(arguments).setNutrition(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(FoodComponent.class, "saturation", function -> function
                .returns(Float.class)
                .invoke(arguments -> food(arguments).getSaturation()));
        builder.extension(FoodComponent.class, "setSaturation", function -> function
                .param("saturation", Float.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    food(arguments).setSaturation(NovaTypeSupport.argument(arguments, 1, Float.class));
                    return null;
                }));
        builder.extension(FoodComponent.class, "canAlwaysEat", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> food(arguments).canAlwaysEat()));
        builder.extension(FoodComponent.class, "setCanAlwaysEat", function -> function
                .param("value", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    food(arguments).setCanAlwaysEat(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    /**
     * 取得 FoodComponent 扩展调用的目标对象。
     *
     * @param arguments 扩展调用参数
     * @return FoodComponent 实例
     */
    private static FoodComponent food(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FoodComponent.class);
    }
}
