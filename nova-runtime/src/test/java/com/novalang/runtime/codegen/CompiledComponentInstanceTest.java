package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证编译后的 Nova 工厂函数可以返回由宿主持有的有状态组件实例。
 */
@DisplayName("编译组件实例生命周期")
class CompiledComponentInstanceTest {

    private static final String COMPONENT_SOURCE =
            "import java com.novalang.runtime.codegen.CallbackSinkFixture\n" +
            "import java com.novalang.runtime.codegen.ComponentHandleFixture\n" +
            "import java java.lang.Runnable\n" +
            "class ComponentCallback(val owner: ComponentHandleFixture, val delta: Int) : Runnable {\n" +
            "    fun run() { owner.onData(delta) }\n" +
            "}\n" +
            "class ComponentHandle : ComponentHandleFixture {\n" +
            "    var componentId: String = \"\"\n" +
            "    var value: Int = 0\n" +
            "    var disposed: Boolean = false\n" +
            "    fun onCreate(id: String) {\n" +
            "        componentId = id\n" +
            "        value = value + 1\n" +
            "    }\n" +
            "    fun onData(delta: Int) { value = value + delta }\n" +
            "    fun bindCallback(sink: CallbackSinkFixture, delta: Int) {\n" +
            "        sink.install(ComponentCallback(this, delta))\n" +
            "    }\n" +
            "    fun getComponentId(): String = componentId\n" +
            "    fun getValue(): Int = value\n" +
            "    fun dispose() { disposed = true }\n" +
            "    fun isDisposed(): Boolean = disposed\n" +
            "}\n" +
            "fun create(): ComponentHandleFixture = ComponentHandle()\n";

    @Test
    @DisplayName("工厂返回宿主接口实例且多个实例状态互相隔离")
    void factoryShouldReturnIndependentStatefulHostInstances() {
        CompiledNova compiled = new Nova().compileToBytecode(
                COMPONENT_SOURCE, "component-instance.nova");

        ComponentHandleFixture first = assertInstanceOf(
                ComponentHandleFixture.class, compiled.call("create"));
        ComponentHandleFixture second = assertInstanceOf(
                ComponentHandleFixture.class, compiled.call("create"));

        assertNotSame(first, second);

        first.onCreate("first");
        first.onData(4);
        second.onCreate("second");
        second.onData(9);

        assertEquals("first", first.getComponentId());
        assertEquals(5, first.getValue());
        assertEquals("second", second.getComponentId());
        assertEquals(10, second.getValue());
        assertFalse(first.isDisposed());
        assertFalse(second.isDisposed());

        first.dispose();

        assertTrue(first.isDisposed());
        assertFalse(second.isDisposed());
        assertEquals(10, second.getValue());
    }

    @Test
    @DisplayName("宿主持有声明类的回调实例并调用对应组件")
    void retainedDeclaredCallbackShouldInvokeItsComponentInstance() {
        CompiledNova compiled = new Nova().compileToBytecode(
                COMPONENT_SOURCE, "component-callback.nova");
        ComponentHandleFixture first = assertInstanceOf(
                ComponentHandleFixture.class, compiled.call("create"));
        ComponentHandleFixture second = assertInstanceOf(
                ComponentHandleFixture.class, compiled.call("create"));
        CallbackSinkFixture firstSink = new CallbackSinkFixture();
        CallbackSinkFixture secondSink = new CallbackSinkFixture();

        first.onCreate("first");
        second.onCreate("second");
        first.bindCallback(firstSink, 3);
        second.bindCallback(secondSink, 7);

        assertTrue(firstSink.hasCallback());
        assertTrue(secondSink.hasCallback());
        assertTrue(firstSink.getCallbackClassName().contains("ComponentCallback"));
        assertFalse(firstSink.getCallbackClassName().contains("Lambda"));

        firstSink.fire();
        firstSink.fire();
        secondSink.fire();

        assertEquals(7, first.getValue());
        assertEquals(8, second.getValue());

        firstSink.clear();

        assertFalse(firstSink.hasCallback());
        assertTrue(secondSink.hasCallback());
        assertEquals(8, second.getValue());
    }
}
