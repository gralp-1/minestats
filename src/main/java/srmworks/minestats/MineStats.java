package srmworks.minestats;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.Collection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import srmworks.minestats.config.MineStatsConfig;
// TODO: Make this configurable.
// TODO: Make it configurable on a per-player basis.
// Fix armour array. ✅
// Round coordinates. ✅

public class MineStats implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("minestats");
	private static MineStatsConfig settings;
	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
			dispatcher.register(CommandManager.literal("minestats")
					.executes((context) -> {
						context.getSource().sendFeedback(encodedURL(context.getSource().getServer()), false);
						return Command.SINGLE_SUCCESS;
					}));
		});
		settings = new MineStatsConfig();
		settings.load();
		LOGGER.warn(settings.toString());
	}
	public static MineStatsConfig getSettings() {
		return settings;
	}

	public Text encodedURL(MinecraftServer server) {
		// Exception cases
		// if (server.isSingleplayer()) {
		// 	return Text.of("This command is not available in singleplayer.");
		// }
		if (server.getPlayerManager().getPlayerList().isEmpty()) {
			return Text.of("No players online.");
		}
		// Get all players
		Collection<ServerPlayerEntity> players = PlayerLookup.all(server);


		String playerJsonString = playerJson(players).toString();
		// String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
		return Text.of(playerJsonString);

	}

	public static JsonObject playerJson(Collection<ServerPlayerEntity> players) {
		JsonObject playerJson = new JsonObject();
		JsonArray playerJsonArray = new JsonArray();

		for (ServerPlayerEntity player : players) {

			JsonArray armourItems = new JsonArray();
			// Iterate through the armour slots - the long and unreadable way
			player.getInventory().armor.stream().forEach((item) -> {armourItems.add(item.getTranslationKey());});

			JsonObject playerSubJson = new JsonObject();
			JsonArray xyz = new JsonArray();
			xyz.add(Math.floor(player.getX()));
			xyz.add(Math.floor(player.getY()));
			xyz.add(Math.floor(player.getZ()));

			// Add all the applicable stats to the player's json object.
			
			playerSubJson.addProperty("name", player.getName().asString());
			playerSubJson.addProperty("uuid", player.getUuidAsString());
			playerSubJson.addProperty("world", player.world.getRegistryKey().getValue().toString());
			playerSubJson.addProperty("health", player.getHealth());
			playerSubJson.addProperty("food", player.getHungerManager().getFoodLevel());
			playerSubJson.addProperty("level", player.experienceLevel);
			playerSubJson.addProperty("saturation", player.getHungerManager().getSaturationLevel());
			playerSubJson.add("coordinates", xyz);
			playerSubJson.add("armour", armourItems);

			playerJsonArray.add(playerSubJson);
			playerJson.add("info", playerJsonArray);
		}
		return playerJson;
	}
}
