package dev.sygii.hotbarapicompat.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapicompat.mixin.HUDOverlayHandlerInvoker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import squeek.appleskin.ModConfig;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.TextureHelper;

public class AppleSkinHungerUnderlay {
    public static class Renderer extends StatusBarRenderer {
        public static final Identifier MOD_ICONS = new Identifier("appleskin", "textures/icons.png");

        public Renderer() {
            super(Identifier.of("appleskin", "hunger_underlay_renderer"), MOD_ICONS, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            float exhaustion = playerEntity.getHungerManager().getExhaustion();
            //HUDOverlayHandler.INSTANCE.onPreRender(context);
            float maxExhaustion = FoodHelper.MAX_EXHAUSTION;
            float ratio = Math.min(1.0F, Math.max(0.0F, exhaustion / maxExhaustion));
            int width = (int)(ratio * 81.0F);
            int height = 9;
            ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeEnableAlpha(0.75F);
            context.drawTexture(TextureHelper.MOD_ICONS, xPosition - width + 9, yPosition, 81 - width, 18, width, height);
            ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeDisableAlpha(0.75F);
            RenderSystem.setShaderTexture(0, TextureHelper.MC_ICONS);
        }
    }

    public static class Logic extends StatusBarLogic {

        public Logic() {
            super(Identifier.of("appleskin", "hunger_underlay_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            if (!FabricLoader.getInstance().isModLoaded("appleskin")) {
                return false;
            }
            return ModConfig.INSTANCE.showFoodExhaustionHudUnderlay;
        }
    }
}
