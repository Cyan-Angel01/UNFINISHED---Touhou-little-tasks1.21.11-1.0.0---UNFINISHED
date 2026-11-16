package com.commoi.maidaddon;

import com.commoi.maidaddon.config.MaidAddonConfig;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class MaidAddon implements ModInitializer {
   public static final String MOD_ID = "maidaddon";

   public void onInitialize() {
     NeoForgeConfigRegistry.INSTANCE.register("maidaddon", ModConfig.Type.COMMON, MaidAddonConfig.COMMON_SPEC, "maidaddon.toml");
   }
}
