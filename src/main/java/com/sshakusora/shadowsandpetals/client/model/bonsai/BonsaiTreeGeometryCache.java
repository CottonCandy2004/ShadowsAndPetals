package com.sshakusora.shadowsandpetals.client.model.bonsai;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import com.sshakusora.shadowsandpetals.client.model.BakedModelSupport;
import com.sshakusora.shadowsandpetals.client.renderer.BonsaiPartCacheKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Reload-aware raw-quad cache for the chunk-rendered bonsai tree mesh. */
public final class BonsaiTreeGeometryCache {
    public static final int TRUNK_TINT_INDEX = 0;
    public static final int LEAVES_TINT_INDEX = 1;

    private static final Map<BonsaiPartCacheKey, List<BakedQuad>> PART_CACHE =
            new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Integer> TARGET_TINT_INDICES =
            new ConcurrentHashMap<>();
    private static volatile @Nullable TextureAtlasSprite baseLogSprite;
    private static volatile @Nullable TextureAtlasSprite baseLeavesSprite;

    private BonsaiTreeGeometryCache() {
    }

    public static List<BakedQuad> getTreeQuads(
            BakedModel model,
            BlockState state,
            ModelData data,
            BonsaiBlockEntity.RenderData renderData,
            @Nullable RenderType renderType
    ) {
        BonsaiPartCacheKey key = BonsaiPartCacheKey.forState(
                renderData.shape(),
                renderData.planted(),
                renderData.dead(),
                renderData.trunkBlockId(),
                renderData.leavesBlockId()
        );
        return PART_CACHE.computeIfAbsent(key, ignored -> buildTreeQuads(model, state, data, renderData, renderType));
    }

    private static List<BakedQuad> buildTreeQuads(
            BakedModel model,
            BlockState state,
            ModelData data,
            BonsaiBlockEntity.RenderData renderData,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> source = new ArrayList<>();
        RandomSource random = RandomSource.create(0x534150L);
        for (Direction direction : Direction.values()) {
            source.addAll(BakedModelSupport.getQuads(
                    model, state, direction, random, data, renderType));
        }
        source.addAll(BakedModelSupport.getQuads(model, state, null, random, data, renderType));

        TextureAtlasSprite logSprite = getBaseLogSprite();
        TextureAtlasSprite leavesSprite = getBaseLeavesSprite();
        TextureAtlasSprite trunkTarget = targetSprite(renderData.trunkBlockId());
        TextureAtlasSprite leavesTarget = renderData.dead()
                ? null : targetSprite(renderData.leavesBlockId());

        List<BakedQuad> result = new ArrayList<>(source.size());
        for (BakedQuad quad : source) {
            BakedQuad remapped = remapQuad(
                    quad, logSprite, trunkTarget, leavesSprite, leavesTarget);
            result.add(remapped);
        }
        return List.copyOf(result);
    }

    private static BakedQuad remapQuad(
            BakedQuad quad,
            TextureAtlasSprite sourceLog,
            @Nullable TextureAtlasSprite targetLog,
            TextureAtlasSprite sourceLeaves,
            @Nullable TextureAtlasSprite targetLeaves
    ) {
        TextureAtlasSprite source = quad.getSprite();
        TextureAtlasSprite target;
        int tintIndex;
        if (spritesMatch(source, sourceLog) && targetLog != null) {
            target = targetLog;
            tintIndex = TRUNK_TINT_INDEX;
        } else if (spritesMatch(source, sourceLeaves) && targetLeaves != null) {
            target = targetLeaves;
            tintIndex = LEAVES_TINT_INDEX;
        } else {
            return quad;
        }

        int[] vertices = quad.getVertices().clone();
        for (int vertex = 0; vertex < 4; vertex++) {
            int offset = vertex * 8;
            float u = Float.intBitsToFloat(vertices[offset + 4]);
            float v = Float.intBitsToFloat(vertices[offset + 5]);
            float localU = normalized(u, source.getU0(), source.getU1());
            float localV = normalized(v, source.getV0(), source.getV1());
            vertices[offset + 4] = Float.floatToRawIntBits(target.getU(localU));
            vertices[offset + 5] = Float.floatToRawIntBits(target.getV(localV));
        }
        return new BakedQuad(vertices, tintIndex, quad.getDirection(), target,
                quad.isShade(), quad.hasAmbientOcclusion());
    }

