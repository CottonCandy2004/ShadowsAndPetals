package com.sshakusora.shadowsandpetals.client.outline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.api.outline.BlockOutlineContext;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import com.sshakusora.shadowsandpetals.block.decoration.VanityBlock;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.EnumMap;
import java.util.Map;

/** Reloadable outlines extracted from the shared vanity lower/upper models. */
public final class VanityOutlineCache extends SimplePreparableReloadListener<VanityOutlineCache.Prepared> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation RELOAD_ID = ShadowsAndPetals.asResource("vanity_outlines");
    private static final ResourceLocation LOWER_MODEL = ShadowsAndPetals.asResource("models/block/vanity/vanity_lower.json");
    private static final ResourceLocation UPPER_MODEL = ShadowsAndPetals.asResource("models/block/vanity/vanity_upper.json");
    private static final VanityOutlineCache INSTANCE = new VanityOutlineCache();
    private volatile Map<DoubleBlockHalf, Map<Direction, OutlineGeometry>> outlines = Map.of();

    private VanityOutlineCache() {
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        for (var vanity : BlockRegistry.VANITIES) {
            BlockOutlineRegistry.register(vanity.get(), VanityOutlineCache::getOutline);
        }
        event.registerReloadListener(INSTANCE);
    }

    private static @Nullable OutlineGeometry getOutline(BlockState state, BlockOutlineContext context) {
        Map<Direction, OutlineGeometry> byDirection = INSTANCE.outlines.get(state.getValue(VanityBlock.HALF));
        return byDirection == null ? null : byDirection.get(state.getValue(VanityBlock.FACING));
    }

    @Override
    protected Prepared prepare(ResourceManager manager, ProfilerFiller profiler) {
        return new Prepared(buildDirections(load(manager, LOWER_MODEL), load(manager, UPPER_MODEL)));
    }

    @Override
    protected void apply(Prepared prepared, ResourceManager manager, ProfilerFiller profiler) {
        outlines = prepared.outlines();
        LOGGER.debug("Loaded model outlines for {} vanity halves", outlines.size());
    }

    private static OutlineGeometry load(ResourceManager manager, ResourceLocation modelId) {
        Resource resource = manager.getResource(modelId).orElseThrow(() ->
                new IllegalArgumentException("Missing vanity outline model " + modelId));
        try (Reader reader = resource.openAsReader()) {
            JsonObject model = JsonParser.parseReader(reader).getAsJsonObject();
            OutlineGeometry geometry = RockeryOutlineGeometry.fromModel(model);
            if (geometry == null || geometry.lines().isEmpty()) {
                throw new IllegalArgumentException("Vanity outline model has no visible geometry " + modelId);
            }
            return geometry;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("Failed to load vanity outline model " + modelId, exception);
        }
    }

    static Map<DoubleBlockHalf, Map<Direction, OutlineGeometry>> buildDirections(
            OutlineGeometry lower, OutlineGeometry upper) {
        EnumMap<DoubleBlockHalf, Map<Direction, OutlineGeometry>> result = new EnumMap<>(DoubleBlockHalf.class);
        result.put(DoubleBlockHalf.LOWER, horizontal(lower));
        result.put(DoubleBlockHalf.UPPER, horizontal(upper));
        return Map.copyOf(result);
    }

    private static Map<Direction, OutlineGeometry> horizontal(OutlineGeometry base) {
        EnumMap<Direction, OutlineGeometry> result = new EnumMap<>(Direction.class);
        result.put(Direction.NORTH, base);
        result.put(Direction.EAST, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.NORTH)));
        result.put(Direction.SOUTH, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.EAST)));
        result.put(Direction.WEST, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.SOUTH)));
        return Map.copyOf(result);
    }

    public record Prepared(Map<DoubleBlockHalf, Map<Direction, OutlineGeometry>> outlines) {
    }
}
