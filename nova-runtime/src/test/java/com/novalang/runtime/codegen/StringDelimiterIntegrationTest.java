package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 分隔符截取在解释器、静态编译和动态接收者上的一致性回归。 */
class StringDelimiterIntegrationTest {

    static Stream<Arguments> delimiterCases() {
        List<Arguments> cases = new ArrayList<>();
        String[] methods = {"substringBefore", "substringAfter", "substringBeforeLast", "substringAfterLast"};
        String[] matched = {"a", "b:c", "a:b", "c"};
        String[] emptyDelimiter = {"", "abc", "abc", ""};
        String[] leading = {"", "abc", "", "abc"};
        String[] trailing = {"abc", "", "abc", ""};
        for (int i = 0; i < methods.length; i++) {
            String method = methods[i];
            for (String delimiter : new String[]{"\":\"", "':'"}) {
                cases.add(Arguments.of("a:b:c", method + "(" + delimiter + ")", matched[i]));
                cases.add(Arguments.of("a:b:c", method + "(" + delimiter + ", \"missing\")", matched[i]));
                cases.add(Arguments.of("abc", method + "(" + delimiter + ")", "abc"));
                cases.add(Arguments.of("abc", method + "(" + delimiter + ", \"missing\")", "missing"));
                cases.add(Arguments.of(":abc", method + "(" + delimiter + ")", leading[i]));
                cases.add(Arguments.of("abc:", method + "(" + delimiter + ")", trailing[i]));
                cases.add(Arguments.of("", method + "(" + delimiter + ", \"missing\")", "missing"));
            }
            cases.add(Arguments.of("abc", method + "(\"\")", emptyDelimiter[i]));
            cases.add(Arguments.of("", method + "(\"\", \"missing\")", ""));
            cases.add(Arguments.of("a::b::c", method + "(\"::\")", matched[i].replace(":", "::")));
            cases.add(Arguments.of("甲.乙.丙", method + "(\".\")", new String[]{"甲", "乙.丙", "甲.乙", "丙"}[i]));
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0}.{1}")
    @MethodSource("delimiterCases")
    void delimiterMethodsAgreeAcrossExecutionPaths(String value, String call, String expected) throws Exception {
        String literal = "\"" + value + "\"";
        assertEquals(expected, new Interpreter().eval(literal + "." + call, "delimiter.nova").asString());
        assertEquals(expected, compile("val value: String = " + literal + "\nreturn value." + call));
        assertEquals(expected, compile("val value: Any = " + literal + "\nreturn value." + call));
    }

    @Test
    void explicitMissingValueCanBeTheReceiver() throws Exception {
        String body = "val value = \"minecraft:overworld\"\nreturn value.substringAfter(\":\", value)";
        assertEquals("overworld", compile(body));
        assertEquals("overworld", new Interpreter().eval(body.replace("return ", ""), "delimiter.nova").asString());
        assertEquals("overworld", compile(body.replace("minecraft:overworld", "overworld")));
    }

    @Test
    void invalidDelimiterAndMissingValueAreRejected() {
        for (String call : new String[]{"substringAfter(1)", "substringBefore(null)",
                "substringAfterLast(\":\", 1)", "substringBeforeLast(\":\", null)"}) {
            assertThrows(Exception.class, () -> new Interpreter().eval("\"abc\"." + call, "delimiter.nova"));
            assertThrows(Exception.class, () -> compile("return \"abc\"." + call));
            assertThrows(Exception.class, () -> compile("val value: Any = \"abc\"\nreturn value." + call));
        }
    }

    private Object compile(String body) throws Exception {
        String source = "object Test {\nfun run(): String {\n" + body + "\n}\n}";
        Map<String, Class<?>> classes = new NovaIrCompiler().compileAndLoad(source, "delimiter.nova");
        Class<?> test = classes.get("Test");
        return test.getMethod("run").invoke(test.getField("INSTANCE").get(null));
    }
}
