package com.reachfly;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReachFlyClient implements ClientModInitializer {

    public static final String MOD_ID = "reachfly";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[f1sch] Initializing f1sch client for MC 26.1...");

        ModConfig.load();
        KeybindHandler.register();
        TeleportHandler.registerPayload();
        ServerSyncHandler.registerPayloads();
        EventHandler.register();

        LOGGER.info("[f1sch] Initialization complete.");
    }
}
