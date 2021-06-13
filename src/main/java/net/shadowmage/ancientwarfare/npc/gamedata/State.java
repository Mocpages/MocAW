package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;
import java.util.Calendar;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class State {
	public int stateid = -1;
	public String name = "Error: No state name provided";
	public MocFaction owner = null; //Used only for the true original owner. Null if it's not a faction's core province.
	public String controller = null;
	public ArrayList<InfluenceHandler> influence;
	
	Calendar cal = Calendar.getInstance();
	public int dateLastUpdated = 0;
	public int stability = 0;
	public ItemStack itemProduced;
	public int amtProduced = 100;
	public int facStorageCap = 100;
	
	
	public State(int id, String n, ItemStack i) {
		name = n;
		stateid = id;
		itemProduced = i;
		influence =  new ArrayList<InfluenceHandler>();
	}
	
	public State(NBTTagCompound tag, MocData data) {
		influence =  new ArrayList<InfluenceHandler>();
		readFromNBT(tag, data);
	}
	
	public void onUpdate(World w) {
		//System.out.println("update " + age);
		
		if(cal.get(Calendar.DAY_OF_YEAR)!= this.dateLastUpdated) {
			dateLastUpdated = cal.get(Calendar.DAY_OF_YEAR);
			dailyUpdate();
		}
	}
	
	
	private void spawnItems(EntityPlayerMP player, int amt) {
		InventoryTools.dropItemInWorld(player.worldObj, new ItemStack(itemProduced.getItem(), amt), player.posX, player.posY, player.posZ);

	}
	
	public void addItems(EntityPlayerMP player, String fac) {
		InfluenceHandler h = this.getHandlerForFac(fac);
		if(h.amtStored <= 0) {return;}
		if(h.amtStored <= 64) {
			spawnItems(player, h.amtStored);
			h.amtStored = 0;
		}else {
			spawnItems(player, 64);
			h.amtStored -= 64;
			addItems(player, fac);
		}
	}
	
	public void dailyUpdate() {
		for(InfluenceHandler h : this.influence) {
			double percentage = this.getControlPercentage(h.facName);
			h.amtStored += percentage * amtProduced;
			h.amtStored = Math.min(h.amtStored, facStorageCap);
		}
		
	}

	public void printToAll(World w, String s) {

		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList){
			if(o instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)o;
				player.addChatComponentMessage(new ChatComponentText(s));
			}
		}
	}


	
	public void updateInfluence(String fac, int amt) {
		InfluenceHandler h = getHandlerForFac(fac);
		if(h == null) { //This fac previously had 0 influence
			if(amt > 0) { //We don't want negative influence
				influence.add(new InfluenceHandler(fac, amt));
			}else {
				influence.add(new InfluenceHandler(fac, 0));
			}
		}else {
			h.influence += amt;
		}
		updateController();
		
	}
	
	//Returns double between 0.0-1.0 which is the % of the state the given fac controls
	public double getControlPercentage(String facName) {
		int totalInfluence = getTotalInfluence() + stability;
		int facInfluence = getInfluenceForFac(facName);
		if(this.controller.equalsIgnoreCase(facName)) { facInfluence += stability;}
		return facInfluence / totalInfluence;
	}
	
	public void updateController() {
		int totalInfluence = getTotalInfluence() + stability;
		for(InfluenceHandler h : influence) {
			if(h.influence > totalInfluence - h.influence) {
				this.controller = h.facName;
			}
		}
	}
	
	public int getTotalInfluence() {
		int total = 0;
		for(InfluenceHandler h : influence) {
			total += h.influence;
		}
		return total;
	}
	
	private InfluenceHandler getHandlerForFac(String facName) {
		for(InfluenceHandler h : influence) {
			if(h.facName.equalsIgnoreCase(facName)) {
				return h;
			}
		}
		return null;
	}

	public int getInfluenceForFac(String name) {
		InfluenceHandler h = getHandlerForFac(name);
		if(h != null) {
			return h.influence;
		}else {
			return 0;
		}
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		System.out.println("wtf " + influence.size());
		tag.setInteger("id", stateid);
		tag.setString("name", name);
		if(dateLastUpdated != 0) {
			tag.setInteger("age", dateLastUpdated);
		}
		if(owner != null) {
			tag.setString("owner", owner.name);
		}
		tag.setTag("item", itemProduced.writeToNBT(new NBTTagCompound()));
		
		NBTTagList iHandlerList = new NBTTagList();
		NBTTagCompound iHandlerTag;  
		for(InfluenceHandler s : influence) {
			iHandlerTag = new NBTTagCompound();
			iHandlerTag = s.writeToNBT(iHandlerTag);
			iHandlerList.appendTag(iHandlerTag);
		}

		System.out.println("Created " + iHandlerList.tagCount() + " tags for " + influence.size());
		tag.setTag("iHandlerList", iHandlerList);
	}
	
	public void readFromNBT(NBTTagCompound tag, MocData data) {
		System.out.println("STATE TEST 1");
		stateid  = tag.getInteger("id");
		name     = tag.getString("name");

		if(tag.hasKey("age")){ dateLastUpdated      = tag.getInteger("age");}
		if(tag.hasKey("owner")){owner    =data.getFaction(tag.getString("owner"));}
		if(tag.hasKey("item")){itemProduced = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));}
		System.out.println("STATE TEST 2");
		if(tag.hasKey("iHandlerList")) {
			System.out.println("STATE TEST 3");
			NBTTagList iHandlerList = tag.getTagList("iHandlerList", Constants.NBT.TAG_COMPOUND);
			System.out.println("Handler size " + iHandlerList.tagCount());
			for(int i = 0; i < iHandlerList.tagCount(); i++) {
				System.out.println("Creating handler");
				influence.add(new InfluenceHandler(iHandlerList.getCompoundTagAt(i)));
			}
		}else {
			System.out.println("No handlers");
		}

	}
	
	public class InfluenceHandler{
		public String facName;
		public int influence;
		public int amtStored = 0;
		
		public InfluenceHandler(String f, int i) {
			facName = f;
			influence = i;
		}
		
		public InfluenceHandler(NBTTagCompound tag) {
			readFromNBT(tag);
		}
		
		public void readFromNBT(NBTTagCompound tag) {
			facName = tag.getString("name");
			influence = tag.getInteger("influence");
			amtStored = tag.getInteger("amtstored");
			
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			System.out.println("Writing influence to nbt for fac " + facName);
			tag.setString("name", facName);
			tag.setInteger("influence", influence);
			tag.setInteger("amtstored", amtStored);
			return tag;
		}
	}

	public int getItemsForFac(String name2) {
		InfluenceHandler h = this.getHandlerForFac(name2);
		if(h != null) {
			return h.amtStored;
		}else {
			return 0;
		}
		
	}
}
