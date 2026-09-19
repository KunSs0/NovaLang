package com.novalang.runtime.host;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JavaTypesInstaller 测试")
class JavaTypesInstallerTest {

    @Test
    @DisplayName("安装默认命名空间变量与函数")
    void installDefaultNamespace() {
        JavaTypes registry = JavaTypes.builder()
                .globalVariable("score", variable -> variable
                        .type(JavaTypeRefs.INT)
                        .mutable()
                        .value(42))
                .globalFunction("add", function -> function
                        .param("a", JavaTypeRefs.INT)
                        .param("b", JavaTypeRefs.INT)
                        .returns(JavaTypeRefs.INT)
                        .invoke(args -> ((Number) args[0]).intValue() + ((Number) args[1]).intValue()))
                .build();

        Nova nova = new Nova();
        JavaTypesInstaller.install(nova, registry);

        assertThat(nova.get("score")).isEqualTo(42);
        assertThat(nova.call("add", 2, 3)).isEqualTo(5);
    }

    @Test
    @DisplayName("安装指定命名空间会合并 default")
    void installSpecificNamespace() {
        JavaTypes registry = JavaTypes.builder()
                .namespace("default", namespace -> namespace
                        .variable("playerName", variable -> variable
                                .type(JavaTypeRefs.STRING)
                                .value("Nova")))
                .namespace("reward", namespace -> namespace
                        .extendsNamespace("default")
                        .function("giveMoney", function -> function
                                .param("amount", JavaTypeRefs.INT)
                                .returns(JavaTypeRefs.UNIT)
                                .invoke(args -> null)))
                .build();

        Nova nova = new Nova();
        JavaTypesInstaller.installNamespace(nova, registry, "reward");

        assertThat(nova.get("playerName")).isEqualTo("Nova");
        assertThat(nova.get("giveMoney")).isNotNull();
    }
}
