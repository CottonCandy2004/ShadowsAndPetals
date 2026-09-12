package com.sshakusora.shadowsandpetals.client.model.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Handle for one additional model and its resource-reload-aware baked cache. */
public final class StandaloneBlockModel implements ClientModelEntry {
    private final ResourceLocation id;
    private final ResourceLocation modelId;
    private final StandaloneModelRotation rotation;
    private volatile @Nullable BakedModel cachedModel;

    public StandaloneBlockModel(ResourceLocation id, ResourceLocation modelId,
                                StandaloneModelRotation rotation) {
        this.id = id;
        this.modelId = modelId;
        this.rotation = rotation;
    }

    public ResourceLocation id() {
        return id;
    }

    public ResourceLocation modelId() {
        return modelId;
    }

    public @Nullable BakedModel get() {
        BakedModel model = cachedModel;
        if (model != null) {
            return model;
        }
        BakedModel raw = Minecraft.getInstance().getModelManager()
                .getModel(ModelResourceLocation.standalone(modelId));
        if (raw == Minecraft.getInstance().getModelManager().getMissingModel()) {
            return null;
        }
        return applyRotation(raw);
    }

    @Override
    public void registerModels(ModelEvent.RegisterAdditional event,
                               Set<ModelResourceLocation> registeredIds) {
        ModelResourceLocation location = ModelResourceLocation.standalone(modelId);
        if (registeredIds.add(location)) {
            event.register(location);
        }
    }

    @Override
    public void cacheModels(ModelEvent.BakingCompleted event) {
        BakedModel raw = event.getModels().get(ModelResourceLocation.standalone(modelId));
        if (raw == null) {
            raw = event.getModelManager().getModel(ModelResourceLocation.standalone(modelId));
        }
        cachedModel = raw == null || raw == event.getModelManager().getMissingModel()
                ? null : applyRotation(raw);
    }

    private BakedModel applyRotation(BakedModel raw) {
        return rotation.equals(StandaloneModelRotation.IDENTITY)
                ? raw : new RotatedBakedModel(raw, rotation);
    }

    /** Legacy 1.21.1 replacement for the removed model-state transform hook. */
    private static final class RotatedBakedModel extends BakedModelWrapper<BakedModel>
            implements IDynamicBakedModel {
        private final StandaloneModelRotation rotation;

        private RotatedBakedModel(BakedModel original, StandaloneModelRotation rotation) {
            super(original);
            this.rotation = rotation;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, @Nullable Direction face, RandomSource random) {
            Direction sourceFace = face == null ? null : inverseDirection(face, rotation);
            List<BakedQuad> source = originalModel.getQuads(state, sourceFace, random);
            return transform(source, rotation);
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, @Nullable Direction face, RandomSource random,
                                        ModelData data, @Nullable RenderType renderType) {
            Direction sourceFace = face == null ? null : inverseDirection(face, rotation);
            List<BakedQuad> source = originalModel instanceof IDynamicBakedModel dynamic
                    ? dynamic.getQuads(state, sourceFace, random, data, renderType)
                    : originalModel.getQuads(state, sourceFace, random);
            return transform(source, rotation);
        }

        @Override
        public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
            return originalModel.applyTransform(context, poseStack, leftHand);
        }

        private static List<BakedQuad> transform(List<BakedQuad> source, StandaloneModelRotation rotation) {
            if (source.isEmpty()) {
                return source;
            }
            List<BakedQuad> result = new ArrayList<>(source.size());
            for (BakedQuad quad : source) {
                result.add(transformQuad(quad, rotation));
            }
            return result;
        }

        private static BakedQuad transformQuad(BakedQuad quad, StandaloneModelRotation rotation) {
            int[] vertices = quad.getVertices().clone();
            Direction direction = rotateDirection(quad.getDirection(), rotation);
            for (int vertex = 0; vertex < 4; vertex++) {
                int offset = vertex * 8;
                float x = Float.intBitsToFloat(vertices[offset]);
                float y = Float.intBitsToFloat(vertices[offset + 1]);
                float z = Float.intBitsToFloat(vertices[offset + 2]);
                float[] point = rotatePoint(x, y, z, rotation);
                vertices[offset] = Float.floatToRawIntBits(point[0]);
                vertices[offset + 1] = Float.floatToRawIntBits(point[1]);
                vertices[offset + 2] = Float.floatToRawIntBits(point[2]);
                vertices[offset + 7] = rotateNormal(vertices[offset + 7], rotation);
            }
            return new BakedQuad(vertices, quad.getTintIndex(), direction, quad.getSprite(),
                    quad.isShade(), quad.hasAmbientOcclusion());
        }

        private static float[] rotatePoint(float x, float y, float z, StandaloneModelRotation rotation) {
            x -= 0.5F;
            y -= 0.5F;
            z -= 0.5F;
            float[] point = rotateX(x, y, z, rotation.xDegrees());
            point = rotateY(point[0], point[1], point[2], rotation.yDegrees());
            point = rotateZ(point[0], point[1], point[2], rotation.zDegrees());
            return new float[]{point[0] + 0.5F, point[1] + 0.5F, point[2] + 0.5F};
        }

        private static float[] rotateX(float x, float y, float z, int degrees) {
            return switch (Math.floorMod(degrees, 360)) {
                case 90 -> new float[]{x, -z, y};
                case 180 -> new float[]{x, -y, -z};
                case 270 -> new float[]{x, z, -y};
                default -> new float[]{x, y, z};
            };
        }

        private static float[] rotateY(float x, float y, float z, int degrees) {
            return switch (Math.floorMod(degrees, 360)) {
                case 90 -> new float[]{z, y, -x};
                case 180 -> new float[]{-x, y, -z};
                case 270 -> new float[]{-z, y, x};
                default -> new float[]{x, y, z};
            };
        }

        private static float[] rotateZ(float x, float y, float z, int degrees) {
            return switch (Math.floorMod(degrees, 360)) {
                case 90 -> new float[]{-y, x, z};
                case 180 -> new float[]{-x, -y, z};
                case 270 -> new float[]{y, -x, z};
                default -> new float[]{x, y, z};
            };
        }

        private static Direction inverseDirection(Direction target, StandaloneModelRotation rotation) {
            for (Direction source : Direction.values()) {
                if (rotateDirection(source, rotation) == target) {
                    return source;
                }
            }
            return target;
        }

        private static Direction rotateDirection(Direction source, StandaloneModelRotation rotation) {
            float[] point = rotatePoint(source.getStepX(), source.getStepY(), source.getStepZ(), rotation);
            return Direction.getNearest(point[0] - 0.5F, point[1] - 0.5F, point[2] - 0.5F);
        }

        private static int rotateNormal(int packed, StandaloneModelRotation rotation) {
            float x = ((byte) (packed & 0xFF)) / 127.0F;
            float y = ((byte) ((packed >>> 8) & 0xFF)) / 127.0F;
            float z = ((byte) ((packed >>> 16) & 0xFF)) / 127.0F;
            float[] point = rotatePoint(x, y, z, rotation);
            int nx = Math.round((point[0] - 0.5F) * 127.0F) & 0xFF;
            int ny = Math.round((point[1] - 0.5F) * 127.0F) & 0xFF;
            int nz = Math.round((point[2] - 0.5F) * 127.0F) & 0xFF;
            return nx | (ny << 8) | (nz << 16);
        }
    }
}
