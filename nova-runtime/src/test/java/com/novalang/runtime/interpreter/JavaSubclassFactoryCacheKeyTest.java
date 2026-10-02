package com.novalang.runtime.interpreter;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaSubclassFactoryCacheKeyTest {

    @Test
    void cacheKeyContainsInterfaceMethodDescriptors() {
        String key = JavaSubclassFactory.buildCacheKey(
                Object.class,
                Collections.<Class<?>>singletonList(ReloadListener.class),
                new HashSet<String>(Collections.singletonList("onEvent")),
                new Class<?>[0]);

        assertTrue(key.contains("onEvent(Ljava/lang/String;)V"));
        assertTrue(key.contains("onEvent(Ljava/lang/String;Ljava/lang/Object;)V"));
    }

    public interface ReloadListener {

        void onEvent(String eventKey);

        void onEvent(String eventKey, Object event);
    }
}
