package com.commoi.maidaddon;

import com.commoi.maidaddon.config.MaidAddonConfig;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig.Type;

public class MaidAddon implements ModInitializer {
   public static final String MOD_ID = "maidaddon";

   public void onInitialize() {
      ForgeConfigRegistry.INSTANCE.register("maidaddon", Type.COMMON, MaidAddonConfig.COMMON_SPEC, "maidaddon.toml");
   }
}
