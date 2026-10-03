package com.vnap;

import com.mojang.logging.LogUtils;
import com.vnap.config.VillagerNewsSettings;
import com.vnap.dialogue.DialogueCatalog;
import com.vnap.sound.SupplementalSoundCatalog;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(VillagerNewsAddonPort.MOD_ID)
public class VillagerNewsAddonPort {
    public static final String MOD_ID = "villager_news_addon_port";
    public static final String ASSET_NAMESPACE = "villager-news-addon-port";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerNewsAddonPort(IEventBus modEventBus) {
        VillagerNewsSettings.load();
        modEventBus.addListener(VillagerNewsAddonPort::registerSounds);
    }

    private static void registerSounds(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> {
            DialogueCatalog.register(helper);
            SupplementalSoundCatalog.register(helper);
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ASSET_NAMESPACE, path);
    }
}