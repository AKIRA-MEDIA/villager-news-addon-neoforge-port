package com.vnap.client;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID, value = Dist.CLIENT)
public final class BabyVillagerScale {
    /** Vanilla 1.21.1 already halves baby villagers; the baby model was authored for a version that does not. */
    private static final float SCALE = 2.0F;

    private BabyVillagerScale() {
    }

    @SubscribeEvent
    public static void pre(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof Villager villager && villager.isBaby()) {
            event.getPoseStack().scale(SCALE, SCALE, SCALE);
        }
    }

    @SubscribeEvent
    public static void post(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof Villager villager && villager.isBaby()) {
            event.getPoseStack().scale(1.0F / SCALE, 1.0F / SCALE, 1.0F / SCALE);
        }
    }
}