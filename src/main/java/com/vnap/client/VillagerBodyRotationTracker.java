package com.vnap.client;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

/**
 * Feeds each villager's body rotation to the dialogue animation state every frame, which drives the turn-in-place
 * animation. The Fabric build did this from a mixin on the villager renderer (VillagerRendererMixin).
 */
@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID, value = Dist.CLIENT)
public final class VillagerBodyRotationTracker {
	private VillagerBodyRotationTracker() {
	}

	@SubscribeEvent
	public static void onRender(RenderLivingEvent.Pre<?, ?> event) {
		if (event.getEntity() instanceof Villager villager) {
			float partialTick = event.getPartialTick();
			float bodyRotation = Mth.rotLerp(partialTick, villager.yBodyRotO, villager.yBodyRot);
			DialogueAnimationState.trackBodyRotation(villager, bodyRotation, villager.tickCount + partialTick);
		}
	}
}
