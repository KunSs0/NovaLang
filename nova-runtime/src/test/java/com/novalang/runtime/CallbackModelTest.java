package com.novalang.runtime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 验证实际字节码路径上的接口实例、函数引用与闭包回调。 */
public class CallbackModelTest {
    public interface Callback {
        String handle(String value);
    }

    public interface EventCallback {
        String onEvent(String eventKey, String payload);
    }

    public static class Host {
        private Callback callback;
        private EventCallback eventCallback;
        public void register(Callback callback) {
            this.callback = callback;
        }
        public String fire(String value) {
            return callback.handle(value);
        }

        public String decorate() {
            return "member";
        }

        public void registerEvent(EventCallback callback) {
            this.eventCallback = callback;
        }

        public String fireEvent(String eventKey, String payload) {
            return eventCallback.onEvent(eventKey, payload);
        }
    }

    private Host register(String source) {
        Host host = new Host();
        Nova nova = new Nova();
        nova.set("host", host);
        nova.compileToBytecode(source, "callback-model.nova").run();
        assertNotNull(host.callback);
        return host;
    }

    @Test
    void classInstanceCallback() {
        Host host = register("import java com.novalang.runtime.CallbackModelTest.Callback\n"
                + "class Handler(val prefix: String) : Callback {\n"
                + "override fun handle(value: String): String { return prefix + value }\n}\n"
                + "host.register(Handler(\"class:\"))");
        assertEquals("class:one", host.fire("one"));
        assertEquals("class:two", host.fire("two"));
    }

    @Test
    void namedFunctionReferenceCallback() {
        Host host = register("fun handle(value: String): String { return \"fun:\" + value }\n"
                + "host.register(::handle)");
        assertEquals("fun:one", host.fire("one"));
        assertEquals("fun:two", host.fire("two"));
    }

    @Test
    void capturingLambdaCallback() {
        Host host = register("val prefix = \"lambda:\"\n"
                + "host.register({ value: String -> prefix + value })");
        assertEquals("lambda:one", host.fire("one"));
        assertEquals("lambda:two", host.fire("two"));
    }

    @Test
    void multiArgumentClassInstanceCallback() {
        Host host = new Host();
        Nova nova = new Nova();
        nova.set("host", host);
        nova.compileToBytecode(
                "import java com.novalang.runtime.CallbackModelTest.EventCallback\n"
                        + "class Handler(val prefix: String, val separator: String) : EventCallback {\n"
                        + "override fun onEvent(eventKey: String, payload: String): String {\n"
                        + "return prefix + eventKey + separator + payload\n"
                        + "}\n}\n"
                        + "host.registerEvent(Handler(\"event:\", \"=\"))",
                "callback-event-class.nova").run();
        assertNotNull(host.eventCallback);
        assertEquals("event:ready=data", host.fireEvent("ready", "data"));
    }

    @Test
    void multiArgumentFunctionReferenceCallback() {
        Host host = new Host();
        Nova nova = new Nova();
        nova.set("host", host);
        nova.compileToBytecode(
                "import java com.novalang.runtime.CallbackModelTest.EventCallback\n"
                        + "fun onEvent(eventKey: String, payload: String): String {\n"
                        + "return eventKey + \"/\" + payload\n"
                        + "}\n"
                        + "host.registerEvent(::onEvent)",
                "callback-event-function.nova").run();
        assertNotNull(host.eventCallback);
        assertEquals("ready/data", host.fireEvent("ready", "data"));
    }

    @Test
    void multiArgumentCapturingLambdaCallback() {
        Host host = new Host();
        Nova nova = new Nova();
        nova.set("host", host);
        nova.compileToBytecode(
                "import java com.novalang.runtime.CallbackModelTest.EventCallback\n"
                        + "val prefix = \"rpc:\"\n"
                        + "host.registerEvent({ eventKey: String, payload: String -> prefix + eventKey + \"=\" + payload })",
                "callback-event-lambda.nova").run();
        assertNotNull(host.eventCallback);
        assertEquals("rpc:ready=data", host.fireEvent("ready", "data"));
    }

    @Test
    void lambdaKeepsEnclosingNovaReceiverForMemberCalls() {
        Host host = new Host();
        Nova nova = new Nova();
        nova.set("host", host);
        nova.compileToBytecode(
                "import java com.novalang.runtime.CallbackModelTest.Callback\n"
                        + "import java com.novalang.runtime.CallbackModelTest.Host\n"
                        + "class Component {\n"
                        + "fun install() {\n"
                        + "host.register({ value: String -> decorate() + \"-callback\" })\n"
                        + "}\n"
                        + "fun decorate(): String { return \"member\" }\n"
                        + "}\n"
                        + "Component().install()",
                "callback-enclosing-receiver.nova").run();
        assertEquals("member-callback", host.fire("ignored"));
    }
}
