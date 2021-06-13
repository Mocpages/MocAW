package net.shadowmage.ancientwarfare.npc.entity;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.economy.BankAccount;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemCocaine;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public abstract class NpcPlayerOwned extends NpcBase
{

private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;

protected NpcAIPlayerOwnedRideHorse horseAI;
public double money = 100.0;
public double pocketMoney = 0.0;

private BlockPosition townHallPosition;
private BlockPosition upkeepAutoBlock;
public double militancy = 0;
public double consciousness = 0;
public double literacy = 0;

public int cocaineTimer = 0;

public InventoryBackpack inventory = new InventoryBackpack(27);

public NpcPlayerOwned(World par1World){
  super(par1World);
  }

@Override
public void setCurrentItemOrArmor(int slot, ItemStack stack)
  {
  super.setCurrentItemOrArmor(slot, stack);
  if(slot==0){onWeaponInventoryChanged();}
  }

@Override
public void onDeath(DamageSource source)
  {
  if(!worldObj.isRemote)
    {
    if(horseAI!=null)
      {
      horseAI.onKilled();
      }
    validateTownHallPosition();
    TileTownHall townHall = getTownHall();
    if(townHall!=null)
      {
      townHall.handleNpcDeath(this, source);
      }
    }  
  super.onDeath(source);  
  }
public MocFaction getFac() {
	  return ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,worldObj, MocData.class)).getFaction(this.getTeam().getRegisteredName());
}

public double getBudget(BankAccount acct) {
	double budget = pocketMoney; //This is our total income for the round.
	MocFaction fac = getFac();
	double goalSavings = fac.nationalBank.getInterest() * 100 * pocketMoney; //So if interest is 1% = 0.01, goal savings are one month's income, etc.
	double actualSavings = acct.getBalance();
	
	if(goalSavings < actualSavings) { //We have less than our goal savings
		budget *= 0.5; //Spend half of our money, save half.
		pocketMoney -= budget;
		acct.deposit(pocketMoney);
	}else if(goalSavings > actualSavings) {//We have surplus savings
		double surplus = actualSavings - goalSavings; //How much extra money we have in the bank.
		surplus *= 0.2; //Only spend 20% of it at once.
		surplus = acct.withdraw(surplus);
		budget += surplus;
	}else {//We have exactly the right amount of savings
		//We don't actually need to do anything here.
	}
	return budget;
}

public void buySubsistenceNeeds(MocFaction fac, BankAccount acct) {
	double price = fac.itemMarket.getPrice(Items.bread);
	if(price < 0) {return;} //A negative price means that there is no food for sale
	if(price < this.pocketMoney) { //We can afford food without resorting to savings
		pocketMoney -= price;
		if(fac.itemMarket.buy(Items.bread, price)) {
			//We have bought our bread
		}
	}else { //We need to dip into our savings
		double amtToWithdraw = price - pocketMoney; //How much we need to withdraw from bank to buy food
		if(amtToWithdraw > acct.getBalance()) { //We can't afford bread, even WITH our savings!
			
		}else {
			amtToWithdraw = acct.withdraw(amtToWithdraw);
			if(amtToWithdraw + pocketMoney >= price) { //Final check in case bank ran out of capital
				if(fac.itemMarket.buy(Items.bread, price)) {
					//We have bought our bread
				}
			}
		}
	}
}

public void buyNeeds() {
	//MocFaction fac = getFac();
	//BankAccount acct = fac.nationalBank.getAccountForNPC(this);
	//buySubsistenceNeeds(fac, acct);
	//double budget = getBudget(acct);
}

@Override
public int getArmorValueOverride()
  {
  return -1;
  }

@Override
public int getAttackDamageOverride()
  {
  return -1;
  }

@Override
public void setTownHallPosition(BlockPosition pos)
  {
  if(pos!=null){this.townHallPosition = pos.copy();}
  else{this.townHallPosition=null;}
  }

@Override
public BlockPosition getTownHallPosition()
  {
  return townHallPosition;
  }

@Override
public TileTownHall getTownHall()
  {
  if(getTownHallPosition()!=null)    
    {
    BlockPosition pos = getTownHallPosition();
    TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileTownHall)
      {
      return (TileTownHall)te;
      }
    }
  return null;
  }

@Override
public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position)
  {
  validateTownHallPosition();
  BlockPosition pos = getTownHallPosition();
  if(pos!=null)
    {    
    double curDist = getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    double newDist = getDistanceSq(position.x+0.5d, position.y, position.z+0.5d);
    if(newDist<curDist)
      {
      setTownHallPosition(position);
      if(upkeepAutoBlock==null || upkeepAutoBlock.equals(pos))
        {
        upkeepAutoBlock=position;
        }
      }
    }
  else
    {
    setTownHallPosition(position);
    if(upkeepAutoBlock==null || upkeepAutoBlock.equals(pos))
      {
      upkeepAutoBlock=position;
      }
    }
  }



