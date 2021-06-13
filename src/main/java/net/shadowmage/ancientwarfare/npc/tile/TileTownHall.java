package net.shadowmage.ancientwarfare.npc.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

public class TileTownHall extends TileEntity implements IOwnable, IInventory, IInteractableTile
{

private String ownerName = "";
private int broadcastRange = 80;//TODO set from config and/or gui?
private int updateDelayTicks = 0;
private int updateDelayMaxTicks = 20*5;//5 second broadcast frequency  TODO set from config
public String name = "DEFAULT";
private List<NpcDeathEntry> deathNotices = new ArrayList<TileTownHall.NpcDeathEntry>();
private List<EntityPlayer> playersWaiting = new ArrayList<EntityPlayer>();
private InventoryBasic inventory = new InventoryBasic(27);
private List<ContainerTownHall> viewers = new ArrayList<ContainerTownHall>();
public int attackTimer =  Integer.MAX_VALUE;
public int captureTimer = -1;
public String team = "";
NBTTagCompound players = new NBTTagCompound();

@Override
public boolean canUpdate()
  {
  return true;
  }

public boolean canRespawn(EntityPlayer player) {
	//TODO check teams match?
	//TODO check that this player hasn't died too much lol
	int lives = 0;
	if(this.players.hasKey(player.getDisplayName())) {
		lives = this.players.getInteger(player.getDisplayName());
	}
	//print(player, name + ": Respawning: " + (lives<=4), EnumChatFormatting.WHITE);
	if(!inBattle()) {
		if(isOnSameTeam(player)) {
			this.players.setInteger(player.getDisplayName(), 0);
			return true;
		}{return false;}
	}
	return lives <= 4;
}

public boolean inBattle() {
	return this.attackTimer <= 20 * 60 * 15;
}

public boolean inActiveBattle() {
	return this.attackTimer <=0;
}

public boolean isOnSameTeam(EntityPlayer player) {
	try {
		return player.isOnSameTeam(worldObj.getPlayerEntityByName(getOwnerName()));
		
	}catch(Exception e) {
		return false;
	}
}

public void emptyInventory(EntityPlayer player) {
	InventoryTools.dropInventoryInWorld(player.worldObj, player.inventory, player.posX, player.posY, player.posZ);
	for(int i = 0; i<player.inventory.getSizeInventory(); i++) {
		player.inventory.setInventorySlotContents(i, null);
	}
}
public void respawn(EntityPlayer player) {
	//player.setPosition(xCoord, yCoord + 1, zCoord);
	this.playersWaiting.add(player);
	int lives = 0;
	if(this.players.hasKey(player.getDisplayName())) {
		lives = this.players.getInteger(player.getDisplayName());
	}
	if(this.attackTimer >= 110000) {
		this.players.setInteger(player.getDisplayName(), 0);
		return;
	}
	this.players.setInteger(player.getDisplayName(), lives+1);
	print(player, "Lives remaining: " + (5-lives), EnumChatFormatting.RED);
	//player.setSpawnChunk(new ChunkCoordinates(xC), (Boolean) null);
	
	//TODO inc death counter
}

public Team getTeam() {
	return this.worldObj.getScoreboard().getTeam(this.team);
}

public boolean isOnTeam(EntityPlayer p) {
	try {
		return p.getTeam().isSameTeam(getTeam());
	}catch(Exception e) {
		return false;
	}
}

private void spawnPerimeter(EntityPlayer p) {
	double angle = Math.random() * Math.PI * 2; //random radian angle
	double[] xz = getRelOffset(xCoord, zCoord, angle, 100, 0);
	double y = p.worldObj.getTopSolidOrLiquidBlock((int)xz[0], (int)xz[1]);
	teleport(p, xz[0], y, xz[1]);
}

public static double[] getRelOffset(double x, double z, double angle, double offX, double offZ) {
	angle = Math.toRadians(angle);
	double xPrime = x * Math.cos(angle) + z * Math.sin(angle);
	double zPrime = -x  * Math.sin(angle) + z * Math.cos(angle);
	
	zPrime += offZ;
	xPrime += offX;

	double x2 = xPrime * Math.cos(angle) - zPrime * Math.sin(angle);
	double z2 = xPrime  * Math.sin(angle) + zPrime * Math.cos(angle);
	
	return new double[] {x2,z2};
}

private static void teleport(EntityPlayer p, double x, double y, double z) {
	String[] args = { Double.toString(x), Double.toString(y), Double.toString(z) };
	(new CommandTeleport()).processCommand(p, args);
}

private void updateRespawns() {
	List<EntityPlayer> successfulRespawns = new ArrayList<EntityPlayer>();
	for(EntityPlayer p : this.playersWaiting) {
		  if(!p.isDead) {
			  if(isOnTeam(p) && this.captureTimer == -1) {
				  teleport(p, xCoord, yCoord+1, zCoord); //Teleport him to OUR location
				  successfulRespawns.add(p);
			  }else { //They're NOT on our team...
				  spawnPerimeter(p); //So respawn them on the PERIMETER of the base.
				  successfulRespawns.add(p);
			  }
		  }
	  }
	
	for(EntityPlayer p : successfulRespawns) {
		if(this.getDistanceFrom(p.posX, p.posY, p.posZ) <= 9) {
			playersWaiting.remove(p);
		}
	}
}

public void printToAll(String s, EnumChatFormatting color) {
	for(Object o : this.worldObj.playerEntities) {
		if(o instanceof EntityPlayer) {
			print((EntityPlayer)o, s, color);
		}
	}
}

public static void print(EntityPlayer player, String message, EnumChatFormatting color) {
	ChatComponentText text = new ChatComponentText(message);
	text.getChatStyle().setColor(color);
	player.addChatComponentMessage(text);
}

public void beginBattle() {
	printToAll("[" + this.name+"]: WARNING: BATTLE BEGINNING IN 15 MINUTES", EnumChatFormatting.GREEN);
	attackTimer = 20 * 60 * 15;
}

private void updateBattle() {
	attackTimer--;

	printBattleStatus();
	try {
		updateCapture();
	}catch(Exception e) {}
	
	if(attackTimer <= -20 * 60 * 30) {
		resetBattle();
	}
}

public void updateCapture() {
	int range = 10;
	List<EntityPlayer> list = getPlayers();
	
	boolean hasEny = false;
	boolean hasFren = false;
	for(EntityPlayer player : list) {
		if(player.isOnSameTeam(worldObj.getPlayerEntityByName(this.getOwnerName()))) { //This is a FRIENDLY player
			if(this.captureTimer > 0) { //If capturing is in progress...
			//	this.captureTimer = -1; //reset capture
				hasFren = true; //There is a friendly entity within range
			}
		}else if(this.attackTimer <= 0){ //Otherwise, this is a hostile player. So, if we're in a battle...
			if(this.captureTimer < 0) { //If our timer hasn't started, we gotta set it up.
				this.captureTimer = 20 * 60; //Default time is one minute
			}else { //Our timer has already begun
				hasEny = true; //So we just set the "found enemy" flag.
			}
		}
	}
	
	
	
	if(hasEny && !hasFren) { //If at LEAST one enemy player is in range, and NO friendly players are in range...
		this.captureTimer--;
		
		printCaptureStatus();
	}
	if(hasFren && !hasEny && captureTimer != -1) {
		this.captureTimer = -1;
		printToAll("[" + this.name+"]: Capture ended.", EnumChatFormatting.GREEN);
	}
	
	if(this.captureTimer == 0) {
		printToAll("[" + this.name+"]: Base captured!", EnumChatFormatting.DARK_RED);
		try {
			this.setOwnerName(getHostileName());
			this.captureTimer = -1;
		}catch(Exception e) {}
	}
}

public String getHostileName() {
	for(EntityPlayer p : getPlayers()) {
		if(!p.isOnSameTeam(worldObj.getPlayerEntityByName(this.getOwnerName()))) {
			return p.getCommandSenderName();
		}
	}
	return null;
}

public static void attackBase(String name, World worldObj) {
	for(Object e : worldObj.loadedTileEntityList) { //For every tile entity in the world...
		if(e instanceof TileTownHall) { //if that tile entity is a town hall...
			TileTownHall th = (TileTownHall)e; 
			if(th.name.equalsIgnoreCase(name)) {
				th.beginBattle();
				return;
			}
		}
	}
}
public static void attackBaseAdmin(String name, World worldObj) {
	for(Object e : worldObj.loadedTileEntityList) { //For every tile entity in the world...
		if(e instanceof TileTownHall) { //if that tile entity is a town hall...
			TileTownHall th = (TileTownHall)e; 
			if(th.name.equalsIgnoreCase(name)) {
				th.beginBattle();
				th.attackTimer = 30;
				return;
			}
		}
	}
}

private List<EntityPlayer> getPlayers(){
	double range = 10;
	AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord-range, yCoord-range/2, zCoord-range, xCoord+range+1, yCoord+range/2+1, zCoord+range+1);
	@SuppressWarnings("unchecked")
	List<EntityPlayer> npcs = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bb);
	return npcs;
}

