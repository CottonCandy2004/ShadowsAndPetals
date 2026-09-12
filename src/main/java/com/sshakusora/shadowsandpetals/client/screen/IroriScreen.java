package com.sshakusora.shadowsandpetals.client.screen;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.menu.IroriMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class IroriScreen extends AbstractContainerScreen<IroriMenu> {
    private static final ResourceLocation TEXTURE =
            ShadowsAndPetals.asResource("textures/gui/irori.png");
    private static final int FLAME_X = 62;
    private static final int FLAME_Y = 5;
    private static final int FLAME_U = 176;
    private static final int FLAME_V = 0;
    private static final int FLAME_WIDTH = 54;
    private static final int FLAME_HEIGHT = 45;

    public IroriScreen(IroriMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = -999;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(TEXTURE, x, y, 0, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.isLit()) {
            int height = Mth.ceil(this.menu.getLitProgress() * (FLAME_HEIGHT - 1)) + 1;
            graphics.blit(TEXTURE,
                    x + FLAME_X,
                    y + FLAME_Y + FLAME_HEIGHT - height,
                    0,
                    FLAME_U,
                    FLAME_V + FLAME_HEIGHT - height,
                    FLAME_WIDTH,
                    height,
                    256,
                    256);
        }
    }
}
