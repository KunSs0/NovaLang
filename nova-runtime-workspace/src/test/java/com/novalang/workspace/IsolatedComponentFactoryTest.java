package com.novalang.workspace;

import com.novalang.runtime.CompiledComponentFactory;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScriptContext;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

/** 工厂复用程序索引，隔离作用域仍隔离模块静态字段及绑定。 */
class IsolatedComponentFactoryTest {
    public interface Probe {
        int next();
    }

    private static final String SOURCE =
            "import java com.novalang.workspace.IsolatedComponentFactoryTest.Probe\n" +
            "import java java.lang.IllegalStateException\n" +
            "object State { var count: Int = 0 }\n" +
            "class Counter : Probe {\n" +
            " var value: Int = 0\n" +
            " fun next(): Int { value += 1; State.count += 1; return value * 100 + State.count }\n" +
            "}\n" +
            "fun create(): Probe = Counter()\n" +
            "fun createWithValue(value: Int): Probe = Counter()\n" +
            "fun fail(): Probe { throw IllegalStateException(\"factory failure\") }\n";

    @Test
    void isolatesScopesAndReusesFactoryWithinScope() {
        Nova nova = new Nova();
        nova.setScriptClassLoader(getClass().getClassLoader());
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        WorkspaceBytecodeArtifactCache.BytecodeArtifact artifact = cache.getOrCompile(
                new WorkspaceBytecodeArtifactCache.CacheKey("test", "factory", SOURCE),
                () -> nova.compileToBytecodeArtifact(SOURCE, "factory.nova"));
        IsolatedComponentFactory<Probe> prepared = artifact.prepareComponentFactory(
                nova, getClass().getClassLoader(), "create", Probe.class);
        CompiledComponentFactory<Probe> firstScope = prepared.createScope();
        CompiledComponentFactory<Probe> secondScope = prepared.createScope();
        Probe first = firstScope.create();
        Probe sibling = firstScope.create();
        Probe independent = secondScope.create();
        assertNotSame(first, sibling);
        assertSame(first.getClass(), sibling.getClass());
        assertNotSame(first.getClass(), independent.getClass());
        assertEquals(101, first.next());
        assertEquals(102, sibling.next());
        assertEquals(101, independent.next());
        assertTrue(firstScope.getProgram().hasFunction("fail"));
        firstScope.getProgram().set("scope-value", 7);
        assertNull(secondScope.getProgram().get("scope-value"));
        assertThrows(IllegalArgumentException.class, () -> artifact.prepareComponentFactory(
                nova, getClass().getClassLoader(), "missing", Probe.class));
        assertThrows(IllegalStateException.class, () -> firstScope.getProgram()
                .prepareComponentFactory("create", String.class).create());
        assertThrows(IllegalArgumentException.class, () -> firstScope.getProgram()
                .prepareComponentFactory("createWithValue", Probe.class));
    }

    @Test
    void restoresOuterContextOnFailure() {
        Nova nova = new Nova();
        nova.setScriptClassLoader(getClass().getClassLoader());
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        WorkspaceBytecodeArtifactCache.BytecodeArtifact artifact = cache.getOrCompile(
                new WorkspaceBytecodeArtifactCache.CacheKey("test", "failure", SOURCE),
                () -> nova.compileToBytecodeArtifact(SOURCE, "factory.nova"));
        CompiledComponentFactory<Probe> factory = artifact.prepareComponentFactory(
                nova, getClass().getClassLoader(), "fail", Probe.class).createScope();
        NovaScriptContext previous = NovaScriptContext.current();
        try {
            NovaScriptContext.init(Collections.<String, Object>singletonMap("outer", "retained"));
            NovaScriptContext outer = NovaScriptContext.current();
            assertThrows(RuntimeException.class, factory::create);
            assertSame(outer, NovaScriptContext.current());
            assertEquals("retained", NovaScriptContext.get("outer"));
        } finally {
            NovaScriptContext.setCurrent(previous);
        }
    }
}
