package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.tile.TileBuilding;

public class ContainerBuildingSetup extends ContainerBase{

public int x, y, z;
public BlockPosition min, max;
public TileBuilding building;

public ContainerBuildingSetup(EntityPlayer player, int x, int y, int z){
  super(player, x, y, z);
  this.x = x;
  this.y = y;
  this.z = z;
  TileEntity te = player.worldObj.getTileEntity(x, y, z);
  building = (TileBuilding)te;
  min = building.pos1.copy();
 // System.out.println("Building pos 1 x " + min.x);
  max = building.pos2.copy();  
  }

@Override
public void sendInitData(){
  if(building instanceof TileBuilding){
    NBTTagCompound tag = new NBTTagCompound();
    tag.setTag("pos1", min.writeToNBT(new NBTTagCompound()));
    tag.setTag("pos2", max.writeToNBT(new NBTTagCompound()));
    //tag.setByteArray("checkedMap", twub.getTargetMap());
    sendDataToGui(tag);
    }
}

@Override
public void handlePacketData(NBTTagCompound tag){
  if(tag.hasKey("guiClosed")){    
    if(tag.hasKey("min") && tag.hasKey("max")){
      BlockPosition min = new BlockPosition(tag.getCompoundTag("min"));
      BlockPosition max = new BlockPosition(tag.getCompoundTag("max"));
      //System.out.println("Handling packet data " + max.x);
      building.pos1 = min;
      building.pos2 = max;
      
      //building.setWorkBoundsMin(min);
      //building.setWorkBoundsMax(max);
      //building.onBoundsAdjusted();
      //building.onPostBoundsAdjusted();
      }

    player.worldObj.markBlockForUpdate(x, y, z);
    }
  }

}
