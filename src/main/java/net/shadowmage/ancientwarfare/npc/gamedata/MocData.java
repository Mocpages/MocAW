package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class MocData extends WorldSavedData{
	public static final String name = "AWMocData";
	
	public ArrayList<MocFaction> factions = new ArrayList<MocFaction>();
	public ArrayList<FTData>     ftData   = new ArrayList<FTData>();
	public ArrayList<Squad>      squads   = new ArrayList<Squad>();
	public ArrayList<State>      states   = new ArrayList<State>();
	
	public MocData(String par1Str) {
		super(par1Str);
	}
	
	public void initStates() {
		if(states.size() > 0) {
			return;
		}
		forceInit();
	}
	
	public void forceInit() {
		//System.out.println("init states");
		states.add(new State(1, "Salamgard", new ItemStack(Item.getItemFromBlock(Blocks.iron_ore))));
		markDirty();
	}
	
	public State getState(int id) {
		for(State s : states) {
			if(s.stateid == id) {
				return s;
			}
		}
		return null;
	}
	
	public State getState(String name) {
		for(State s: states) {
			if(s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
	
	public void updateStates(World w) {
		//System.out.println("Updating states  " + states.size());
		for(State s : states) {
			s.onUpdate(w);
		}
		markDirty();
	}

	public MocData(){
	  super(name);
	}
	
	public FTData getFTFor(String playername) {
		for(FTData f : ftData) {
			System.out.println("name: " + f.pName);
			if(f.pName.contentEquals(playername)) {
				return f;
			}
		}
		FTData f = new FTData(0,0,playername);
		ftData.add(f);
		return f;
	}
	
	public void changeXZ(double x, double z, String playername) {
		for(FTData f : ftData) {
			if(f.pName.contentEquals(playername)) {
				f.xGoal=x;
				f.zGoal=z;
				System.out.println("setting data for " + playername + " to x " + f.xGoal + " z " + f.zGoal);
			}
		}
	}
	
	public boolean addFaction(String name) {
		if(this.getFaction(name) == null) {
			factions.add(new MocFaction(name));
			markDirty();
			return true;
		}else {
			markDirty();
			return false;
		}
		
	///	System.out.println("adding fac");
		
	}
	
	public MocFaction getFaction(String name) {
	//	System.out.println("Getting data for " + name);
		for(MocFaction fac : factions) {
			if(fac.name.equalsIgnoreCase(name)) {
		//		System.out.println("found fac");
				return fac;
			}
		}
		
		return null;
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		System.out.println("reading from NBT");
		NBTTagList facList = tag.getTagList("facList", Constants.NBT.TAG_COMPOUND);
		System.out.println("# of facs: " + facList.tagCount());
		for(int i = 0; i < facList.tagCount(); i++) {
			factions.add(new MocFaction(facList.getCompoundTagAt(i)));
		}
		
		NBTTagList stateList = tag.getTagList("stateList", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < stateList.tagCount(); i++) {
			states.add(new State(stateList.getCompoundTagAt(i), this));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
	//	System.out.println("writing to nbt");

		  NBTTagList facList = new NBTTagList();
		  NBTTagCompound facTag;
		  System.out.println("Writing " + factions.size() + " factions");
		  for(MocFaction f : factions) {
			  facTag = new NBTTagCompound();
			  f.writeToNBT(facTag);
			  facList.appendTag(facTag);
			  System.out.println("Wrote " + f.name + "!");
		  }
		  tag.setTag("facList", facList);
		  
		  NBTTagList stateList = new NBTTagList();
		  NBTTagCompound stateTag;  
		  for(State s : states) {
			  stateTag = new NBTTagCompound();
			  s.writeToNBT(stateTag);
			  stateList.appendTag(stateTag);
		  }
		  tag.setTag("stateList", stateList);
	}

	public State getState(int x, int z) {
		int id = States.getId(x,  z);
		if(id >= 1) {
			return this.getState(id);
		}
		return null;
	}
	
	public void onTick(World w) {
	//	System.out.println("remote: " + w.isRemote);
	//	System.out.println("UK Exists: " + getFaction("UnitedKingdom").name);
	//	System.out.println("Ticking");
		for(MocFaction f : this.factions) {
			f.onTick(w);

		}
		initStates();
		updateStates(w);
		this.markDirty();
	}
	
	public Squad getSquad(EntityPlayer p) {
		for(Squad s : squads) {
			if(s.isPlayerInSquad(p)) {
				return s;
			}
		}
		return null;
	}
	
	public Squad getSquad(String name) {
		for(Squad s : squads) {
			if(s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
	
	public boolean createSquad(EntityPlayer player, String type, String name) {
		if(getSquad(player) != null){
			return false;
		}
		squads.add(new Squad(player, name, type));
		return true;
	}

	public MocFaction getFaction(EntityPlayer player) {
		for(MocFaction f : factions) {
			if(f.players.contains(player.getCommandSenderName())) {
				return f;
			}
		}
		return null;
	}
}
