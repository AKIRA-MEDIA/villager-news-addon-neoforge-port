package com.vnap.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vnap.client.DialogueAnimationState;
import com.vnap.client.VillagerNewsRenderState;
import com.vnap.entity.VillagerNewsData;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the render events used on 1.21.1: the renderer now draws from a render state, so the sign data and the
 * body rotation are copied while the state is filled, and the baby model's size correction is applied in scale().
 */
@Mixin(VillagerRenderer.class)
public abstract class VillagerRendererMixin {
	/** Vanilla halves baby villagers; the baby model was authored for a version that does not. */
	private static final float VNAP$BABY_SCALE = 2.0F;

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/npc/Villager;Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;F)V", at = @At("TAIL"))
	private void vnap$extractRenderState(Villager villager, VillagerRenderState state, float partialTick, CallbackInfo ci) {
		DialogueAnimationState.trackBodyRotation(villager, state.bodyRot, villager.tickCount + partialTick);
		VillagerNewsData data = (VillagerNewsData) villager;
		VillagerNewsRenderState renderState = (VillagerNewsRenderState) state;
		renderState.vnap$setSignMessage(data.vnap$signMessage());
		renderState.vnap$setSignType(data.vnap$signType());
	}

	@Inject(method = "scale(Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
	private void vnap$scaleBaby(VillagerRenderState state, PoseStack poseStack, CallbackInfo ci) {
		if (state.isBaby) poseStack.scale(VNAP$BABY_SCALE, VNAP$BABY_SCALE, VNAP$BABY_SCALE);
	}
}
