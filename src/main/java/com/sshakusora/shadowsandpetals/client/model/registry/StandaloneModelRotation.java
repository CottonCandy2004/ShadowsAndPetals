package com.sshakusora.shadowsandpetals.client.model.registry;

/** A quarter-turn rotation applied to a standalone baked model. */
public record StandaloneModelRotation(int xDegrees, int yDegrees, int zDegrees) {
    public static final StandaloneModelRotation IDENTITY = new StandaloneModelRotation(0, 0, 0);

    public StandaloneModelRotation {
        if (Math.floorMod(xDegrees, 90) != 0
                || Math.floorMod(yDegrees, 90) != 0
                || Math.floorMod(zDegrees, 90) != 0) {
            throw new IllegalArgumentException("Standalone model rotations must use quarter turns");
        }
    }

    public static StandaloneModelRotation of(int xDegrees, int yDegrees) {
        return new StandaloneModelRotation(xDegrees, yDegrees, 0);
    }
}
