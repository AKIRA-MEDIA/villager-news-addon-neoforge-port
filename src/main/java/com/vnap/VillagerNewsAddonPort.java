package com.vnap;

import com.mojang.logging.LogUtils;
import com.vnap.config.VillagerNewsSettings;
import com.vnap.dialogue.DialogueCatalog;
import com.vnap.item.VillagerNewsItems;
import com.vnap.network.VillagerNewsSettingsNetwork;
import com.vnap.sound.SupplementalSoundCatalog;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import com.vnap.client.ClientPayloadHandlers;
import com.vnap.network.DialogueAnimationPayload;
import com.vnap.network.HurtEffectPayload;

@Mod(VillagerNewsAddonPort.MOD_ID)
public class VillagerNewsAddonPort {
    public static final String MOD_ID = "villager_news_addon_port";
    public static final String ASSET_NAMESPACE = "villager-news-addon-port";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerNewsAddonPort(IEventBus modEventBus) {
        VillagerNewsSettings.load();
        modEventBus.addListener(VillagerNewsAddonPort::onRegister);
        modEventBus.addListener(VillagerNewsAddonPort::onRegisterPayloads);
        VillagerNewsSettingsNetwork.registerEvents();
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> {
            DialogueCatalog.register(helper);
            SupplementalSoundCatalog.register(helper);
        });
        event.register(Registries.ITEM, VillagerNewsItems::registerItems);
        event.register(Registries.CREATIVE_MODE_TAB, VillagerNewsItems::registerTab);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        VillagerNewsSettingsNetwork.registerPayloads(registrar);
        registrar.playToClient(DialogueAnimationPayload.TYPE, DialogueAnimationPayload.CODEC, ClientPayloadHandlers::dialogueAnimation);
        registrar.playToClient(HurtEffectPayload.TYPE, HurtEffectPayload.CODEC, ClientPayloadHandlers::hurtEffect);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ASSET_NAMESPACE, path);
    }
}