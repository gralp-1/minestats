package srmworks.minestats;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.Base64;
import java.util.Collection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineStats implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("minestats");
	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
			dispatcher.register(CommandManager.literal("minestats")
					.executes((context) -> {
						context.getSource().sendFeedback(commandExecution(context.getSource().getServer()), false);
						return Command.SINGLE_SUCCESS;
					}));
		});
	}

	public Text commandExecution(MinecraftServer server) {
	
		Collection<ServerPlayerEntity> players = PlayerLookup.all(server);

		JsonObject playerJson = new JsonObject();
		JsonArray playerJsonArray = new JsonArray();

		// Triple nested recursion lesgo
		for (ServerPlayerEntity player : players) {

			JsonArray armourItems = new JsonArray();
			// Iterate through the armour slots
			player.getArmorItems().forEach((itemStack) -> {
				armourItems.add(itemStack.getItem().getName().asString());
			});

			JsonObject playerSubJson = new JsonObject();
			JsonArray xyz = new JsonArray();
			xyz.add(Math.floor(player.getX()));
			xyz.add(Math.floor(player.getY()));
			xyz.add(Math.floor(player.getZ()));

			// Add all the applicable stats to the player's json object.
			
			// TODO: Make this configurable.
			// TODO: Fix armour array.
			// TODO: Add more stats.
			// TODO: Fix this mess.
			// // TODO: Round coordinates.

			playerSubJson.addProperty("name", player.getName().asString());
			playerSubJson.addProperty("uuid", player.getUuidAsString());
			playerSubJson.addProperty("world", player.world.getRegistryKey().getValue().toString());
			playerSubJson.addProperty("health", player.getHealth());
			playerSubJson.addProperty("food", player.getHungerManager().getFoodLevel());
			playerSubJson.addProperty("level", player.experienceLevel);
			playerSubJson.add("coordinates", xyz);
			playerSubJson.add("armour", armourItems);

			playerJsonArray.add(playerSubJson);
		}
		playerJson.add("info", playerJsonArray);

		String playerJsonString = playerJson.toString();
		// String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
		return Text.of(playerJsonString);

	}

}
