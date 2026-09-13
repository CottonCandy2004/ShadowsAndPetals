package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RockeryPreviewRendererTest {
    /** Centre of a 52×52 preview, the size the JEI carving page uses. */
    private static final float SIZE = 52.0F;
    private static final float CENTER = SIZE / 2.0F;

    @Test
    void selectionShapeDoesNotExpandIntoAnAdjacentStone() {
        AABB first = RockeryPreviewRenderer.selectionShape(
                new RockeryDimensions(2, 1, 1),
                new Vec3i(0, 0, 0)
        ).bounds();
        assertTrue(first.minX < 0.0D);
        assertEquals(1.0D, first.maxX);

        AABB second = RockeryPreviewRenderer.selectionShape(
                new RockeryDimensions(2, 1, 1),
                new Vec3i(1, 0, 0)
        ).bounds();
        assertEquals(0.0D, second.minX);
        assertTrue(second.maxX > 1.0D);
    }

    @Test
    void hitTestIgnoresPointersOutsideThePreview() {
        RockeryDimensions dimensions = new RockeryDimensions(3, 1, 1);
        assertEquals(-1, hitTest(dimensions, CENTER + 40.0F, CENTER));
    }

    @Test
    void hitTestPicksTheStoneOnThePointerSide() {
        RockeryDimensions dimensions = new RockeryDimensions(3, 1, 1);

        // Querying the projected centre of the rightmost stone must not fall back
        // to the middle one, which sits one block-width closer to the centre.
        assertEquals(2, hitTest(dimensions, CENTER + 9.0F, CENTER));
        assertEquals(0, hitTest(dimensions, CENTER - 9.0F, CENTER));
    }

    @Test
    void hitTestTreatsUpOnScreenAsUpInTheModel() {
        // Parts iterate Y-last within a column: index 1 is the top stone of 1×2×1.
        RockeryDimensions dimensions = new RockeryDimensions(1, 2, 1);

        assertEquals(1, hitTest(dimensions, CENTER, CENTER - 14.0F));
        assertEquals(0, hitTest(dimensions, CENTER, CENTER + 14.0F));
    }

    private static int hitTest(RockeryDimensions dimensions, float mouseX, float mouseY) {
        return RockeryPreviewRenderer.hitTest(
                dimensions,
                0.0F,
                RockeryPreviewState.scaleFor(dimensions, (int) SIZE, (int) SIZE),
                CENTER,
                CENTER,
                mouseX,
                mouseY
        );
    }
}
