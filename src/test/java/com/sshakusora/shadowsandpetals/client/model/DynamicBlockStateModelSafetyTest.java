package com.sshakusora.shadowsandpetals.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Compatibility tests for the 1.21.1 baked-model dispatch boundary. */
class DynamicBlockStateModelSafetyTest {
    @Test
    void legacyBakedModelDelegateReceivesTheThreeArgumentCall() {
        AtomicBoolean called = new AtomicBoolean();
        BakedModel delegate = proxyModel((proxy, method, args) -> {
            if (method.getName().equals("getQuads") && method.getParameterCount() == 3) {
                called.set(true);
                return List.of();
            }
            return defaultValue(method.getReturnType());
        });

        BakedModelSupport.getQuads(
                delegate,
                null,
                null,
                RandomSource.create(7L),
                ModelData.EMPTY,
                null
        );

        assertTrue(called.get());
    }

    @Test
    void modelDataPathStillWorksForAPlainLegacyDelegate() {
        AtomicBoolean called = new AtomicBoolean();
        BakedModel delegate = proxyModel((proxy, method, args) -> {
            if (method.getName().equals("getQuads") && method.getParameterCount() == 3) {
                called.set(true);
                return List.of();
            }
            return defaultValue(method.getReturnType());
        });

        BakedModelSupport.getQuads(
                delegate,
                null,
                null,
                RandomSource.create(11L),
                ModelData.EMPTY,
                null
        );

        assertTrue(called.get());
    }

    private static BakedModel proxyModel(java.lang.reflect.InvocationHandler handler) {
        return (BakedModel) Proxy.newProxyInstance(
                BakedModel.class.getClassLoader(),
                new Class<?>[]{BakedModel.class},
                handler
        );
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0F;
        if (type == double.class) return 0.0D;
        if (type == char.class) return (char) 0;
        return null;
    }
}
