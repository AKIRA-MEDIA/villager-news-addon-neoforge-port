package com.vnap.dialogue;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Makes a baby villager react when it is created by using a spawn egg on a villager.
 * The Fabric build hooked SpawnEggItem.spawnOffspringFromSpawnEgg with a mixin; here the egg use is noted when the
 * player interacts, and the baby that appears in the same tick next to that villager is matched to it.
 */
@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID)
public final class SpawnEggBabyTracker {
	private record EggUse(UUID player, long tick, double x, double y, double z) {
	}

	private static final List<EggUse> RECENT = new ArrayList<>();

	private SpawnEggBabyTracker() {
	}

	@SubscribeEvent
	public static void onUseEggOnEntity(PlayerInteractEvent.EntityInteract event) {
		if (!(event.getLevel() instanceof ServerLevel level) || !(event.getItemStack().getItem() instanceof SpawnEggItem)) return;
		Entity target = event.getTarget();
		long now = level.getGameTime();
		RECENT.removeIf(use -> now - use.tick() > 2L);
		RECENT.add(new EggUse(event.getEntity().getUUID(), now, target.getX(), target.getY(), target.getZ()));
	}

	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent event) {
		if (RECENT.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || event.loadedFromDisk()) return;
		if (!(event.getEntity() instanceof Villager baby) || !baby.isBaby()) return;
		long now = level.getGameTime();
		for (EggUse use : RECENT) {
			if (now - use.tick() > 1L || baby.distanceToSqr(use.x(), use.y(), use.z()) > 9.0) continue;
			Player player = level.getPlayerByUUID(use.player());
			RECENT.remove(use);
			if (player != null) ContextualDialogueController.onBabySpawnedFromEgg(baby, player);
			return;
		}
	}
}
