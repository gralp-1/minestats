package srmworks.minestats;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.UResource.Value;
import com.mojang.brigadier.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// TODO: Make this configurable.
// TODO: Make it configurable on a per-player basis.
// Fix armour array. ✅
// Round coordinates. ✅

public class MineStats implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("minestats");
	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
			dispatcher.register(CommandManager.literal("minestats")
					.executes((context) -> {
						context.getSource().sendFeedback(encodedURL(context.getSource().getServer()), false);
						return Command.SINGLE_SUCCESS;
					}));
		});
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
		String encodedPlayerJsonString = Base64.getEncoder().encodeToString(playerJsonString.getBytes());
		String url = "http://localhost:8080/?data=" + encodedPlayerJsonString;
		ClickEvent openLink = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		Style linkText = Style.EMPTY.withColor(Formatting.AQUA).withClickEvent(openLink);
		// Return the URL with the linkText style
		return new LiteralText("Click here to view stats!")
			.styled(style -> linkText);
	}

	public static JsonObject playerJson(Collection<ServerPlayerEntity> players) {
		JsonObject finalJson = new JsonObject();
		JsonArray allPlayers = new JsonArray();
		// Doing it this way because I'm too lazy to change the whole thing
		int i = 0;
		for (ServerPlayerEntity player : players) {

			JsonArray armourItems = new JsonArray();
			// Iterate through the armour slots - the long and unreadable way
			player.getInventory().armor.stream().forEach((item) -> {armourItems.add(item.getTranslationKey());});

			JsonObject playerJson = new JsonObject();
			JsonArray xyz = new JsonArray();
			xyz.add(Math.floor(player.getX()));
			xyz.add(Math.floor(player.getY()));
			xyz.add(Math.floor(player.getZ()));

			// Add all the applicable stats to the player's json object.
			
			playerJson.addProperty("name",       player.getName().asString());
			playerJson.addProperty("uuid",       player.getUuidAsString());
			playerJson.addProperty("world",      player.world.getRegistryKey().getValue().toString());
			playerJson.addProperty("health",     player.getHealth());
			playerJson.addProperty("food",       player.getHungerManager().getFoodLevel());
			playerJson.addProperty("xp",         player.totalExperience);
			playerJson.addProperty("saturation", player.getHungerManager().getSaturationLevel());
			playerJson.add("coordinates",        xyz);
			playerJson.add("armour",             armourItems);

			allPlayers.add(playerJson);
			
			i++;
		}
		finalJson.add("info", allPlayers);
		return finalJson;
	}
}
