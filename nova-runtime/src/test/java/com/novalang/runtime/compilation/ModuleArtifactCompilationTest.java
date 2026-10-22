package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledComponentFactory;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证模块图可以编译为链接后的字节码产物，并继续支持隔离组件作用域。 */
class ModuleArtifactCompilationTest {

    public interface Probe {
        int next();
    }

    @Test
    void compilesRelativeModulesIntoReusableArtifact() throws IOException {
        Path directory = Files.createTempDirectory("nova-module-artifact-");
        try {
            Files.write(directory.resolve("helper.nova"), (
                    "object ArtifactState { var count: Int = 0 }\n"
                            + "fun helperValue(): Int = 4\n"
                            + "class HelperType {\n"
                            + "    fun value(): Int = 3\n"
                            + "}\n").getBytes(StandardCharsets.UTF_8));
            Files.write(directory.resolve("second.nova"), (
                    "class SecondType {\n"
                            + "    fun value(): Int = 2\n"
                            + "}\n").getBytes(StandardCharsets.UTF_8));
            Files.write(directory.resolve("entry.nova"), (
                    "import java com.novalang.runtime.compilation.ModuleArtifactCompilationTest.Probe\n"
                            + "import \"./helper.nova\"\n"
                            + "import \"./second.nova\"\n"
                            + "fun useHelper(value: HelperType): Int = value.value()\n"
                            + "fun useSecond(value: SecondType): Int = value.value()\n"
                            + "class ArtifactProbe : Probe {\n"
                            + "    val helpers = mutableMapOf<String, HelperType>()\n"
                            + "    fun next(): Int {\n"
                            + "        helpers.put(\"one\", HelperType())\n"
                            + "        ArtifactState.count += 1\n"
                            + "        return helperValue() + useHelper(HelperType()) + useSecond(SecondType()) + ArtifactState.count\n"
                            + "    }\n"
                            + "}\n"
                            + "fun create(): Probe = ArtifactProbe()\n").getBytes(StandardCharsets.UTF_8));

            Nova nova = new Nova();
            nova.setScriptClassLoader(getClass().getClassLoader());
            Map<String, byte[]> bytecode = nova.compileToBytecodeArtifact(
                    directory.resolve("entry.nova"), new CompilationConfig.Builder().build());

            assertFalse(bytecode.isEmpty(), "模块图应产出链接字节码");
            BytecodeArtifactCache cache = new BytecodeArtifactCache();
            BytecodeArtifactCache.BytecodeArtifact artifact = cache.getOrCompile(
                    new BytecodeArtifactCache.CacheKey("test", "module-artifact", "module-graph"),
                    () -> bytecode);
            IsolatedComponentFactory<Probe> prepared = artifact.prepareComponentFactory(
                    nova, getClass().getClassLoader(), "create", Probe.class);
            CompiledComponentFactory<Probe> firstScope = prepared.createScope();
            CompiledComponentFactory<Probe> secondScope = prepared.createScope();

            Probe first = firstScope.create();
            Probe sibling = firstScope.create();
            Probe independent = secondScope.create();
            assertNotSame(first, sibling);
            assertEquals(10, first.next());
            assertEquals(11, sibling.next());
            assertEquals(10, independent.next(), "不同作用域不共享模块静态状态");
            assertTrue(firstScope.getProgram().hasFunction("helperValue"));
        } finally {
            deleteDirectory(directory);
        }
    }

    /** 删除测试临时目录；Windows 上需按子项优先顺序删除。 */
    private void deleteDirectory(Path directory) throws IOException {
        try (java.util.stream.Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
    }
}
