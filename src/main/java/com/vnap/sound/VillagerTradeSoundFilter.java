package com.vnap.sound;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

import java.util.Set;

/**
 * Stops the vanilla villager and wandering trader trade / celebrate sounds at the source, like the Fabric version's
 * AbstractVillager mixin did: the sound is cancelled on the server before it is ever sent to players.
 */
@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID)
public final class VillagerTradeSoundFilter {
	private static final Set<ResourceLocation> SILENCED = Set.of(
		ResourceLocation.withDefaultNamespace("entity.villager.yes"),
		ResourceLocation.withDefaultNamespace("entity.villager.no"),
		ResourceLocation.withDefaultNamespace("entity.villager.celebrate"),
		ResourceLocation.withDefaultNamespace("entity.wandering_trader.yes"),
		ResourceLocation.withDefaultNamespace("entity.wandering_trader.no")
	);

	private VillagerTradeSoundFilter() {
	}

	@SubscribeEvent
	public static void onPlaySound(PlayLevelSoundEvent event) {
		Holder<SoundEvent> sound = event.getSound();
		if (sound != null && SILENCED.contains(sound.value().location())) event.setCanceled(true);
	}
}