    public static List<BakedQuad> rotateQuads(List<BakedQuad> source, int rotationSegment) {
        if (source.isEmpty()) {
            return source;
        }
        double angle = Math.toRadians(
                com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiModelTransform
                        .rotationDegrees(rotationSegment));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        List<BakedQuad> result = new ArrayList<>(source.size());
        for (BakedQuad quad : source) {
            result.add(rotateQuad(quad, sin, cos));
        }
        return List.copyOf(result);
    }

    /** Returns the first tint index used by a target block's baked model. */
    public static int getTargetTintIndex(@Nullable ResourceLocation blockId) {
        if (blockId == null) {
            return -1;
        }
        return TARGET_TINT_INDICES.computeIfAbsent(blockId, BonsaiTreeGeometryCache::findTargetTintIndex);
    }

    private static int findTargetTintIndex(ResourceLocation blockId) {
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return -1;
        }
        BlockState state = block.defaultBlockState();
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(
                BlockModelShaper.stateToModelLocation(state));
        RandomSource random = RandomSource.create(0x534150C0L);
        for (Direction direction : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, direction, random)) {
                if (quad.getTintIndex() >= 0) {
                    return quad.getTintIndex();
                }
            }
        }
        for (BakedQuad quad : model.getQuads(state, null, random)) {
            if (quad.getTintIndex() >= 0) {
                return quad.getTintIndex();
            }
        }
        return -1;
    }

    private static BakedQuad rotateQuad(BakedQuad quad, double sin, double cos) {
        int[] vertices = quad.getVertices().clone();
        for (int vertex = 0; vertex < 4; vertex++) {
            int offset = vertex * 8;
            float x = Float.intBitsToFloat(vertices[offset]);
            float z = Float.intBitsToFloat(vertices[offset + 2]);
            double dx = x - 0.5D;
            double dz = z - 0.5D;
            vertices[offset] = Float.floatToRawIntBits((float) (dx * cos + dz * sin + 0.5D));
            vertices[offset + 2] = Float.floatToRawIntBits((float) (-dx * sin + dz * cos + 0.5D));
            vertices[offset + 7] = rotateNormal(vertices[offset + 7], sin, cos);
        }
        Direction direction = rotateDirection(quad.getDirection(), sin, cos);
        return new BakedQuad(vertices, quad.getTintIndex(), direction, quad.getSprite(),
                quad.isShade(), quad.hasAmbientOcclusion());
    }

    private static int rotateNormal(int packed, double sin, double cos) {
        float x = ((byte) (packed & 0xFF)) / 127.0F;
        float z = ((byte) ((packed >>> 16) & 0xFF)) / 127.0F;
        int nx = Math.round((float) (x * cos + z * sin) * 127.0F) & 0xFF;
        int nz = Math.round((float) (-x * sin + z * cos) * 127.0F) & 0xFF;
        return nx | (packed & 0x0000FF00) | (nz << 16);
    }

    private static Direction rotateDirection(Direction direction, double sin, double cos) {
        double x = direction.getStepX();
        double z = direction.getStepZ();
        return Direction.getNearest(x * cos + z * sin, direction.getStepY(), -x * sin + z * cos);
    }

    private static @Nullable TextureAtlasSprite targetSprite(@Nullable ResourceLocation blockId) {
        if (blockId == null) {
            return null;
        }
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return null;
        }
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(
                BlockModelShaper.stateToModelLocation(block.defaultBlockState()));
        return model.getParticleIcon();
    }

    private static synchronized TextureAtlasSprite getBaseLogSprite() {
        if (baseLogSprite == null) {
            baseLogSprite = atlas().getSprite(ShadowsAndPetals.asResource("block/maple_log"));
        }
        return baseLogSprite;
    }

    private static synchronized TextureAtlasSprite getBaseLeavesSprite() {
        if (baseLeavesSprite == null) {
            baseLeavesSprite = atlas().getSprite(ShadowsAndPetals.asResource("block/maple_leaves_0"));
        }
        return baseLeavesSprite;
    }

    private static TextureAtlas atlas() {
        return Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    private static float normalized(float value, float min, float max) {
        return Math.clamp((value - min) / (max - min), 0.0F, 1.0F);
    }

    private static boolean spritesMatch(TextureAtlasSprite first, TextureAtlasSprite second) {
        return first.atlasLocation().equals(second.atlasLocation())
                && first.getU0() == second.getU0()
                && first.getU1() == second.getU1()
                && first.getV0() == second.getV0()
                && first.getV1() == second.getV1();
    }

    /** Discards model and atlas references after a resource reload. */
    public static synchronized void invalidate() {
        PART_CACHE.clear();
        TARGET_TINT_INDICES.clear();
        baseLogSprite = null;
        baseLeavesSprite = null;
    }
}
