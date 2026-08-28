package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java 类继承 & 接口实现 — 编译模式集成测试
 *
 * <p>端到端：Nova 源码 → 编译 → 加载 → 反射验证生成的字节码
 * 正确继承 Java 类 / 实现 Java 接口</p>
 */
@DisplayName("编译模式: Java 类继承 & 接口实现")
class CompileJavaInheritanceTest {

    private static final NovaIrCompiler compiler = new NovaIrCompiler();

    private Map<String, Class<?>> compileAndLoad(String source) {
        return compiler.compileAndLoad(source, "test.nova");
    }

    private Object getInstance(Class<?> clazz) throws Exception {
        return clazz.getField("INSTANCE").get(null);
    }

    private Object invoke(Object target, Class<?> clazz, String method) throws Exception {
        Method m = clazz.getDeclaredMethod(method);
        m.setAccessible(true);
        return m.invoke(target);
    }

    private Object invoke(Object target, Class<?> clazz, String method, Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = clazz.getDeclaredMethod(method, paramTypes);
        m.setAccessible(true);
        return m.invoke(target, args);
    }

    // ============ 命名类实现 Java 接口 ============

    @Nested
    @DisplayName("命名类实现 Java 接口")
    class NamedClassImplementsInterface {

        @Test
        @DisplayName("Java import 原类名可直接用于父接口")
        void directJavaImportCanBeUsedAsSuperInterface() throws Exception {
            String code =
                "import java java.util.concurrent.Callable\n" +
                "class DirectImportedWork : Callable {\n" +
                "    fun call(): Any { return \"done\" }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("DirectImportedWork");

            assertNotNull(clazz);
            assertTrue(java.util.concurrent.Callable.class.isAssignableFrom(clazz));
            Object instance = clazz.getDeclaredConstructor().newInstance();
            assertEquals("done", ((java.util.concurrent.Callable<?>) instance).call());
        }

        @Test
        @DisplayName("Java 接口实现可传给强类型参数")
        void implementedJavaInterfaceCanBePassedToTypedParameter() throws Exception {
            String code =
                "import java java.util.concurrent.Callable\n" +
                "class ImportedWork : Callable {\n" +
                "    fun call(): Any { return \"done\" }\n" +
                "}\n" +
                "class WorkConsumer {\n" +
                "    fun accept(work: Callable): Any { return work.call() }\n" +
                "    fun execute(): Any { return accept(ImportedWork()) }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> consumerClass = loaded.get("WorkConsumer");

            assertNotNull(consumerClass);
            Object consumer = consumerClass.getDeclaredConstructor().newInstance();
            assertEquals("done", invoke(consumer, consumerClass, "execute"));
        }

        @Test
        @DisplayName("Java import 别名可用于父接口")
        void importedAliasCanBeUsedAsSuperInterface() throws Exception {
            String code =
                "import java java.util.concurrent.Callable as WorkContract\n" +
                "class ImportedWork : WorkContract {\n" +
                "    fun call(): Any { return \"done\" }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("ImportedWork");

            assertNotNull(clazz);
            assertTrue(java.util.concurrent.Callable.class.isAssignableFrom(clazz));
            Object instance = clazz.getDeclaredConstructor().newInstance();
            assertEquals("done", ((java.util.concurrent.Callable<?>) instance).call());
        }

        @Test
        @DisplayName("Java import 别名经 typealias 后可用于父接口")
        void importedAliasCanBeReExportedThroughTypeAlias() throws Exception {
            String code =
                "import java java.util.concurrent.Callable as WorkContractType\n" +
                "typealias WorkContract = WorkContractType\n" +
                "class TypeAliasedWork : WorkContract {\n" +
                "    fun call(): Any { return \"done\" }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("TypeAliasedWork");

            assertNotNull(clazz);
            assertTrue(java.util.concurrent.Callable.class.isAssignableFrom(clazz));
            Object instance = clazz.getDeclaredConstructor().newInstance();
            assertEquals("done", ((java.util.concurrent.Callable<?>) instance).call());
        }

