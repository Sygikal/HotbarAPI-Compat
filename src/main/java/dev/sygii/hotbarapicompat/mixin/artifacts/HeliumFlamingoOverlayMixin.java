package dev.sygii.hotbarapicompat.mixin.artifacts;

import artifacts.client.HeliumFlamingoOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeliumFlamingoOverlay.class)
public class HeliumFlamingoOverlayMixin {

    @ModifyVariable(method = "renderOverlay", at = @At(value = "STORE"), ordinal = 0)
    private static boolean cancelFlamingo(boolean value) {
        return false;
    }

}
