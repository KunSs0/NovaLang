package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 脚本类的同名方法按参数类型分派，业务不应自行做类型判断。 */
class CompiledMethodOverloadTest {
    @Test
    void dispatchesReferenceAndPrimitiveOverloads() {
        String source = "class PointV3 {}\nclass CameraV3 {\n"
                + " fun fixed(value: String): String { return \"id\" }\n"
                + " fun fixed(value: PointV3): String { return \"point\" }\n"
                + " fun move(value: String, ticks: Int): String { return value + ticks }\n"
                + " fun move(value: PointV3, ticks: Int): String { return \"point\" + ticks }\n"
                + "}\nfun verify(): String { val camera = CameraV3(); return camera.fixed(\"a\") + camera.fixed(PointV3()) + camera.move(\"b\", 2) + camera.move(PointV3(), 3) }";
        assertEquals("idpointb2point3", new Nova().compileToBytecode(source, "method-overloads-v3.nova").call("verify"));
    }

    @Test
    void rejectsDuplicateParameterSignatures() {
        String source = "class Camera { fun fixed(value: String): String = value\n fun fixed(other: String): Int = 1 }";
        assertThrows(RuntimeException.class, () -> new Nova().compileToBytecode(source, "duplicate-method.nova"));
    }
}
