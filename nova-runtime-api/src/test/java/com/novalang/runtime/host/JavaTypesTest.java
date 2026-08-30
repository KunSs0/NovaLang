package com.novalang.runtime.host;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JavaTypes 测试")
class JavaTypesTest {

    @Test
    @DisplayName("命名空间解析会合并 default 与 extends")
    void resolveNamespaceMergesDefaultAndParents() {
        JavaTypes registry = JavaTypes.builder()
                .globalFunction("log", function -> function
                        .param("message", JavaTypeRefs.STRING)
                        .returns(JavaTypeRefs.UNIT)
                        .doc("输出日志"))
                .namespace("default", namespace -> namespace
                        .variable("player", variable -> variable
                                .type("Player")
                                .readonly()))
                .namespace("rewardBase", namespace -> namespace
                        .extension(CharSequence.class, "trimmed", function -> function
                                .returns(JavaTypeRefs.STRING))
                        .function("grantBase", function -> function
                                .param("count", JavaTypeRefs.INT)
                                .returns(JavaTypeRefs.UNIT)))
                .namespace("reward", namespace -> namespace
                        .extendsNamespace("rewardBase")
                        .function("giveItem", function -> function
                                .param("itemId", JavaTypeRefs.STRING)
                                .param("count", JavaTypeRefs.INT)
                                .returns(JavaTypeRefs.UNIT)))
                .build();

        JavaNamespaceDescriptor resolved = registry.resolveNamespace("reward");

        assertThat(resolved.getGlobals())
                .extracting(JavaSymbolDescriptor::getName)
                .containsExactly("log", "player", "grantBase", "giveItem");
        assertThat(resolved.getExtensions())
                .extracting(extension -> extension.getFunction().getName())
                .containsExactly("trimmed");
    }

    @Test
    @DisplayName("JSON 导出包含 globals 与 namespaces")
    void jsonWriterExportsExpectedShape() {
        JavaTypes registry = JavaTypes.builder()
                .globalVariable("player", variable -> variable
                        .type("Player")
                        .doc("当前玩家"))
                .extension(String.class, "shout", function -> function
                        .returns(JavaTypeRefs.STRING))
                .extensionProperty(StringBuilder.class, "content", property -> property
                        .type(String.class)
                        .getter(arguments -> arguments[0].toString())
                        .setter(arguments -> {
                            StringBuilder builder = (StringBuilder) arguments[0];
                            builder.setLength(0);
                            builder.append(arguments[1]);
                            return null;
                        }))
                .namespace("reward", namespace -> namespace
                        .function("giveItem", function -> function
                                .param("itemId", JavaTypeRefs.STRING)
                                .param("count", JavaTypeRefs.INT)
                                .returns(JavaTypeRefs.UNIT)
                                .doc("发放奖励")))
                .build();

        String json = JavaTypesJsonWriter.toJson(registry);

        assertThat(json).contains("\"version\": 1");
        assertThat(json).contains("\"globals\"");
        assertThat(json).contains("\"player\"");
        assertThat(json).contains("\"namespaces\"");
        assertThat(json).contains("\"reward\"");
        assertThat(json).contains("\"giveItem\"");
        assertThat(json).contains("\"extensions\"");
        assertThat(json).contains("\"targetType\": \"java.lang.String\"");
        assertThat(json).contains("\"shout\"");
        assertThat(json).contains("\"extensionProperties\"");
        assertThat(json).contains("\"content\"");
        assertThat(json).contains("\"mutable\": true");
    }
}
