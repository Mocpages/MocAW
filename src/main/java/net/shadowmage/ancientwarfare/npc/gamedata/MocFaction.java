package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.economy.Bank;
import net.shadowmage.ancientwarfare.npc.economy.Market;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class MocFaction {
	public String name;
	public float poor_income_tax = 0.0F;
	public float middle_income_tax = 0.0F;
	public float rich_income_tax = 0.0F;
	public float import_tariff = 0.0F;
	public float export_tariff = 0.0F;
	public float treasury = 0.0F;
	public float educationSpending = 0.0F;
	public ArrayList<NpcPlayerOwned> npcs = new ArrayList<NpcPlayerOwned>();
	public double avgLit = 0.0F;
	Calendar cal = Calendar.getInstance();
	long MIL_PER_DAY = 86400000;
	int lastUpdate;
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> invites = new ArrayList<String>();

	public BlockPosition prison = null;
	public ArrayList<String> prisoners = new ArrayList<String>();
	public ArrayList<Base> bases = new ArrayList<Base>();

	public int influence = 0;
	
	public Bank nationalBank = new Bank();
	public Market itemMarket = new Market(nationalBank);

	//List<String> enemiesInWar = new ArrayList<String>();

	public static double INTEREST_RATE = 0.01;
	
	public MocFaction(String n) {
		name=n;
		lastUpdate = cal.get(Calendar.DAY_OF_YEAR);
	}
	
	public boolean isPlayerInvited(ICommandSender p) {
		for(String s : invites) {
			if(s.equalsIgnoreCase(p.getCommandSenderName())) {
				return true;
			}
		}
		return false;
	}
	
	public void kickPlayer(ICommandSender p) {
		String s = p.getCommandSenderName();
		if(players.contains(s)) {
			players.remove(s);
		}
		
		if(invites.contains(s)) {
			invites.remove(s);
		}
	}
	
	public void addNpc(NpcPlayerOwned p) {
		//System.out.println("ADDING");
		if(!npcs.contains(p)) {
			//System.out.println("ADDING 2");
			npcs.add(p);
		}
	}

	public Base getBase(String name){
		for(Base b : bases){
			if(b.name.equalsIgnoreCase(name)){
				return b;
			}
		}
		return null;
	}
	
	public MocFaction(NBTTagCompound tag) {
		readFromNBT(tag);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		System.out.println("yeet: " + name);/*
		this.poor_income_tax = tag.getFloat("pit");
		this.middle_income_tax = tag.getFloat("mit");
		this.rich_income_tax = tag.getFloat("hit");
		this.import_tariff = tag.getFloat("it");
		this.export_tariff = tag.getFloat("et");
		this.treasury = tag.getFloat("tr");
		this.educationSpending = tag.getFloat("ed");*/
		//this.avgLit = tag.getDouble("avLit");
		if(tag.hasKey("updated")) { this.lastUpdate = tag.getInteger("updated");}
		if(tag.hasKey("bank")) {nationalBank = new Bank(tag.getCompoundTag("bank"));}
		if(tag.hasKey("influence")) {influence = tag.getInteger("influence");}
		NBTTagList playerList = tag.getTagList("playerList", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < playerList.tagCount(); i++) {
			players.add(playerList.getCompoundTagAt(i).getString("name"));
		}

		NBTTagList baseList = tag.getTagList("baseList", Constants.NBT.TAG_COMPOUND);
		System.out.println("Reading from NBT. Bases: " + baseList.tagCount());
		for(int i = 0; i < baseList.tagCount(); i++) {
			bases.add(new Base(baseList.getCompoundTagAt(i)));
		}

		//if(tag.hasKey("prison")) { prison = new BlockPosition(tag.getCompoundTag("prison"));}
	}

	
	public void writeToNBT(NBTTagCompound tag) {
		System.out.println("writing fac: " + name);
		tag.setString("name", name);
		//tag.setFloat("pit", poor_income_tax);
		//tag.setFloat("mit", middle_income_tax);
		//tag.setFloat("hit", rich_income_tax);
		//tag.setFloat("it", import_tariff);
		//tag.setFloat("et", export_tariff);
		//tag.setFloat("tr", treasury);
		//tag.setFloat("ed", educationSpending);
		//tag.setDouble("avLit", this.getAvLit());
		tag.setInteger("updated", this.lastUpdate);
		tag.setInteger("influence", influence);
		
		//NBTTagCompound bank = new NBTTagCompound();
		//nationalBank.writeToNBT(bank);
		//tag.setTag("bank", bank);
		
		  NBTTagList playerList = new NBTTagList();
		  NBTTagCompound playerTag;  
		  for(String s : players) {
			  playerTag = new NBTTagCompound();
			  playerTag.setString("name", s);;
			  playerList.appendTag(playerTag);
		  }
		  tag.setTag("playerList", playerList);

		NBTTagList baseList = new NBTTagList();
		NBTTagCompound baseTag;
		for(Base b : bases) {
			baseTag = new NBTTagCompound();
			b.writeToNBT(baseTag);
			baseList.appendTag(baseTag);
		}
		System.out.println("Writing to NBT " + baseList.tagCount());
		tag.setTag("baseList", baseList);

		  if(prison != null) {
			  tag.setTag("prison", prison.writeToNBT(new NBTTagCompound()));
		  }
	}
	
	public double getAvLit() {
		double avg = 0.0;
		System.out.println("SIZE: " + npcs.size());
		for(NpcPlayerOwned npc : this.npcs) {
			avg += npc.literacy;
		}
		return avg / npcs.size();
	}
	
	private void updateDebt() {
		if(cal.get(Calendar.DAY_OF_YEAR) != lastUpdate) {
			int numDays = cal.get(Calendar.DAY_OF_YEAR) - lastUpdate;
			lastUpdate = cal.get(Calendar.DAY_OF_YEAR);
			double interest = this.poor_income_tax * INTEREST_RATE;

			for(int i = 0; i<= numDays; i++) { //If for some reason we've missed multiple days, do em all.
				if(this.treasury < interest) {
					//TODO handle default; commit fuckeries.
				}else {
					this.treasury -= interest;
				}
			}
		}
	}
	
	private void updateEducation() {
		this.treasury -= this.educationSpending;
		double remainingCash = this.educationSpending;
		for(NpcPlayerOwned npc : this.npcs) {
			if(remainingCash >= 1.0) {
				remainingCash --;
				npc.literacy += 0.0001;
			}
		}
	}
	
	public EntityPlayerMP getPlayer(String name) {
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if(o instanceof EntityPlayerMP) {
				EntityPlayerMP p = (EntityPlayerMP)o;
				if(p.getCommandSenderName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
	public void onTick(World w) {
	//	updateDebt();

		  for(Base b : bases){
		  	b.update();
		  }

		  System.out.println("Remote: " + w.isRemote);
		  System.out.println("Name: " + this.name);
		  System.out.println("prisoners: " + this.prisoners.size());
		  if(w.getWorldTime() % 40 == 0) {
			  for(String s : this.prisoners) {
				  EntityPlayerMP player = getPlayer(s);
				  System.out.println("      Username: " + s);
				  if(player != null) {
					  player.addChatComponentMessage(new ChatComponentText("Get fuccced"));
					  player.setPositionAndUpdate(prison.x, prison.y, prison.z);
				  }
			  }
			 // nationalBank.resolveChecks();
			  //nationalBank.updateInterest();
			  //itemMarket.cleanLists();
			  if(this.lastUpdate != cal.get(Calendar.DAY_OF_YEAR)) {
				  this.influence = Math.min(10, influence + 4);
				  this.lastUpdate = cal.get(Calendar.DAY_OF_YEAR);
			  }
		  }

	}
}