private void resetBattle() {
	printToAll("[" + this.name+"]: Battle over", EnumChatFormatting.GREEN);
	attackTimer = 110000;
	players = new NBTTagCompound();
}

public void printCaptureStatus() {
	if(captureTimer == 60 * 20 - 1) {
		printToAll("[" + this.name+"]: Capturing has begun.", EnumChatFormatting.DARK_RED);
	}
	if(captureTimer == 45 * 20) {
		printToAll("[" + this.name+"]: Base will be captured in 45 seconds.", EnumChatFormatting.YELLOW);
	}else if(captureTimer == 30 * 20) {
		printToAll("[" + this.name+"]: Base will be captured in 30 seconds.", EnumChatFormatting.YELLOW);
	}else if(captureTimer == 15 * 20) {
		printToAll("[" + this.name+"]: Base will be captured in 15 seconds.", EnumChatFormatting.YELLOW);
	}else if(0 < captureTimer && captureTimer < 200 && captureTimer % 20 == 0) {
		printToAll("[" + this.name+"]: Base will be captured in " + captureTimer / 20 + " seconds.", EnumChatFormatting.RED);
	}
}

public void printBattleStatus() {
	int TICKS_PER_MINUTE = 60 * 20;
	if(attackTimer == TICKS_PER_MINUTE * 10) {
		printToAll("[" + this.name+"]: Battle will begin in 10 minutes.", EnumChatFormatting.YELLOW);
	}else if(attackTimer == TICKS_PER_MINUTE * 5) {
		printToAll("[" + this.name+"]: Battle will begin in 5 minutes.", EnumChatFormatting.YELLOW);
	}else if(attackTimer == TICKS_PER_MINUTE) {
		printToAll("[" + this.name+"]: Battle will begin in 1 minute.", EnumChatFormatting.RED);
	}else if(attackTimer == TICKS_PER_MINUTE / 2) {
		printToAll("[" + this.name+"]: Battle will begin in 30 seconds.", EnumChatFormatting.RED);
	}else if(attackTimer == 15 * 20) {
		printToAll("[" + this.name+"]: Battle will begin in 15 seconds.", EnumChatFormatting.RED);
	}else if(attackTimer <= 10*20 && attackTimer >= 0 && attackTimer % 20 == 0) {
		printToAll("[" + this.name+"]: " + attackTimer / 20, EnumChatFormatting.DARK_RED);
	}
	if(attackTimer == 0) {
		EnumChatFormatting format = EnumChatFormatting.DARK_RED;
		
		printToAll("[" + this.name+"]: The battle begins now. Good luck!", EnumChatFormatting.DARK_RED);
		printToAll("[" + this.name+"]: 30 minutes remaining in the battle.", EnumChatFormatting.GREEN);
	}
	
	if(attackTimer == TICKS_PER_MINUTE * - 10) {
		printToAll("[" + this.name+"]: 20 minutes remaining in the battle.", EnumChatFormatting.GREEN);
	}else if(attackTimer == TICKS_PER_MINUTE * - 20) {
		printToAll("[" + this.name+"]: 10 minutes remaining in the battle.", EnumChatFormatting.YELLOW);
	}else if(attackTimer == TICKS_PER_MINUTE * - 25) {
		printToAll("[" + this.name+"]: 5 minutes remaining in the battle.", EnumChatFormatting.YELLOW);
	}else if(attackTimer == TICKS_PER_MINUTE * - 29) {
		printToAll("[" + this.name+"]: 1 minute remaining in the battle.", EnumChatFormatting.RED);
	}else if(attackTimer == TICKS_PER_MINUTE * -29 - (20*30)) {
		printToAll("[" + this.name+"]: 30 seconds remaining in the battle.", EnumChatFormatting.RED);
	}else if(attackTimer == TICKS_PER_MINUTE * -29 - (20*45)) {
		printToAll("[" + this.name+"]: 15 seconds remaining in the battle.", EnumChatFormatting.RED);
	}else if(attackTimer <= TICKS_PER_MINUTE * -29.75 && attackTimer % 20 == 0) {
		printToAll("[" + this.name+"]: " + ((attackTimer / 20) - 1740), EnumChatFormatting.DARK_RED);
	}
}

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  
  updateRespawns();
  
  if(this.attackTimer < 100000) {
	  updateBattle();
  }
  
  if(worldObj.getWorldTime() % 1200 == 0) {
  }
  updateDelayTicks--;
  if(updateDelayTicks<=0)
    {
    broadcast();
    updateDelayTicks = updateDelayMaxTicks;
    }
  }

