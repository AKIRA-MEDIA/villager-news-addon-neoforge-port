package com.vnap.network;

import com.vnap.client.VillagerNewsSettingsState;
import com.vnap.config.VillagerNewsSettings;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class VillagerNewsSettingsNetwork {
	private VillagerNewsSettingsNetwork() {
	}

	public static void registerPayloads(PayloadRegistrar registrar) {
		registrar.playBidirectional(
				VillagerNewsSettingsPayload.TYPE,
				VillagerNewsSettingsPayload.CODEC,
				new DirectionalPayloadHandler<VillagerNewsSettingsPayload>(
						(payload, context) -> VillagerNewsSettingsState.apply(payload),
						(payload, context) -> {
							if (!(context.player() instanceof ServerPlayer player)) return;
							if (canEdit(player)) {
								VillagerNewsSettings.update(payload.chattiness(), payload.rareVoicelines(), payload.spawnSpecialVillagers());
							}
							send(player);
						}
				)
		);
	}

	public static void registerEvents() {
		NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
			if (event.getEntity() instanceof ServerPlayer player) send(player);
		});
	}

	public static void send(ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new VillagerNewsSettingsPayload(
				VillagerNewsSettings.chattiness(),
				VillagerNewsSettings.rareVoicelines(),
				VillagerNewsSettings.spawnSpecialVillagers(),
				canEdit(player)
		));
	}

	private static boolean canEdit(ServerPlayer player) {
		return player.level().getServer().isSingleplayerOwner(player.getGameProfile())
				|| player.hasPermissions(2);
	}
}