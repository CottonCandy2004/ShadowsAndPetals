package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;

/** Legacy 1.21.1 model callback names retained as no-op hooks. */
public final class StandardBlockModels {
    private StandardBlockModels() {}
    public static void cubeAll(BlockModelContext context, SAPBlockModelGenerator generator) {}
    public static void cubeAll(Object... ignored) {}
    public static void simpleBlock(Object... ignored) {}
    public static void simpleBlockWithItem(Object... ignored) {}
    public static void simpleWaterloggedBlockWithItem(Object... ignored) {}
    public static void horizontalFacingCubeAll(Object... ignored) {}
    public static void fluid(Object... ignored) {}
    public static void verticalSlab(Object... ignored) {}
    public static void slab(Object... ignored) {}
    public static void stairs(Object... ignored) {}
    public static void parentBlockItem(Object... ignored) {}
}
