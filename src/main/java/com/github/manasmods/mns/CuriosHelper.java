package com.github.manasmods.mns;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.Optional;

public class CuriosHelper {
    public static ItemStack getNetworkAccess(List<Item> networkAccessItems, Player player) {
        return networkAccessItems
            .stream()
            .map(item -> CuriosApi.getCuriosHelper().findFirstCurio(player, item))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .map(SlotResult::stack)
            .orElse(ItemStack.EMPTY);
    }
}
