package com.sshakusora.shadowsandpetals.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Baked block geometry bound to the bones of one SAP animation rig.
 *
 * <p>The 26.x renderer submitted {@code BlockStateModelPart} instances through
 * the render-state collector.  1.21.1 exposes the same geometry as
 * {@link BakedModel}, so the compatibility layer applies each bone's parent
 * transform and submits the model directly through the block model renderer.</p>
 */
public final class AnimatedBlockModel {
    private final AnimationResourceRef.Rig rig;
    private final List<Binding> bindings;

    public AnimatedBlockModel(
            AnimationResourceRef.Rig rig,
            Collection<Binding> bindings
    ) {
        this.rig = Objects.requireNonNull(rig, "rig");
        Objects.requireNonNull(bindings, "bindings");
        List<Binding> copied = new ArrayList<>(bindings.size());
        for (Binding binding : bindings) {
            Objects.requireNonNull(binding, "binding");
            if (!binding.rig().equals(rig)) {
                throw new IllegalArgumentException(
                        "Binding " + binding.boneName() + " belongs to rig "
                                + binding.rig().id() + ", expected " + rig.id());
            }
            copied.add(binding);
        }
        this.bindings = List.copyOf(copied);
    }

    public AnimationResourceRef.Rig rig() {
        return rig;
    }

    public List<Binding> bindings() {
        return bindings;
    }

    /**
     * Renders every bound model with the authored transform of its bone.
     * Animation-related stack ownership stays inside this method; callers may
     * wrap it in additional block orientation or multi-block offsets.
     */
    public void render(
            RigPose pose,
            PoseStack poseStack,
            BlockRenderDispatcher blockRenderer,
            BlockState tintState,
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        Objects.requireNonNull(pose, "pose");
        Objects.requireNonNull(poseStack, "poseStack");
        Objects.requireNonNull(blockRenderer, "blockRenderer");
        Objects.requireNonNull(tintState, "tintState");
        Objects.requireNonNull(buffers, "buffers");
        if (!pose.rig().id().equals(rig.id())) {
            throw new IllegalArgumentException(
                    "Animated block model expects rig " + rig.id()
                            + ", got " + pose.rig().id());
        }

        for (Binding binding : bindings) {
            BakedModel model = binding.model();
            if (model == null) {
                continue;
            }
            poseStack.pushPose();
            PoseStackRigBinder.apply(poseStack, pose, binding.boneName(), binding.mirrorX());
            RenderType renderType = binding.renderType();
            blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(),
                    buffers.getBuffer(renderType),
                    tintState,
                    model,
                    1.0F,
                    1.0F,
                    1.0F,
                    light,
                    overlay,
                    ModelData.EMPTY,
                    renderType
            );
            poseStack.popPose();
        }
    }

    /** A baked model group owned by one rig bone. */
    public record Binding(
            AnimationResourceRef.Rig rig,
            String boneName,
            BakedModel model,
            RenderType renderType,
            boolean mirrorX
    ) {
        public Binding {
            Objects.requireNonNull(rig, "rig");
            Objects.requireNonNull(boneName, "boneName");
            if (boneName.isBlank()) {
                throw new IllegalArgumentException("Animated block binding bone cannot be blank");
            }
            Objects.requireNonNull(model, "model");
            Objects.requireNonNull(renderType, "renderType");
        }

        public Binding(
                AnimationResourceRef.Rig rig,
                String boneName,
                BakedModel model
        ) {
            this(rig, boneName, model, RenderType.cutout(), false);
        }

        public Binding(
                AnimationResourceRef.Rig rig,
                String boneName,
                BakedModel model,
                RenderType renderType
        ) {
            this(rig, boneName, model, renderType, false);
        }
    }
}