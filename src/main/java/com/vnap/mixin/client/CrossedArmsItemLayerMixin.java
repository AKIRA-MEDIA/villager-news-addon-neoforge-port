package com.vnap.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vnap.client.VillagerArms;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
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
 * Draws the item a villager holds in front of its crossed arms at vanilla's position instead of the position
 * Entity Model Features derives from the custom model. Runs before EMF's patch (lower priority number = earlier)
 * and cancels the original, so EMF's offset never applies to villagers.
 *
 * With FOLLOW_ARMS on, the item is attached to the arms, so it moves with them when the arms are animated (for
 * example the dialogue gestures). The numbers are chosen so that, with the arms at rest, the item sits exactly where
 * vanilla draws it. Set FOLLOW_ARMS to false to go back to a fixed position on the body.
 *
 * Tune the six values below. Offsets are in blocks (0.0625 = one pixel) and rotations are extra degrees. With
 * FOLLOW_ARMS on, they are measured in the arms' own tilted frame. After changing a value, restart the game.
 */
@Mixin(value = CrossedArmsItemLayer.class, priority = 500)
public abstract class CrossedArmsItemLayerMixin {
	private static final boolean FOLLOW_ARMS = true;
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
		boolean attached = FOLLOW_ARMS && VillagerArms.moveToArms(((RenderLayer<?, ?>) (Object) this).getParentModel(), poseStack);
		if (attached) {
			// The arms pivot sits at (0, 3, -1) pixels, tilted 42.97 degrees. This puts the item where vanilla draws
			// it (0, 0.4, -0.4, flipped 180 degrees) when the arms are at rest.
			poseStack.translate(0.0F + OFFSET_X, 0.3855F + OFFSET_Y, -0.1022F + OFFSET_Z);
			poseStack.mulPose(Axis.XP.rotationDegrees(222.9718F + EXTRA_ROTATE_X));
		} else {
			poseStack.translate(0.0F + OFFSET_X, 0.4F + OFFSET_Y, -0.4F + OFFSET_Z);
			poseStack.mulPose(Axis.XP.rotationDegrees(180.0F + EXTRA_ROTATE_X));
		}
		if (EXTRA_ROTATE_Y != 0.0F) poseStack.mulPose(Axis.YP.rotationDegrees(EXTRA_ROTATE_Y));
		if (EXTRA_ROTATE_Z != 0.0F) poseStack.mulPose(Axis.ZP.rotationDegrees(EXTRA_ROTATE_Z));
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
