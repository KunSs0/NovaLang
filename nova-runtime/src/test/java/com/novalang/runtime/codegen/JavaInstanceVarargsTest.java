package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Java 实例/接口 varargs 的编译边界，不依赖 Bukkit。 */
class JavaInstanceVarargsTest {
    static Stream<Arguments> cases() {
        List<Arguments> cases = new ArrayList<>();
        String[][] scenarios = {
                {"零参数", "return api.record()", "[]"},
                {"单参数", "return api.record(\"one\")", "[one]"},
                {"多参数", "return api.record(\"one\", 2, true)", "[one, 2, true]"},
                {"空值参数", "return api.record(null)", "[null]"},
                {"固定前缀与可变参数", "return api.prefixed(\"p\", \"a\", \"b\")", "p[a, b]"},
                {"基本类型可变参数", "return api.sum(1, 2, 3).toString()", "6"},
                {"显式对象数组不嵌套", "return api.record(VarargsFixture.objects())", "[a, b]"},
                {"显式基本类型数组", "return api.sum(VarargsFixture.numbers()).toString()", "6"},
                {"固定重载优先", "return api.choose(\"one\")", "fixed:one"},
                {"可变参数重载", "return api.choose(\"one\", \"two\")", "varargs:2"},
                {"同一调用点缓存后重复调用", "var result = \"\"; for (value in listOf(\"first\", \"second\")) { result = api.record(value) }; return result", "[second]"}
        };
        for (boolean isolated : new boolean[]{false, true}) {
            for (boolean throughInterface : new boolean[]{false, true}) {
                for (String[] scenario : scenarios) {
                    cases.add(Arguments.of(isolated, throughInterface, scenario[0], scenario[1], scenario[2]));
                }
            }
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "isolated={0}, interface={1}: {2}")
    @MethodSource("cases")
    void shouldPackInstanceArguments(boolean isolated, boolean throughInterface,
                                     String scenario, String body, String expected) {
        String type = throughInterface ? "VarargsApi" : "VarargsFixture";
        String source = "import java com.novalang.runtime.codegen.JavaInstanceVarargsTest.VarargsFixture\n"
                + "import java com.novalang.runtime.codegen.JavaInstanceVarargsTest.VarargsApi\n"
                + "fun probe(api: " + type + "): String { " + body + " }\n";
        CompiledNova compiled = new Nova().compileToBytecode(source, "instance-varargs.nova");
        compiled.run();
        VarargsFixture fixture = new VarargsFixture();
        Object result = isolated
                ? compiled.callIsolated("probe", Collections.emptyMap(), fixture)
                : compiled.call("probe", fixture);
        assertEquals(expected, result, scenario);
    }

    public interface VarargsApi {
        String record(Object... values);
        String prefixed(String prefix, String... values);
        int sum(int... values);
        String choose(String value);
        String choose(String... values);
    }

    public static final class VarargsFixture implements VarargsApi {
        public String record(Object... values) { return Arrays.deepToString(values); }
        public String prefixed(String prefix, String... values) { return prefix + Arrays.toString(values); }
        public int sum(int... values) {
            int result = 0;
            for (int value : values) {
                result += value;
            }
            return result;
        }
        public String choose(String value) { return "fixed:" + value; }
        public String choose(String... values) { return "varargs:" + values.length; }
        public static Object[] objects() { return new Object[]{"a", "b"}; }
        public static int[] numbers() { return new int[]{1, 2, 3}; }
    }
}
