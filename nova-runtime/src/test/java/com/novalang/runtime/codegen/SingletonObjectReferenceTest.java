package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 单例对象作为值传递的最小复现；断言正确行为，不把当前 null 缺陷当作预期。 */
@DisplayName("单例对象引用在解释、字节码与隔离调用中的一致性")
class SingletonObjectReferenceTest {

    private static final String API = "object TitleApi {\n"
            + "    fun send(): String { return \"ok\" }\n"
            + "}\n";

    static Stream<Arguments> cases() {
        List<Arguments> cases = new ArrayList<Arguments>();
        for (Mode mode : Mode.values()) {
            cases.add(Arguments.of(mode, "直接调用对照", API
                    + "fun probe(): String { return TitleApi.send() }\n"));
            cases.add(Arguments.of(mode, "局部单例引用", API
                    + "fun probe(): String {\n"
                    + "    val title = TitleApi\n"
                    + "    return title.send()\n"
                    + "}\n"));
            cases.add(Arguments.of(mode, "推导类型门面字段", API
                    + "object Facade { val title = TitleApi }\n"
                    + "fun probe(): String { return Facade.title.send() }\n"));
            cases.add(Arguments.of(mode, "显式类型门面字段", API
                    + "object Facade { val title: TitleApi = TitleApi }\n"
                    + "fun probe(): String { return Facade.title.send() }\n"));
            // 解释器逐条执行声明，前向引用的未定义变量不属于本次 null 复现。
            if (mode != Mode.INTERPRETED) {
                cases.add(Arguments.of(mode, "门面先于单例声明",
                        "object Facade { val title = TitleApi }\n" + API
                        + "fun probe(): String { return Facade.title.send() }\n"));
            }
            cases.add(Arguments.of(mode, "函数返回单例", API
                    + "fun api(): TitleApi { return TitleApi }\n"
                    + "fun probe(): String { return api().send() }\n"));
            cases.add(Arguments.of(mode, "单例作为参数", API
                    + "fun send(api: TitleApi): String { return api.send() }\n"
                    + "fun probe(): String { return send(TitleApi) }\n"));
            cases.add(Arguments.of(mode, "闭包内读取单例", API
                    + "fun probe(): String {\n"
                    + "    val read = { val api = TitleApi; api.send() }\n"
                    + "    return read()\n"
                    + "}\n"));
            cases.add(Arguments.of(mode, "局部值遮蔽单例", API
                    + "fun probe(): String { val TitleApi = \"ok\"; return TitleApi }\n"));
            cases.add(Arguments.of(mode, "参数遮蔽单例", API
                    + "fun read(TitleApi: String): String { return TitleApi }\n"
                    + "fun probe(): String { return read(\"ok\") }\n"));
            cases.add(Arguments.of(mode, "接收者字段遮蔽单例", API
                    + "class Holder {\n"
                    + "    val TitleApi = \"ok\"\n"
                    + "    fun read(): String { return TitleApi }\n"
                    + "}\n"
                    + "fun probe(): String { return Holder().read() }\n"));
            cases.add(Arguments.of(mode, "普通类实例字段对照",
                    "class TitleApi { fun send(): String { return \"ok\" } }\n"
                    + "object Facade { val title = TitleApi() }\n"
                    + "fun probe(): String { return Facade.title.send() }\n"));
            cases.add(Arguments.of(mode, "别名共享同一单例状态",
                    "object Counter {\n"
                    + "    var value = 0\n"
                    + "    fun increment() { value = value + 1 }\n"
                    + "    fun read(): Int { return value }\n"
                    + "}\n"
                    + "fun probe(): String {\n"
                    + "    val first = Counter\n"
                    + "    val second = Counter\n"
                    + "    first.increment()\n"
                    + "    if (second.read() == 1 && Counter.read() == 1) {\n"
                    + "        return \"ok\"\n"
                    + "    }\n"
                    + "    return \"not-shared\"\n"
                    + "}\n"));
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0} / {1}")
    @MethodSource("cases")
    void shouldPreserveSingletonReference(Mode mode, String scenario, String source) {
        Nova nova = new Nova();
        Object result;
        if (mode == Mode.INTERPRETED) {
            nova.eval(source, "singleton-reference.nova");
            result = nova.call("probe");
        } else {
            CompiledNova compiled = nova.compileToBytecode(source, "singleton-reference.nova");
            compiled.run();
            if (mode == Mode.ISOLATED_BYTECODE) {
                result = compiled.callIsolated("probe", Collections.<String, Object>emptyMap());
            } else {
                result = compiled.call("probe");
            }
        }
        assertEquals("ok", result, scenario);
    }

    enum Mode {
        INTERPRETED,
        BYTECODE,
        ISOLATED_BYTECODE
    }
}
