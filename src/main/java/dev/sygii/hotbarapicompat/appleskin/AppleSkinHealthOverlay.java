package dev.sygii.hotbarapicompat.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sygii.hotbarapi.elements.StatusBarLogic;
import dev.sygii.hotbarapi.elements.StatusBarRenderer;
import dev.sygii.hotbarapicompat.mixin.appleskin.HUDOverlayHandlerInvoker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.TextureHelper;
import squeek.appleskin.util.IntPoint;

import java.util.Random;
import java.util.Vector;

public class AppleSkinHealthOverlay {

    public static class Renderer extends StatusBarRenderer {
        public static final Identifier MOD_ICONS = new Identifier("appleskin", "textures/icons.png");
        private final Random random = new Random();
        public final Vector<IntPoint> healthBarOffsets = new Vector();

        public Renderer() {
            super(Identifier.of("appleskin", "health_overlay_renderer"), MOD_ICONS, Position.LEFT, Direction.L2R);
        }

        @Override
        public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int xPosition, int yPosition, StatusBarLogic logic) {
            HUDOverlayHandlerInvoker invoker = ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE);
            //System.out.println("sex");
            float maxHealth = playerEntity.getMaxHealth();
            float absorptionHealth = (float)Math.ceil((double)playerEntity.getAbsorptionAmount());
            int healthBars = (int)Math.ceil((double)((maxHealth + absorptionHealth) / 2.0F));
            if (healthBars < 0 || healthBars > 1000) {
                healthBars = 0;
            }

            int healthRows = (int)Math.ceil((double)((float)healthBars / 10.0F));
            int healthRowHeight = Math.max(10 - (healthRows - 2), 3);
            boolean shouldAnimatedHealth = false;
            if (ModConfig.INSTANCE.showVanillaAnimationsOverlay) {
                shouldAnimatedHealth = Math.ceil((double)playerEntity.getHealth()) <= (double)4.0F;
            }

            this.random.setSeed((long)(client.inGameHud.getTicks() * 312871));

            if (this.healthBarOffsets.size() != healthBars) {
                this.healthBarOffsets.setSize(healthBars);
            }

            for(int i = healthBars - 1; i >= 0; --i) {
                int row = (int)Math.ceil((double)((float)(i + 1) / 10.0F)) - 1;
                int x = xPosition + i % 10 * 8;
                int y = yPosition - row * healthRowHeight;
                if (shouldAnimatedHealth) {
                    y += this.random.nextInt(2);
                }

                IntPoint point = (IntPoint)this.healthBarOffsets.get(i);
                if (point == null) {
                    point = new IntPoint();
                    this.healthBarOffsets.set(i, point);
                }

                point.x = x - xPosition;
                point.y = y - yPosition;
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
                /*FoodValuesEvent foodValuesEvent = new FoodValuesEvent(playerEntity, heldItem, FoodHelper.getDefaultFoodValues(heldItem), modifiedFoodValues);
                ((EventHandler)FoodValuesEvent.EVENT.invoker()).interact(foodValuesEvent);
                modifiedFoodValues = foodValuesEvent.modifiedFoodValues;*/
                if (shouldShowEstimatedHealth(heldItem, modifiedFoodValues)) {
                    float foodHealthIncrement = FoodHelper.getEstimatedHealthIncrement(heldItem, modifiedFoodValues, playerEntity);
                    float currentHealth = playerEntity.getHealth();
                    float modifiedHealth = Math.min(currentHealth + foodHealthIncrement, playerEntity.getMaxHealth());
                    //HUDOverlayEvent.HealthRestored healthRenderEvent = null;
                    if (currentHealth < modifiedHealth) {
                        //healthRenderEvent = new HUDOverlayEvent.HealthRestored(modifiedHealth, heldItem, modifiedFoodValues, xPosition, yPosition, context);
                        this.drawHealthOverlay(context, playerEntity.getHealth(), modifiedHealth, client, xPosition, yPosition, invoker.getFlashAlpha());
                    }

                    /*if (healthRenderEvent != null) {
                        ((EventHandler) HUDOverlayEvent.HealthRestored.EVENT.invoker()).interact(healthRenderEvent);
                    }

                    if (healthRenderEvent != null && !healthRenderEvent.isCanceled) {
                        this.drawHealthOverlay(context, playerEntity.getHealth(), modifiedHealth, client, xPosition, yPosition, invoker.getFlashAlpha());
                    }*/
                }
            }
        }

        public void drawHealthOverlay(DrawContext context, float health, float modifiedHealth, MinecraftClient mc, int right, int top, float alpha) {
            if (!(modifiedHealth <= health)) {
                ((HUDOverlayHandlerInvoker)HUDOverlayHandler.INSTANCE).invokeEnableAlpha(alpha);
                mc.getTextureManager().bindTexture(TextureHelper.MC_ICONS);
                int fixedModifiedHealth = (int)Math.ceil((double)modifiedHealth);
                boolean isHardcore = mc.player.getWorld() != null && mc.player.getWorld().getLevelProperties().isHardcore();
                int startHealthBars = (int)Math.max((double)0.0F, Math.ceil((double)health) / (double)2.0F);
                int endHealthBars = (int)Math.max((double)0.0F, Math.ceil((double)(modifiedHealth / 2.0F)));
                int iconStartOffset = 16;
                int iconSize = 9;

                for(int i = startHealthBars; i < endHealthBars; ++i) {
                    IntPoint offset = (IntPoint)this.healthBarOffsets.get(i);
                    if (offset != null) {
                        int x = right + offset.x;
                        int y = top + offset.y;
                        int v = 0 * iconSize;
                        int u = iconStartOffset + 4 * iconSize;
                        int ub = iconStartOffset + 1 * iconSize;
                        if (i * 2 + 1 == fixedModifiedHealth) {
                            u += 1 * iconSize;
                        }

                        if (isHardcore) {
                            v = 5 * iconSize;
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

        public boolean shouldShowEstimatedHealth(ItemStack hoveredStack, FoodValues modifiedFoodValues) {
            if (!ModConfig.INSTANCE.showFoodHealthHudOverlay) {
                return false;
            } else if (this.healthBarOffsets.size() == 0) {
                return false;
            } else {
                MinecraftClient mc = MinecraftClient.getInstance();
                PlayerEntity player = mc.player;
                HungerManager stats = player.getHungerManager();
                if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                    return false;
                } else if (stats.getFoodLevel() >= 18) {
                    return false;
                } else if (player.hasStatusEffect(StatusEffects.POISON)) {
                    return false;
                } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
                    return false;
                } else {
                    return !player.hasStatusEffect(StatusEffects.REGENERATION);
                }
            }
        }
    }

    public static class Logic extends StatusBarLogic {

        public Logic() {
            super(Identifier.of("appleskin", "health_overlay_logic"), (ent) -> 0, (ent) -> 0);
        }

        @Override
        public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
            if (!FabricLoader.getInstance().isModLoaded("appleskin")) {
                return false;
            }
            return ModConfig.INSTANCE.showFoodHealthHudOverlay;
        }
    }
}
