package com.sshakusora.shadowsandpetals.client.animation;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Reloadable registry for the small rig/controller layer used by Shadows & Petals.
 *
 * <p>1.21.1 exposes entity animation clips through {@link AnimationLoader}, while
 * the mod-specific rig and controller metadata is owned by this listener. The
 * listeners are deliberately kept separate so NeoForge remains responsible for
 * parsing its entity-animation JSON.</p>
 */
public final class SAPAnimationResources implements PreparableReloadListener {
    public static final SAPAnimationResources INSTANCE = new SAPAnimationResources();

    private static final int FORMAT_VERSION = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final FileToIdConverter RIGS = FileToIdConverter.json("sap/animations/rigs");
    private static final FileToIdConverter CONTROLLERS = FileToIdConverter.json("sap/animations/controllers");

    private volatile Map<ResourceLocation, RigDefinition> rigs = Map.of();
    private volatile Map<ResourceLocation, AnimationControllerDefinition> controllers = Map.of();
    private volatile Set<ResourceLocation> clipIds = Set.of();

    private SAPAnimationResources() {
    }

    public RigDefinition rig(ResourceLocation id) {
        RigDefinition result = rigs.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Unknown animation rig " + id);
        }
        return result;
    }

    public @Nullable RigDefinition findRig(ResourceLocation id) {
        return rigs.get(id);
    }

    public AnimationControllerDefinition controller(ResourceLocation id) {
        AnimationControllerDefinition result = controllers.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Unknown animation controller " + id);
        }
        return result;
    }

    public @Nullable AnimationControllerDefinition findController(ResourceLocation id) {
        return controllers.get(id);
    }

    public Set<ResourceLocation> rigIds() {
        return rigs.keySet();
    }

    public Set<ResourceLocation> controllerIds() {
        return controllers.keySet();
    }

    public Set<ResourceLocation> clipIds() {
        return clipIds;
    }

    /** Returns the effective duration of a controller state after its playback speed. */
    public float stateDurationSeconds(AnimationResourceRef.State stateRef) {
        Objects.requireNonNull(stateRef, "stateRef");
        AnimationControllerDefinition.State state =
                controller(stateRef.controller().id()).state(stateRef.name());
        if (state.clip() == null || state.speed() <= 0.0F) {
            throw new IllegalArgumentException(
                    "Animation state " + stateRef.name()
                            + " must have a clip and positive speed");
        }

        AnimationDefinition clip = AnimationLoader.INSTANCE.getAnimation(state.clip());
        if (clip == null) {
            throw new IllegalArgumentException(
                    "Animation state " + stateRef.name()
                            + " references missing clip " + state.clip());
        }
        validateClip(state.clip(), clip);
        float duration = clip.lengthInSeconds() / state.speed();
        if (!Float.isFinite(duration) || duration <= 0.0F) {
            throw new IllegalArgumentException(
                    "Animation state " + stateRef.name()
                            + " has invalid effective duration " + duration);
        }
        return duration;
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager manager,
            ProfilerFiller preparationProfiler,
            ProfilerFiller applicationProfiler,
            Executor preparationExecutor,
            Executor applicationExecutor
    ) {
        return CompletableFuture
                .supplyAsync(() -> prepare(manager), preparationExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::apply, applicationExecutor);
    }

    private Prepared prepare(ResourceManager manager) {
        SAPAnimationRegistry.Snapshot registrations = SAPAnimationRegistry.snapshot();
        Map<ResourceLocation, RigDefinition> preparedRigs = load(
                manager,
                RIGS,
                registrations.rigs().stream().map(AnimationResourceRef.Rig::id).toList(),
                this::parseRig);
        Map<ResourceLocation, AnimationControllerDefinition> preparedControllers = load(
                manager,
                CONTROLLERS,
                registrations.controllers().stream()
                        .map(AnimationResourceRef.Controller::id)
                        .toList(),
                this::parseController);
        Set<ResourceLocation> preparedClipIds = registrations.clips().stream()
                .map(AnimationResourceRef.Clip::id)
                .collect(Collectors.toUnmodifiableSet());

        validate(registrations, preparedRigs, preparedControllers);
        return new Prepared(
                Map.copyOf(preparedRigs),
                Map.copyOf(preparedControllers),
                Set.copyOf(preparedClipIds));
    }

    private void apply(Prepared prepared) {
        rigs = prepared.rigs();
        controllers = prepared.controllers();
        clipIds = prepared.clipIds();
        UseAnimationPlaybackManager.INSTANCE.clear();
        LOGGER.info(
                "Loaded {} SAP animation rigs and {} controllers; registered {} entity clips",
                rigs.size(), controllers.size(), clipIds.size());
    }

    private <T> Map<ResourceLocation, T> load(
            ResourceManager manager,
            FileToIdConverter converter,
            List<ResourceLocation> ids,
            JsonParser<T> parser
    ) {
        Map<ResourceLocation, T> result = new HashMap<>();
        for (ResourceLocation id : ids) {
            ResourceLocation fileId = converter.idToFile(id);
            Resource resource = manager.getResource(fileId).orElseThrow(() ->
                    new IllegalArgumentException("Missing registered animation resource " + fileId));
            try (Reader reader = resource.openAsReader()) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json == null) {
                    throw new IllegalArgumentException("Animation resource is empty");
                }
                result.put(id, parser.parse(id, json));
            } catch (IOException | RuntimeException exception) {
                throw new IllegalArgumentException("Failed to load animation resource " + fileId, exception);
            }
        }
        return result;
    }

    private RigDefinition parseRig(ResourceLocation id, JsonObject json) {
        validateFormatVersion(id, json);
        JsonArray bonesJson = GsonHelper.getAsJsonArray(json, "bones");
        List<RigDefinition.BoneSpec> bones = new ArrayList<>(bonesJson.size());
        for (JsonElement element : bonesJson) {
            JsonObject bone = GsonHelper.convertToJsonObject(element, "bone");
            String name = GsonHelper.getAsString(bone, "name");
            String parent = GsonHelper.getAsString(bone, "parent", null);
            Vector3f pivot = vector(bone.get("pivot"), new Vector3f());
            JsonObject rest = GsonHelper.getAsJsonObject(bone, "rest", new JsonObject());
            Vector3f translation = vector(rest.get("translation"), new Vector3f());
            Vector3f rotation = vector(rest.get("rotation"), new Vector3f())
                    .mul((float) (Math.PI / 180.0));
            Vector3f scale = vector(rest.get("scale"), new Vector3f(1.0F));
            bones.add(new RigDefinition.BoneSpec(
                    name,
                    parent,
                    pivot,
                    new BoneTransform(translation, rotation, scale)));
        }
        return RigDefinition.create(id, bones);
    }

    private AnimationControllerDefinition parseController(ResourceLocation id, JsonObject json) {
        validateFormatVersion(id, json);
        ResourceLocation rig = ResourceLocation.parse(GsonHelper.getAsString(json, "rig"));
        String initial = GsonHelper.getAsString(json, "initial");
        Map<String, AnimationControllerDefinition.State> states = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry
                : GsonHelper.getAsJsonObject(json, "states").entrySet()) {
            JsonObject state = GsonHelper.convertToJsonObject(entry.getValue(), "state");
            ResourceLocation clip = state.has("clip")
                    ? ResourceLocation.parse(GsonHelper.getAsString(state, "clip"))
                    : null;
            float speed = GsonHelper.getAsFloat(state, "speed", 1.0F);
            ClipWrap wrap = ClipWrap.valueOf(
                    GsonHelper.getAsString(state, "wrap", "definition")
                            .toUpperCase(Locale.ROOT));
            Set<String> mask = stringSet(state.getAsJsonArray("mask"));
            boolean additive = GsonHelper.getAsBoolean(state, "additive", false);
            List<AnimationControllerDefinition.EventMarker> events = new ArrayList<>();
            JsonArray eventJson = state.getAsJsonArray("events");
            if (eventJson != null) {
                for (JsonElement eventElement : eventJson) {
                    JsonObject event = GsonHelper.convertToJsonObject(eventElement, "event");
                    events.add(new AnimationControllerDefinition.EventMarker(
                            GsonHelper.getAsFloat(event, "time"),
                            ResourceLocation.parse(GsonHelper.getAsString(event, "id"))));
                }
            }
            states.put(entry.getKey(), new AnimationControllerDefinition.State(
                    clip, speed, wrap, mask, additive, events));
        }

        List<AnimationControllerDefinition.Transition> transitions = new ArrayList<>();
        JsonArray transitionJson = json.getAsJsonArray("transitions");
        if (transitionJson != null) {
            for (JsonElement transitionElement : transitionJson) {
                JsonObject transition = GsonHelper.convertToJsonObject(
                        transitionElement, "transition");
                transitions.add(new AnimationControllerDefinition.Transition(
                        GsonHelper.getAsString(transition, "from"),
                        GsonHelper.getAsString(transition, "to"),
                        GsonHelper.getAsFloat(transition, "duration", 0.0F)));
            }
        }
        return new AnimationControllerDefinition(id, rig, initial, states, transitions);
    }

    private static void validateFormatVersion(ResourceLocation id, JsonObject json) {
        int formatVersion = GsonHelper.getAsInt(json, "format_version", FORMAT_VERSION);
        if (formatVersion != FORMAT_VERSION) {
            throw new IllegalArgumentException(
                    "Animation resource " + id + " uses unsupported format version "
                            + formatVersion + "; expected " + FORMAT_VERSION);
        }
    }

    private static void validate(
            SAPAnimationRegistry.Snapshot registrations,
            Map<ResourceLocation, RigDefinition> rigs,
            Map<ResourceLocation, AnimationControllerDefinition> controllers
    ) {
        Map<ResourceLocation, SAPAnimationRegistry.Registration> registrationByController =
                new HashMap<>();
        for (SAPAnimationRegistry.Registration registration : registrations.registrations()) {
            registrationByController.put(registration.controller().id(), registration);
        }

        for (AnimationControllerDefinition controller : controllers.values()) {
            SAPAnimationRegistry.Registration registration =
                    registrationByController.get(controller.id());
            if (registration == null || !registration.rig().id().equals(controller.rig())) {
                throw new IllegalArgumentException(
                        "Controller " + controller.id() + " does not use its registered rig");
            }
            RigDefinition rig = rigs.get(controller.rig());
            if (rig == null) {
                throw new IllegalArgumentException(
                        "Controller " + controller.id()
                                + " references missing rig " + controller.rig());
            }
            for (Map.Entry<String, AnimationControllerDefinition.State> entry
                    : controller.states().entrySet()) {
                AnimationControllerDefinition.State state = entry.getValue();
                for (String bone : state.mask()) {
                    requireRigEntry(controller.id(), rig, bone, "masked bone");
                }
                if (state.clip() == null) {
                    if (!state.events().isEmpty()) {
                        throw new IllegalArgumentException(
                                "Controller " + controller.id() + " state " + entry.getKey()
                                        + " has events but no clip");
                    }
                    continue;
                }
                if (!registration.clips().contains(new AnimationResourceRef.Clip(state.clip()))) {
                    throw new IllegalArgumentException(
                            "Controller " + controller.id()
                                    + " references unregistered clip " + state.clip());
                }
                float previousEventTime = -1.0F;
                for (AnimationControllerDefinition.EventMarker event : state.events()) {
                    if (!Float.isFinite(event.time())
                            || event.time() < previousEventTime
                            || event.time() < 0.0F) {
                        throw new IllegalArgumentException(
                                "Controller " + controller.id() + " state " + entry.getKey()
                                        + " has invalid event time " + event.time());
                    }
                    previousEventTime = event.time();
                }
            }
            Set<String> transitionPairs = new HashSet<>();
            for (AnimationControllerDefinition.Transition transition : controller.transitions()) {
                controller.state(transition.from());
                controller.state(transition.to());
                String pair = transition.from() + "\u0000" + transition.to();
                if (!transitionPairs.add(pair)) {
                    throw new IllegalArgumentException(
                            "Controller " + controller.id() + " has duplicate transition "
                                    + transition.from() + " -> " + transition.to());
                }
            }
        }

        validateProfiles(registrations.profiles(), rigs, controllers);
        validateBlockAnimations(registrations.blockAnimations(), rigs, controllers);
    }

    private static void validateBlockAnimations(
            Set<BlockAnimationDefinition> definitions,
            Map<ResourceLocation, RigDefinition> rigs,
            Map<ResourceLocation, AnimationControllerDefinition> controllers
    ) {
        for (BlockAnimationDefinition definition : definitions) {
            RigDefinition rig = rigs.get(definition.rig().id());
            if (rig == null) {
                throw new IllegalArgumentException(
                        "Block animation " + definition.id() + " references missing rig "
                                + definition.rig().id());
            }
            AnimationControllerDefinition controller = controllers.get(definition.controller().id());
            if (controller == null) {
                throw new IllegalArgumentException(
                        "Block animation " + definition.id()
                                + " references missing controller " + definition.controller().id());
            }
            if (!controller.rig().equals(definition.rig().id())) {
                throw new IllegalArgumentException(
                        "Block animation " + definition.id() + " uses controller "
                                + definition.controller().id() + " with rig " + controller.rig());
            }
            controller.state(definition.defaultState().name());
        }
    }

    private static void validateProfiles(
            Set<UseAnimationProfile> profiles,
            Map<ResourceLocation, RigDefinition> rigs,
            Map<ResourceLocation, AnimationControllerDefinition> controllers
    ) {
        for (UseAnimationProfile profile : profiles) {
            RigDefinition rig = rigs.get(profile.rig().id());
            if (rig == null) {
                throw new IllegalArgumentException(
                        "Use-animation profile " + profile.id() + " references missing rig "
                                + profile.rig().id());
            }
            AnimationControllerDefinition controller = controllers.get(profile.controller().id());
            if (controller == null) {
                throw new IllegalArgumentException(
                        "Use-animation profile " + profile.id()
                                + " references missing controller " + profile.controller().id());
            }
            controller.state(profile.defaultState().name());

            UseAnimationSequence sequence = profile.sequence();
            if (sequence != null) {
                requireTimedSequenceState(profile, controller, sequence.intro());
                requireTimedSequenceState(profile, controller, sequence.loop());
                requireTimedSequenceState(profile, controller, sequence.outro());
            }
            if (profile.firstPerson() != null) {
                for (AnimationResourceRef.Socket socket : profile.firstPerson().itemSockets().values()) {
                    requireRigEntry(profile.id(), rig, socket.name(), "socket");
                }
            }
            if (profile.thirdPerson() != null) {
                for (AnimationResourceRef.Bone bone : profile.thirdPerson().bones().values()) {
                    requireRigEntry(profile.id(), rig, bone.name(), "bone");
                }
            }
        }
    }

    private static void requireTimedSequenceState(
            UseAnimationProfile profile,
            AnimationControllerDefinition controller,
            AnimationResourceRef.State stateRef
    ) {
        if (!stateRef.controller().equals(profile.controller())) {
            throw new IllegalArgumentException(
                    "Use-animation profile " + profile.id()
                            + " has a sequence state from another controller");
        }
        AnimationControllerDefinition.State state = controller.state(stateRef.name());
        if (state.clip() == null || state.speed() <= 0.0F) {
            throw new IllegalArgumentException(
                    "Use-animation profile " + profile.id() + " sequence state "
                            + stateRef.name() + " must have a clip and positive speed");
        }
    }

    private static void requireRigEntry(
            ResourceLocation owner,
            RigDefinition rig,
            String name,
            String type
    ) {
        if (rig.indexOf(name) < 0) {
            throw new IllegalArgumentException(
                    owner + " references missing " + type + " " + name
                            + " in rig " + rig.id());
        }
    }

    private static void validateClip(ResourceLocation id, AnimationDefinition clip) {
        float length = clip.lengthInSeconds();
        if (!Float.isFinite(length) || length <= 0.0F) {
            throw new IllegalArgumentException(
                    "Animation clip " + id + " has invalid length " + length);
        }
        for (Map.Entry<String, List<AnimationChannel>> boneEntry
                : clip.boneAnimations().entrySet()) {
            if (boneEntry.getKey().isBlank()) {
                throw new IllegalArgumentException("Animation clip " + id + " has a blank bone name");
            }
            for (AnimationChannel channel : boneEntry.getValue()) {
                Keyframe[] keyframes = channel.keyframes();
                if (keyframes.length == 0) {
                    throw new IllegalArgumentException(
                            "Animation clip " + id + " has an empty channel on " + boneEntry.getKey());
                }
                float previousTime = -1.0F;
                for (Keyframe keyframe : keyframes) {
                    float timestamp = keyframe.timestamp();
                    if (!Float.isFinite(timestamp)
                            || timestamp < 0.0F
                            || timestamp > length
                            || timestamp <= previousTime
                            || !finite(keyframe.target())) {
                        throw new IllegalArgumentException(
                                "Animation clip " + id + " has invalid keyframe on "
                                        + boneEntry.getKey());
                    }
                    previousTime = timestamp;
                }
            }
        }
    }

    private static boolean finite(Vector3fc vector) {
        return Float.isFinite(vector.x())
                && Float.isFinite(vector.y())
                && Float.isFinite(vector.z());
    }

    private static Set<String> stringSet(@Nullable JsonArray json) {
        if (json == null) {
            return Set.of();
        }
        Set<String> result = new HashSet<>();
        for (JsonElement element : json) {
            String bone = GsonHelper.convertToString(element, "mask bone");
            if (!result.add(bone)) {
                throw new IllegalArgumentException("Duplicate animation mask bone " + bone);
            }
        }
        return result;
    }

    private static Vector3f vector(@Nullable JsonElement element, Vector3f fallback) {
        if (element == null) {
            return fallback;
        }
        JsonArray array = GsonHelper.convertToJsonArray(element, "vector");
        if (array.size() != 3) {
            throw new IllegalArgumentException("Animation vectors must have exactly three values");
        }
        return new Vector3f(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat());
    }

    private record Prepared(
            Map<ResourceLocation, RigDefinition> rigs,
            Map<ResourceLocation, AnimationControllerDefinition> controllers,
            Set<ResourceLocation> clipIds
    ) {
    }

    @FunctionalInterface
    private interface JsonParser<T> {
        T parse(ResourceLocation id, JsonObject json);
    }
}
