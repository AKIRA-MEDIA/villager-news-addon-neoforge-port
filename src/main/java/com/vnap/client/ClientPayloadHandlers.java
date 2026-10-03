package com.vnap.client;

import com.vnap.network.DialogueAnimationPayload;
import com.vnap.network.HurtEffectPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {
    }

    public static void dialogueAnimation(DialogueAnimationPayload payload, IPayloadContext context) {
        // TODO when ported:
        // DialogueSoundState.start(payload);
        // DialogueAnimationState.start(payload);
        // DialogueSubtitleState.start(payload);
    }

    public static void hurtEffect(HurtEffectPayload payload, IPayloadContext context) {
        // TODO when ported: SupplementalSoundState.play(payload);
    }
}