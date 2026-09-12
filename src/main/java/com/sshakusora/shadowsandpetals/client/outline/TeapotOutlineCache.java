package com.sshakusora.shadowsandpetals.client.outline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.api.outline.BlockOutlineContext;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import com.sshakusora.shadowsandpetals.block.decoration.CopperTeapotBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillPart;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Reloadable outlines for the copper teapot and its Irori composite. */
public final class TeapotOutlineCache extends SimplePreparableReloadListener<TeapotOutlineCache.Prepared> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation RELOAD_ID = ShadowsAndPetals.asResource("teapot_outlines");
    private static final ResourceLocation MAIN_MODEL = ShadowsAndPetals.asResource("models/block/teapot/copper/main.json");
    private static final TeapotOutlineCache INSTANCE = new TeapotOutlineCache();
    private volatile Map<Boolean, Map<Direction, OutlineGeometry>> outlines = Map.of();
    private volatile Map<IroriGrillPart, Map<Direction, OutlineGeometry>> compositeOutlines = Map.of();

    private TeapotOutlineCache() {
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        BlockOutlineRegistry.register(BlockRegistry.COPPER_TEAPOT.get(), TeapotOutlineCache::getOutline);
        BlockOutlineRegistry.register(BlockRegistry.IRORI_GRILL_COPPER_TEAPOT.get(), TeapotOutlineCache::getCompositeOutline);
        event.registerReloadListener(INSTANCE);
    }

    private static @Nullable OutlineGeometry getOutline(BlockState state, BlockOutlineContext context) {
        Map<Direction, OutlineGeometry> byDirection = INSTANCE.outlines.get(false);
        return byDirection == null ? null : byDirection.get(state.getValue(CopperTeapotBlock.FACING));
    }

    private static @Nullable OutlineGeometry getCompositeOutline(BlockState state, BlockOutlineContext context) {
        Map<Direction, OutlineGeometry> byDirection = INSTANCE.compositeOutlines
                .get(state.getValue(IroriGrillBlock.GRILL_PART));
        return byDirection == null ? null : byDirection.get(state.getValue(CopperTeapotBlock.FACING));
    }

    @Override
    protected Prepared prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Boolean, Map<Direction, OutlineGeometry>> base = buildDirections(load(manager, MAIN_MODEL));
        EnumMap<IroriGrillPart, Map<Direction, OutlineGeometry>> composite = new EnumMap<>(IroriGrillPart.class);
        for (IroriGrillPart part : IroriGrillPart.values()) {
            ResourceLocation id = ShadowsAndPetals.asResource(
                    "models/block/grill/double/" + part.modelName() + "_upper.json");
            OutlineGeometry grill = load(manager, id);
            if (part == IroriGrillPart.STRIP_WEST || part == IroriGrillPart.STRIP_EAST) {
                grill = RockeryOutlineGeometry.rotateClockwise(grill);
            }
            EnumMap<Direction, OutlineGeometry> byDirection = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                byDirection.put(direction, combine(base.get(true).get(direction), grill));
            }
            composite.put(part, Map.copyOf(byDirection));
        }
        return new Prepared(base, Map.copyOf(composite));
    }

    @Override
    protected void apply(Prepared prepared, ResourceManager manager, ProfilerFiller profiler) {
        outlines = prepared.outlines();
        compositeOutlines = prepared.compositeOutlines();
        LOGGER.debug("Loaded model outlines for the copper teapot and its Irori grill composite");
    }

    private static OutlineGeometry load(ResourceManager manager, ResourceLocation modelId) {
        Resource resource = manager.getResource(modelId).orElseThrow(() ->
                new IllegalArgumentException("Missing outline model " + modelId));
        try (Reader reader = resource.openAsReader()) {
            JsonObject model = JsonParser.parseReader(reader).getAsJsonObject();
            OutlineGeometry geometry = RockeryOutlineGeometry.fromModel(model);
            if (geometry == null || geometry.lines().isEmpty()) {
                throw new IllegalArgumentException("Outline model has no visible geometry " + modelId);
            }
            return geometry;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("Failed to load outline model " + modelId, exception);
        }
    }

    static Map<Boolean, Map<Direction, OutlineGeometry>> buildDirections(OutlineGeometry base) {
        OutlineGeometry raised = RockeryOutlineGeometry.translate(
                base, 0.0D, CopperTeapotBlock.IRORI_RENDER_OFFSET * 16.0D, 0.0D);
        return Map.of(false, horizontal(base), true, horizontal(raised));
    }

    private static Map<Direction, OutlineGeometry> horizontal(OutlineGeometry base) {
        EnumMap<Direction, OutlineGeometry> result = new EnumMap<>(Direction.class);
        result.put(Direction.NORTH, base);
        result.put(Direction.EAST, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.NORTH)));
        result.put(Direction.SOUTH, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.EAST)));
        result.put(Direction.WEST, RockeryOutlineGeometry.rotateClockwise(result.get(Direction.SOUTH)));
        return Map.copyOf(result);
    }

    static OutlineGeometry combine(OutlineGeometry first, OutlineGeometry second) {
        List<OutlineGeometry.Line> lines = new ArrayList<>(first.lines().size() + second.lines().size());
        lines.addAll(first.lines());
        lines.addAll(second.lines());
        return OutlineGeometry.of(lines);
    }

    public record Prepared(
            Map<Boolean, Map<Direction, OutlineGeometry>> outlines,
            Map<IroriGrillPart, Map<Direction, OutlineGeometry>> compositeOutlines
    ) {
    }
}
