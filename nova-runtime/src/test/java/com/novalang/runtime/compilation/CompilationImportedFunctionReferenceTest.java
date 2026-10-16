package com.novalang.runtime.compilation;

import com.novalang.runtime.ScriptCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证独立编译模块导出的函数引用可在初始化期间绑定并随程序关闭。
 */
class CompilationImportedFunctionReferenceTest {
    @TempDir
    Path root;

    @Test
    void importedReferencesBindDuringInitialization() throws Exception {
        CompilationTestSupport.write(root, "handlers.nova",
                "fun ready(player, payload) { return player.toString() + payload.toString() }\n"
                + "fun count(value: Int): Int { return value + 1 }\n"
                + "fun empty() {}\n");
        Path entry = CompilationTestSupport.write(root, "main.nova",
                "import \"./handlers\"\n"
                + "import java com.novalang.runtime.ScriptCallbacks\n"
                + "val callback = ScriptCallbacks.bind(::ready)\n"
                + "fun getCallback() { return callback }\n"
                + "fun getCount() { return ScriptCallbacks.bind(::count) }\n"
                + "fun getEmpty() { return ScriptCallbacks.bind(::empty) }\n");
        ScriptCallback callback;
        try (CompilationHarness compilation = new CompilationHarness(entry, nova -> { })) {
            compilation.compileAndRun();
            callback = (ScriptCallback) compilation.call("getCallback", Collections.emptyMap());
            assertTrue(callback.isValid());
            assertEquals("player:ready", callback.invoke("player:", "ready"));
            ScriptCallback count = (ScriptCallback) compilation.call("getCount", Collections.emptyMap());
            assertEquals(4, count.invoke(3));
            ScriptCallback empty = (ScriptCallback) compilation.call("getEmpty", Collections.emptyMap());
            assertNull(empty.invoke());
        }
        assertFalse(callback.isValid());
        assertThrows(IllegalStateException.class, () -> callback.invoke("player:", "ready"));
    }
}
