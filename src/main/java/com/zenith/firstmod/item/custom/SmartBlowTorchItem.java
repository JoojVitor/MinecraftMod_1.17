package com.zenith.firstmod.item.custom;

import com.google.common.collect.ImmutableMap;
import com.zenith.firstmod.block.ModBlocks;
import com.zenith.firstmod.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class SmartBlowTorchItem extends Item {
    private static final Map<Block, Item> BLOW_TORCH_ITEM_CRAFT =
            new ImmutableMap.Builder<Block, Item>()
                    .put(
                            ModBlocks.TITANIUM_BLOCK.get(),
                            ModItems.TITANIUM_NUGGET.get()
                    )
                    .put(
                            Blocks.SAND,
                            Blocks.GLASS.asItem()
                    )
                    .build();

    public SmartBlowTorchItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) {
            Level level = pContext.getLevel();
            BlockPos positionClicked = pContext.getClickedPos();
            Block blockClicked = level.getBlockState(positionClicked).getBlock();

            if(canBlowTorch(blockClicked)) {
                ItemEntity itemEntity = new ItemEntity(
                        level, positionClicked.getX(), positionClicked.getY(), positionClicked.getZ(),
                        new ItemStack(BLOW_TORCH_ITEM_CRAFT.get(blockClicked), 1));

                level.destroyBlock(positionClicked, false);
                level.addFreshEntity(itemEntity);
                pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), p -> {
                    p.broadcastBreakEvent(pContext.getHand());
                });
            } else {
                pContext.getPlayer().sendMessage(new TextComponent("Cannot blow torch this block!"),
                        Util.NIL_UUID);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private boolean canBlowTorch(Block block) {
        return BLOW_TORCH_ITEM_CRAFT.containsKey(block);
    }
}
