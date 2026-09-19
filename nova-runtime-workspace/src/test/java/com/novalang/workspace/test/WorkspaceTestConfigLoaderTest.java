package com.novalang.workspace.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkspaceTestConfigLoaderTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldLoadSharedAndCaseClasspathFromWorkspaceConfig() throws Exception {
        Path jar = temporaryDirectory.resolve("sample.jar");
        Files.createFile(jar);
        Path runtime = temporaryDirectory.resolve("libraries");
        Files.createDirectories(runtime);
        Path testFile = temporaryDirectory.resolve("scene.mock.nova");
        Files.write(testFile, "fun test() {}\n".getBytes(StandardCharsets.UTF_8));
        Path config = temporaryDirectory.resolve("nova.config.yml");
        String content = "test:\n"
                + "  dependencies:\n"
                + "    sample:\n"
                + "      jar: sample.jar\n"
                + "      java-types:\n"
                + "        - class: com.example.ExampleTypes\n"
                + "          method: create\n"
                + "  classpath:\n"
                + "    - sample\n"
                + "  runtime:\n"
                + "    - libraries\n"
                + "  cases:\n"
                + "    - file: scene.mock.nova\n"
                + "      classpath:\n"
                + "        - sample\n";
        Files.write(config, content.getBytes(StandardCharsets.UTF_8));

        WorkspaceTestConfig loaded = new WorkspaceTestConfigLoader().load(config);

        assertEquals(jar.toAbsolutePath().normalize(),
                loaded.getDependencies().get("sample").getJar());
        assertEquals("com.example.ExampleTypes",
                loaded.getDependencies().get("sample").getJavaTypes().get(0).getClassName());
        assertEquals("sample", loaded.getClasspath().get(0));
        assertEquals(runtime.toAbsolutePath().normalize(), loaded.getRuntime().get(0));
        assertEquals(testFile.toAbsolutePath().normalize(), loaded.getCases().get(0).getFile());
        assertEquals("sample", loaded.getCases().get(0).getClasspath().get(0));
    }
}
