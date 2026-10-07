package com.novalang.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/** 直接通过语言运行时验证脚本接收者，不依赖 Workspace 或业务插件。 */
@DisplayName("脚本 this 接收调用绑定")
class ScriptContextReceiverTest {

    @Test
    @DisplayName("脚本入口和顶层公共函数读取同一次调用的绑定")
    void entryAndFunctionsReadExecutionBindings() {
        CompiledNova compiled = compile(
                "fun read(): Any? { return this.incident }\n"
                + "read()", "entry-receiver.nova");
        Object incident = new Object();

        assertSame(incident, compiled.runIsolated(bindings("incident", incident)));
        assertSame(incident, compiled.callIsolated("read", bindings("incident", incident)));
        assertNull(NovaScriptContext.current());
    }

    @Test
    @DisplayName("this 不会错误地指向顶层函数的第一个参数")
    void receiverIsNotFirstArgument() {
        CompiledNova compiled = compile(
                "fun read(decoy): Any? { return this.incident }", "argument-receiver.nova");
        Object incident = new Object();

        assertSame(incident, compiled.callIsolated("read", bindings("incident", incident),
                bindings("incident", "wrong")));
    }

    @Test
    @DisplayName("成员写入与裸变量共享当前调用绑定，不回写隔离调用输入")
    void memberWritesUseSameBindingsAsBareNames() {
        CompiledNova compiled = compile(
                "fun update(): Int {\n"
                + " this.value = this.value + 1\n"
                + " value = value + 1\n"
                + " return this.value\n"
                + "}", "write-receiver.nova");
        Map<String, Object> input = bindings("value", 10);

        assertEquals(12, ((Number) compiled.callIsolated("update", input)).intValue());
        assertEquals(10, input.get("value"));
        assertEquals(12, ((Number) compiled.callIsolated("update", input)).intValue());
    }

    @Test
    @DisplayName("缺失和显式 null 绑定返回 null，不借用参数的同名属性")
    void nullableMembersBelongToExecutionContext() {
        CompiledNova compiled = compile(
                "fun read(decoy): Any? { return this.incident }", "nullable-receiver.nova");

        assertNull(compiled.callIsolated("read", bindings("incident", null),
                bindings("incident", "wrong")));
        assertNull(compiled.callIsolated("read", Collections.emptyMap(),
                bindings("incident", "wrong")));
    }

    @Test
    @DisplayName("函数引用和嵌套闭包回调携带绑定，事件覆盖后恢复原始绑定")
    void callbacksRetainExecutionBindings() {
        CompiledNova compiled = compile(
                "import java com.novalang.runtime.ScriptCallbacks\n"
                + "fun read(): Any? { return this.incident }\n"
                + "fun reference() { return ScriptCallbacks.bind(::read) }\n"
                + "fun closure() {\n"
                + " return ScriptCallbacks.bind({\n"
                + "  val nested = { this.incident }\n"
                + "  nested()\n"
                + " })\n"
                + "}", "callback-receiver.nova");
        Object incident = new Object();
        Object replacement = new Object();
        ScriptCallback reference = (ScriptCallback) compiled.callIsolated("reference",
                bindings("incident", incident));
        ScriptCallback closure = (ScriptCallback) compiled.callIsolated("closure",
                bindings("incident", incident));

        assertSame(incident, reference.invoke());
        assertSame(incident, closure.invoke());
        assertSame(replacement, closure.invokeWithBindings(bindings("incident", replacement)));
        assertSame(incident, closure.invoke());
        assertNull(NovaScriptContext.current());
    }

    @Test
    @DisplayName("类方法及其闭包的 this 保持类实例语义")
    void classAndLexicalLambdaKeepTheirReceiver() {
        CompiledNova compiled = compile(
                "class Holder(val incident: String) {\n"
                + " fun read(): String { return this.incident }\n"
                + " fun closure(): String {\n"
                + "  val reader = { this.incident }\n"
                + "  return reader() as String\n"
                + " }\n"
                + "}\n"
                + "fun read(): String {\n"
                + " val holder = Holder(\"instance\")\n"
                + " return holder.read() + holder.closure()\n"
                + "}", "class-receiver.nova");

        assertEquals("instanceinstance", compiled.callIsolated("read", bindings("incident", "script")));
    }

