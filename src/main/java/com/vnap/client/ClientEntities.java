package com.vnap.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

final class ClientEntities {
    private ClientEntities() {
    }

    static Entity find(UUID id) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        for (Entity entity : level.entitiesForRendering()) {
            if (entity.getUUID().equals(id)) return entity;
        }
        return null;
    }
}