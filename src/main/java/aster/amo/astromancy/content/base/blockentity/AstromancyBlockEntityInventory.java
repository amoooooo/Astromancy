package aster.amo.astromancy.content.base.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AstromancyBlockEntityInventory extends ItemStackHandler {
    public final int slotCount;
    public final int slotSize;
    public Predicate<ItemStack> inputPredicate;
    public Predicate<ItemStack> outputPredicate;
    public final Optional<IItemHandler> inventoryOptional = Optional.of(this);

    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<ItemStack> nonEmptyStacks = new ArrayList<>();
    public int emptyItemAmount;
    public int nonEmptyItemAmount;
    public int firstEmptyItemIndex;

    public AstromancyBlockEntityInventory(int slotCount, int slotSize, Predicate<ItemStack> inputPredicate, Predicate<ItemStack> outputPredicate) {
        this(slotCount, slotSize, inputPredicate);
        this.outputPredicate = outputPredicate;
    }

    public AstromancyBlockEntityInventory(int slotCount, int slotSize, Predicate<ItemStack> inputPredicate) {
        this(slotCount, slotSize);
        this.inputPredicate = inputPredicate;
    }

    public AstromancyBlockEntityInventory(int slotCount, int slotSize) {
        super(slotCount);
        this.slotCount = slotCount;
        this.slotSize = slotSize;
        updateData();
    }

    @Override
    public void onContentsChanged(int slot) {
        updateData();
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slotSize;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (inputPredicate != null) {
            if (!inputPredicate.test(stack)) {
                return false;
            }
        }
        return super.isItemValid(slot, stack);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (outputPredicate != null) {
            if (!outputPredicate.test(super.extractItem(slot, amount, true))) {
                return ItemStack.EMPTY;
            }
        }
        return super.extractItem(slot, amount, simulate);
    }

    public void updateData() {
        items = getItems();
        nonEmptyStacks = getNonEmptyItemStacks();
        emptyItemAmount = getEmptyItemCount();
        nonEmptyItemAmount = getNonEmptyItemCount();
        firstEmptyItemIndex = getFirstEmptyItemIndex();
    }

    public void load(CompoundTag compound, HolderLookup.Provider provider) {
        load(compound, "inventory", provider);
    }

    public void load(CompoundTag compound, String name, HolderLookup.Provider provider) {
        deserializeNBT(provider, compound.getCompound(name));
        if (stacks.size() != slotCount) {
            int missing = slotCount - stacks.size();
            for (int i = 0; i < missing; i++) {
                stacks.add(ItemStack.EMPTY);
            }
        }
        updateData();
    }

    public void save(CompoundTag compound, HolderLookup.Provider provider) {
        save(compound, "inventory", provider);
    }

    public void save(CompoundTag compound, String name, HolderLookup.Provider provider) {
        compound.put(name, serializeNBT(provider));
    }

    public int getFirstEmptyItemIndex() {
        for (int i = 0; i < slotCount; i++) {
            if (getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public boolean isEmpty() {
        return nonEmptyItemAmount == 0;
    }

    protected int getNonEmptyItemCount() {
        return (int) getStacks().stream().filter(s -> !s.isEmpty()).count();
    }

    protected int getEmptyItemCount() {
        return (int) getStacks().stream().filter(ItemStack::isEmpty).count();
    }

    protected ArrayList<ItemStack> getNonEmptyItemStacks() {
        return getStacks().stream().filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
    }

    protected ArrayList<Item> getItems() {
        return getStacks().stream().map(ItemStack::getItem).collect(Collectors.toCollection(ArrayList::new));
    }

    protected ArrayList<Item> getNonEmptyItems() {
        return getNonEmptyItemStacks().stream().map(ItemStack::getItem).collect(Collectors.toCollection(ArrayList::new));
    }

    public void clear() {
        for (int i = 0; i < slotCount; i++) {
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void dumpItems(Level level, BlockPos pos) {
        dumpItems(level, pos.getCenter().add(0.5, 0.5, 0.5));
    }

    public void dumpItems(Level level, Vec3 pos) {
        for (int i = 0; i < slotCount; i++) {
            if (!getStackInSlot(i).isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.x(), pos.y(), pos.z(), getStackInSlot(i)));
            }
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public ItemStack interact(Level level, Player player, InteractionHand handIn) {
        ItemStack held = player.getItemInHand(handIn);
        player.swing(handIn, true);
        int size = nonEmptyStacks.size() - 1;
        if ((held.isEmpty() || firstEmptyItemIndex == -1) && size != -1) {
            ItemStack takeOutStack = nonEmptyStacks.get(size);
            if (takeOutStack.getItem().equals(held.getItem())) {
                return insertItem(level, held);
            }
            ItemStack extractedStack = extractItem(level, held, player);
            boolean success = !extractedStack.isEmpty();
            if (success) {
                insertItem(level, held);
            }
            return extractedStack;
        } else {
            return insertItem(level, held);
        }
    }

    public ItemStack extractItem(Level level, ItemStack heldStack, Player player) {
        if (!level.isClientSide) {
            List<ItemStack> nonEmptyStacks = this.nonEmptyStacks;
            if (nonEmptyStacks.isEmpty()) {
                return heldStack;
            }
            ItemStack takeOutStack = nonEmptyStacks.get(nonEmptyStacks.size() - 1);
            int slot = stacks.indexOf(takeOutStack);
            if (extractItem(slot, takeOutStack.getCount(), true).equals(ItemStack.EMPTY)) {
                return heldStack;
            }
            extractItem(player, takeOutStack, slot);
            return takeOutStack;
        }
        return ItemStack.EMPTY;
    }

    public void extractItem(Player playerEntity, ItemStack stack, int slot) {
        playerEntity.addItem(stack);
        setStackInSlot(slot, ItemStack.EMPTY);
    }

    public ItemStack insertItem(Level level, ItemStack stack) {
        if (!level.isClientSide) {
            if (!stack.isEmpty()) {
                ItemStack simulate = insertItem(stack, true);
                if (simulate.equals(stack)) {
                    return ItemStack.EMPTY;
                }
                int count = stack.getCount() - simulate.getCount();
                if (count > slotSize) {
                    count = slotSize;
                }
                ItemStack input = stack.split(count);
                insertItem(input, false);
                return input;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItem(this, stack, simulate);
    }
}
