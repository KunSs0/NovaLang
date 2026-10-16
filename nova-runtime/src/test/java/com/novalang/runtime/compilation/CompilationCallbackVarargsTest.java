package com.novalang.runtime.compilation;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;
import com.novalang.runtime.ScriptCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 使用真实 ScriptCallback 复现 Java Object... 调用边界。
 * 仅使用合成事件，不读取服务端资源，也不以抛出异常作为预期成功。
 */
@DisplayName("Compilation 回调应将单个事件打包为一个可变参数")
class CompilationCallbackVarargsTest {
    @TempDir
    Path root;

    private NovaScheduler previousScheduler;
    private CompilationHarness compilation;
    private ScriptCallback callback;
    private Object event;

    @BeforeEach
    void loadCompilation() throws Exception {
        previousScheduler = SchedulerHolder.get();
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
        CompilationTestSupport.write(root, "lib/events.nova",
                // 稳定回调是宿主生命周期 API，必须覆盖真实 Java varargs，不能用 Nova Lambda 替代。
                "import java com.novalang.runtime.ScriptCallback\n"
                        + "class InitializedEvent(val value: String) {}\n"
                        + "object Receipt { var calls = 0 }\n"
                        + "class Controller(val callback: ScriptCallback?) {\n"
                        + " fun optionCallback(): ScriptCallback? { return callback }\n"
                        + " fun dispatch(event: InitializedEvent): Any? {\n"
                        + "  val selected = optionCallback()\n"
                        + "  if (selected != null) { return selected.invoke(event) }\n"
                        + "  error(\"Missing callback\")\n"
                        + " }\n"
                        + "}\n");
        CompilationTestSupport.write(root, "sender.nova",
                "import \"@/lib/events\"\n"
                        + "import java com.novalang.runtime.ScriptCallback\n"
                        + "fun makeEvent(): InitializedEvent { return InitializedEvent(\"ready\") }\n"
                        + "fun sendDirect(callback: ScriptCallback, event: InitializedEvent): Any? {\n"
                        + " return callback.invoke(event)\n"
                        + "}\n"
                        + "fun sendThroughController(callback: ScriptCallback, event: InitializedEvent): Any? {\n"
                        + " return Controller(callback).dispatch(event)\n"
                        + "}\n");
        // 主文件引用的两个模块共享 events，覆盖生成类跨模块传入稳定回调的边界。
        CompilationTestSupport.write(root, "receiver.nova",
                "import \"@/lib/events\"\n"
                        + "import java com.novalang.runtime.ScriptCallbacks\n"
                        + "fun bindReceive() { return ScriptCallbacks.bind(::receive) }\n"
                        + "fun receive(event: InitializedEvent): InitializedEvent {\n"
                        + " Receipt.calls = Receipt.calls + 1\n"
                        + " return event\n"
                        + "}\n"
                        + "fun calls(): Int { return Receipt.calls }\n");
        Path config = CompilationTestSupport.writeMain(root, "sender.nova", "receiver.nova");
        compilation = new CompilationHarness(config, nova -> { });
        compilation.compileAndRun();
        callback = (ScriptCallback) compilation.call("bindReceive", Collections.emptyMap());
        event = compilation.call("makeEvent", Collections.emptyMap());
    }

    @AfterEach
    void disposeCompilation() {
        try {
            if (compilation != null) {
                compilation.close();
            }
        } finally {
            if (previousScheduler == null) {
                SchedulerHolder.clear();
            } else {
                SchedulerHolder.set(previousScheduler);
            }
        }
    }

    @Test
    @DisplayName("Java 直接调用对照：同一个事件应送达一次")
    void javaCallerDeliversOneEvent() {
        assertDeliveredOnce(callback.invoke(event));
    }

    @Test
    @DisplayName("Nova 显式类型参数：callback.invoke(event) 应送达一次")
    void scriptCallerDeliversOneEvent() {
        assertDeliveredOnce(compilation.call("sendDirect", Collections.emptyMap(), callback, event));
    }

    @Test
    @DisplayName("共享控制器可空回调：判空后 invoke(event) 应送达一次")
    void sharedControllerDeliversOneEvent() {
        assertDeliveredOnce(compilation.call("sendThroughController", Collections.emptyMap(), callback, event));
    }

    private void assertDeliveredOnce(Object result) {
        assertSame(event, result, "回调必须接收原事件，不能把它当作参数数组或复制成另一对象");
        assertEquals(1, compilation.call("calls", Collections.emptyMap()));
    }
}
