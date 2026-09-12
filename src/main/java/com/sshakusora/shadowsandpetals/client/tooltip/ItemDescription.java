package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import com.sshakusora.shadowsandpetals.tooltip.TooltipModifier;
import com.sshakusora.shadowsandpetals.tooltip.TooltipTranslationKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.GRAY;

/**
 * Three-state item tooltip description driven by localisation keys.
 * <p>
 * Reads keys in the form {@code {itemDescriptionId}.tooltip.summary},
 * {@code .conditionN} / {@code .behaviourN}, and {@code .controlN} / {@code .actionN}
 * and exposes them as three line-sets selected by keyboard modifiers.
 */
public record ItemDescription(List<Component> baseline, List<Component> onShift, List<Component> onCtrl) {

    public List<Component> currentLines() {
        Minecraft minecraft = Minecraft.getInstance();
        if (Screen.hasShiftDown()) {
            return onShift.isEmpty() ? baseline : onShift;
        }
        if (Screen.hasControlDown()) {
            return onCtrl.isEmpty() ? baseline : onCtrl;
        }
        return baseline;
    }

    @Nullable
    public static ItemDescription of(Item item) {
        String key = tooltipTranslationPrefix(item);
        if (key == null) {
            return null;
        }
        if (!I18n.exists(key + ".summary")) {
            return null;
        }
        return new Builder(key).build();
    }

    /**
     * Returns the translation prefix used by the generated item tooltip data.
     *
     * <p>In 1.21.1, {@code BlockItem#getDescriptionId()} delegates to its block
     * and therefore returns a {@code block.*} key. Tooltip data is intentionally
     * generated under the item namespace for both ordinary items and block
     * items, so the registry id must be used here instead of the display name
     * description id.</p>
     */
    @Nullable
    static String tooltipTranslationPrefix(Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        return itemId == null ? null : tooltipTranslationPrefix(itemId);
    }

    static String tooltipTranslationPrefix(ResourceLocation itemId) {
        return TooltipTranslationKeys.itemTooltip(itemId);
    }

    static final class Builder {
        private final String key;
        private final List<String> summaries = new ArrayList<>();
        private final List<String[]> behaviours = new ArrayList<>();
        private final List<String[]> actions = new ArrayList<>();

        Builder(String key) {
            this.key = key;
        }

        ItemDescription build() {
            if (I18n.exists(key + ".summary")) {
                summaries.add(I18n.get(key + ".summary"));
            }
            for (int i = 1; i < 100; i++) {
                String conditionKey = key + ".condition" + i;
                if (!I18n.exists(conditionKey)) {
                    break;
                }
                behaviours.add(new String[]{
                        I18n.get(conditionKey),
                        I18n.get(key + ".behaviour" + i)
                });
            }
            for (int i = 1; i < 100; i++) {
                String controlKey = key + ".control" + i;
                if (!I18n.exists(controlKey)) {
                    break;
                }
                actions.add(new String[]{
                        I18n.get(controlKey),
                        I18n.get(key + ".action" + i)
                });
            }

            List<Component> baseline = new ArrayList<>();
            List<Component> shift = new ArrayList<>();
            List<Component> ctrl = new ArrayList<>();

            for (String summary : summaries) {
                shift.addAll(TooltipHelper.cutTextComponent(
                        Component.literal(summary),
                        TooltipHelper.PRIMARY_STYLE,
                        TooltipHelper.HIGHLIGHT_STYLE));
            }
            if (!behaviours.isEmpty()) {
                shift.add(CommonComponents.EMPTY);
            }
            for (String[] pair : behaviours) {
                shift.add(Component.literal(pair[0]).withStyle(GRAY));
                shift.addAll(TooltipHelper.cutTextComponent(
                        Component.literal(pair[1]),
                        TooltipHelper.PRIMARY_STYLE,
                        TooltipHelper.HIGHLIGHT_STYLE,
                        1));
            }

            for (String[] pair : actions) {
                ctrl.add(Component.literal(pair[0]).withStyle(GRAY));
                ctrl.addAll(TooltipHelper.cutTextComponent(
                        Component.literal(pair[1]),
                        TooltipHelper.PRIMARY_STYLE,
                        TooltipHelper.HIGHLIGHT_STYLE,
                        1));
            }

            boolean hasDescription = !shift.isEmpty();
            boolean hasControls = !ctrl.isEmpty();
            if (hasDescription || hasControls) {
                MutableComponent shiftKey = Component.translatable(BuiltinLanguageKeys.TOOLTIP_HOLD_KEY_SHIFT.key());
                MutableComponent ctrlKey = Component.translatable(BuiltinLanguageKeys.TOOLTIP_HOLD_KEY_CTRL.key());

                for (List<Component> target : List.of(baseline, shift, ctrl)) {
                    boolean isShift = target == shift;
                    boolean isCtrl = target == ctrl;

                    if (hasDescription) {
                        target.addFirst(TooltipHelper.buildHint(
                                BuiltinLanguageKeys.TOOLTIP_HOLD_FOR_DESCRIPTION.key(), shiftKey, isShift));
                    }
                    if (hasControls) {
                        target.addFirst(TooltipHelper.buildHint(
                                BuiltinLanguageKeys.TOOLTIP_HOLD_FOR_CONTROLS.key(), ctrlKey, isCtrl));
                    }
                    if (isShift || isCtrl) {
                        int gapIndex = hasDescription && hasControls ? 2 : 1;
                        if (gapIndex < target.size()) {
                            target.add(gapIndex, CommonComponents.EMPTY);
                        }
                    }
                }
            }

            if (!hasDescription) {
                ctrl.clear();
                shift.addAll(baseline);
            }
            if (!hasControls) {
                ctrl.clear();
                ctrl.addAll(baseline);
            }
            return new ItemDescription(List.copyOf(baseline), List.copyOf(shift), List.copyOf(ctrl));
        }
    }

    /** Adds the locale-aware description and refreshes it after a language change. */
    public static final class Modifier implements TooltipModifier {
        private final Supplier<Item> itemSupplier;
        private Item cachedItem;
        private String cachedLocale;
        @Nullable
        private ItemDescription description;

        public Modifier(Item item) {
            this(() -> item);
        }

        public Modifier(Supplier<Item> itemSupplier) {
            this.itemSupplier = itemSupplier;
        }

        @Override
        public void modify(ItemTooltipEvent event) {
            if (cachedItem == null) {
                cachedItem = itemSupplier.get();
                if (cachedItem == null) {
                    return;
                }
            }
            ensureFresh();
            if (description != null) {
                event.getToolTip().addAll(1, description.currentLines());
            }
        }

        private void ensureFresh() {
            String locale = Minecraft.getInstance().getLanguageManager().getSelected();
            if (!locale.equals(cachedLocale)) {
                cachedLocale = locale;
                description = ItemDescription.of(cachedItem);
            }
        }
    }
}
