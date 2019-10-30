package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedTrader;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;

public class NpcTrader extends NpcPlayerOwned
{

public EntityPlayer trader;//used by guis/containers to prevent further interaction
private List<POTrade> tradeList = new ArrayList<POTrade>();
private static List<NpcTrader> traderList = new ArrayList<NpcTrader>();
private NpcAIPlayerOwnedTrader tradeAI;
public InventoryBackpack backpackInventory;

public NpcTrader(World par1World)
  {
  super(par1World);
  
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIPlayerOwnedRideHorse(this)));
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
  this.tasks.addTask(3, new NpcAIFleeHostiles(this));
  this.tasks.addTask(4, tradeAI = new NpcAIPlayerOwnedTrader(this));
  this.tasks.addTask(5, new NpcAIPlayerOwnedGetFood(this));  
  this.tasks.addTask(6, new NpcAIPlayerOwnedIdleWhenHungry(this)); 
  this.tasks.addTask(7, new NpcAIMoveHome(this, 50.f, 3.f, 30.f, 3.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  
  this.traderList.add(this);
  }

@Override
public void onDeath(DamageSource source) {
	super.onDeath(source);
}

public static NpcTrader getCheapestFoodVendor(NpcBase n) {
	NpcTrader trade = null;
	for(NpcTrader t : traderList) {
		trade = t;
	}
	return trade;
}

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return stack!=null && stack.getItem() instanceof ItemTradeOrder;
  }

@Override
public void onOrdersInventoryChanged()
  {
  tradeList=null;
  ItemStack order = ordersStack;
  if(order!=null && order.getItem() instanceof ItemTradeOrder)
    {
    tradeList = TradeOrder.getTradeOrder(order).getTradeList();
    }
  tradeAI.onOrdersUpdated();
  }

@Override
public String getNpcSubType()
  {
  return "";
  }

@Override
public String getNpcType()
  {
  return "trader";
  }

@Override
public void onWeaponInventoryChanged()
  {
  super.onWeaponInventoryChanged();
  if(getEquipmentInSlot(0)!=null && getEquipmentInSlot(0).getItem()==AWItems.backpack)
    {
    backpackInventory = ItemBackpack.getInventoryFor(getEquipmentInSlot(0));
    }
  else
    {
    backpackInventory=null;
    }
  }

@Override
protected boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote){return true;}
  boolean baton = player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
  if(baton){return true;}  
  if(player.getCommandSenderName().equals(getOwnerName()))//owner
    {
    if(player.isSneaking())
      {
      if(this.followingPlayerName==null)
        {
        this.followingPlayerName = player.getCommandSenderName();    
        }
      else if(this.followingPlayerName.equals(player.getCommandSenderName()))
        {
        this.followingPlayerName = null;
        }
      else
        {
        this.followingPlayerName = player.getCommandSenderName();      
        }
      }
    else
      {
      openGUI(player);  
      }    
    }
  else//non-owner
    {
    if(!player.worldObj.isRemote && getFoodRemaining()>0 && trader==null)
      {
      trader=player;    
      openAltGui(player);
      }
    }
  return true;   
  }

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);  
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, getEntityId(), 0, 0);  
  }

@Override
public boolean hasAltGui()
  {
  return true;
  }

@Override
public boolean shouldBeAtHome()
  {
  if((!worldObj.provider.hasNoSky && !worldObj.provider.isDaytime()) || worldObj.isRaining())
    { 
    return true;
    }
  return false;
  }

@Override
public void onLivingUpdate()
  {  
  super.onLivingUpdate();
  super.setFoodRemaining(10);
 }

@Override
public boolean isHostileTowards(Entity e)
  {
  return false;
  }

public POTradeList getTradeList()
  {
	return null;
 // return tradeList;
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setTag("tradeAI", tradeAI.writeToNBT(new NBTTagCompound()));
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  onOrdersInventoryChanged();
  tradeAI.readFromNBT(tag.getCompoundTag("tradeAI"));
  }

}