private boolean validateTownHallPosition()
  {
  if(getTownHallPosition()==null){return false;}  
  BlockPosition pos = getTownHallPosition();
  if(!worldObj.blockExists(pos.x, pos.y, pos.z)){return true;}//cannot validate, unloaded...assume good 
  TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
  if(te instanceof TileTownHall)
    {
    if(canBeCommandedBy(((TileTownHall) te).getOwnerName()))
      {
      return true;
      }
    }
  setTownHallPosition(null);
  return false;
  }

@Override
public Command getCurrentCommand()
  {
  return playerIssuedCommand;
  }

@Override
public void handlePlayerCommand(Command cmd)
  {  
  if(cmd!=null && cmd.type==CommandType.ATTACK)
    {
    Entity e = cmd.getEntityTarget(worldObj);
    AWLog.logDebug("handling attack command : "+e);
    if(e instanceof EntityLivingBase)
      {
      EntityLivingBase elb = (EntityLivingBase)e;
      if(isHostileTowards(elb))//only allow targets npc is hostile towards
        {
        if(elb instanceof NpcPlayerOwned)//if target is also a player-owned npc
          {
          NpcPlayerOwned n = (NpcPlayerOwned)elb;
          if(n.getTeam()!=getTeam())//only allow target if they are not on the same team (non-teamed cannot attack other non-teamed, cannot attack team-mates npcs either)
            {
            setAttackTarget(n);
            }
          }
        else
          {
          setAttackTarget(elb);        
          }      
        }
      }
    cmd=null;
    }
  this.setPlayerCommand(cmd); 
  }

@Override
public void setPlayerCommand(Command cmd)
  {
  this.playerIssuedCommand = cmd;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    Team t = npc.getTeam();
    return t!=getTeam();
    }
  else if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return npc.isHostileTowards(this);//cheap trick to determine if should be hostile or not using the faction-based npcs standing towards this players npcs...handled in NpcFaction
    }
  else if(e instanceof EntityPlayer)
    {
    Team t = worldObj.getScoreboard().getPlayersTeam(e.getCommandSenderName());
    return t!=getTeam();
    }
  else
    {
    String n = EntityList.getEntityString(e);
    List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
    if(targets.contains(n))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean canTarget(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    Team t = npc.getTeam();
    return t!=getTeam();//do not allow npcs to target their own teams npcs
    }
  else if (e instanceof EntityPlayer)
    {
    Team t = worldObj.getScoreboard().getPlayersTeam(e.getCommandSenderName());
    return t!=getTeam();//do not allow npcs to target their own teams players
    }
  return e instanceof EntityLivingBase;
  }

@Override
public boolean canBeAttackedBy(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    return npc.getTeam()!=getTeam();//can only be attacked by non-same team -- disable friendly fire and combat amongst neutrals
    }
  return true;
  }

protected boolean isHostileTowards(Team team)
  {
  Team a = getTeam();
  if(a!=null && team!=null && a!=team){return true;}
  return false;
  }

@Override
public void onWeaponInventoryChanged(){
  updateTexture();
  

  }

@Override
public int getFoodRemaining()
  {
  return foodValueRemaining;
  }

@Override
public void setFoodRemaining(int food)
  {
  this.foodValueRemaining = food;
  }

@Override
public BlockPosition getUpkeepPoint()
  {  
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepPosition();
    }
  return upkeepAutoBlock;
  }

@Override
public void setUpkeepAutoPosition(BlockPosition pos)
  {
  upkeepAutoBlock = pos;
  }

@Override
public int getUpkeepBlockSide()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepBlockSide();
    }
  return 0;
  }

@Override
public int getUpkeepDimensionId()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepDimension();
    }
  return worldObj.provider.dimensionId;
  }

@Override
public int getUpkeepAmount()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepAmount();
    }
  return AWNPCStatics.npcDefaultUpkeepWithdraw;
  }

@Override
public boolean requiresUpkeep()
  {
  return true;
  }

@Override
protected boolean interact(EntityPlayer player){
  if(player.worldObj.isRemote){return false;}
  Team t = player.getTeam();
  Team t1 = getTeam();
  
  if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemCocaine && cocaineTimer <= 0) {
	  player.getHeldItem().stackSize --;
	  EntityItem ei = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, new ItemStack(AWNpcItemLoader.money));
	  player.worldObj.spawnEntityInWorld(ei);

	  cocaineTimer = 20 * 60 * 25; // 25 minutes
	  return true;
  }
  
  boolean baton = player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
  if(t==t1 && this.canBeCommandedBy(player.getCommandSenderName()) && !baton)
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
    return true;
    }
  return true;
  }

