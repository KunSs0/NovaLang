package com.novalang.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("NovaDynamic 已绑定实例句柄适配")
class NovaDynamicBoundHandleTest {

    static final class VoidTarget {
        private String providerId;

        public void registerScriptProsperProvider(String providerId) {
            this.providerId = providerId;
        }
    }

    static final class JvmStaticBridgeTarget {
        private static String providerId;

        public static void registerScriptProsperProvider(String providerId) {
            JvmStaticBridgeTarget.providerId = providerId;
        }
    }

    @Test
    @DisplayName("已绑定实例 void 句柄补回 receiver 后可通过动态调用约定执行")
    void boundVoidInstanceHandleShouldKeepReceiverSlot() throws Throwable {
        VoidTarget target = new VoidTarget();
        MethodHandle instanceMethod = MethodHandles.lookup().findVirtual(
                VoidTarget.class,
                "registerScriptProsperProvider",
                MethodType.methodType(void.class, String.class));
        MethodHandle boundMethod = instanceMethod.bindTo(target);

        MethodType callSiteType = MethodType.methodType(Object.class, Object.class, Object.class);
        MethodHandle adapted = NovaDynamic.adaptInstanceHandle(boundMethod, callSiteType);

        assertEquals(callSiteType, adapted.type());
        assertNull(adapted.invokeWithArguments(target, "combo-score"));
        assertEquals("combo-score", target.providerId);
    }

    @Test
    @DisplayName("实例 void 方法解析为通用动态调用句柄")
    void resolvedVoidInstanceMethodShouldAdaptToGenericCallSite() throws Throwable {
        MethodHandle resolved = NovaDynamic.resolveForCallSite(
                VoidTarget.class,
                "registerScriptProsperProvider",
                1,
                new Object[]{"combo-score"});
        VoidTarget target = new VoidTarget();

        assertEquals(MethodType.methodType(Object.class, Object.class, Object.class), resolved.type());
        assertNull(resolved.invokeWithArguments(target, "combo-score"));
        assertEquals("combo-score", target.providerId);
    }

    @Test
    @DisplayName("Kotlin object 的静态 bridge 通过实例动态路径可调用")
    void jvmStaticBridgeShouldIgnoreSyntheticReceiver() throws Throwable {
        JvmStaticBridgeTarget.providerId = null;
        MethodType callSiteType = MethodType.methodType(Object.class, Object.class, Object.class);
        CallSite callSite = NovaBootstrap.bootstrapInvoke(
                MethodHandles.lookup(),
                "registerScriptProsperProvider",
                callSiteType);

        Object result = callSite.dynamicInvoker().invokeWithArguments(
                new JvmStaticBridgeTarget(),
                "combo-score");

        assertNull(result);
        assertEquals("combo-score", JvmStaticBridgeTarget.providerId);
    }
}