public void addViewer(ContainerTownHall viewer)
  {
  if(!viewers.contains(viewer)){viewers.add(viewer);}
  }

public void removeViewer(ContainerTownHall viewer)
  {
  while(viewers.contains(viewer)){viewers.remove(viewer);}
  }

private void broadcast()
  {
  List<NpcPlayerOwned> npcs = getNpcsInArea();
  BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
  for(NpcPlayerOwned npc : npcs)
    {
    if(npc.canBeCommandedBy(getOwnerName()))
      {
      npc.handleTownHallBroadcast(this, pos);      
      }
    }
  }

public void clearDeathNotices()
  {
  deathNotices.clear();
  informViewers();
  }

public void informViewers()
  {
  for(ContainerTownHall cth : viewers)
    {
	  
    cth.onTownHallDeathListUpdated();
    }
  }



public void handleNpcDeath(NpcPlayerOwned npc, DamageSource source)
  {
  boolean canRes = true;//TODO set canRes  based on distance from town-hall?
  NpcDeathEntry entry = new NpcDeathEntry(npc, source, canRes);
  deathNotices.add(entry);
  informViewers();
  }

private List<NpcPlayerOwned> getNpcsInArea()
  {
  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord-broadcastRange, yCoord-broadcastRange/2, zCoord-broadcastRange, xCoord+broadcastRange+1, yCoord+broadcastRange/2+1, zCoord+broadcastRange+1);
  @SuppressWarnings("unchecked")
  List<NpcPlayerOwned> npcs = worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
  return npcs;
  }

