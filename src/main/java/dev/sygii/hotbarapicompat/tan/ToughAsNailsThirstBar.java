package dev.sygii.hotbarapicompat.tan;

import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapicompat.mixin.ThirstOverlayRendererAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModConfig;
import toughasnails.thirst.ThirstOverlayRenderer;

public class ToughAsNailsThirstBar {

    public static class Renderer extends StatusBarRenderer {

        private static final Identifier ICONS = Identifier.tryParse("toughasnails:textures/gui/icons.png");

        public Renderer() {
            super(Identifier.of("toughasnails", "thirst_renderer"), ICONS, StatusBarRenderer.Position.LEFT, StatusBarRenderer.Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            IThirst thirst = ThirstHelper.getThirst(playerEntity);

            ThirstOverlayRendererAccessor.getRANDOM().setSeed(ThirstOverlayRendererAccessor.getUpdateCounter() * 312871L);

            for(int i = 0; i < 10; ++i) {
                int dropletHalf = i * 2 + 1;
                int iconIndex = 0;
                int startX = xPosition - i * 8;
                int startY = yPosition;
                int backgroundU = 0;
                if (playerEntity.hasStatusEffect(TANEffects.THIRST)) {
                    iconIndex += 4;
                    backgroundU += 117;
                }

                if (thirst.getHydration() <= 0.0F && ThirstOverlayRendererAccessor.getUpdateCounter() % (thirst.getThirst() * 3 + 1) == 0) {
                    startY = yPosition + (ThirstOverlayRendererAccessor.getRANDOM().nextInt(3) - 1);
                }

                context.drawTexture(ThirstOverlayRenderer.OVERLAY, startX, startY, backgroundU, 32, 9, 9);
                if (thirst.getThirst() > dropletHalf) {
                    context.drawTexture(ThirstOverlayRenderer.OVERLAY, startX, startY, (iconIndex + 4) * 9, 32, 9, 9);
                } else if (thirst.getThirst() == dropletHalf) {
                    context.drawTexture(ThirstOverlayRenderer.OVERLAY, startX, startY, (iconIndex + 5) * 9, 32, 9, 9);
                }
            }
        }
    }

    public static class Logic extends StatusBarLogic {

        public Logic() {
            super(Identifier.of("toughasnails", "thirst_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            if (!FabricLoader.getInstance().isModLoaded("toughasnails")) {
                return false;
            }
            return ModConfig.thirst.enableThirst;
        }
    }
}
