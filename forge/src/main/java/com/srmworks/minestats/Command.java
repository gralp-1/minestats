package com.srmworks.minestats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.*;
import net.minecraftforge.registries.IForgeRegistryEntry;


import java.util.Base64;
import java.util.List;
import static net.minecraft.commands.Commands.literal;


// imagine making ~~readable~~ working code, IMAGINE

public class Command {
    public static void register() {
//        Make a command
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

        dispatcher.register(
                literal("minestats")
                        .executes(
                                (c) -> {
                                    c
                                            .getSource()
                                            .getPlayerOrException()
                                            .sendMessage(
                                                    StyleText.linkStyle("Click here to view stats!",
                                                            encodedURL(c.getSource().getServer())),
                                                            c.getSource().getEntity().getUUID());
                                    return 1;
                                }
                        ));
    }
    public static String encodedURL(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        String playerJsonString = playerJson(players).toString();
        String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
        return encodedPlayerJsonString;
    }


    public static JsonObject playerJson(List<ServerPlayer> players) {
        JsonObject finalJson = new JsonObject();
        JsonArray allPlayers = new JsonArray();
        // Doing it this way because I'm too lazy to change the whole thing
        for (ServerPlayer player : players) {

            JsonArray armourItems = new JsonArray();
            // Iterate through the armour slots - the long and unreadable way
            player.getInventory().armor.stream().forEach((item) -> armourItems.add(item.getDisplayName().getString()));

            JsonObject playerJson = new JsonObject();
            JsonArray xyz = new JsonArray();
            xyz.add(Math.floor(player.getX()));
            xyz.add(Math.floor(player.getY()));
            xyz.add(Math.floor(player.getZ()));

            // Add all the applicable stats to the player's json object.

            playerJson.addProperty("name",           player.getName().getString());
            playerJson.addProperty("uuid",           player.getStringUUID());
//            if (Dimension)
//            Get the player's dimension ID
                playerJson.addProperty("world",      player.getLevel().dimension().toString());
//            if (Health)
                playerJson.addProperty("health",     player.getHealth());
//            if (Hunger)
                playerJson.addProperty("food",       player.getFoodData().getFoodLevel());
//            if (Experience)
                playerJson.addProperty("xp",         player.totalExperience);
//            if (Saturation)
                playerJson.addProperty("saturation", player.getFoodData().getSaturationLevel());
//            if (Coordinates)
                playerJson.add("coordinates",        xyz);
//            if (Armour)
                playerJson.add("armour",             armourItems);

            allPlayers.add(playerJson);

        }
        finalJson.add("info", allPlayers);
        return finalJson;
    }
}