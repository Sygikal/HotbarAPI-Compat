package dev.sygii.hotbarapicompat.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstOverlayRenderer;

import java.util.Random;

@Mixin(ThirstOverlayRenderer.class)
public abstract class ThirstOverlayRendererMixin {

	@Inject(method = "onBeginRenderAir", at = @At(value = "HEAD"), remap = false, cancellable = true)
	private static void init(CallbackInfo info) {
		info.cancel();
	}
}