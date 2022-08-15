package com.github.manasmods.mns;

import lombok.Getter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ManaAndStorage.MOD_ID)
public class ManaAndStorage {
    public static final String MOD_ID = "mns";
    @Getter
    private static final Logger logger = LogManager.getLogger();
    @Getter
    private static ManaAndStorage instance;

    public ManaAndStorage() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::generateData);
    }

    private void generateData(final GatherDataEvent e) {

    }
}
