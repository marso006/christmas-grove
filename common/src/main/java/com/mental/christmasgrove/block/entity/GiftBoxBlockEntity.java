package com.mental.christmasgrove.block.entity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.UUIDUtil;
import com.mental.christmasgrove.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The inside of a Gift Box: up to nine wrapped items, who wrapped them, and whether it is a
 * "surprise" (a present found in the world or given by Santa, which rolls random loot instead).
 * Contents and flags travel with the item when the box is broken, like a shulker box.
 */
public class GiftBoxBlockEntity extends BaseContainerBlockEntity {

    public static final String SURPRISE_TAG = "ChristmasSurprise";
    private static final String WRAPPER_TAG = "ChristmasWrapper";
    private static final String WRAPPER_NAME_TAG = "ChristmasWrapperName";

    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    /** True for boxes placed by worldgen or structures; cleared when placed from an ordinary item. */
    private boolean surprise = true;
    @Nullable
    private UUID wrapper;
    private String wrapperName = "";

    public GiftBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GIFT_BOX.get(), pos, state);
    }

    public boolean isSurprise() {
        return surprise;
    }

    @Nullable
    public UUID wrapper() {
        return wrapper;
    }

    public String wrapperName() {
        return wrapperName;
    }

    /** Copies of the wrapped items (empty slots skipped). */
    public java.util.List<ItemStack> contents() {
        return items.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).toList();
    }

    public void setWrapper(UUID uuid, String name) {
        this.wrapper = uuid;
        this.wrapperName = name;
        setChanged();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.christmas_grove.gift_box");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new DispenserMenu(id, inventory, this);
    }

    // ---- Saving ----------------------------------------------------------------------------------

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        surprise = tag.getBooleanOr(SURPRISE_TAG, true);
        wrapper = tag.read(WRAPPER_TAG, UUIDUtil.CODEC).orElse(null);
        wrapperName = tag.getStringOr(WRAPPER_NAME_TAG, "");
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putBoolean(SURPRISE_TAG, surprise);
        if (wrapper != null) {
            tag.store(WRAPPER_TAG, UUIDUtil.CODEC, wrapper);
            tag.putString(WRAPPER_NAME_TAG, wrapperName);
        }
    }

    // ---- Item components (what the dropped box item remembers) -----------------------------------

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        CompoundTag data = input.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        surprise = data.getBooleanOr(SURPRISE_TAG, false);
        wrapper = data.read(WRAPPER_TAG, UUIDUtil.CODEC).orElse(null);
        wrapperName = data.getStringOr(WRAPPER_NAME_TAG, "");
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        CompoundTag data = new CompoundTag();
        if (surprise) {
            data.putBoolean(SURPRISE_TAG, true);
        }
        if (wrapper != null) {
            data.store(WRAPPER_TAG, UUIDUtil.CODEC, wrapper);
            data.putString(WRAPPER_NAME_TAG, wrapperName);
        }
        if (!data.isEmpty()) {
            components.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput tag) {
        super.removeComponentsFromTag(tag);
        tag.discard("Items");
        tag.discard(SURPRISE_TAG);
        tag.discard(WRAPPER_TAG);
        tag.discard(WRAPPER_NAME_TAG);
    }
}
