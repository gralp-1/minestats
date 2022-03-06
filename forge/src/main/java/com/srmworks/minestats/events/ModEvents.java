package com.srmworks.minestats.events;

import com.srmworks.minestats.MineStats;
import com.srmworks.minestats.commands.MineStatsCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = MineStats.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new MineStatsCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}