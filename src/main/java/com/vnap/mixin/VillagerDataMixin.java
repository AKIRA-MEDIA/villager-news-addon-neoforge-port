package com.vnap.mixin;

import com.mojang.serialization.Codec;
import com.vnap.dialogue.ContextualDialogueController;
import com.vnap.entity.VillagerNewsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerDataMixin implements VillagerNewsData {
	@Unique
	private static final EntityDataAccessor<Boolean> VNAP_HAS_NOSE = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Integer> VNAP_COSMETIC = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);
	@Unique
	private static final EntityDataAccessor<Integer> VNAP_SIGN_MESSAGE = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);
	@Unique
	private static final EntityDataAccessor<Integer> VNAP_SIGN_TYPE = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);
	@Unique
	private VillagerData vnap$originalVillagerData;
	@Unique
	private MerchantOffers vnap$originalVillagerOffers;

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void vnap$defineData(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(VNAP_HAS_NOSE, true);
		builder.define(VNAP_COSMETIC, 0);
		builder.define(VNAP_SIGN_MESSAGE, -1);
		builder.define(VNAP_SIGN_TYPE, -1);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void vnap$saveData(CompoundTag tag, CallbackInfo ci) {
		tag.putBoolean("VillagerNewsHasNose", vnap$hasNose());
		tag.putInt("VillagerNewsCosmetic", vnap$cosmetic());
		tag.putInt("VillagerNewsSignMessage", vnap$signMessage());
		tag.putInt("VillagerNewsSignType", vnap$signType());
		if (vnap$originalVillagerData != null && vnap$originalVillagerOffers != null) {
			vnap$store(tag, "VillagerNewsOriginalData", VillagerData.CODEC, vnap$originalVillagerData);
			vnap$store(tag, "VillagerNewsOriginalOffers", MerchantOffers.CODEC, vnap$originalVillagerOffers);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void vnap$loadData(CompoundTag tag, CallbackInfo ci) {
		Villager villager = (Villager) (Object) this;
		vnap$setHasNose(!tag.contains("VillagerNewsHasNose") || tag.getBoolean("VillagerNewsHasNose"));
		vnap$setCosmetic(tag.contains("VillagerNewsCosmetic") ? tag.getInt("VillagerNewsCosmetic") : 0);
		int signMessage = tag.contains("VillagerNewsSignMessage") ? tag.getInt("VillagerNewsSignMessage") : -1;
		vnap$setSignMessage(signMessage);
		int equippedSign = ContextualDialogueController.signType(villager.getMainHandItem());
		int defaultSignType = equippedSign >= 0 ? equippedSign : signMessage >= 0 ? 0 : -1;
		vnap$setSignType(tag.contains("VillagerNewsSignType") ? tag.getInt("VillagerNewsSignType") : defaultSignType);
		if (equippedSign >= 0) villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		vnap$originalVillagerData = vnap$read(tag, "VillagerNewsOriginalData", VillagerData.CODEC);
		vnap$originalVillagerOffers = vnap$read(tag, "VillagerNewsOriginalOffers", MerchantOffers.CODEC);
		if (vnap$originalVillagerData == null || vnap$originalVillagerOffers == null) {
			vnap$originalVillagerData = null;
			vnap$originalVillagerOffers = null;
		}
	}

	@Redirect(
			method = "customServerAiStep",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;stopTrading()V")
	)
	private void vnap$keepSpecialTradeOpen(Villager villager) {
		if (!ContextualDialogueController.isSpecialTrader(villager)) villager.setTradingPlayer(null);
	}

	@ModifyVariable(method = "setVillagerData", at = @At("HEAD"), argsOnly = true)
	private VillagerData vnap$preventSpecialProfession(VillagerData value) {
		Villager villager = (Villager) (Object) this;
		return ContextualDialogueController.isSpecialTrader(villager)
				? value.setProfession(VillagerProfession.NONE).setLevel(1)
				: value;
	}

	@Unique
	private RegistryOps<Tag> vnap$ops() {
		return ((Villager) (Object) this).level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
	}

	@Unique
	private <T> void vnap$store(CompoundTag tag, String key, Codec<T> codec, T value) {
		codec.encodeStart(vnap$ops(), value).result().ifPresent(encoded -> tag.put(key, encoded));
	}

	@Unique
	private <T> T vnap$read(CompoundTag tag, String key, Codec<T> codec) {
		if (!tag.contains(key)) return null;
		return codec.parse(vnap$ops(), tag.get(key)).result().orElse(null);
	}

	@Unique
	private MerchantOffers vnap$copyOffers(MerchantOffers offers) {
		RegistryOps<Tag> ops = vnap$ops();
		return MerchantOffers.CODEC.encodeStart(ops, offers)
				.flatMap(encoded -> MerchantOffers.CODEC.parse(ops, encoded))
				.result().orElseGet(MerchantOffers::new);
	}

	@Override
	public boolean vnap$hasOriginalVillagerState() {
		return vnap$originalVillagerData != null && vnap$originalVillagerOffers != null;
	}

	@Override
	public void vnap$captureOriginalVillagerState() {
		if (vnap$hasOriginalVillagerState()) return;
		Villager villager = (Villager) (Object) this;
		vnap$originalVillagerData = villager.getVillagerData();
		vnap$originalVillagerOffers = vnap$copyOffers(villager.getOffers());
	}

	@Override
	public void vnap$restoreOriginalVillagerState() {
		if (!vnap$hasOriginalVillagerState()) return;
		Villager villager = (Villager) (Object) this;
		VillagerData originalData = vnap$originalVillagerData;
		MerchantOffers originalOffers = vnap$copyOffers(vnap$originalVillagerOffers);
		vnap$originalVillagerData = null;
		vnap$originalVillagerOffers = null;
		villager.setVillagerData(originalData);
		villager.getOffers().clear();
		villager.getOffers().addAll(originalOffers);
	}

	@Override
	public boolean vnap$hasNose() {
		return ((Villager) (Object) this).getEntityData().get(VNAP_HAS_NOSE);
	}

	@Override
	public void vnap$setHasNose(boolean value) {
		((Villager) (Object) this).getEntityData().set(VNAP_HAS_NOSE, value);
	}

	@Override
	public int vnap$cosmetic() {
		return ((Villager) (Object) this).getEntityData().get(VNAP_COSMETIC);
	}

	@Override
	public void vnap$setCosmetic(int value) {
		((Villager) (Object) this).getEntityData().set(VNAP_COSMETIC, Math.max(0, Math.min(4, value)));
	}

	@Override
	public int vnap$signMessage() {
		return ((Villager) (Object) this).getEntityData().get(VNAP_SIGN_MESSAGE);
	}

	@Override
	public void vnap$setSignMessage(int value) {
		((Villager) (Object) this).getEntityData().set(VNAP_SIGN_MESSAGE, Math.max(-1, Math.min(86, value)));
	}

	@Override
	public int vnap$signType() {
		return ((Villager) (Object) this).getEntityData().get(VNAP_SIGN_TYPE);
	}

	@Override
	public void vnap$setSignType(int value) {
		((Villager) (Object) this).getEntityData().set(VNAP_SIGN_TYPE, Math.max(-1, Math.min(11, value)));
	}
}