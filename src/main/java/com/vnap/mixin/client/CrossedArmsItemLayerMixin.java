package com.vnap.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the item a villager holds in front of its crossed arms using vanilla's own numbers, instead of the
 * position EMF derives from the custom model. Runs before EMF's patch (lower priority number = earlier) and
 * cancels the original, so EMF's offset never applies to villagers.
 *
 * Tune the six values below. Offsets are in blocks (0.0625 = one pixel); rotations are extra degrees on top of
 * vanilla's own 180 degree flip. After changing a value, restart the game.
 */
@Mixin(value = CrossedArmsItemLayer.class, priority = 500)
public abstract class CrossedArmsItemLayerMixin {
	private static final float OFFSET_X = 0.0F;
	private static final float OFFSET_Y = 0.0F;
	private static final float OFFSET_Z = 0.0F;
	private static final float EXTRA_ROTATE_X = 0.0F;
	private static final float EXTRA_ROTATE_Y = 0.0F;
	private static final float EXTRA_ROTATE_Z = 0.0F;

	@Shadow @Final private ItemInHandRenderer itemInHandRenderer;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void vnap$vanillaHeldItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
			float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (!(entity instanceof Villager)) return;
		ci.cancel();
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
		if (stack.isEmpty()) return;
		poseStack.pushPose();
		poseStack.translate(0.0F + OFFSET_X, 0.4F + OFFSET_Y, -0.4F + OFFSET_Z);
		poseStack.mulPose(Axis.XP.rotationDegrees(180.0F + EXTRA_ROTATE_X));
		if (EXTRA_ROTATE_Y != 0.0F) poseStack.mulPose(Axis.YP.rotationDegrees(EXTRA_ROTATE_Y));
		if (EXTRA_ROTATE_Z != 0.0F) poseStack.mulPose(Axis.ZP.rotationDegrees(EXTRA_ROTATE_Z));
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
