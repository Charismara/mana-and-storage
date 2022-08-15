package com.github.manasmods.mns;

import com.github.manasmods.mns.handler.ChalkRuneHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ManaAndStorage.MOD_ID)
@Log4j2
public class ManaAndStorage {
    public static final String MOD_ID = "mns";
    @Getter
    private static ManaAndStorage instance;

    public ManaAndStorage() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonInit);
    }

    private void commonInit(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            if (ModList.get().isLoaded("refinedstorage")) enableAddon("Refined Storage", ChalkRuneHandler::enableRSAddon);
            if (ModList.get().isLoaded("refinedstorageaddons")) enableAddon("Refined Storage Addons", ChalkRuneHandler::enableRSAddonsAddon);
        });
    }

    private void enableAddon(String name, Runnable enable) {
        log.info("Found '{}' Mod. Enabling {} compat", name, name);
        enable.run();
    }
}
