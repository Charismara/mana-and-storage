package com.github.manasmods.mns.handler;

import com.github.manasmods.mns.CuriosHelper;
import com.mna.blocks.tileentities.ChalkRuneTile;
import com.mna.tools.MATags;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.item.NetworkItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class RefinedStorageHandler {
    private static final int drainedEnergy = 3;
    private static final List<Item> networkAccessItems = List.of(
        RSItems.WIRELESS_GRID.get(),
        RSItems.CREATIVE_WIRELESS_GRID.get()
    );

    public static boolean check(Player player, Level world, BlockPos pos, BlockState state, ChalkRuneTile runeTile, ResourceLocation ritualReagent, boolean curios) {
        ItemStack networkAccessItem = getNetworkAccess(player);
        if (curios && networkAccessItem.isEmpty()) {
            networkAccessItem = CuriosHelper.getNetworkAccess(networkAccessItems, player);
        }
        if (networkAccessItem.isEmpty()) return false;

        LazyOptional<IEnergyStorage> energy = networkAccessItem.getCapability(CapabilityEnergy.ENERGY, null);
        AtomicBoolean result = new AtomicBoolean(false);
        ItemStack finalNetworkAccessItem = networkAccessItem;
        energy.ifPresent(iEnergyStorage -> {
            NetworkItem networkAccessItemItem = (NetworkItem) finalNetworkAccessItem.getItem();
            if (iEnergyStorage.getEnergyStored() < drainedEnergy && !networkAccessItemItem.equals(RSItems.CREATIVE_WIRELESS_GRID.get())) return;
            networkAccessItemItem.applyNetwork(world.getServer(), finalNetworkAccessItem,
                network -> {
                    if (!networkAccessItemItem.equals(RSItems.CREATIVE_WIRELESS_GRID.get())) network.getNetworkItemManager().getItem(player).drainEnergy(drainedEnergy);
                    Item item = ForgeRegistries.ITEMS.getValue(ritualReagent);
                    if (item != null) {
                        ItemStack stack = network.extractItem(new ItemStack(item), 1, Action.PERFORM);
                        if (stack.isEmpty()) return;
                        runeTile.setGhostItem(false);
                        runeTile.setItem(0, stack);
                        world.sendBlockUpdated(pos, state, state, 2);
                        world.updateNeighbourForOutputSignal(pos, state.getBlock());
                        result.set(true);
                    } else {
                        for (Item allowedItem : MATags.getItemTagContents(ritualReagent)) {
                            ItemStack stack = network.extractItem(new ItemStack(allowedItem), 1, Action.PERFORM);
                            if (stack.isEmpty()) continue;

                            runeTile.setGhostItem(false);
                            runeTile.setItem(0, stack);
                            world.sendBlockUpdated(pos, state, state, 2);
                            world.updateNeighbourForOutputSignal(pos, state.getBlock());
                            result.set(true);
                            break;
                        }
                    }
                },
                error -> player.displayClientMessage(error, false));
        });

        return result.get();
    }

    private static ItemStack getNetworkAccess(final Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (networkAccessItems.contains(stack.getItem())) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
