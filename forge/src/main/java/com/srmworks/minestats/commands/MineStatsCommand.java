package com.srmworks.minestats.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.srmworks.minestats.StyleText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;


import java.util.Base64;
import java.util.List;

import static com.srmworks.minestats.config.Config.*;

// imagine making /* readable */ working code, IMAGINE

public class MineStatsCommand {
//        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
//        dispatcher.register(
//                literal("minestats")
//                        .executes(
//                                (c) -> {
//                                    c
//                                            .getSource()
//                                            .getPlayerOrException()
//                                            .sendMessage(
//                                                    StyleText.linkStyle("Click here to view stats!",
//                                                            encodedURL(c.getSource().getServer())),
//                                                            Objects.requireNonNull(c.getSource().getEntity()).getUUID());
//                                    return 1;
//                                }
//                        ));
    public MineStatsCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("minestats").executes(
                (command) -> encodedURL(command.getSource())
        ));
    }

    private int encodedURL(CommandSourceStack source) throws CommandSyntaxException {
        List<ServerPlayer> players = source.getServer().getPlayerList().getPlayers();
        ServerPlayer player = source.getPlayerOrException();

        String playerJsonString = playerJson(players).toString();
        String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
        String url = "https://minestats.srmworks.com/player/" + encodedPlayerJsonString;

        player.sendMessage(
                new TextComponent("Click here to view stats!")
                        .setStyle(
                                Style
                                        .EMPTY
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                                .withColor(ChatFormatting.AQUA)),
                player.getUUID());
        return 1;
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
            if (Dimension.get())
                playerJson.addProperty("world",      player.getLevel().dimension().toString());
            if (Health.get())
                playerJson.addProperty("health",     player.getHealth());
            if (Hunger.get())
                playerJson.addProperty("food",       player.getFoodData().getFoodLevel());
            if (Experience.get())
                playerJson.addProperty("xp",         player.totalExperience);
            if (Saturation.get())
                playerJson.addProperty("saturation", player.getFoodData().getSaturationLevel());
            if (Coordinates.get())
                playerJson.add("coordinates",        xyz);
            if (Armour.get())
                playerJson.add("armour",             armourItems);

            allPlayers.add(playerJson);

        }
        finalJson.add("info", allPlayers);
        return finalJson;
    }
}