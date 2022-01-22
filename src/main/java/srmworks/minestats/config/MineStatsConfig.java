package srmworks.minestats.config;


import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;


public class MineStatsConfig extends Config implements ConfigContainer {

	public MineStatsConfig() {
		super("minestats");
	}

    @Transitive
    @ConfigEntries
    public static class DataTypes implements ConfigGroup {
		private boolean showCoordinates = true;
		private boolean showArmour      = true;
		private boolean showHealth      = true;
		private boolean showLevel       = true;
		private boolean showWorld       = true;
		private boolean showFood        = true;
		private boolean showSaturation  = true;
	}

	/*
				playerSubJson.addProperty("name", player.getName().asString());
			playerSubJson.addProperty("uuid", player.getUuidAsString());
			playerSubJson.addProperty("world", player.world.getRegistryKey().getValue().toString());
			playerSubJson.addProperty("health", player.getHealth());
			playerSubJson.addProperty("food", player.getHungerManager().getFoodLevel());
			playerSubJson.addProperty("level", player.experienceLevel);
			playerSubJson.add("coordinates", xyz);
			playerSubJson.add("armour", armourItems);
 */
}