        @Test
        @DisplayName("嵌套 Java 类型 import 别名可用于方法签名")
        void nestedImportedAliasCanBeUsedInMethodSignature() throws Exception {
            String code =
                "import java java.util.Map.Entry as EntryType\n" +
                "typealias ImportedEntry = EntryType\n" +
                "class EntryReader {\n" +
                "    fun read(entry: ImportedEntry): Any { return entry.getKey() }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("EntryReader");

            assertNotNull(clazz);
            Method method = clazz.getDeclaredMethod("read", Object.class);
            assertEquals(Object.class, method.getReturnType());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object entry = new java.util.AbstractMap.SimpleEntry<String, String>("key", "value");
            assertEquals("key", method.invoke(instance, entry));
        }

        @Test
        @DisplayName("嵌套 Java 类型可按原类名直接导入")
        void nestedJavaTypeCanBeImportedWithoutAlias() throws Exception {
            String code =
                "import java java.util.Map.Entry\n" +
                "class DirectEntryReader {\n" +
                "    fun read(entry: Entry): Any { return entry.getKey() }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("DirectEntryReader");

            assertNotNull(clazz);
            Method method = clazz.getDeclaredMethod("read", Object.class);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object entry = new java.util.AbstractMap.SimpleEntry<String, String>("key", "value");
            assertEquals("key", method.invoke(instance, entry));
        }

        @Test
        @DisplayName("class : Runnable — 生成的类实现 Runnable 接口")
        void testClassImplementsRunnable() throws Exception {
            String code =
                "class MyRunner : Runnable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyRunner");

            assertNotNull(clazz, "MyRunner 类应已编译");
            assertTrue(Runnable.class.isAssignableFrom(clazz),
                    "MyRunner 应实现 Runnable 接口");

            // 实例化并调用
            Object instance = clazz.getDeclaredConstructor().newInstance();
            assertTrue(instance instanceof Runnable);

            // 调用 run() 不应抛异常
            ((Runnable) instance).run();
        }

        @Test
        @DisplayName("class : Comparable — 实现带返回值的接口方法")
        void testClassImplementsComparable() throws Exception {
            String code =
                "class MyNum(val value: Int) : Comparable {\n" +
                "    fun compareTo(other: Any): Int = value - (other as MyNum).value\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyNum");

