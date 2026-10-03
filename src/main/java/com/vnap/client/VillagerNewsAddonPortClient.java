package com.vnap.client;

import com.vnap.VillagerNewsAddonPort;
import com.vnap.item.VillagerNewsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod(value = VillagerNewsAddonPort.MOD_ID, dist = Dist.CLIENT)
public final class VillagerNewsAddonPortClient {
	public VillagerNewsAddonPortClient(IEventBus modEventBus, ModContainer container) {
		VillagerNewsClientSettings.load();
		container.registerExtensionPoint(IConfigScreenFactory.class,
				(modContainer, parent) -> HandbookScreen.settingsScreen(parent));
		NeoForge.EVENT_BUS.addListener(VillagerNewsAddonPortClient::onUseItem);
		NeoForge.EVENT_BUS.addListener(VillagerNewsAddonPortClient::onLogout);
		// TODO: DialogueSubtitleState.register(), EMF animation variables,
		// sign layer (modEventBus AddLayers), and the client tick listener
		// go back in as their classes are ported.
	}

	private static void onUseItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.getLevel().isClientSide()) return;
		if (event.getItemStack().getItem() != VillagerNewsItems.HANDBOOK) return;
		Minecraft.getInstance().setScreen(new HandbookScreen());
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}

	private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
		VillagerNewsSettingsState.reset();
		// TODO: clear the dialogue sound, animation and subtitle states here
	}
}