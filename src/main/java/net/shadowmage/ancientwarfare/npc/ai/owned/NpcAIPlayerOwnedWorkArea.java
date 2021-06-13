package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemBuildingSettings;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkPriorityType;

public class NpcAIPlayerOwnedWorkArea extends NpcAI
{

public int ticksAtSite = 0;
public int workIndex;
ItemStack orderStack;
boolean init = false;
NpcWorker worker;
ItemBuildingSettings scanSettings = new ItemBuildingSettings();

public NpcAIPlayerOwnedWorkArea(NpcBase npc)
  {
  super(npc);
  if(!(npc instanceof NpcWorker))
    {
    throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
    }
  worker = (NpcWorker)npc;
  this.setMutexBits(MOVE+ATTACK);
  }

@Override
public boolean shouldExecute()
  {  
  if(!npc.getIsAIEnabled()){return false;}
  if(npc.getFoodRemaining()<=0 || npc.shouldBeAtHome()){return false;} 
  if(!init){
    orderStack = npc.ordersStack;
    init = true;
  }
  if(orderStack != null && orderStack.getItem() == AWNpcItemLoader.scanner){
	  scanSettings = ItemBuildingSettings.getSettingsFor(orderStack, scanSettings);
	  return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  { 
  if(!npc.getIsAIEnabled()){return false;}
  if(npc.getFoodRemaining()<=0 || npc.shouldBeAtHome()){return false;} 
  if(orderStack!=null && orderStack.getItem() == AWNpcItemLoader.scanner){
	  if(getFirstTarget() != null) {
		  return true;
	  }
    }
  return false;
  }

public BlockPosition getFirstTarget() {
	if(scanSettings.hasPos1() && scanSettings.hasPos2()) {
		int minX = Math.min(scanSettings.pos1().x, scanSettings.pos2().x);
		int maxX = Math.max(scanSettings.pos1().x, scanSettings.pos2().x);
		int minZ = Math.min(scanSettings.pos1().z, scanSettings.pos2().z);
		int maxZ = Math.max(scanSettings.pos1().z, scanSettings.pos2().z);
		int minY = Math.min(scanSettings.pos1().y, scanSettings.pos2().y);
		int maxY = Math.max(scanSettings.pos1().y, scanSettings.pos2().y);
		
		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				for(int y = maxY; y >= minY; y--) {
					Block b = npc.worldObj.getBlock(x, y, z);
					if(b.getMaterial()==Material.wood) {
						return new BlockPosition(x,y,z);
					}
				}
			}
		}
	}
	return null;
}

@Override
public void updateTask()
  {
  BlockPosition pos = getFirstTarget();
  if(pos == null) {return;}
  double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
//  AWLog.logDebug("distance to site: "+dist);
  if(dist > 5.d*5.d)
    {
//    AWLog.logDebug("moving to worksite..."+pos);
    npc.addAITask(TASK_MOVE);
    ticksAtSite=0;
    moveToPosition(pos, dist);
    }
  else
    {
//    AWLog.logDebug("working at site....."+pos);
    npc.getNavigator().clearPathEntity();
    npc.removeAITask(TASK_MOVE);
    workAtSite();
    }
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_WORK);
  }

protected void workAtSite(){
  ticksAtSite++;
  if(npc.ticksExisted%10==0){npc.swingItem();}
  if(ticksAtSite>=AWNPCStatics.npcWorkTicks){
    ticksAtSite=0;
    BlockPosition pos = getFirstTarget();
    InventoryTools.dropItemInWorld(npc.worldObj, new ItemStack(Item.getItemFromBlock(npc.worldObj.getBlock(pos.x, pos.y, pos.z)), 1), pos.x, pos.y, pos.z);
    npc.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.air);
    }  
  }

public void onOrdersChanged(){
  orderStack = npc.ordersStack;
  workIndex = 0;
  ticksAtSite = 0;
  }

@Override
public void resetTask()
  {
  ticksAtSite = 0;
  this.npc.removeAITask(TASK_WORK + TASK_MOVE);
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticksAtSite = tag.getInteger("ticksAtSite");
  workIndex = tag.getInteger("workIndex");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticksAtSite", ticksAtSite);
  tag.setInteger("workIndex", workIndex);
  return tag;
  }


}
