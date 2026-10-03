package com.vnap.client;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.util.Set;

/**
 * Silences the vanilla villager and wandering trader trade / celebrate sounds.
 * The Fabric build removed them at the source with a mixin on AbstractVillager; here they are dropped
 * as they reach the sound engine, which does not depend on any 1.21.1 method names.
 */
@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID, value = Dist.CLIENT)
public final class VillagerTradeSoundSilencer {
	private static final Set<ResourceLocation> SILENCED = Set.of(
		ResourceLocation.withDefaultNamespace("entity.villager.yes"),
		ResourceLocation.withDefaultNamespace("entity.villager.no"),
		ResourceLocation.withDefaultNamespace("entity.villager.celebrate"),
		ResourceLocation.withDefaultNamespace("entity.wandering_trader.yes"),
		ResourceLocation.withDefaultNamespace("entity.wandering_trader.no")
	);

	private VillagerTradeSoundSilencer() {
	}

	@SubscribeEvent
	public static void onPlaySound(PlaySoundEvent event) {
		SoundInstance sound = event.getSound();
		if (sound != null && SILENCED.contains(sound.getLocation())) event.setSound(null);
	}
}
