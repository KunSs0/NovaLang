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

/** 合成 fixture：类成员被 HIR 优化时，构造器仍必须执行有序实例初始化。 */
@DisplayName("类优化不能丢失字段初始化和 init 块")
class OptimizedClassInitializationTest {
    static Stream<Arguments> cases() {
        List<Arguments> cases = new ArrayList<>();
        for (Mode mode : Mode.values()) {
            cases.add(Arguments.of(mode, "方法常量折叠后保留布尔字段",
                    "class Controller(val id: String) {\n"
                            + " var active = true\n"
                            + " fun folded(): Int { return 1 + 2 }\n"
                            + " fun check(): Boolean { return !active }\n"
                            + "}\n"
                            + "fun probe(): Boolean { return Controller(\"test\").check() }", false));
            cases.add(Arguments.of(mode, "字段常量折叠后仍初始化",
                    "class Counter(val id: String) { val count = 20 + 22 }\n"
                            + "fun probe(): Int { return Counter(\"test\").count }", 42));
            cases.add(Arguments.of(mode, "保留字段与 init 块的顺序和副作用",
                    "object Trace { var text = \"\"; fun mark(value: String): String { text = text + value; return value } }\n"
                            + "class Controller(val id: String) {\n"
                            + " val first = Trace.mark(id)\n"
                            + " init { Trace.mark(\"B\") }\n"
                            + " val second = Trace.mark(\"C\")\n"
                            + " init { Trace.mark(\"D\") }\n"
                            + " fun folded(): Int { return 1 + 2 }\n"
                            + "}\n"
                            + "fun probe(): String { Controller(\"A\"); return Trace.text }", "ABCD"));
            cases.add(Arguments.of(mode, "默认构造参数仍先于字段初始化",
                    "class Controller(val id: String = \"ready\") {\n"
                            + " val label = id + \"!\"\n"
                            + " val handles = mutableListOf<String>()\n"
                            + " fun folded(): Int { return 1 + 2 }\n"
                            + "}\n"
                            + "fun probe(): String { val c = Controller(); c.handles.add(c.label); return c.handles.get(0) }", "ready!"));
            cases.add(Arguments.of(mode, "无主构造参数的 init 块不能丢失",
                    "class Controller {\n"
                            + " var text = \"A\"\n"
                            + " init { text = text + \"B\" }\n"
                            + " fun folded(): Int { return 1 + 2 }\n"
                            + "}\n"
                            + "fun probe(): String { return Controller().text }", "AB"));
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0} / {1}")
    @MethodSource("cases")
    void shouldPreserveInitialization(Mode mode, String scenario, String source, Object expected) {
        Nova nova = new Nova();
        Object result;
        if (mode == Mode.INTERPRETED) {
            nova.eval(source, "class-initialization.nova");
            result = nova.call("probe");
        } else {
            CompiledNova compiled = nova.compileToBytecode(source, "class-initialization.nova");
            compiled.run();
            if (mode == Mode.ISOLATED_BYTECODE) {
                result = compiled.callIsolated("probe", Collections.<String, Object>emptyMap());
            } else {
                result = compiled.call("probe");
            }
        }
        assertEquals(expected, result, scenario);
    }

    enum Mode {
        INTERPRETED, BYTECODE, ISOLATED_BYTECODE
    }
}
