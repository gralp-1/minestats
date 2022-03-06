package com.srmworks.minestats;

import com.srmworks.minestats.config.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("minestats")
public class MineStats
{
    // Directly reference a slf4j logger
    public  static final String MODID  = "minestats";
    public MineStats()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "minestats.toml");
        MinecraftForge.EVENT_BUS.register(this);
    }


}
