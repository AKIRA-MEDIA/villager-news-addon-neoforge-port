package com.vnap.item;

import com.vnap.VillagerNewsAddonPort;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VillagerNewsItems {
	private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();
	private static final Map<Item, Integer> COSMETICS = new LinkedHashMap<>();

	public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
			VillagerNewsAddonPort.id("items"));
	public static final Item HANDBOOK = add("handbook", new Item(single("handbook")));
	public static final Item MAYOR_HAT = add("mayor_hat", new Item(head("mayor_hat")));
	public static final Item MICROPHONE = add("microphone", new Item(single("microphone")));
	public static final Item MOUSTACHE = add("moustache", new Item(head("moustache")));
	public static final Item TESTIFICATE_MAN_HELMET = add("testificate_man_helmet", new Item(head("testificate_man_helmet")));
	public static final Item VILLAGER_NOSE = add("villager_nose", new Item(head("villager_nose")));
	public static final Item MAYOR_VILLAGER_SPAWN_EGG = spawnEgg("mayor_villager_spawn_egg", EntityType.VILLAGER, "Mayor Villager", 0xFFFFFF, 0xFFFFFF);
	public static final Item TESTIFICATE_MAN_SPAWN_EGG = spawnEgg("testificate_man_spawn_egg", EntityType.VILLAGER, "Testificate Man", 0xFFFFFF, 0xFFFFFF);
	public static final Item VILLAGER_5_SPAWN_EGG = spawnEgg("villager_5_spawn_egg", EntityType.VILLAGER, "Villager #5", 0xFFFFFF, 0xFFFFFF);
	public static final Item VILLAGER_9_SPAWN_EGG = spawnEgg("villager_9_spawn_egg", EntityType.VILLAGER, "Villager #9", 0xFFFFFF, 0xFFFFFF);
	public static final Item UNTOUCHABLE_VILLAGER_SPAWN_EGG = spawnEgg("untouchable_villager_spawn_egg", EntityType.VILLAGER, "Villager Unreachable", 0xFFFFFF, 0xFFFFFF);
	public static final Item WOOLY_SPAWN_EGG = spawnEgg("wooly_spawn_egg", EntityType.SHEEP, "Wooly The Sheep", 0xFFFFFF, 0xFFFFFF);

	static {
		COSMETICS.put(MAYOR_HAT, 1);
		COSMETICS.put(TESTIFICATE_MAN_HELMET, 2);
		COSMETICS.put(MICROPHONE, 3);
		COSMETICS.put(MOUSTACHE, 4);
	}

	private VillagerNewsItems() {
	}

	public static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
		ITEMS.forEach((id, item) -> helper.register(id, item));
	}

	public static void registerTab(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
		helper.register(CREATIVE_TAB_KEY.location(), CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.villager-news-addon-port.items"))
				.icon(() -> new ItemStack(HANDBOOK))
				.displayItems((parameters, output) -> {
					output.accept(HANDBOOK);
					output.accept(MAYOR_HAT);
					output.accept(TESTIFICATE_MAN_HELMET);
					output.accept(MICROPHONE);
					output.accept(MOUSTACHE);
					output.accept(VILLAGER_NOSE);
					output.accept(MAYOR_VILLAGER_SPAWN_EGG);
					output.accept(TESTIFICATE_MAN_SPAWN_EGG);
					output.accept(VILLAGER_5_SPAWN_EGG);
					output.accept(VILLAGER_9_SPAWN_EGG);
					output.accept(UNTOUCHABLE_VILLAGER_SPAWN_EGG);
					output.accept(WOOLY_SPAWN_EGG);
				})
				.build());
	}

	public static int cosmetic(Item item) {
		return COSMETICS.getOrDefault(item, 0);
	}

	public static Item cosmeticItem(int cosmetic) {
		return COSMETICS.entrySet().stream().filter(entry -> entry.getValue() == cosmetic)
				.map(Map.Entry::getKey).findFirst().orElse(null);
	}

	private static ResourceKey<Item> key(String path) {
		return ResourceKey.create(Registries.ITEM, VillagerNewsAddonPort.id(path));
	}

	/** 1.21.2 items must know their registry id when their properties are built. */
	private static Item.Properties single(String path) {
		return new Item.Properties().stacksTo(1).setId(key(path));
	}

	/** Worn on the head by right-click, replacing the old HeadItem class (items use the equippable component now). */
	private static Item.Properties head(String path) {
		return single(path).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build());
	}

	private static Item add(String path, Item item) {
		ITEMS.put(VillagerNewsAddonPort.id(path), item);
		return item;
	}

	private static Item spawnEgg(String path, EntityType<? extends Mob> type, String entityName, int primary, int secondary) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
		tag.putString("CustomName", "{\"text\":\"" + entityName + "\"}");
		tag.putBoolean("PersistenceRequired", true);
		Item.Properties properties = new Item.Properties().setId(key(path)).component(DataComponents.ENTITY_DATA, CustomData.of(tag));
		return add(path, new SpawnEggItem(type, primary, secondary, properties));
	}
}