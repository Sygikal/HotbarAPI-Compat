package dev.sygii.hotbarapicompat.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapicompat.mixin.HUDOverlayHandlerInvoker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.api.handler.EventHandler;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.TextureHelper;
import squeek.appleskin.util.IntPoint;

import java.util.Random;
import java.util.Vector;

public class AppleSkinHungerOverlay {
    public static class Renderer extends StatusBarRenderer {
        public static final Identifier MOD_ICONS = new Identifier("appleskin", "textures/icons.png");
        private final Random random = new Random();
        public final Vector<IntPoint> foodBarOffsets = new Vector();

        public Renderer() {
            super(Identifier.of("appleskin", "hunger_overlay_renderer"), MOD_ICONS, Position.LEFT, Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            int preferFoodBars = 10;

            HUDOverlayHandlerInvoker invoker = ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE);

            boolean shouldAnimatedFood = false;
            if (ModConfig.INSTANCE.showVanillaAnimationsOverlay) {
                HungerManager hungerManager = playerEntity.getHungerManager();
                float saturationLevel = hungerManager.getSaturationLevel();
                int foodLevel = hungerManager.getFoodLevel();
                shouldAnimatedFood = saturationLevel <= 0.0F && client.inGameHud.getTicks() % (foodLevel * 3 + 1) == 0;
            }

            this.random.setSeed((long)(client.inGameHud.getTicks() * 312871));

            if (this.foodBarOffsets.size() != 10) {
                this.foodBarOffsets.setSize(10);
            }

            for(int i = 0; i < 10; ++i) {
                int x = xPosition - i * 8 - 9;
                int y = yPosition;
                if (shouldAnimatedFood) {
                    y = yPosition + (this.random.nextInt(3) - 1);
                }

                IntPoint point = (IntPoint)this.foodBarOffsets.get(i);
                if (point == null) {
                    point = new IntPoint();
                    this.foodBarOffsets.set(i, point);
                }

                point.x = x - xPosition;
                point.y = y - yPosition;
            }

            if (ModConfig.INSTANCE.showSaturationHudOverlay) {
                this.drawSaturationOverlay(context, 0.0f, playerEntity.getHungerManager().getSaturationLevel(), client, xPosition + 9, yPosition, 1.0F);
            }

            ItemStack heldItem = playerEntity.getMainHandStack();
            if (ModConfig.INSTANCE.showFoodValuesHudOverlayWhenOffhand && !FoodHelper.canConsume(heldItem, playerEntity)) {
                heldItem = playerEntity.getOffHandStack();
            }

            boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && FoodHelper.canConsume(heldItem, playerEntity);
            if (!shouldRenderHeldItemValues) {
                invoker.invokeResetFlash();
            } else {
                FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(heldItem, playerEntity);

                if (ModConfig.INSTANCE.showFoodValuesHudOverlay) {
                    /*HUDOverlayEvent.HungerRestored hungerRenderEvent = new HUDOverlayEvent.HungerRestored(stats.getFoodLevel(), heldItem, modifiedFoodValues, right, top, context);
                    ((EventHandler) HUDOverlayEvent.HungerRestored.EVENT.invoker()).interact(hungerRenderEvent);
                    if (hungerRenderEvent.isCanceled) {
                        return;
                    }*/

                    int foodHunger = modifiedFoodValues.hunger;
                    float foodSaturationIncrement = modifiedFoodValues.getSaturationIncrement();
                    this.drawHungerOverlay(context, foodHunger, playerEntity.getHungerManager().getFoodLevel(), client, xPosition + 9, yPosition, invoker.getFlashAlpha(), FoodHelper.isRotten(heldItem));

                    int newFoodValue = playerEntity.getHungerManager().getFoodLevel() + foodHunger;
                    float newSaturationValue = playerEntity.getHungerManager().getSaturationLevel() + foodSaturationIncrement;
                    if (ModConfig.INSTANCE.showSaturationHudOverlay) {
                        float saturationGained = newSaturationValue > (float) newFoodValue ? (float) newFoodValue - playerEntity.getHungerManager().getSaturationLevel() : foodSaturationIncrement;
                        this.drawSaturationOverlay(context, saturationGained, playerEntity.getHungerManager().getSaturationLevel(), client, xPosition + 9, yPosition, invoker.getFlashAlpha());
                    }
                }

            }
        }

