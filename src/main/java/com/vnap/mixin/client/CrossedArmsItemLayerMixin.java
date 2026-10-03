package com.vnap.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossedArmsItemLayer.class)
public abstract class CrossedArmsItemLayerMixin {
    // Tune these. Units are blocks (0.0625 = 1 pixel). In this space +Y is down and -Z is forward.
    private static final float VNAP_OFFSET_X = 0.0F;
    private static final float VNAP_OFFSET_Y = 0.0F;
    private static final float VNAP_OFFSET_Z = 0.0F;

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = At.Shift.AFTER))
    private void vnap$moveHeldItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity,
                                   float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                                   float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Villager) poseStack.translate(VNAP_OFFSET_X, VNAP_OFFSET_Y, VNAP_OFFSET_Z);
    }
}