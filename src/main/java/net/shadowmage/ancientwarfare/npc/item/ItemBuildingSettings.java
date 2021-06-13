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
package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ItemBuildingSettings
{

boolean[] setKeys = new boolean[4];
BlockPosition pos1;
BlockPosition pos2;

public ItemBuildingSettings()
  {
  pos1 = new BlockPosition();
  pos2 = new BlockPosition();
  }

/**
 * @param item
 * @return
 */
public static ItemBuildingSettings getSettingsFor(ItemStack stack, ItemBuildingSettings settings)
  {
  NBTTagCompound tag;
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("structData"))
    {
    tag = stack.getTagCompound().getCompoundTag("structData");
    }
  else
    {
    tag = new NBTTagCompound();
    }
  for(int i = 0; i < 4; i++)
    {
    settings.setKeys[i] = false;
    }
  if(tag.hasKey("pos1"))
    {
    settings.pos1.read(tag.getCompoundTag("pos1"));
    settings.setKeys[0] = true;
    }
  if(tag.hasKey("pos2"))      
    {
    settings.pos2.read(tag.getCompoundTag("pos2"));
    settings.setKeys[1] = true;
    }


  return settings;
  }

public static void setSettingsFor(ItemStack item, ItemBuildingSettings settings)
  {
  NBTTagCompound tag = new NBTTagCompound();  
  if(settings.setKeys[0])
    {
    NBTTagCompound tag1 = new NBTTagCompound();
    settings.pos1.writeToNBT(tag1);
    tag.setTag("pos1", tag1);
    }  
  if(settings.setKeys[1])
    {
    NBTTagCompound tag1 = new NBTTagCompound();
    settings.pos2.writeToNBT(tag1);
    tag.setTag("pos2", tag1);
    }

  item.setTagInfo("structData", tag);
  }

public void setPos1(int x, int y, int z)
  {
  pos1.reassign(x, y, z);
  setKeys[0] = true;
  }

public void setPos2(int x, int y, int z)
  {
  pos2.reassign(x, y, z);
  setKeys[1] = true;
  }


public boolean hasPos1()
  {
  return setKeys[0];
  }

public boolean hasPos2()
  {
  return setKeys[1];
  }

public boolean hasBuildKey()
  {
  return setKeys[2];
  }

public boolean hasName()
  {
  return setKeys[3];
  }

public BlockPosition pos1()
  {
  return pos1;
  }

public BlockPosition pos2()
  {
  return pos2;
  }




public boolean isContained(World worldObj) {
	int minX = Math.min(pos1.x, pos2.x);
	int maxX = Math.max(pos1.x, pos2.x);
	int minZ = Math.min(pos1.z, pos2.z);
	int maxZ = Math.max(pos1.z, pos2.z);
	int minY = Math.min(pos1.y, pos2.y);
	int maxY = Math.max(pos1.y, pos2.y);
	for(int x = minX; x <= maxX; x++) {
		for(int z = minZ; z <= maxZ; z++) {
			for(int y = minY; y <= maxY; y++) {
				if(y == maxY) {//If we're in the top tier, check the block above us
					if(worldObj.getBlock(x, y+1, z) == Blocks.air) { return false;}
				}
				if(y == minY) {//If we're in the bottom then check the floor
					if(worldObj.getBlock(x, y-1, z) == Blocks.air) { return false;}
				}
				
				if(x == maxX) {//Check x+1 block
					if(worldObj.getBlock(x+1, y, z) == Blocks.air) { return false;}
				}
				if(x == minX) {//Check x-1 block
					if(worldObj.getBlock(x-1, y, z) == Blocks.air) { return false;}
				}
				
				if(z == maxZ) {//Check z+1
					if(worldObj.getBlock(x, y, z+1) == Blocks.air) { return false;}
				}
				if(z == minZ) {//Check z-1
					if(worldObj.getBlock(x, y, z-1) == Blocks.air) { return false;}
				}
			}
		}
	}
	return true;
}


public void clearSettings()
  {
  for(int i = 0; i<3; i++)
    {
    this.setKeys[i] = false;
    }
  }

}