        public void drawHungerOverlay(DrawContext context, int hungerRestored, int foodLevel, MinecraftClient mc, int right, int top, float alpha, boolean useRottenTextures) {
            if (hungerRestored > 0) {
                ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeEnableAlpha(alpha);
                int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));
                int startFoodBars = Math.max(0, foodLevel / 2);
                int endFoodBars = (int)Math.ceil((double)((float)modifiedFood / 2.0F));
                int iconStartOffset = 16;
                int iconSize = 9;

                for(int i = startFoodBars; i < endFoodBars; ++i) {
                    IntPoint offset = (IntPoint)this.foodBarOffsets.get(i);
                    if (offset != null) {
                        int x = right + offset.x;
                        int y = top + offset.y;
                        int v = 3 * iconSize;
                        int u = iconStartOffset + 4 * iconSize;
                        int ub = iconStartOffset + 1 * iconSize;
                        if (useRottenTextures) {
                            u += 4 * iconSize;
                            ub += 12 * iconSize;
                        }

                        if (i * 2 + 1 == modifiedFood) {
                            u += 1 * iconSize;
                        }

                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.25F);
                        context.drawTexture(TextureHelper.MC_ICONS, x, y, ub, v, iconSize, iconSize);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                        context.drawTexture(TextureHelper.MC_ICONS, x, y, u, v, iconSize, iconSize);
                    }
                }

                ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeDisableAlpha(alpha);
            }
        }

        public void drawSaturationOverlay(DrawContext context, float saturationGained, float saturationLevel, MinecraftClient mc, int right, int top, float alpha) {
            if (!(saturationLevel + saturationGained < 0.0F)) {
                ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeEnableAlpha(alpha);
                float modifiedSaturation = Math.max(0.0F, Math.min(saturationLevel + saturationGained, 20.0F));
                int startSaturationBar = 0;
                int endSaturationBar = (int)Math.ceil((double)(modifiedSaturation / 2.0F));
                if (saturationGained != 0.0F) {
                    startSaturationBar = (int)Math.max(saturationLevel / 2.0F, 0.0F);
                }

                int iconSize = 9;

                for(int i = startSaturationBar; i < endSaturationBar; ++i) {
                    IntPoint offset = (IntPoint)this.foodBarOffsets.get(i);
                    if (offset != null) {
                        int x = right + offset.x;
                        int y = top + offset.y;
                        int v = 0;
                        int u = 0;
                        float effectiveSaturationOfBar = modifiedSaturation / 2.0F - (float)i;
                        if (effectiveSaturationOfBar >= 1.0F) {
                            u = 3 * iconSize;
                        } else if ((double)effectiveSaturationOfBar > (double)0.5F) {
                            u = 2 * iconSize;
                        } else if ((double)effectiveSaturationOfBar > (double)0.25F) {
                            u = 1 * iconSize;
                        }

                        context.drawTexture(TextureHelper.MOD_ICONS, x, y, u, v, iconSize, iconSize);
                    }
                }

                RenderSystem.setShaderTexture(0, TextureHelper.MC_ICONS);
                ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeDisableAlpha(alpha);
            }
        }
    }

    public static class Logic extends StatusBarLogic {

        public Logic() {
            super(Identifier.of("appleskin", "hunger_overlay_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            if (!FabricLoader.getInstance().isModLoaded("appleskin")) {
                return false;
            }
            return ModConfig.INSTANCE.showSaturationHudOverlay || ModConfig.INSTANCE.showFoodValuesHudOverlay;
        }
    }
}
