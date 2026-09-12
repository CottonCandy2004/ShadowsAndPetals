package com.sshakusora.shadowsandpetals.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;

/**
 * 1.21.1 first-person compatibility facade.
 *
 * <p>The 26.x implementation submitted hand and item nodes through the newer
 * render-state collector.  In 1.21.1 the vanilla hand renderer owns that
 * submission, while {@link UseAnimationPlayer} owns the arm/item pose hook.
 * This facade keeps the old conceptual entry point available and delegates to
 * the target-era pose pipeline.</p>
 */
public final class FirstPersonRigBinder {
    private FirstPersonRigBinder() {
    }

    /** Applies a resource-driven first-person item animation through the target API. */
    public static boolean apply(
            UseAnimationProfile profile,
            PoseStack poseStack,
            LocalPlayer player,
            HumanoidArm renderedArm,
            HumanoidArm actualUseArm,
            float localTimeSeconds
    ) {
        return UseAnimationPlayer.applyFirstPerson(
                profile, poseStack, player, renderedArm, actualUseArm, localTimeSeconds);
    }

    /** Applies an already sampled first-person rig pose to the current stack. */
    public static boolean apply(
            UseAnimationProfile profile,
            PoseStack poseStack,
            LocalPlayer player,
            HumanoidArm renderedArm,
            HumanoidArm actualUseArm,
            RigPose pose
    ) {
        return UseAnimationPlayer.applyFirstPerson(
                profile, poseStack, player, renderedArm, actualUseArm, pose);
    }

    /** Applies the complete parent-chain transform of a rig socket. */
    public static void apply(
            PoseStack poseStack,
            RigPose pose,
            AnimationResourceRef.Socket socket,
            boolean mirrorX
    ) {
        PoseStackRigBinder.apply(poseStack, pose, socket, mirrorX);
    }

    /** Applies only a socket's local transform when vanilla already positioned the arm. */
    public static void applyLocalTransform(
            PoseStack poseStack,
            RigPose pose,
            AnimationResourceRef.Socket socket,
            boolean mirrorX
    ) {
        PoseStackRigBinder.applyLocalTransform(poseStack, pose, socket, mirrorX);
    }
}
