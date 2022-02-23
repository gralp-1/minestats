package org.spongepowered.example;

import com.google.inject.Inject;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Plugin("minestats")
public class MineStats{

    private final PluginContainer container;
    private final Logger logger;

    @Inject
    MineStats(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        // Perform any one-time setup
        this.logger.info("Constructing MineStats");
        // Load configuration
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command> event) {
        // Register a simple command
        // When possible, all commands should be registered within a command register event
        event.register(this.container, Command.builder()
            .permission("minestats.minestats")
            .executor(ctx -> {
                ctx.sendMessage(Identity.nil(), LinearComponents.linear(

                ));

                return CommandResult.success();
            })
            .build(), "minestats", "ms");
    }

    public String encodedURL(Server server) {
		Collection<ServerPlayer> players = server.onlinePlayers();
		String playerJsonString = playerJson(players).toString();
		String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
//		return "http://localhost:8080/?data=" + encodedPlayerJsonString;
		return playerJsonString;
	}

    public static JsonObject playerJson(Collection<ServerPlayer> players) {
		JsonObject finalJson = new JsonObject();
		JsonArray allPlayers = new JsonArray();
		// Doing it this way because I'm too lazy to change the whole thing
		for (ServerPlayer player : players) {

			JsonArray armourItems = new JsonArray();
			// Iterate through the armour slots - the long and unreadable way
			armourItems.add(player.inventory().armor().slot(EquipmentTypes.HEAD).toString());
			armourItems.add(player.inventory().armor().slot(EquipmentTypes.CHEST).toString());
			armourItems.add(player.inventory().armor().slot(EquipmentTypes.LEGS).toString());
			armourItems.add(player.inventory().armor().slot(EquipmentTypes.FEET).toString());

			JsonObject playerJson = new JsonObject();
			JsonArray xyz = new JsonArray();
			xyz.add(Math.floor(player.location().x()));
			xyz.add(Math.floor(player.location().y()));
			xyz.add(Math.floor(player.location().z()));

			// Add all the applicable stats to the player's json object.
			
			playerJson.addProperty("name",       player.user().name());
			playerJson.addProperty("uuid",       player.user().uniqueId().toString());
			// Get the dimension of the player
			playerJson.addProperty("dimension",  player.world().toString());
			playerJson.addProperty("health",     player.health().toString());
			playerJson.addProperty("food",       player.foodLevel().toString());
			playerJson.addProperty("xp",         player.experience().toString());
			playerJson.addProperty("saturation", player.saturation().toString());
			playerJson.add("coordinates",        xyz);
			playerJson.add("armour",             armourItems);

			allPlayers.add(playerJson);
			
		}
		finalJson.add("info", allPlayers);
		return finalJson;
	}
}
