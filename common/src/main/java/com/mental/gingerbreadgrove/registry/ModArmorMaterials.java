package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

/**
 * Armor materials. The Santa Suit protects about as well as iron, is very enchantable, and (through
 * the #freeze_immune_wearables tag) stops you freezing in powder snow.
 * Textures: assets/gingerbread_grove/textures/models/armor/santa_suit_layer_1.png and _layer_2.png.
 */
public final class ModArmorMaterials {

    public static final Holder<ArmorMaterial> SANTA_SUIT = Services.REGISTRY.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL, "santa_suit", () -> new ArmorMaterial(
                    defense(2, 5, 6, 2), 22, SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(Items.RED_WOOL),
                    List.of(new ArmorMaterial.Layer(Constants.id("santa_suit"))),
                    0.5F, 0.0F));

    private ModArmorMaterials() {
    }

    private static EnumMap<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.BODY, chestplate);
        return map;
    }

    /** Forces class loading so the static fields above register. Call before ModItems. */
    public static void init() {
    }
}
