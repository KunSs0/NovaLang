package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 区分 JVM JavaBean getter 与 Nova 构造器属性的调用规则。
 */
@DisplayName("Nova 属性 getter 互操作")
class NovaPropertyGetterInteropTest {

    @Test
    @DisplayName("Kotlin 属性对应的 JVM getter 可以被 Nova 强类型调用")
    void kotlinStyleJvmGettersShouldCompileAndRun() throws Exception {
        String source = "import java com.novalang.runtime.codegen.NovaPropertyGetterInteropTest.KotlinStylePoint\n"
                + "object Test {\n"
                + "    fun run(): Double {\n"
                + "        val point = KotlinStylePoint(1.0, 2.0, 3.0, 4.0f, 5.0f)\n"
                + "        return point.getX() + point.getY() + point.getZ()"
                + " + point.getPitch().toDouble() + point.getYaw().toDouble()\n"
                + "    }\n"
                + "}\n";

        Object result = compileAndRun(source);

        assertEquals(15.0D, ((Number) result).doubleValue());
    }

    @Test
    @DisplayName("不存在的 Nova 属性 getX 必须在编译期被拒绝")
    void missingNovaPropertyGetterShouldFailCompilation() {
        String source = "class Frame(val x: Double) { }\n"
                + "object Test {\n"
                + "    fun run(): Double {\n"
                + "        val frame = Frame(3.0)\n"
                + "        return frame.getX()\n"
                + "    }\n"
                + "}\n";

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> compile(source, "nova-property-java-bean-getter.nova")
        );

        assertTrue(exception.getMessage().contains("getX"));
        assertTrue(exception.getMessage().contains("未声明方法"));
    }

    @Test
    @DisplayName("Nova 构造器 val 属性应通过属性语法强类型读取")
    void novaConstructorPropertyShouldUsePropertySyntax() throws Exception {
        String source = "class Frame(val x: Double, val y: Double, val z: Double) { }\n"
                + "object Test {\n"
                + "    fun run(): Double {\n"
                + "        val frame = Frame(1.0, 2.0, 3.0)\n"
                + "        return frame.x + frame.y + frame.z\n"
                + "    }\n"
                + "}\n";

        Object result = compileAndRun(source);

        assertEquals(6.0D, ((Number) result).doubleValue());
    }

    private Object compileAndRun(String source) throws Exception {
        Map<String, Class<?>> loaded = compile(source, "nova-property-getter.nova");
        Class<?> testClass = loaded.get("Test");
        assertNotNull(testClass);
        Object instance = testClass.getField("INSTANCE").get(null);
        Method run = testClass.getDeclaredMethod("run");
        run.setAccessible(true);
        return run.invoke(instance);
    }

    private Map<String, Class<?>> compile(String source, String sourceName) {
        NovaIrCompiler compiler = new NovaIrCompiler();
        compiler.setEnableSemanticAnalysis(true);
        compiler.setStrictSemanticMode(true);
        return compiler.compileAndLoad(source, sourceName);
    }

    /** 与 Kotlin data/property class 生成的公开 JVM getter 形状一致。 */
    public static final class KotlinStylePoint {

        private final double x;
        private final double y;
        private final double z;
        private final float pitch;
        private final float yaw;

        public KotlinStylePoint(double x, double y, double z, float pitch, float yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public float getPitch() {
            return pitch;
        }

        public float getYaw() {
            return yaw;
        }
    }
}
