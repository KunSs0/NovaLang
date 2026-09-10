package com.novalang.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 普通 for 绑定必须保留元素本身的类型，只有解构绑定才读取构造参数类型。 */
class ForElementTypeRegressionTest {
    @Test
    void singleClassElementKeepsItsMethods() {
        verify("val entries = mutableListOf<Entry>()\nentries.add(Entry(AtomicInteger(1), \"a\"))\n"
                + "for (entry in entries) { result = entry.marker() }", "a!");
    }

    @Test
    void singleSetElementKeepsItsMethods() {
        verify("val entries = mutableSetOf<Entry>()\nentries.add(Entry(AtomicInteger(1), \"b\"))\n"
                + "for (entry in entries) { result = entry.marker() }", "b!");
    }

    @Test
    void singlePairElementIsNotItsFirstComponent() {
        verify("val entries = listOf(AtomicInteger(1) to \"c!\")\n"
                + "for (entry in entries) { result = entry.second.toString() }", "c!");
    }

    @Test
    void positionalDestructuringStillResolvesComponents() {
        verify("val entries = listOf(\"key\" to \"d!\")\n"
                + "for ((key, entry) in entries) { result = key + entry }", "keyd!");
    }

    private void verify(String loop, String expected) {
        String source = "import java java.util.concurrent.atomic.AtomicInteger\nclass Entry(val host: AtomicInteger, val key: String) {\n"
                + "    fun marker(): String { return key + \"!\" }\n"
                + "}\n"
                + "fun test(): String {\nvar result = \"\"\n" + loop + "\nreturn result\n}\n";
        Nova nova = new Nova();
        CompiledNova compiled = nova.compileToBytecode(source, "for-element-type.nova");
        compiled.run();
        assertEquals(expected, compiled.call("test"));
    }
}
