package com.vnap.client;

/** Extra data carried on the villager render state, because 1.21.2 renders from the state, not the entity. */
public interface VillagerNewsRenderState {
	int vnap$signMessage();

	void vnap$setSignMessage(int value);

	int vnap$signType();

	void vnap$setSignType(int value);
}
