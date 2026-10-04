package com.vnap.client;

import com.mojang.logging.LogUtils;
import com.vnap.VillagerNewsAddonPort;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Optional built-in resource pack that carries this mod's Minecraft-namespace overrides (the Entity Model Features
 * models and rules plus the textures). When the setting is on, the pack sits above every user resource pack, so
 * packs such as Fresh Animations can no longer replace the Villager News villager, trader and Wooly models.
 */
@EventBusSubscriber(modid = VillagerNewsAddonPort.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VillagerNewsPriorityPack {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String PACK_ID = "mod/villager_news_addon_port_priority";
	private static final String KEY = "override_resource_packs";
	private static Boolean enabled;

	private VillagerNewsPriorityPack() {
	}

	@SubscribeEvent
	public static void onAddPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
		event.addRepositorySource(consumer -> {
			if (!enabled()) return;
			try {
				Pack pack = createPack();
				if (pack != null) consumer.accept(pack);
			} catch (Exception exception) {
				LOGGER.error("Villager News could not add its priority resource pack", exception);
			}
		});
	}

	private static Pack createPack() {
		var modFile = ModList.get().getModFileById(VillagerNewsAddonPort.MOD_ID);
		if (modFile == null) return null;
		Path path = modFile.getFile().findResource("resourcepacks", "vnap_priority");
		if (path == null || !Files.isDirectory(path)) {
			LOGGER.warn("Villager News priority resource pack folder was not found: {}", path);
			return null;
		}
		PackLocationInfo info = new PackLocationInfo(PACK_ID, Component.literal("Villager News Priority"),
			PackSource.BUILT_IN, Optional.empty());
		Pack pack = Pack.readMetaAndCreate(info, new PathPackResources.PathResourcesSupplier(path),
			PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Pack.Position.TOP, true));
		if (pack == null) LOGGER.warn("Villager News priority resource pack could not be read");
		else LOGGER.info("Villager News models take priority over resource packs");
		return pack;
	}

	/** True when Villager News models and animations should override resource packs. Defaults to on. */
	public static synchronized boolean enabled() {
		if (enabled == null) {
			Properties properties = new Properties();
			Path file = file();
			if (Files.isRegularFile(file)) {
				try (InputStream in = Files.newInputStream(file)) {
					properties.load(in);
				} catch (IOException exception) {
					LOGGER.warn("Could not read {}", file, exception);
				}
			}
			enabled = Boolean.parseBoolean(properties.getProperty(KEY, "true"));
		}
		return enabled;
	}

	public static synchronized void setEnabled(boolean value) {
		if (enabled != null && enabled == value) return;
		enabled = value;
		Properties properties = new Properties();
		properties.setProperty(KEY, Boolean.toString(value));
		Path file = file();
		try (OutputStream out = Files.newOutputStream(file)) {
			properties.store(out, "Villager News: true lets its models and animations override resource packs such as Fresh Animations");
		} catch (IOException exception) {
			LOGGER.warn("Could not save {}", file, exception);
		}
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getResourcePackRepository().reload();
		minecraft.reloadResourcePacks();
	}

	private static Path file() {
		return FMLPaths.CONFIGDIR.get().resolve("villager_news_addon_port-resource_packs.properties");
	}
}