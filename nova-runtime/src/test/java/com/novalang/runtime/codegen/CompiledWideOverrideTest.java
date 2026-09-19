package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** 原生 long/double 的双槽参数与 float 参数必须在进入 MIR 方法体前正确装箱。 */
public class CompiledWideOverrideTest {
    public abstract static class Host {
        public abstract String tick(long millis);
        public abstract double resize(float width, double scale, long count, String suffix);
    }

    @Test
    void readsWideHostParametersWithLocalVariables() {
        String source = "import java com.novalang.runtime.codegen.CompiledWideOverrideTest.Host\n" +
                "class Component : Host() {\n" +
                " var count: Int = 0\n" +
                " override fun tick(millis: Long): String {\n" +
                "  count += 1\n" +
                "  val result = count + \":\" + millis\n" +
                "  return result\n" +
                " }\n" +
                " override fun resize(width: Float, scale: Double, count: Long, suffix: String): Double {\n" +
                "  val result = width * scale + count\n" +
                "  return result\n" +
                " }\n" +
                "}\nfun create(): Host = Component()\n";
        Host host = (Host) new Nova().compileToBytecode(source, "wide-override.nova").call("create");
        assertEquals("1:50", host.tick(50L));
        assertEquals("2:9000000000", host.tick(9000000000L));
        assertEquals(10.0, host.resize(2.5f, 2.0, 5L, "end"));
    }
}
