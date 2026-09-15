package com.sshakusora.shadowsandpetals.client.outline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LampOutlineCacheTest {
    private static final double EPSILON = 1.0E-6D;

    @Test
    void everyLampOffModelProducesVisibleNonDegenerateGeometry() throws IOException {
        Map<String, Integer> expectedElementCounts = Map.of(
                "bedroom_lamp", 17,
                "wall_lamp", 19,
                "emergency_lamp", 23,
                "desk_lamp", 9
        );
        for (Map.Entry<String, Integer> entry : expectedElementCounts.entrySet()) {
            String name = entry.getKey();
            // The migrated OBJ lamp families keep their original element
            // model under lamp_geometry/ so that the 1.21.1 OBJ wrapper does
            // not feed 26.1.2 fields to the vanilla model deserializer.
            String resourceName = "assets/shadowsandpetals/lamp_geometry/" + name + "/off.json";
            JsonObject model;
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
                assertNotNull(stream, resourceName);
                model = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                        .getAsJsonObject();
            }

            assertEquals(entry.getValue(), model.getAsJsonArray("elements").size(), resourceName);
            OutlineGeometry geometry = RockeryOutlineGeometry.fromModel(model);
            assertNotNull(geometry, resourceName);
            assertFalse(geometry.lines().isEmpty(), resourceName);
            assertTrue(geometry.lines().stream().noneMatch(line ->
                    line.from().distanceToSqr(line.to()) <= EPSILON * EPSILON
            ), resourceName);
        }
    }

    @Test
    void migratedLampModelsUseObjWrappersAndKeepEmissiveOnFaces() throws IOException {
        Map<String, String[]> variants = Map.of(
                "bedroom_lamp", new String[]{"off", "on"},
                "desk_lamp", new String[]{"off", "on"},
                "emergency_lamp", new String[]{"off", "on"},
                "recessed_lamp", new String[]{"down_off", "down_on", "up_off", "up_on"},
                "wall_lamp", new String[]{"off", "on"}
        );

        for (Map.Entry<String, String[]> entry : variants.entrySet()) {
            for (String variant : entry.getValue()) {
                String base = "assets/shadowsandpetals/models/block/"
                        + entry.getKey() + "/" + variant;
                JsonObject wrapper;
                try (InputStream stream = getClass().getClassLoader().getResourceAsStream(base + ".json")) {
                    assertNotNull(stream, base + ".json");
                    wrapper = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                            .getAsJsonObject();
                }
                assertEquals("neoforge:obj", wrapper.get("loader").getAsString(), base);
                assertFalse(wrapper.has("elements"), base);

                try (InputStream stream = getClass().getClassLoader().getResourceAsStream(base + ".obj")) {
                    assertNotNull(stream, base + ".obj");
                    String obj = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    assertTrue(obj.contains("usemtl "), base);
                    if (variant.endsWith("_on") || variant.equals("on")) {
                        assertTrue(obj.contains("_glow"), base);
                    }
                }
                try (InputStream stream = getClass().getClassLoader().getResourceAsStream(base + ".mtl")) {
                    assertNotNull(stream, base + ".mtl");
                    String mtl = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    assertTrue(mtl.contains("Ka 1 1 1"), base);
                }
            }
        }
    }

    @Test
    void blockstatesAndItemParentsResolveToMigratedObjModels() throws IOException {
        for (String family : new String[]{"bedroom_lamp", "desk_lamp", "emergency_lamp", "recessed_lamp", "wall_lamp"}) {
            JsonObject blockstate = loadJson("assets/shadowsandpetals/blockstates/" + family + ".json");
            for (Map.Entry<String, com.google.gson.JsonElement> variant
                    : blockstate.getAsJsonObject("variants").entrySet()) {
                JsonObject state = variant.getValue().getAsJsonObject();
                String modelId = state.get("model").getAsString();
                JsonObject model = loadJson(modelResource(modelId));
                if (model.has("loader")) {
                    assertEquals("neoforge:obj", model.get("loader").getAsString(), variant.getKey());
                } else {
                    assertTrue(model.has("parent"), variant.getKey());
                    assertTrue(model.get("parent").getAsString().startsWith(
                            "shadowsandpetals:block/recessed_lamp/"), variant.getKey());
                    double expectedSlabOffset = modelId.contains("down_slab") ? -0.5D : 0.5D;
                    assertEquals(expectedSlabOffset,
                            model.getAsJsonObject("transform").getAsJsonArray("translation").get(1).getAsDouble(),
                            variant.getKey());
                }
            }

            JsonObject item = loadJson("assets/shadowsandpetals/models/item/" + family + ".json");
            String parentId = item.get("parent").getAsString();
            JsonObject parent = loadJson(modelResource(parentId));
            assertEquals("neoforge:obj", parent.get("loader").getAsString(), family + " item");
        }
    }

    private static String modelResource(String modelId) {
        int separator = modelId.indexOf(':');
        assertTrue(separator > 0, modelId);
        return "assets/" + modelId.substring(0, separator) + "/models/"
                + modelId.substring(separator + 1) + ".json";
    }

    private static JsonObject loadJson(String resourceName) throws IOException {
        try (InputStream stream = LampOutlineCacheTest.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            assertNotNull(stream, resourceName);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        }
    }

    @Test
    void horizontalRotationReturnsToTheOriginalGeometryAfterFourTurns() {
        OutlineGeometry original = OutlineGeometry.box(2.0D, 3.0D, 4.0D, 11.0D, 13.0D, 15.0D);
        OutlineGeometry rotated = original;
        for (int turn = 0; turn < 4; turn++) {
            rotated = LampOutlineCache.transform(rotated, LampOutlineCache::rotateClockwise);
        }

        assertEquals(original.lines(), rotated.lines());
    }

    @Test
    void directionalRotationsPreserveTheExpectedInstallationBounds() {
        OutlineGeometry source = OutlineGeometry.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        Map<Direction, Bounds> expected = new EnumMap<>(Direction.class);
        expected.put(Direction.UP, new Bounds(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D));
        expected.put(Direction.DOWN, new Bounds(4.0D, 4.0D, 4.0D, 12.0D, 16.0D, 12.0D));
        expected.put(Direction.NORTH, new Bounds(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 16.0D));
        expected.put(Direction.EAST, new Bounds(0.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D));
        expected.put(Direction.SOUTH, new Bounds(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 12.0D));
        expected.put(Direction.WEST, new Bounds(4.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));

        for (Map.Entry<Direction, Bounds> entry : expected.entrySet()) {
            OutlineGeometry transformed = LampOutlineCache.transform(
                    source,
                    point -> LampOutlineCache.transformPoint(point, entry.getKey())
            );
            assertEquals(entry.getValue(), Bounds.of(transformed), entry.getKey().toString());
        }
    }

    @Test
    void fixedLampDirectionLookupReturnsTheCachedGeometryIdentity() {
        OutlineGeometry cached = OutlineGeometry.box(3.5D, 0.0D, 3.5D, 12.5D, 13.0D, 12.5D);
        Map<Direction, OutlineGeometry> fixedCache = Map.of(Direction.UP, cached);

        assertSame(cached, LampOutlineCache.selectDirection(fixedCache, Direction.UP));
        assertSame(cached, LampOutlineCache.selectDirection(fixedCache, Direction.UP));
    }

    private record Bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        private static Bounds of(OutlineGeometry geometry) {
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;
            for (OutlineGeometry.Line line : geometry.lines()) {
                for (Vec3 point : new Vec3[]{line.from(), line.to()}) {
                    minX = Math.min(minX, point.x);
                    minY = Math.min(minY, point.y);
                    minZ = Math.min(minZ, point.z);
                    maxX = Math.max(maxX, point.x);
                    maxY = Math.max(maxY, point.y);
                    maxZ = Math.max(maxZ, point.z);
                }
            }
            return new Bounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Bounds bounds)) {
                return false;
            }
            return close(minX, bounds.minX)
                    && close(minY, bounds.minY)
                    && close(minZ, bounds.minZ)
                    && close(maxX, bounds.maxX)
                    && close(maxY, bounds.maxY)
                    && close(maxZ, bounds.maxZ);
        }

        private static boolean close(double first, double second) {
            return Math.abs(first - second) <= EPSILON;
        }
    }
}

