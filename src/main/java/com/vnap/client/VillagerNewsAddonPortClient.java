package com.vnap.client;

import com.vnap.VillagerNewsAddonPort;
import com.vnap.item.VillagerNewsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import traben.entity_model_features.EMFAnimationApi;

import java.io.IOException;
import java.util.function.Supplier;

@Mod(value = VillagerNewsAddonPort.MOD_ID, dist = Dist.CLIENT)
public final class VillagerNewsAddonPortClient {
	public VillagerNewsAddonPortClient(IEventBus modEventBus, ModContainer container) {
		VillagerNewsClientSettings.load();
		container.registerExtensionPoint(IConfigScreenFactory.class,
			(modContainer, parent) -> HandbookScreen.settingsScreen(parent));
		registerAnimationVariables();
		modEventBus.addListener(DialogueSubtitleState::register);
		modEventBus.addListener(VillagerNewsAddonPortClient::addLayers);
		NeoForge.EVENT_BUS.addListener(VillagerNewsAddonPortClient::onUseItem);
		NeoForge.EVENT_BUS.addListener(VillagerNewsAddonPortClient::onTick);
		NeoForge.EVENT_BUS.addListener(VillagerNewsAddonPortClient::onLogout);
		VillagerNewsAddonPort.LOGGER.info("Registered synchronized EMF facial and dialogue animations");
	}

	private static void registerAnimationVariables() {
		try {
			DialogueAnimationState.load();
			registerFloat("vnap_speaking", DialogueAnimationState::speaking, "Whether the Villager News character is speaking");
			registerFloat("vnap_mouth_open", DialogueAnimationState::mouthOpen, "Current Villager News mouth opening");
			registerFloat("vnap_mouth_width", DialogueAnimationState::mouthWidth, "Current Villager News mouth width");
			registerFloat("vnap_mouth_closed", DialogueAnimationState::mouthClosed, "Current Villager News closed-mouth layer");
			registerFloat("vnap_has_nose", DialogueAnimationState::hasNose, "Villager News nose visibility");
			registerFloat("vnap_cosmetic_mayor_hat", () -> DialogueAnimationState.cosmetic(1), "Villager News mayor hat visibility");
			registerFloat("vnap_cosmetic_helmet", () -> DialogueAnimationState.cosmetic(2), "Villager News helmet visibility");
			registerFloat("vnap_cosmetic_microphone", () -> DialogueAnimationState.cosmetic(3), "Villager News microphone visibility");
			registerFloat("vnap_cosmetic_moustache", () -> DialogueAnimationState.cosmetic(4), "Villager News moustache visibility");
			for (String variable : DialogueAnimationState.animationVariables()) {
				registerFloat(variable, () -> DialogueAnimationState.transform(variable), "Synchronized Villager News dialogue transform");
			}
		} catch (IOException | RuntimeException exception) {
			throw new IllegalStateException("Could not load Villager News animations", exception);
		} catch (Exception exception) {
			throw new IllegalStateException("Could not register Villager News EMF animation variables", exception);
		}
	}

	private static void addLayers(EntityRenderersEvent.AddLayers event) {
		EntityRenderer<?> renderer = event.getRenderer(EntityType.VILLAGER);
		if (renderer instanceof VillagerRenderer villagerRenderer) {
			villagerRenderer.addLayer(new VillagerNewsSignLayer(villagerRenderer));
		}
	}

	private static void onUseItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.getLevel().isClientSide()) return;
		if (event.getItemStack().getItem() != VillagerNewsItems.HANDBOOK) return;
		Minecraft.getInstance().setScreen(new HandbookScreen());
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}

	private static void onTick(ClientTickEvent.Post event) {
		Minecraft client = Minecraft.getInstance();
		DialogueSoundState.tick(client);
		DialogueAnimationState.tick(client);
		DialogueSubtitleState.tick(client);
	}

	private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
		DialogueSoundState.clear(Minecraft.getInstance());
		DialogueAnimationState.clear();
		DialogueSubtitleState.clear();
		VillagerNewsSettingsState.reset();
	}

	private static void registerFloat(String name, Supplier<Float> supplier, String description) throws Exception {
		EMFAnimationApi.registerSingletonAnimationVariable(VillagerNewsAddonPort.MOD_ID, name, description, supplier);
	}
}
