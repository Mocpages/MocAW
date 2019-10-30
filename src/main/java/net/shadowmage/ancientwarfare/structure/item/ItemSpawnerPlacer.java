/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ItemSpawnerPlacer extends Item implements IItemClickable
{

/**
 * @param itemID
 */
public ItemSpawnerPlacer(String itemName)
  {
  this.setUnlocalizedName(itemName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setTextureName("ancientwarfare:structure/"+itemName);
  }

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  list.add(StatCollector.translateToLocal("guistrings.selected_mob")+":");
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData"))
    {
    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
    String mobID = tag.getString("EntityId");
    if(mobID.equals(""))
      {
      mobID = "No Selection!!";
      }
    list.add(mobID);
    }  
  else
    {
    list.add(StatCollector.translateToLocal("guistrings.no_selection"));
    }
  list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("guistrings.spawner.warning_1"));
  list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("guistrings.spawner.warning_2"));
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  if(player==null || player.worldObj==null || player.worldObj.isRemote || stack==null)
    {
    return;
    }
  MovingObjectPosition mophit = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
  if(player.capabilities.isCreativeMode && player.isSneaking())
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER, 0, 0, 0);
    }
  else if(mophit!=null && mophit.typeOfHit == MovingObjectType.BLOCK)
    {   
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData"))
      {
      World world = player.worldObj;
      BlockPosition hit = new BlockPosition(mophit.blockX, mophit.blockY, mophit.blockZ);
      hit.offsetForMCSide(mophit.sideHit);
      world.setBlock(hit.x, hit.y, hit.z, Blocks.mob_spawner);
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
      TileEntityMobSpawner te = (TileEntityMobSpawner) world.getTileEntity(hit.x, hit.y, hit.z);
      tag.setInteger("x", hit.x);
      tag.setInteger("y", hit.y);
      tag.setInteger("z", hit.z);
      te.readFromNBT(tag); 

      if(!player.capabilities.isCreativeMode)
        {
        stack.stackSize--;
        if(stack.stackSize<=0)
          {
          player.inventory.mainInventory[player.inventory.currentItem]=null;
          }
        }
      }
    else
      {
      /**
       * TODO output chat message about missing NBT-data on item / no selection
       */
      }
    }
  else
    {
    /**
     * TODO output chat message about null hit/ w/e
     */
    }
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {  
  }



}