public boolean withdrawFood(IInventory inventory, int side)
  {
  int amount = getUpkeepAmount() - getFoodRemaining();
  if(amount<=0){return true;}
  ItemStack stack;
  int val;
  int eaten = 0;
  if(side>=0 && inventory instanceof ISidedInventory)
    {
    int[] ind = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
    for(int i : ind)
      {
      stack = inventory.getStackInSlot(i);
      val = AncientWarfareNPC.statics.getFoodValue(stack);
      if(val<=0){continue;}
      while(eaten < amount && stack.stackSize>0)
        {
        eaten+=val;
        stack.stackSize--;
        inventory.markDirty();
        }
      if(stack.stackSize<=0)
        {
        inventory.setInventorySlotContents(i, null);
        }
      }    
    }
  else
    {
    for(int i = 0 ; i<inventory.getSizeInventory();i++)
      {
      stack = inventory.getStackInSlot(i);
      val = AncientWarfareNPC.statics.getFoodValue(stack);
      if(val<=0){continue;}
      while(eaten < amount && stack.stackSize>0)
        {
        eaten+=val;
        stack.stackSize--;
        inventory.markDirty();
        }
      if(stack.stackSize<=0)
        {
        inventory.setInventorySlotContents(i, null);
        }
      }    
    }
  setFoodRemaining(getFoodRemaining()+eaten);
  return getFoodRemaining()>=getUpkeepAmount();
  }

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);  
  }
private static DecimalFormat df = new DecimalFormat("0.00");

@Override
public void onLivingUpdate()
  {  
  super.onLivingUpdate();

/*  if(ordersStack != null) {
	  NBTTagCompound a = this.ordersStack.getTagCompound();
	  if(a == null) { a = new NBTTagCompound();}
	  if(!a.hasKey("Mocc")) {
		  a.setString("Mocc", "KYS");
	  }
	  ordersStack.setTagCompound(a);*/
  //}
  
	/*
	 * if(worldObj.getWorldTime() % 40 == 0) { literacy -= 0.00001; try { MocFaction
	 * f = ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,worldObj,
	 * MocData.class)).getFaction(this.getTeam().getRegisteredName());
	 * f.addNpc(this);
	 * 
	 * }catch(Exception e) {} buyNeeds(); }
	 */
  if(foodValueRemaining>0){foodValueRemaining--;}
  if(this.cocaineTimer>0){cocaineTimer--;}

  }

public String getIdealogy() {
	return null;
}

@Override
public void travelToDimension(int par1)
  {
  this.townHallPosition=null;
  this.upkeepAutoBlock=null;
  super.travelToDimension(par1);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  if(tag.hasKey("command")){playerIssuedCommand = new Command(tag.getCompoundTag("command"));} 
  if(tag.hasKey("townHall")){townHallPosition = new BlockPosition(tag.getCompoundTag("townHall"));}
  if(tag.hasKey("upkeepPos")){upkeepAutoBlock = new BlockPosition(tag.getCompoundTag("upkeepPos"));}
  if(tag.hasKey("cocaine")){this.cocaineTimer = tag.getInteger("cocaine");}

  onWeaponInventoryChanged();
  //if(tag.hasKey("money")) {money = tag.getDouble("money");}
  //if(tag.hasKey("mil")) {militancy = tag.getDouble("mil");}
  //if(tag.hasKey("con")) {consciousness = tag.getDouble("con");}
  //if(tag.hasKey("literacy")) {literacy = tag.getDouble("literacy");}
  //if(tag.hasKey("inv")) {InventoryTools.readInventoryFromNBT(inventory, tag.getCompoundTag("inv"));}
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  if(playerIssuedCommand!=null){tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));}
  if(townHallPosition!=null){tag.setTag("townHall", townHallPosition.writeToNBT(new NBTTagCompound()));}
  if(upkeepAutoBlock!=null){tag.setTag("upkeepPos", upkeepAutoBlock.writeToNBT(new NBTTagCompound()));}
  tag.setInteger("cocaine", cocaineTimer);
  //tag.setDouble("money", this.money);
  //tag.setDouble("mil", militancy);
  //tag.setDouble("con", consciousness);
  //tag.setDouble("literacy", literacy);
  //tag.setTag("inv", InventoryTools.writeInventoryToNBT(inventory, new NBTTagCompound()));
  }

}
