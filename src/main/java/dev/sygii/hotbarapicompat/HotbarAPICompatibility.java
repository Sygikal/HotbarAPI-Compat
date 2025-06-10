package dev.sygii.hotbarapicompat;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHealthOverlay;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHungerOverlay;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHungerUnderlay;
import dev.sygii.hotbarapicompat.artifacts.ArtifactsHeliumFlamingoBar;
import dev.sygii.hotbarapicompat.tan.ToughAsNailsThirstBar;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarAPICompatibility implements ModInitializer {
	public static final String MOD_ID = "hotbarapi-compatibility";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//Tough as Nails
		HotbarAPI.registerStatusBarLogic(new ToughAsNailsThirstBar.Logic());
		HotbarAPI.registerStatusBarRenderer(new ToughAsNailsThirstBar.Renderer());
		//Appleskin
		HotbarAPI.registerStatusBarLogic(new AppleSkinHungerUnderlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHungerUnderlay.Renderer());
		HotbarAPI.registerStatusBarLogic(new AppleSkinHungerOverlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHungerOverlay.Renderer());
		HotbarAPI.registerStatusBarLogic(new AppleSkinHealthOverlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHealthOverlay.Renderer());
		//Artifacts
		HotbarAPI.registerStatusBarLogic(new ArtifactsHeliumFlamingoBar.Logic());
		HotbarAPI.registerStatusBarRenderer(new ArtifactsHeliumFlamingoBar.Renderer());
	}
}