@Override
public void setOwnerName(String name){
  if(name==null){name="";}
  this.ownerName = name;
  try {
	  EntityPlayer owner = this.worldObj.getPlayerEntityByName(name);
	  this.team = owner.getTeam().getRegisteredName();
  }catch(Exception e) {}
}

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  ownerName = tag.getString("owner");
  InventoryTools.readInventoryFromNBT(inventory, tag.getCompoundTag("inventory"));  
  NBTTagList entryList = tag.getTagList("deathNotices", Constants.NBT.TAG_COMPOUND);
  NpcDeathEntry entry;
  for(int i = 0;i < entryList.tagCount(); i++)
    {
    entry = new NpcDeathEntry(entryList.getCompoundTagAt(i));
    deathNotices.add(entry);
    }
  
  if(tag.hasKey("name")) {this.name = tag.getString("name");}
  if(tag.hasKey("team")) {this.team = tag.getString("team");}

  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("owner", ownerName);
  tag.setTag("inventory", InventoryTools.writeInventoryToNBT(inventory, new NBTTagCompound()));
  NBTTagList entryList = new NBTTagList();
  for(NpcDeathEntry entry : deathNotices)
    {
    entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("deathNotices", entryList);
  tag.setString("name", name);
  tag.setString("team", team);
  }

@Override
public int getSizeInventory(){return inventory.getSizeInventory();}

@Override
public ItemStack getStackInSlot(int var1){return inventory.getStackInSlot(var1);}

@Override
public ItemStack decrStackSize(int var1, int var2){return inventory.decrStackSize(var1, var2);}

@Override
public ItemStack getStackInSlotOnClosing(int var1){return inventory.getStackInSlotOnClosing(var1);}

@Override
public void setInventorySlotContents(int var1, ItemStack var2){inventory.setInventorySlotContents(var1, var2);}

@Override
public String getInventoryName(){return inventory.getInventoryName();}

@Override
public boolean hasCustomInventoryName(){return inventory.hasCustomInventoryName();}

@Override
public int getInventoryStackLimit(){return inventory.getInventoryStackLimit();}

@Override
public boolean isUseableByPlayer(EntityPlayer var1){return true;}

@Override
public void openInventory(){}

@Override
public void closeInventory(){}

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2){return inventory.isItemValidForSlot(var1, var2);}

public static class NpcDeathEntry
{
public ItemStack stackToSpawn;
public String npcType;
public String npcName;
public String deathCause;
public boolean resurrected;
public boolean canRes;
public boolean beingResurrected;

public NpcDeathEntry(NBTTagCompound tag)
  {
  readFromNBT(tag);
  } 

public NpcDeathEntry(NpcPlayerOwned npc, DamageSource source, boolean canRes)
  {
  this.stackToSpawn = ItemNpcSpawner.getSpawnerItemForNpc(npc);
  this.npcType = npc.getNpcFullType();
  this.npcName = npc.getCustomNameTag();
  this.deathCause = source.damageType;
  this.canRes = canRes;
  }

public final void readFromNBT(NBTTagCompound tag)
  {
  stackToSpawn = InventoryTools.readItemStack(tag.getCompoundTag("spawnerStack"));
  npcType = tag.getString("npcType");
  npcName = tag.getString("npcName");
  deathCause = tag.getString("deathCause");
  resurrected = tag.getBoolean("resurrected");
  canRes = tag.getBoolean("canRes");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("spawnerStack", InventoryTools.writeItemStack(stackToSpawn, new NBTTagCompound()));
  tag.setString("npcType", npcType);
  tag.setString("npcName", npcName);
  tag.setString("deathCause", deathCause);
  tag.setBoolean("resurrected", resurrected);
  tag.setBoolean("canRes", canRes);
  return tag;
  }
}



@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TOWN_HALL, xCoord, yCoord, zCoord);
    }

  return false;
  }

public List<NpcDeathEntry> getDeathList()
  {
  return deathNotices;
  }

}
