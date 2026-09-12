package com.sshakusora.shadowsandpetals.client.outline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.api.outline.BlockOutlineContext;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/** Reloadable selection geometry for the four lamp model families. */
public final class LampOutlineCache extends SimplePreparableReloadListener<LampOutlineCache.Prepared> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation RELOAD_ID = ShadowsAndPetals.asResource("lamp_outlines");
    private static final LampOutlineCache INSTANCE = new LampOutlineCache();
    private volatile Map<Block, Definition> definitions = Map.of();
    private volatile Map<Block, Map<Direction, OutlineGeometry>> outlines = Map.of();

    private LampOutlineCache() {
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        Map<Block, Definition> registered = new IdentityHashMap<>();
        add(registered, BlockRegistry.BEDROOM_LAMP.get(), "bedroom_lamp", Orientation.FIXED);
        add(registered, BlockRegistry.WALL_LAMP.get(), "wall_lamp", Orientation.HORIZONTAL);
        add(registered, BlockRegistry.EMERGENCY_LAMP.get(), "emergency_lamp", Orientation.DIRECTIONAL);
        add(registered, BlockRegistry.DESK_LAMP.get(), "desk_lamp", Orientation.HORIZONTAL);
        INSTANCE.definitions = immutableIdentityMap(registered);
        registered.keySet().forEach(block -> BlockOutlineRegistry.register(block, LampOutlineCache::getOutline));
        event.registerReloadListener(INSTANCE);
    }

    private static @Nullable OutlineGeometry getOutline(BlockState state, BlockOutlineContext context) {
        Definition definition = INSTANCE.definitions.get(state.getBlock());
        if (definition == null) {
            return null;
        }
        Map<Direction, OutlineGeometry> byDirection = INSTANCE.outlines.get(state.getBlock());
        return byDirection == null ? null : byDirection.get(definition.direction(state));
    }

    @Override
    protected Prepared prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Block, Map<Direction, OutlineGeometry>> prepared = new IdentityHashMap<>();
        for (Definition definition : definitions.values()) {
            prepared.put(definition.block(), buildDirections(load(manager, definition), definition.orientation()));
        }
        return new Prepared(immutableIdentityMap(prepared));
    }

    @Override
    protected void apply(Prepared prepared, ResourceManager manager, ProfilerFiller profiler) {
        outlines = prepared.outlines();
        LOGGER.debug("Loaded model outlines for {} lamp blocks", outlines.size());
    }

    private static void add(Map<Block, Definition> definitions, Block block, String modelName, Orientation orientation) {
        definitions.put(block, new Definition(block,
                ShadowsAndPetals.asResource("models/block/" + modelName + "/off.json"), orientation));
    }

    private static OutlineGeometry load(ResourceManager manager, Definition definition) {
        Resource resource = manager.getResource(definition.model()).orElseThrow(() ->
                new IllegalArgumentException("Missing lamp outline model " + definition.model()));
        try (Reader reader = resource.openAsReader()) {
            JsonObject model = JsonParser.parseReader(reader).getAsJsonObject();
            OutlineGeometry geometry = RockeryOutlineGeometry.fromModel(model);
            if (geometry == null || geometry.lines().isEmpty()) {
                throw new IllegalArgumentException("Lamp outline model has no visible geometry " + definition.model());
            }
            return geometry;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("Failed to load lamp outline model " + definition.model(), exception);
        }
    }

    private static Map<Direction, OutlineGeometry> buildDirections(OutlineGeometry base, Orientation orientation) {
        EnumMap<Direction, OutlineGeometry> result = new EnumMap<>(Direction.class);
        switch (orientation) {
            case FIXED -> result.put(Direction.UP, base);
            case HORIZONTAL -> {
                result.put(Direction.NORTH, base);
                result.put(Direction.EAST, transform(result.get(Direction.NORTH), LampOutlineCache::rotateClockwise));
                result.put(Direction.SOUTH, transform(result.get(Direction.EAST), LampOutlineCache::rotateClockwise));
                result.put(Direction.WEST, transform(result.get(Direction.SOUTH), LampOutlineCache::rotateClockwise));
            }
            case DIRECTIONAL -> {
                result.put(Direction.UP, base);
                result.put(Direction.DOWN, transform(base, point -> transformPoint(point, Direction.DOWN)));
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    result.put(direction, transform(base, point -> transformPoint(point, direction)));
                }
            }
        }
        return Map.copyOf(result);
    }

    static OutlineGeometry transform(OutlineGeometry geometry, UnaryOperator<Vec3> transform) {
        return OutlineGeometry.of(geometry.lines().stream()
                .map(line -> new OutlineGeometry.Line(transform.apply(line.from()), transform.apply(line.to())))
                .toList());
    }

    static Vec3 rotateClockwise(Vec3 point) {
        return new Vec3(16.0D - point.z, point.y, point.x);
    }

    static Vec3 transformPoint(Vec3 point, Direction direction) {
        return switch (direction) {
            case UP -> point;
            case DOWN -> new Vec3(point.x, 16.0D - point.y, 16.0D - point.z);
            case NORTH -> new Vec3(point.x, point.z, 16.0D - point.y);
            case EAST -> new Vec3(point.y, point.z, point.x);
            case SOUTH -> new Vec3(16.0D - point.x, point.z, point.y);
            case WEST -> new Vec3(16.0D - point.y, point.z, 16.0D - point.x);
        };
    }

    static OutlineGeometry selectDirection(Map<Direction, OutlineGeometry> cache, Direction direction) {
        OutlineGeometry selected = cache.get(direction);
        return selected != null ? selected : cache.get(Direction.UP);
    }

    private static <V> Map<Block, V> immutableIdentityMap(Map<Block, V> source) {
        return Collections.unmodifiableMap(new IdentityHashMap<>(source));
    }

    enum Orientation { FIXED, HORIZONTAL, DIRECTIONAL }

    record Definition(Block block, ResourceLocation model, Orientation orientation) {
        private Direction direction(BlockState state) {
            return switch (orientation) {
                case FIXED -> Direction.UP;
                case HORIZONTAL -> state.getValue(HorizontalDirectionalBlock.FACING);
                case DIRECTIONAL -> state.getValue(DirectionalBlock.FACING);
            };
        }
    }

    public record Prepared(Map<Block, Map<Direction, OutlineGeometry>> outlines) {
    }
}
