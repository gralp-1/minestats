package com.srmworks.minestats.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class Config {
    public static final Builder BUILDER = new Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Boolean> Health;
    public static ForgeConfigSpec.ConfigValue<Boolean> Hunger;
    public static ForgeConfigSpec.ConfigValue<Boolean> Saturation;
    public static ForgeConfigSpec.ConfigValue<Boolean> Experience;
    public static ForgeConfigSpec.ConfigValue<Boolean> Dimension;
    public static ForgeConfigSpec.ConfigValue<Boolean> Armour;
    public static ForgeConfigSpec.ConfigValue<Boolean> Coordinates;

    static {
        BUILDER.push("Config for MineStats");

        Health      = BUILDER.define("showHealth", true);
        Hunger      = BUILDER.define("showHunger", true);
        Saturation  = BUILDER.define("showSaturation", true);
        Experience  = BUILDER.define("showExperience", true);
        Dimension   = BUILDER.define("showDimension", true);
        Armour      = BUILDER.define("showArmour", true);
        Coordinates = BUILDER.define("showCoordinates", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
