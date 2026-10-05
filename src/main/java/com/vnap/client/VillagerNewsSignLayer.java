package com.vnap.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vnap.VillagerNewsAddonPort;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/** Draws the sign board a villager is holding. Reads the sign from the render state, copied over by VillagerRendererMixin. */
public final class VillagerNewsSignLayer extends RenderLayer<VillagerRenderState, VillagerModel> {
	// Tuning knobs. Offsets are in blocks (0.0625 = one pixel); rotation is extra degrees. Restart the game after changing.
	private static final boolean USE_EMF_POSITIONER = true;
	private static final float OFFSET_X = 0.0F;
	private static final float OFFSET_Y = 0.0F;
	private static final float OFFSET_Z = 0.0F;
	private static final float EXTRA_ROTATE_X = 0.0F;

	private static final int MESSAGES = 87;
	/** 1.21.1 has no pale oak sign, so that slot uses oak. */
	private static final ResourceLocation[] BOARD_TEXTURES = {
		sign("oak"), sign("spruce"), sign("birch"), sign("jungle"), sign("acacia"), sign("dark_oak"),
		sign("mangrove"), sign("cherry"), sign("oak"), sign("bamboo"), sign("crimson"), sign("warped")
	};
	private static final ResourceLocation TEXT_TEXTURE = VillagerNewsAddonPort.id("textures/entity/sign_text.png");

	public VillagerNewsSignLayer(RenderLayerParent<VillagerRenderState, VillagerModel> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, VillagerRenderState state,
			float yRot, float xRot) {
		VillagerNewsRenderState sign = (VillagerNewsRenderState) state;
		int type = sign.vnap$signType();
		int message = sign.vnap$signMessage();
		if (state.isInvisible || state.isBaby || type < 0 || type >= BOARD_TEXTURES.length
				|| message < 0 || message >= MESSAGES) return;
		poseStack.pushPose();
		if (position(poseStack)) {
			poseStack.translate(OFFSET_X, 5.75F / 16.0F + OFFSET_Y, -1.75F / 16.0F + OFFSET_Z);
			poseStack.mulPose(Axis.XP.rotationDegrees(42.97F + EXTRA_ROTATE_X));
			drawBoard(poseStack.last(), buffer.getBuffer(RenderType.entityCutout(BOARD_TEXTURES[type])), packedLight);
			drawText(poseStack.last(), buffer.getBuffer(RenderType.entityCutout(TEXT_TEXTURE)), packedLight, message);
		}
		poseStack.popPose();
	}

	/** Moves to the villager's arms: EMF's attachment point when the model defines one, otherwise the vanilla arms part. */
	private boolean position(PoseStack poseStack) {
		VillagerModel model = getParentModel();
		if (USE_EMF_POSITIONER && (Object) model instanceof IEMFModel emfModel && emfModel.emf$isEMFModel()) {
			EMFModelPartRoot root = emfModel.emf$getEMFRootModel();
			Consumer<PoseStack> positioner = root == null ? null : root.getPositionerForAttachment(EMFAttachment.Type.VILLAGER);
			if (positioner != null) {
				positioner.accept(poseStack);
				return true;
			}
		}
		try {
			model.root().getChild("arms").translateAndRotate(poseStack);
			return true;
		} catch (NoSuchElementException exception) {
			return false;
		}
	}

	private static ResourceLocation sign(String wood) {
		return ResourceLocation.withDefaultNamespace("textures/entity/signs/" + wood + ".png");
	}

	/** Board geometry unchanged; texture coordinates are pixels on the 64x32 entity sign sheet. */
	private static void drawBoard(PoseStack.Pose pose, VertexConsumer vc, int light) {
		final float l = -0.50625F, r = 0.50625F, t = -0.25625F, b = 0.25625F, f = -0.04792F, k = 0.04792F;
		// front (2,2)-(26,14)
		v(pose, vc, light, l, b, f, 2, 14, 0, 0, -1);
		v(pose, vc, light, r, b, f, 26, 14, 0, 0, -1);
		v(pose, vc, light, r, t, f, 26, 2, 0, 0, -1);
		v(pose, vc, light, l, t, f, 2, 2, 0, 0, -1);
		// back (28,2)-(52,14)
		v(pose, vc, light, r, b, k, 28, 14, 0, 0, 1);
		v(pose, vc, light, l, b, k, 52, 14, 0, 0, 1);
		v(pose, vc, light, l, t, k, 52, 2, 0, 0, 1);
		v(pose, vc, light, r, t, k, 28, 2, 0, 0, 1);
		// top (2,0)-(26,2)
		v(pose, vc, light, l, t, k, 2, 0, 0, -1, 0);
		v(pose, vc, light, l, t, f, 2, 2, 0, -1, 0);
		v(pose, vc, light, r, t, f, 26, 2, 0, -1, 0);
		v(pose, vc, light, r, t, k, 26, 0, 0, -1, 0);
		// bottom (26,0)-(50,2)
		v(pose, vc, light, l, b, f, 26, 0, 0, 1, 0);
		v(pose, vc, light, l, b, k, 26, 2, 0, 1, 0);
		v(pose, vc, light, r, b, k, 50, 2, 0, 1, 0);
		v(pose, vc, light, r, b, f, 50, 0, 0, 1, 0);
		// left (0,2)-(2,14)
		v(pose, vc, light, l, b, k, 0, 14, -1, 0, 0);
		v(pose, vc, light, l, b, f, 2, 14, -1, 0, 0);
		v(pose, vc, light, l, t, f, 2, 2, -1, 0, 0);
		v(pose, vc, light, l, t, k, 0, 2, -1, 0, 0);
		// right (26,2)-(28,14)
		v(pose, vc, light, r, b, f, 26, 14, 1, 0, 0);
		v(pose, vc, light, r, b, k, 28, 14, 1, 0, 0);
		v(pose, vc, light, r, t, k, 28, 2, 1, 0, 0);
		v(pose, vc, light, r, t, f, 26, 2, 1, 0, 0);
	}

	/** The message is one 1/87th-high strip of the stacked text texture. */
	private static void drawText(PoseStack.Pose pose, VertexConsumer vc, int light, int message) {
		float topV = message / (float) MESSAGES;
		float bottomV = (message + 1) / (float) MESSAGES;
		final float z = -0.06042F;
		vertex(pose, vc, light, -0.5F, 0.1875F, z, 0.0F, bottomV, 0, 0, -1);
		vertex(pose, vc, light, 0.5F, 0.1875F, z, 1.0F, bottomV, 0, 0, -1);
		vertex(pose, vc, light, 0.5F, -0.1875F, z, 1.0F, topV, 0, 0, -1);
		vertex(pose, vc, light, -0.5F, -0.1875F, z, 0.0F, topV, 0, 0, -1);
	}

	private static void v(PoseStack.Pose pose, VertexConsumer vc, int light, float x, float y, float z,
			float pixelU, float pixelV, float nx, float ny, float nz) {
		vertex(pose, vc, light, x, y, z, pixelU / 64.0F, pixelV / 32.0F, nx, ny, nz);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer vc, int light, float x, float y, float z,
			float u, float v, float nx, float ny, float nz) {
		vc.addVertex(pose, x, y, z)
			.setColor(-1)
			.setUv(u, v)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(light)
			.setNormal(pose, nx, ny, nz);
	}
}