    @Test
    @DisplayName("扩展函数的 this 保持扩展接收者语义")
    void extensionKeepsItsReceiver() {
        CompiledNova compiled = compile(
                "fun String.echo(): String { return this }\n"
                + "fun read(): String { return \"extension\".echo() }", "extension-receiver.nova");

        assertEquals("extension", compiled.callIsolated("read", bindings("incident", "script")));
    }

    @Test
    @DisplayName("作用域简写的 this 指向显式接收者")
    void scopeShorthandKeepsExplicitReceiver() {
        String source = "class Counter(var count: Int) {\n"
                + " fun read(): Int { return this.count }\n}\n"
                + "fun read(): Int {\n"
                + " val counter: Counter? = Counter(4)\n"
                + " val updated = counter?.{ this.count = count + 1 }\n"
                + " return updated!!.read()\n"
                + "}";
        CompiledNova compiled = compile(source, "scope-receiver.nova");
        assertEquals(5, compiled.callIsolated("read", bindings("count", 100)));

        Nova interpreted = new Nova();
        assertEquals(5, interpreted.eval(source + "\nread()"));
    }

    @Test
    @DisplayName("解释器顶层 this 同样直接访问语言绑定")
    void interpretedScriptUsesLanguageBindings() {
        Nova nova = new Nova();
        Object incident = new Object();
        nova.set("incident", incident);
        assertSame(incident, nova.eval(
                "fun read(decoy): Any? { return this.incident }\nread(\"wrong\")"));
    }

    @Test
    @DisplayName("绑定名与 Java 属性同名时仍读取绑定")
    void receiverDoesNotExposeJavaProperties() {
        CompiledNova compiled = compile("fun read(): Any? {\n"
                + " val receiver = this\n"
                + " return receiver.class\n}", "member-name-receiver.nova");

        assertEquals("binding", compiled.callIsolated("read", bindings("class", "binding")));
        assertNull(compiled.callIsolated("read", bindings("class", null)));
    }

    @Test
    @DisplayName("异步任务和顶层公共函数共享提交时的调用绑定")
    void asyncTaskCarriesScriptReceiver() throws Exception {
        CompiledNova compiled = compile(
                "fun read(): Any? { return this.incident }\n"
                + "fun submit() { return async { read() } }", "async-receiver.nova");
        Object incident = new Object();
        Future<?> result = (Future<?>) compiled.callIsolated("submit", bindings("incident", incident));

        assertSame(incident, result.get(5, TimeUnit.SECONDS));
        assertNull(NovaScriptContext.current());
    }

    @Test
    @DisplayName("嵌套调用和失败调用结束后恢复之前的脚本上下文")
    void nestedAndFailedCallsRestorePreviousContext() {
        CompiledNova compiled = compile(
                "fun read(): Any? { return this.incident }\n"
                + "fun fail() { error(\"expected\") }", "restore-receiver.nova");
        Object outer = new Object();
        Object inner = new Object();
        NovaScriptContext.init(bindings("incident", outer));
        NovaScriptContext previous = NovaScriptContext.current();
        try {
            assertSame(inner, compiled.callIsolated("read", bindings("incident", inner)));
            assertSame(previous, NovaScriptContext.current());
            assertThrows(RuntimeException.class,
                    () -> compiled.callIsolated("fail", bindings("incident", inner)));
            assertSame(previous, NovaScriptContext.current());
            assertSame(outer, NovaScriptContext.get("incident"));
        } finally {
            NovaScriptContext.clear();
        }
    }

    @Test
    @DisplayName("同一编译程序的并发调用不会串用 this 绑定")
    void concurrentCallsHaveIndependentReceivers() throws Exception {
        CompiledNova compiled = compile(
                "fun read(decoy): Any? { return this.incident }", "parallel-receiver.nova");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<Object>> futures = new ArrayList<>();
            for (int index = 0; index < 32; index++) {
                final int marker = index;
                futures.add(executor.submit(() -> compiled.callIsolated("read",
                        bindings("incident", marker), bindings("incident", "wrong"))));
            }
            for (int index = 0; index < futures.size(); index++) {
                assertEquals(index, futures.get(index).get(5, TimeUnit.SECONDS));
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private static CompiledNova compile(String source, String fileName) {
        return new Nova().compileToBytecode(source, fileName);
    }

    private static Map<String, Object> bindings(String key, Object value) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key, value);
        return bindings;
    }
}
