package me.thetechnicboy.requestspawn;

import com.mojang.logging.LogUtils;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

@Mod("requestspawn")
public class RequestSpawn {

    public static MinecraftServer server;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean HTTPServerOnline = false;
    private static HttpServer httpServer;

    public RequestSpawn() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.CONFIG);
    }

    private void setup(final FMLCommonSetupEvent event) {
        int _port = ModConfig.PORT.get();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(_port), 0);
            httpServer.createContext("/spawn", new SpawnHandler());
            httpServer.setExecutor(null);
            httpServer.start();
            LOGGER.info("Server started on port " + _port);
        } catch (IOException e) {
            LOGGER.error(e.getMessage() + " " + e.getCause());
        }


    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        HTTPServerOnline = true;
        server = ServerLifecycleHooks.getCurrentServer();
    }

    @SubscribeEvent
    public void onServerClose(ServerStoppingEvent event) {
        HTTPServerOnline = false;
    }

}
