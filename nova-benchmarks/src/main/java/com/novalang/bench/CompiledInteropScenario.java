package com.novalang.bench;

import java.util.function.IntSupplier;

/**
 * 单个编译后 Java 互操作基准场景。
 */
final class CompiledInteropScenario {

    private final String name;
    private final String description;
    private final String novaSource;
    private final int interopCallsPerRun;
    private final IntSupplier javaNative;

    CompiledInteropScenario(String name, String description, String novaSource,
                            int interopCallsPerRun, IntSupplier javaNative) {
        this.name = name;
        this.description = description;
        this.novaSource = novaSource;
        this.interopCallsPerRun = interopCallsPerRun;
        this.javaNative = javaNative;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    String getNovaSource() {
        return novaSource;
    }

    int getInteropCallsPerRun() {
        return interopCallsPerRun;
    }

    int runJavaNative() {
        return javaNative.getAsInt();
    }
}
