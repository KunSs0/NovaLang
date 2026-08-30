package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** Vehicle 在 1.12.2 没有独立于 Entity 基础表的 Fluxon 别名。 */
final class NovaVehicle {

    private NovaVehicle() {
    }

    static void register(JavaTypes.Builder builder) {
        // Vehicle 的 velocity 别名及其余公共成员由 Entity 扩展按接口继承规则提供。
    }
}
