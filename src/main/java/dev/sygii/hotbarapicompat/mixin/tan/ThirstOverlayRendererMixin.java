package dev.sygii.hotbarapicompat.mixin.tan;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstOverlayRenderer;

@Mixin(ThirstOverlayRenderer.class)
public abstract class ThirstOverlayRendererMixin {

	@Inject(method = "onBeginRenderAir", at = @At(value = "HEAD"), remap = false, cancellable = true)
	private static void init(CallbackInfo info) {
		info.cancel();
	}
}