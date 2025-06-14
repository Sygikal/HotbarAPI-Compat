package dev.sygii.hotbarapicompat.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class HotbarAPICompatMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!FabricLoader.getInstance().isModLoaded("toughasnails")
                && (mixinClassName.equals("ThirstOverlayRendererAccessor") || mixinClassName.equals("ThirstOverlayRendererMixin")))
            return false;

        if (!FabricLoader.getInstance().isModLoaded("appleskin")
                && (mixinClassName.equals("HUDOverlayHandlerInvoker")))
            return false;

        if (!FabricLoader.getInstance().isModLoaded("artifacts")
                && (mixinClassName.equals("HeliumFlamingoOverlayMixin")))
            return false;

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}