package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.WorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;

public class ItemBlockWorksiteAutoCrafting extends ItemBlock
{

public ItemBlockWorksiteAutoCrafting(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  metadata = BlockRotationHandler.getMetaForPlacement(player, (IRotatableBlock) field_150939_a, side);
  if(super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
    {
    WorksiteAutoCrafting te = (WorksiteAutoCrafting) world.getTileEntity(x, y, z);
    te.setOwningPlayer(player.getCommandSenderName());
    return true;
    }
  return false;
  }
}
