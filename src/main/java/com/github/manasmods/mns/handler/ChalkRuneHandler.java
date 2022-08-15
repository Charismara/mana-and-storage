package com.github.manasmods.mns.handler;

import com.github.manasmods.mns.ManaAndStorage;
import com.mna.api.rituals.IRitualReagent;
import com.mna.blocks.BlockInit;
import com.mna.blocks.ritual.ChalkRuneBlock;
import com.mna.blocks.tileentities.ChalkRuneTile;
import com.mna.entities.rituals.EntityRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = ManaAndStorage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChalkRuneHandler {
    private static boolean refinedStorage = false;
    private static boolean refinedStorageAddons = false;

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getWorld().isClientSide()) return;
        if (!e.getItemStack().isEmpty()) return;

        final BlockState state = e.getWorld().getBlockState(e.getPos());
        if (!state.is(BlockInit.CHALK_RUNE.get())) return;

        ChalkRuneBlock block = (ChalkRuneBlock) state.getBlock();
        BlockEntity blockEntity = e.getWorld().getBlockEntity(e.getPos());
        if (blockEntity == null) return;
        if (!(blockEntity instanceof ChalkRuneTile runeTile)) return;

        boolean escape = !runeTile.getDisplayedItem().isEmpty();
        e.setCancellationResult(block.use(state, e.getWorld(), e.getPos(), e.getPlayer(), e.getHand(), e.getHitVec()));
        e.setCanceled(true);
        if (escape) return;

        IRitualReagent ritualReagent = getReagentFromNearbyRitual(e.getWorld(), e.getPos());
        if (ritualReagent == null) return;

        if (refinedStorage) {
            if (RefinedStorageHandler.check(e.getPlayer(), e.getWorld(), e.getPos(), state, runeTile, ritualReagent.getResourceLocation())) {
                return;
            }
        }

        if (refinedStorageAddons) {
            if (RefinedStorageAddonsHandler.check(e.getPlayer(), e.getWorld(), e.getPos(), state, runeTile, ritualReagent.getResourceLocation())) {
                return;
            }
        }
    }

    @Nullable
    private static IRitualReagent getReagentFromNearbyRitual(Level world, BlockPos pos) {
        AABB bb = (new AABB(pos)).inflate(5.0);
        List<EntityRitual> rituals = world.getEntitiesOfClass(EntityRitual.class, bb);
        return rituals != null && rituals.size() == 1 ? rituals.get(0).getReagentForPosition(pos) : null;
    }

    public static void enableRSAddon() {
        refinedStorage = true;
    }

    public static void enableRSAddonsAddon() {
        refinedStorageAddons = true;
    }
}
