package dev.sygii.hotbarapicompat.artifacts;

import artifacts.component.SwimData;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModGameRules;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ArtifactsHeliumFlamingoBar {
    public static class Renderer extends StatusBarRenderer {

        private static final Identifier HELIUM_FLAMINGO_ICON = Identifier.tryParse("artifacts:textures/gui/icons.png");

        public Renderer() {
            super(Identifier.of("artifacts", "flamingo_renderer"), HELIUM_FLAMINGO_ICON, StatusBarRenderer.Position.RIGHT, StatusBarRenderer.Direction.R2L);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            Entity swimDataEnt = client.getCameraEntity();
            if (swimDataEnt instanceof LivingEntity) {
                LivingEntity player = (LivingEntity)swimDataEnt;
                SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
                if (swimData != null) {

                    int swimTime = swimData.getSwimTime();
                    RenderSystem.enableBlend();
                    int left = xPosition;
                    int top = yPosition;
                    if (Math.abs(swimTime) != 0) {

                        int maxProgressTime;
                        if (swimTime > 0) {
                            maxProgressTime = Math.max(1, ModGameRules.HELIUM_FLAMINGO_FLIGHT_DURATION.get());
                        } else {
                            maxProgressTime = Math.max(20, ModGameRules.HELIUM_FLAMINGO_RECHARGE_DURATION.get());
                        }

                        float progress = 1.0F - (float) Math.abs(swimTime) / (float) maxProgressTime;
                        int full = MathHelper.ceil(((double) progress - (double) 2.0F / (double) maxProgressTime) * (double) 10.0F);
                        int partial = MathHelper.ceil(progress * 10.0F) - full;

                        for (int i = 0; i < full + partial; ++i) {
                            context.drawTexture(HELIUM_FLAMINGO_ICON, left - i * 8, top, -90, (float) (i < full ? 0 : 9), 0.0F, 9, 9, 32, 16);
                        }

                        RenderSystem.disableBlend();
                    }
                }
            }
        }
    }

    public static class Logic extends StatusBarLogic {

        public Logic() {
            super(Identifier.of("artifacts", "flamingo_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            if (!FabricLoader.getInstance().isModLoaded("artifacts")) {
                return false;
            }
            if (ModGameRules.HELIUM_FLAMINGO_FLIGHT_DURATION.get() > 0) {
                Entity swimDataent = MinecraftClient.getInstance().getCameraEntity();
                if (swimDataent instanceof LivingEntity player) {
                    SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
                    if (swimData == null) {
                        return false;
                    }
                    int swimTime = swimData.getSwimTime();
                    if (Math.abs(swimTime) == 0) {
                        return false;
                    }
                    return true;
                }
            }

            return false;
        }
    }
}
