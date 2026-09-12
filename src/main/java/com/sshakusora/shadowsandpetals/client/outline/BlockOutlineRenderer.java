package com.sshakusora.shadowsandpetals.client.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Draws model-unit outline geometry through the legacy 1.21.1 line buffer. */
public final class BlockOutlineRenderer {
    private static final float MODEL_UNIT_TO_BLOCK_UNIT = 1.0F / 16.0F;

    private BlockOutlineRenderer() {
    }

    public static void render(
            OutlineGeometry geometry,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            BlockPos blockPos,
            Vec3 cameraPosition,
            int color
    ) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        for (OutlineGeometry.Line line : geometry.lines()) {
            Vec3 from = line.from();
            Vec3 to = line.to();
            float x = (float) ((to.x - from.x) * MODEL_UNIT_TO_BLOCK_UNIT);
            float y = (float) ((to.y - from.y) * MODEL_UNIT_TO_BLOCK_UNIT);
            float z = (float) ((to.z - from.z) * MODEL_UNIT_TO_BLOCK_UNIT);
            float length = (float) Math.sqrt(x * x + y * y + z * z);
            if (length <= 1.0E-6F) {
                continue;
            }

            Vector3f normal = new Vector3f(x / length, y / length, z / length);
            float fromX = (float) (blockPos.getX() + from.x * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.x);
            float fromY = (float) (blockPos.getY() + from.y * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.y);
            float fromZ = (float) (blockPos.getZ() + from.z * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.z);
            float toX = (float) (blockPos.getX() + to.x * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.x);
            float toY = (float) (blockPos.getY() + to.y * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.y);
            float toZ = (float) (blockPos.getZ() + to.z * MODEL_UNIT_TO_BLOCK_UNIT - cameraPosition.z);

            buffer.addVertex(pose, fromX, fromY, fromZ)
                    .setColor(color)
                    .setNormal(pose, normal.x(), normal.y(), normal.z());
            buffer.addVertex(pose, toX, toY, toZ)
                    .setColor(color)
                    .setNormal(pose, normal.x(), normal.y(), normal.z());
        }
    }
}
