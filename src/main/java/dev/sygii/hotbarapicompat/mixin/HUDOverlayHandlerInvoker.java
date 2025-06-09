package dev.sygii.hotbarapicompat.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(HUDOverlayHandler.class)
public interface HUDOverlayHandlerInvoker {

    @Invoker("enableAlpha")
    public void invokeEnableAlpha(float alpha);

    @Invoker("disableAlpha")
    public void invokeDisableAlpha(float alpha);

    @Invoker("resetFlash")
    public void invokeResetFlash();

    @Invoker("shouldShowEstimatedHealth")
    public boolean invokeShouldShowEstimatedHealth(ItemStack hoveredStack, FoodValues modifiedFoodValues);

    @Accessor
    float getFlashAlpha();
}
