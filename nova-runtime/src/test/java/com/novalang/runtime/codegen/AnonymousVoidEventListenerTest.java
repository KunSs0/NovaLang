package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** 回归测试：匿名对象实现双参数 void 外部接口时必须生成精确 JVM 方法描述符。 */
class AnonymousVoidEventListenerTest {

    @Test
    void anonymousEventListenerImplementsVoidMethod() {
        String source =
                "import java com.novalang.runtime.codegen.EventListenerFixture\n" +
                "fun create(): EventListenerFixture = object : EventListenerFixture {\n" +
                "    override fun onEvent(key: String, event: Any?) { }\n" +
                "}\n";

        CompiledNova compiled = new Nova().compileToBytecode(source, "anonymous-event-listener.nova");
        Object value = compiled.call("create");
        assertNotNull(value);
        EventListenerFixture listener = (EventListenerFixture) value;

        assertDoesNotThrow(() -> listener.onEvent("click", "payload"));
    }

    @Test
    void anonymousEventListenerCanCallEnclosingMethod() {
        String source =
                "import java com.novalang.runtime.codegen.EventListenerFixture\n" +
                "class Owner {\n" +
                "    fun sendAction(action: String) { }\n" +
                "    fun listener(): EventListenerFixture = object : EventListenerFixture {\n" +
                "        override fun onEvent(key: String, event: Any?) { sendAction(\"click\") }\n" +
                "    }\n" +
                "}\n" +
                "fun create(): EventListenerFixture = Owner().listener()\n";

        CompiledNova compiled = new Nova().compileToBytecode(source, "anonymous-event-listener-owner.nova");
        EventListenerFixture listener = (EventListenerFixture) compiled.call("create");
        assertDoesNotThrow(() -> listener.onEvent("click", "payload"));
    }
}
