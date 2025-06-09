package dev.sygii.hotbarapicompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import toughasnails.thirst.ThirstOverlayRenderer;

import java.util.Random;

@Mixin(ThirstOverlayRenderer.class)
public interface ThirstOverlayRendererAccessor {
    @Accessor("updateCounter")
    public static int getUpdateCounter() {
        throw new AssertionError();
    }

    @Accessor("RANDOM")
    public static Random getRANDOM() {
        throw new AssertionError();
    }
}