            assertNotNull(clazz);
            assertTrue(Comparable.class.isAssignableFrom(clazz),
                    "MyNum 应实现 Comparable 接口");
        }

        @Test
        @DisplayName("class : 多个接口")
        void testClassImplementsMultipleInterfaces() throws Exception {
            String code =
                "class Multi : Runnable, Cloneable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("Multi");

            assertNotNull(clazz);
            assertTrue(Runnable.class.isAssignableFrom(clazz),
                    "Multi 应实现 Runnable");
            assertTrue(Cloneable.class.isAssignableFrom(clazz),
                    "Multi 应实现 Cloneable");
        }
    }

    // ============ 命名类继承 Java 类 ============

    @Nested
    @DisplayName("命名类继承 Java 类")
    class NamedClassExtendsJavaClass {

        @Test
        @DisplayName("class : Thread(name) — 继承 Thread 并传构造器参数")
        void testClassExtendsThread() throws Exception {
            String code =
                "class MyThread : Thread(\"worker\") {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyThread");

            assertNotNull(clazz, "MyThread 类应已编译");
            assertTrue(Thread.class.isAssignableFrom(clazz),
                    "MyThread 应继承 Thread");

            // 验证不是接口
            assertFalse(clazz.isInterface());
        }

        @Test
        @DisplayName("继承的类有正确的超类")
        void testSuperclassIsCorrect() throws Exception {
            String code =
                "class Worker : Thread(\"w\") {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("Worker");

            assertNotNull(clazz);
            assertEquals(Thread.class, clazz.getSuperclass(),
                    "Worker 的超类应为 Thread");
        }

        @Test
        @DisplayName("class : Thread() — 无参构造器")
        void testClassExtendsThreadNoArgs() throws Exception {
            String code =
                "class SimpleThread : Thread() {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("SimpleThread");

            assertNotNull(clazz);
            assertTrue(Thread.class.isAssignableFrom(clazz));
        }
    }

    // ============ 匿名对象实现 Java 接口 ============

    @Nested
    @DisplayName("匿名对象实现 Java 接口")
    class AnonymousObjectImplementsInterface {

        @Test
        @DisplayName("object : Runnable — 编译生成实现接口的匿名类")
        void testAnonymousRunnable() throws Exception {
            String code =
                "object Test {\n" +
                "    fun getRunner(): Any {\n" +
                "        return object : Runnable {\n" +
                "            fun run() { }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> testClass = loaded.get("Test");
            Object instance = getInstance(testClass);

            Object runner = invoke(instance, testClass, "getRunner");
            assertNotNull(runner);
            assertTrue(runner instanceof Runnable,
                    "匿名对象应实现 Runnable");
        }
    }

    // ============ 匿名对象继承 Java 类 ============

    @Nested
    @DisplayName("匿名对象继承 Java 类")
    class AnonymousObjectExtendsJavaClass {

        @Test
        @DisplayName("object : Thread(name) — 编译生成继承 Thread 的匿名类")
        void testAnonymousThread() throws Exception {
            String code =
                "object Test {\n" +
                "    fun getThread(): Any {\n" +
                "        return object : Thread(\"anon-worker\") {\n" +
                "            fun run() { }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> testClass = loaded.get("Test");
            Object instance = getInstance(testClass);

            Object thread = invoke(instance, testClass, "getThread");
            assertNotNull(thread);
            assertTrue(thread instanceof Thread,
                    "匿名对象应继承 Thread");
            assertEquals("anon-worker", ((Thread) thread).getName(),
                    "Thread 名称应正确传递");
        }

        @Test
        @DisplayName("object : Thread() — 无参构造器匿名对象")
        void testAnonymousThreadNoArgs() throws Exception {
            String code =
                "object Test {\n" +
                "    fun getThread(): Any {\n" +
                "        return object : Thread() {\n" +
                "            fun run() { }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> testClass = loaded.get("Test");
            Object instance = getInstance(testClass);

            Object thread = invoke(instance, testClass, "getThread");
            assertNotNull(thread);
            assertTrue(thread instanceof Thread);
        }
    }

    // ============ resolveJavaClassMeta 验证 ============

    @Nested
    @DisplayName("Java 类型解析 (resolveJavaClassMeta)")
    class JavaTypeResolution {

        @Test
        @DisplayName("java.lang 包类型自动解析 (Thread)")
        void testJavaLangResolution() throws Exception {
            String code =
                "class T1 : Thread(\"t\") {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("T1");
            assertNotNull(clazz);
            assertEquals(Thread.class, clazz.getSuperclass());
        }

        @Test
        @DisplayName("java.lang 包接口自动解析 (Runnable)")
        void testRunnableResolution() throws Exception {
            String code =
                "class R1 : Runnable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("R1");
            assertNotNull(clazz);
            assertTrue(Runnable.class.isAssignableFrom(clazz));
            // 超类应仍为 Object（不是 Runnable）
            assertEquals(Object.class, clazz.getSuperclass());
        }

        @Test
        @DisplayName("java.util 包类型自动解析 (Comparator)")
        void testJavaUtilResolution() throws Exception {
            String code =
                "class MyComp : Comparator {\n" +
                "    fun compare(a: Any, b: Any): Int = 0\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyComp");
            assertNotNull(clazz);
            assertTrue(java.util.Comparator.class.isAssignableFrom(clazz));
        }

        @Test
        @DisplayName("java.io 包类型自动解析")
        void testJavaIoResolution() throws Exception {
            String code =
                "class MySerializable : Serializable {\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MySerializable");
            assertNotNull(clazz);
            assertTrue(java.io.Serializable.class.isAssignableFrom(clazz));
        }
    }

    // ============ 字节码结构验证 ============

    @Nested
    @DisplayName("字节码结构验证")
    class BytecodeStructure {

        @Test
        @DisplayName("接口实现类 — 超类为 Object")
        void testInterfaceImplSuperclass() throws Exception {
            String code =
                "class Impl : Runnable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("Impl");

            assertEquals(Object.class, clazz.getSuperclass(),
                    "实现接口的类，超类应为 Object");
        }

        @Test
        @DisplayName("类继承 — 超类为指定的 Java 类")
        void testClassInheritanceSuperclass() throws Exception {
            String code =
                "class Sub : Thread(\"s\") {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("Sub");

            assertEquals(Thread.class, clazz.getSuperclass(),
                    "继承 Thread 的类，超类应为 Thread");
        }

        @Test
        @DisplayName("覆盖的方法为 public")
        void testOverriddenMethodIsPublic() throws Exception {
            String code =
                "class MyR : Runnable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyR");

            Method runMethod = clazz.getDeclaredMethod("run");
            assertTrue(Modifier.isPublic(runMethod.getModifiers()),
                    "run() 方法应为 public");
        }

        @Test
        @DisplayName("匿名类 — 生成正确的内部类名")
        void testAnonymousClassNaming() throws Exception {
            String code =
                "object Host {\n" +
                "    fun create(): Any {\n" +
                "        return object : Runnable {\n" +
                "            fun run() { }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);

            // 应该存在 Host 和 Host$1（匿名内部类）
            Class<?> hostClass = loaded.get("Host");
            assertNotNull(hostClass, "Host 类应存在");

            // 匿名类名应包含 Host$ 前缀
            boolean hasAnonymous = false;
            for (String name : loaded.keySet()) {
                if (name.startsWith("Host$")) {
                    hasAnonymous = true;
                    Class<?> anonClass = loaded.get(name);
                    assertTrue(Runnable.class.isAssignableFrom(anonClass),
                            "匿名类应实现 Runnable");
                }
            }
            assertTrue(hasAnonymous, "应生成匿名内部类");
        }
    }

    // ============ 方法覆盖验证 ============

    @Nested
    @DisplayName("方法覆盖验证")
    class MethodOverride {

        @Test
        @DisplayName("Runnable.run() 被正确覆盖")
        void testRunOverridden() throws Exception {
            String code =
                "class MyR : Runnable {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyR");

            // 应该有 run() 方法
            Method run = clazz.getDeclaredMethod("run");
            assertNotNull(run);
            assertEquals(void.class, run.getReturnType());
        }

        @Test
        @DisplayName("Thread.run() 被正确覆盖")
        void testThreadRunOverridden() throws Exception {
            String code =
                "class MyT : Thread(\"t\") {\n" +
                "    fun run() { }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> clazz = loaded.get("MyT");

            Method run = clazz.getDeclaredMethod("run");
            assertNotNull(run);
            assertEquals(void.class, run.getReturnType());
        }
    }

    // ============ Nova 类间继承（对比验证） ============

    @Nested
    @DisplayName("Nova 类继承 — 对比验证不受影响")
    class NovaInheritanceUnaffected {

        @Test
        @DisplayName("显式 Map 返回类型不会被错误推断成声明类")
        void explicitMapReturnTypeIsPreservedAtCallSite() throws Exception {
            String code =
                "class MessageFactory {\n" +
                "    fun createMessage(): Map {\n" +
                "        val message = mutableMapOf()\n" +
                "        message.put(\"text\", \"hello\")\n" +
                "        return message\n" +
                "    }\n" +
                "}\n" +
                "class MessageConsumer {\n" +
                "    fun execute(): Any {\n" +
                "        val message = MessageFactory().createMessage()\n" +
                "        return message.get(\"text\")\n" +
                "    }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> consumerClass = loaded.get("MessageConsumer");

            assertNotNull(consumerClass);
            Object consumer = consumerClass.getDeclaredConstructor().newInstance();
            assertEquals("hello", invoke(consumer, consumerClass, "execute"));
        }

        @Test
        @DisplayName("object 方法保留 Map 返回类型并保持 Any 为动态值")
        void objectMethodPreservesDeclaredReferenceReturnTypes() throws Exception {
            String code =
                "object ObjectMessageFactory {\n" +
                "    fun createMap(): Map {\n" +
                "        val message = mutableMapOf()\n" +
                "        message.put(\"text\", \"map\")\n" +
                "        return message\n" +
                "    }\n" +
                "    fun createAny(): Any {\n" +
                "        val message = mutableMapOf()\n" +
                "        message.put(\"text\", \"any\")\n" +
                "        return message\n" +
                "    }\n" +
                "}\n" +
                "class ObjectMessageConsumer {\n" +
                "    fun readMap(): Any { return ObjectMessageFactory.createMap().get(\"text\") }\n" +
                "    fun readAny(): Any { return ObjectMessageFactory.createAny().get(\"text\") }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> consumerClass = loaded.get("ObjectMessageConsumer");

            assertNotNull(consumerClass);
            Object consumer = consumerClass.getDeclaredConstructor().newInstance();
            assertEquals("map", invoke(consumer, consumerClass, "readMap"));
            assertEquals("any", invoke(consumer, consumerClass, "readAny"));
        }

        @Test
        @DisplayName("接口与继承调用保留集合返回类型")
        void interfaceAndOverrideCallsPreserveCollectionReturnTypes() throws Exception {
            String code =
                "interface MessageSource { fun create(): Map }\n" +
                "class BaseMessageSource : MessageSource {\n" +
                "    fun create(): Map {\n" +
                "        val message = mutableMapOf()\n" +
                "        message.put(\"text\", \"base\")\n" +
                "        return message\n" +
                "    }\n" +
                "}\n" +
                "class ChildMessageSource : BaseMessageSource {\n" +
                "    override fun create(): Map {\n" +
                "        val message = super.create()\n" +
                "        message.put(\"text\", \"child\")\n" +
                "        return message\n" +
                "    }\n" +
                "}\n" +
                "class SourceConsumer {\n" +
                "    fun read(source: MessageSource): Any { return source.create().get(\"text\") }\n" +
                "    fun execute(): Any { return read(ChildMessageSource()) }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> consumerClass = loaded.get("SourceConsumer");

            assertNotNull(consumerClass);
            Object consumer = consumerClass.getDeclaredConstructor().newInstance();
            assertEquals("child", invoke(consumer, consumerClass, "execute"));
        }

        @Test
        @DisplayName("List 与自定义类返回类型可继续访问成员")
        void listAndCustomClassReturnTypesRemainUsable() throws Exception {
            String code =
                "class Payload(val text: String)\n" +
                "class TypedFactory {\n" +
                "    fun createList(): List { return listOf(\"first\") }\n" +
                "    fun createPayload(): Payload { return Payload(\"payload\") }\n" +
                "}\n" +
                "class TypedConsumer {\n" +
                "    fun readList(): Any { return TypedFactory().createList().get(0) }\n" +
                "    fun readPayload(): Any { return TypedFactory().createPayload().text }\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);
            Class<?> consumerClass = loaded.get("TypedConsumer");

            assertNotNull(consumerClass);
            Object consumer = consumerClass.getDeclaredConstructor().newInstance();
            assertEquals("first", invoke(consumer, consumerClass, "readList"));
            assertEquals("payload", invoke(consumer, consumerClass, "readPayload"));
        }

        @Test
        @DisplayName("Nova class : NovaClass — 正常继承不受影响")
        void testNovaClassInheritanceWorks() throws Exception {
            String code =
                "class Base {\n" +
                "    fun greet(): String = \"hello\"\n" +
                "}\n" +
                "class Child : Base {\n" +
                "    fun childGreet(): String = \"world\"\n" +
                "}\n";
            Map<String, Class<?>> loaded = compileAndLoad(code);

            Class<?> baseClass = loaded.get("Base");
            Class<?> childClass = loaded.get("Child");
            assertNotNull(baseClass);
            assertNotNull(childClass);

            // Child 的超类应为 Base
            assertEquals(baseClass, childClass.getSuperclass());
        }
    }
}
