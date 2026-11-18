package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.Constants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Armor materials (plain records since 1.21.2). The Santa Suit protects about as well as iron, is
 * very enchantable, and (through #freeze_immune_wearables) stops you freezing in powder snow.
 * Each material points at an equipment model: assets/christmas_grove/models/equipment/&lt;name&gt;.json.
 */
public final class ModArmorMaterials {

    public static final ArmorMaterial SANTA_SUIT = new ArmorMaterial(20, defense(2, 5, 6, 2), 22,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 0.0F, ItemTags.WOOL, Constants.id("santa_suit"));
    /** Same stats as the suit, but its own model: the 3D hat (see SantaHatModel). */
    public static final ArmorMaterial SANTA_HAT = new ArmorMaterial(20, defense(2, 5, 6, 2), 22,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 0.0F, ItemTags.WOOL, Constants.id("santa_hat"));

    private ModArmorMaterials() {
    }

    private static Map<ArmorType, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, leggings);
        map.put(ArmorType.CHESTPLATE, chestplate);
        map.put(ArmorType.HELMET, helmet);
        map.put(ArmorType.BODY, chestplate);
        return map;
    }

    /** Kept so ChristmasGrove.init stays the same across versions. */
    public static void init() {
    }
}
