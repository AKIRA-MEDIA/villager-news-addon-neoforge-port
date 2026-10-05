package com.vnap.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/** Moves a pose stack to the villager's arms, so things drawn there follow the arm animation. */
public final class VillagerArms {
	private VillagerArms() {
	}

	/**
	 * Applies the arms transform (the animated one, as of this frame). Uses Entity Model Features' attachment point
	 * when the model defines one, otherwise the model's own "arms" part. Returns false when neither is available.
	 */
	public static boolean moveToArms(EntityModel<?> model, PoseStack poseStack) {
		if ((Object) model instanceof IEMFModel emfModel && emfModel.emf$isEMFModel()) {
			EMFModelPartRoot root = emfModel.emf$getEMFRootModel();
			Consumer<PoseStack> positioner = root == null ? null : root.getPositionerForAttachment(EMFAttachment.Type.VILLAGER);
			if (positioner != null) {
				positioner.accept(poseStack);
				return true;
			}
		}
		if (model instanceof HierarchicalModel<?> hierarchical) {
			try {
				hierarchical.root().getChild("arms").translateAndRotate(poseStack);
				return true;
			} catch (NoSuchElementException exception) {
				return false;
			}
		}
		return false;
	}
}
