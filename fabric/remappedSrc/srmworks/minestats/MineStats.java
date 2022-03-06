package srmworks.minestats;

import com.mojang.serialization.Decoder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import srmworks.minestats.SimpleConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Base64;
import java.util.Collection;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// TODO(#3): Make this configurable.
// TODO: Make it configurable on a per-player basis.
// TODO: Keep code clean ❌
// Fix armour array. ✅
// Round coordinates. ✅


public class MineStats implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("minestats");
	public SimpleConfig config = SimpleConfig.of("minestats").provider(this::provider).request();
	public final Boolean Hunger      = config.getOrDefault("Hunger",      true);
	public final Boolean Armour      = config.getOrDefault("Armour",      true);
	public final Boolean Health      = config.getOrDefault("Health",      true);
	public final Boolean Dimension   = config.getOrDefault("Dimension",   true);
	public final Boolean Saturation  = config.getOrDefault("Saturation",  true);
	public final Boolean Experience  = config.getOrDefault("Experience",  true);
	public final Boolean Coordinates = config.getOrDefault("Coordinates", false);

	private String provider(String filename) {
		return """
			#Minstats Config

			Health=true
			Hunger=true
			Saturation=true
			Experience=true
			Dimension=true
			Armour=true
			Coordinates=false
			""";
	}
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


	public JsonObject playerJson(Collection<ServerPlayerEntity> players) {
		JsonObject finalJson = new JsonObject();
		JsonArray allPlayers = new JsonArray();
		// Doing it this way because I'm too lazy to change the whole thing
		int i = 0;
		for (ServerPlayerEntity player : players) {

			JsonArray armourItems = new JsonArray();
			// Iterate through the armour slots - the long and unreadable way
			player.getInventory().armor.stream().forEach((item) -> armourItems.add(item.getTranslationKey()));

			JsonObject playerJson = new JsonObject();
			JsonArray xyz = new JsonArray();
			xyz.add(Math.floor(player.getX()));
			xyz.add(Math.floor(player.getY()));
			xyz.add(Math.floor(player.getZ()));

			// Add all the applicable stats to the player's json object.

			playerJson.addProperty("name",           player.getName().asString());
			playerJson.addProperty("uuid",           player.getUuidAsString());
			if (Dimension)
				playerJson.addProperty("world",      player.world.getRegistryKey().getValue().toString());
			if (Health)
				playerJson.addProperty("health",     player.getHealth());
			if (Hunger)
				playerJson.addProperty("food",       player.getHungerManager().getFoodLevel());
			if (Experience)
				playerJson.addProperty("xp",         player.totalExperience);
			if (Saturation)
				playerJson.addProperty("saturation", player.getHungerManager().getSaturationLevel());
			if (Coordinates)
				playerJson.add("coordinates",        xyz);
			if (Armour)
				playerJson.add("armour",             armourItems);

			allPlayers.add(playerJson);
			
			i++;
		}
		finalJson.add("info", allPlayers);
		return finalJson;
	}
}
