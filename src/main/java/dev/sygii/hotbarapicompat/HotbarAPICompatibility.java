package dev.sygii.hotbarapicompat;

import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHealthOverlay;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHungerOverlay;
import dev.sygii.hotbarapicompat.appleskin.AppleSkinHungerUnderlay;
import dev.sygii.hotbarapicompat.tan.ToughAsNailsThirstBar;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarAPICompatibility implements ModInitializer {
	public static final String MOD_ID = "hotbarapi-compatibility";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		HotbarAPI.registerStatusBarLogic(new ToughAsNailsThirstBar.Logic());
		HotbarAPI.registerStatusBarRenderer(new ToughAsNailsThirstBar.Renderer());

		HotbarAPI.registerStatusBarLogic(new AppleSkinHungerUnderlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHungerUnderlay.Renderer());
		HotbarAPI.registerStatusBarLogic(new AppleSkinHungerOverlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHungerOverlay.Renderer());
		HotbarAPI.registerStatusBarLogic(new AppleSkinHealthOverlay.Logic());
		HotbarAPI.registerStatusBarRenderer(new AppleSkinHealthOverlay.Renderer());
		LOGGER.info("Hello Fabric world!");
	}
}