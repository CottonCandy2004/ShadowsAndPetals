package com.sshakusora.shadowsandpetals.client.screen;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.menu.TeapotMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class TeapotScreen extends AbstractContainerScreen<TeapotMenu> {
    private static final ResourceLocation BASIC_TEXTURE =
            ShadowsAndPetals.asResource("textures/gui/teapot/basic.png");
    private static final ResourceLocation WATER_TEXTURE =
            ShadowsAndPetals.asResource("textures/gui/teapot/water.png");
    private static final ResourceLocation TEA_TEXTURE =
            ShadowsAndPetals.asResource("textures/gui/teapot/tea.png");

    private static final int TEXTURE_SIZE = 256;
    private static final int GUI_WIDTH = 200;
    private static final int GUI_HEIGHT = 182;

    public TeapotScreen(TeapotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = -999;
        this.inventoryLabelY = -999;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BASIC_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                TEXTURE_SIZE,
                TEXTURE_SIZE);

        ResourceLocation fluidTexture = getFluidTexture();
        if (fluidTexture != null) {
            graphics.blit(fluidTexture,
                    this.leftPos,
                    this.topPos,
                    0,
                    0.0F,
                    0.0F,
                    this.imageWidth,
                    this.imageHeight,
                    TEXTURE_SIZE,
                    TEXTURE_SIZE);
        }
    }

    private @Nullable ResourceLocation getFluidTexture() {
        if (!this.menu.hasFluid()) {
            return null;
        }
        return this.menu.hasWater() ? WATER_TEXTURE : TEA_TEXTURE;
    }
}
