package me.thetechnicboy.requestspawn;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static me.thetechnicboy.requestspawn.RequestSpawn.LOGGER;
import static me.thetechnicboy.requestspawn.RequestSpawn.server;

public class SpawnHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("POST")) {
            if(exchange.getRequestHeaders().getFirst("Content-Type").equals("application/json")) {
                handleRequest(exchange);
            } else {
                exchange.sendResponseHeaders(415, -1);
            }
        }
        else{
            exchange.sendResponseHeaders(405, -1);
        }
    }

    public static void handleRequest(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        reader.close();

        KeyValuePair<Boolean, String> result = handleJSON(body.toString());
        if (result.getKey()) {
            String response = "Entity spawn request received: " + result.getValue();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            String response = "Error: " + result.getValue();
            exchange.sendResponseHeaders(400, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        }
        exchange.getResponseBody().close();

    }

    public static KeyValuePair<Boolean, String> handleJSON(String body){

        if(!RequestSpawn.HTTPServerOnline){
            return new KeyValuePair<>(false, "World is not loaded!");
        }

        JsonObject obj;
        //TRY TO PARSE THE STRING
        try{
            LOGGER.error(body);
            obj = JsonParser.parseString(body).getAsJsonObject();
        }
        catch(Exception e){
            return new KeyValuePair<>(false, e.getMessage());
        }

        String mob = "";
        String player = "";
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;

        //TRY TO READ VALUES
        try { mob = obj.get("mob").getAsString(); }
        catch(Exception e){ }
        try { player = obj.get("player").getAsString(); }
        catch(Exception e){ }
        try { x = obj.get("x").getAsInt(); }
        catch(Exception e){ }
        try { y = obj.get("y").getAsInt(); }
        catch(Exception e){ }
        try { z = obj.get("z").getAsInt(); }
        catch(Exception e){ }


        //TEST IF MANDORY VALUES ARE THERE
        if(mob.equals("") || (player.equals("") && (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE || z == Integer.MIN_VALUE))){
            return new KeyValuePair<>(false, "JSON misformed");
        }

        //TEST IF MOB EXISTS
        ResourceLocation _mob = new ResourceLocation(mob);
        if(!ForgeRegistries.ENTITIES.containsKey(_mob)){
            return new KeyValuePair<>(false, "Entity type not found: " + mob);
        }

        //MAKE COMMAND
        String command;
        if(player != ""){
            command = "execute at " + player + " run summon " + mob + " ~ ~ ~";
        }
        else {
            command = "summon " + mob + " " + x + " " + y + " " + z;
        }

        //NOW EXECUTE THE COMMAND
        if(server != null){
            server.execute(() -> {
                server.getCommands().performCommand(server.createCommandSourceStack(), command);
            });
            return new KeyValuePair<>(true, command);
        } else {
            return new KeyValuePair<>(false, "Server instance not found");
        }

    }
}
