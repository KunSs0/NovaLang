package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

/** 盔甲架四肢与头部姿态的可选编译期别名。 */
@Requires(classes = {"org.bukkit.entity.ArmorStand"})
public final class NovaArmorStandPose {

    private NovaArmorStandPose() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ArmorStand.class, "leftArmPose", function -> function
                .returns(EulerAngle.class)
                .invoke(arguments -> stand(arguments).getLeftArmPose()));
        builder.extension(ArmorStand.class, "setLeftArmPose", function -> function
                .param("pose", EulerAngle.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setLeftArmPose(argument(arguments, 1, EulerAngle.class));
                    return null;
                }));
        builder.extension(ArmorStand.class, "rightArmPose", function -> function
                .returns(EulerAngle.class)
                .invoke(arguments -> stand(arguments).getRightArmPose()));
        builder.extension(ArmorStand.class, "setRightArmPose", function -> function
                .param("pose", EulerAngle.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setRightArmPose(argument(arguments, 1, EulerAngle.class));
                    return null;
                }));
        builder.extension(ArmorStand.class, "leftLegPose", function -> function
                .returns(EulerAngle.class)
                .invoke(arguments -> stand(arguments).getLeftLegPose()));
        builder.extension(ArmorStand.class, "setLeftLegPose", function -> function
                .param("pose", EulerAngle.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setLeftLegPose(argument(arguments, 1, EulerAngle.class));
                    return null;
                }));
        builder.extension(ArmorStand.class, "rightLegPose", function -> function
                .returns(EulerAngle.class)
                .invoke(arguments -> stand(arguments).getRightLegPose()));
        builder.extension(ArmorStand.class, "setRightLegPose", function -> function
                .param("pose", EulerAngle.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setRightLegPose(argument(arguments, 1, EulerAngle.class));
                    return null;
                }));
        builder.extension(ArmorStand.class, "headPose", function -> function
                .returns(EulerAngle.class)
                .invoke(arguments -> stand(arguments).getHeadPose()));
        builder.extension(ArmorStand.class, "setHeadPose", function -> function
                .param("pose", EulerAngle.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setHeadPose(argument(arguments, 1, EulerAngle.class));
                    return null;
                }));
        builder.extension(ArmorStand.class, "isMarker", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> stand(arguments).isMarker()));
        builder.extension(ArmorStand.class, "setMarker", function -> function
                .param("marker", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    stand(arguments).setMarker(argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static ArmorStand stand(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ArmorStand.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
