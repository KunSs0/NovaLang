package com.novalang.runtime.compilation;

import com.novalang.runtime.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证同名虚拟动作共享依赖状态、独立调用绑定及子资源生命周期。
 */
class CompilationVirtualActionsTest {
    @Test
    void virtualActionsShareOneProgramAndKeepScopedCallbacks() {
        SourceUnit root = SourceUnit.virtual("host", "import \"first\"\nimport \"second\"", null, null, 1, 0);
        SourceUnit shared = SourceUnit.virtual("shared", "object Counter { var value = 0; fun increment() { value = value + 1 } }", null, null, 1, 0);
        SourceUnit first = SourceUnit.inline("first", "import \"shared\"\nCounter.increment()\nreturn this.player", null, null, 1, "execute", "Any?");
        SourceUnit second = SourceUnit.virtual("second", "import \"shared\"\nfun execute(): Int { return Counter.value }", null, null, 1, 0);
        Nova nova = new Nova();
        CompiledNova program = new NovaCompiler().compile(nova, root, new CompilationConfig.Builder().build(),
                Arrays.asList(first, second, shared), new BytecodeArtifactCache());
        ScriptCallbackScope scope = new ScriptCallbackScope();
        program.registerResource(scope);
        ScriptCallbackContext context = program.getCallbackContext(scope);
        AtomicInteger closed = new AtomicInteger();
        context.register(closed::incrementAndGet);
        try {
            program.run();
            assertEquals("alice", program.callModuleIsolated("first", "execute", Collections.singletonMap("player", "alice"), context));
            assertEquals("bob", program.callModuleIsolated("first", "execute", Collections.singletonMap("player", "bob"), context));
            assertEquals(2, program.callModuleIsolated("second", "execute", Collections.emptyMap(), context));
            assertThrows(CompilationException.class, () -> program.callModuleIsolated("missing", "execute", Collections.emptyMap(), context));
            scope.close();
            assertEquals(1, closed.get());
            assertFalse(context.isValid());
            assertThrows(RuntimeException.class, () -> program.callModuleIsolated("first", "execute", Collections.emptyMap(), context));
        } finally {
            program.close();
        }
        assertEquals(1, closed.get());
    }

    @Test
    void duplicateVirtualSourcesFailBeforeCompilation() {
        SourceUnit root = SourceUnit.virtual("root", "", null, null, 1, 0);
        SourceUnit item = SourceUnit.virtual("item", "", null, null, 1, 0);
        assertThrows(CompilationException.class, () -> new ModuleResolver().resolve(root,
                new CompilationConfig.Builder().build(), Arrays.asList(item, item)));
    }
}
