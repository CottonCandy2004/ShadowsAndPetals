package com.sshakusora.shadowsandpetals.client.renderer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
public final class ClientFluidRenderInfo {
  private ClientFluidRenderInfo() {}
  public static int applyLightEmission(int light, FluidStack stack) { return light; }
  public static Info createItemSurface(FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) { return new Info(null, -1, 0); }
  public record Info(@Nullable TextureAtlasSprite sprite, int color, int lightEmission) {}
  public static final class Cache<T> { public Cache() {} public void invalidate() {} }
}