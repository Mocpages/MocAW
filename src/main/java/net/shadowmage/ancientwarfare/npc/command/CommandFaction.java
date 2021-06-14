package net.shadowmage.ancientwarfare.npc.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;
import net.shadowmage.ancientwarfare.npc.gamedata.State;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class CommandFaction implements ICommand
{

private int permissionLevel = 0;

public int compareTo(ICommand par1ICommand)
  {
  return this.getCommandName().compareTo(par1ICommand.getCommandName());
  }

@Override
public int compareTo(Object par1Obj)
  {
  return this.compareTo((ICommand)par1Obj);
  }

@Override
public String getCommandName()
  {
  return "awfaction";
  }

@Override
public String getCommandUsage(ICommandSender var1)
  {
  return "command.aw.faction.usage";
  }

@SuppressWarnings("rawtypes")
@Override
public List getCommandAliases()
  {
  return null;
  }

	/*
	 * public void processCommand3(ICommandSender var1, String[] var2) { String cmd
	 * = var2[0];
	 * 
	 * FactionData data = AWGameData.INSTANCE.getData(FactionData.name,
	 * var1.getEntityWorld(), FactionData.class); if(cmd.equalsIgnoreCase("create"))
	 * { String playerName = var2[1]; MocFaction f = new MocFaction(var2[2],
	 * playerName); //data.factions.add(f); var1.addChatMessage(new
	 * ChatComponentText("Created faction " + var2[2])); }else
	 * if(cmd.equalsIgnoreCase("list")) { /var1.addChatMessage(new
	 * ChatComponentText("Factions List:")); for(MocFaction f : data.factions) {
	 * var1.addChatMessage(new ChatComponentText("    " + f.name)); } } }
	 */


public ArrayList<TileTownHall> getTHs(String fac, World worldObj){
	ArrayList<TileTownHall> output = new ArrayList<TileTownHall>();
	for(Object e : worldObj.loadedTileEntityList) { //For every tile entity in the world...
		if(e instanceof TileTownHall) { //if that tile entity is a town hall...
			TileTownHall th = (TileTownHall)e; 
			if(worldObj.getPlayerEntityByName(th.getOwnerName()).getTeam().getRegisteredName().equalsIgnoreCase(fac)) {
				output.add(th);
			}
		}//worldObj.getPlayerEntityByName(this.getOwnerName())
	}
	return output;
}

public void printError(ICommandSender tgt, String text) {
	print(tgt, text, EnumChatFormatting.RED);
}


public static void print(ICommandSender player, String message, EnumChatFormatting color) {
	ChatComponentText text = new ChatComponentText(message);
	text.getChatStyle().setColor(color);
	player.addChatMessage(text);
}


public void joinFaction(ICommandSender var1, String[] var2, MocData data) {
	MocFaction f = data.getFaction(var2[1]);
	if(f == null) {
		printError(var1, "No such faction.");
	}else if(f.isPlayerInvited(var1)){
		MocFaction f2 = data.getFaction((EntityPlayerMP)var1);
		if(f2 != null) {
			f2.players.remove(var1.getCommandSenderName());
			printError(var1, "Left faction " + f2.name);
		}
		print(var1, "Joined faction " + f.name + ".", EnumChatFormatting.GREEN);
		f.players.add(var1.getCommandSenderName());
	}else {
		printError(var1, "You are not invited to this faction.");
	}
	data.markDirty();
}

@SuppressWarnings("unchecked")
@Override
public void processCommand(ICommandSender var1, String[] var2) {
	String cmd = var2[0];
	String playerName;
	MocData data = AWGameData.INSTANCE.getData(MocData.name,var1.getEntityWorld(), MocData.class);
	if(cmd.equalsIgnoreCase("adminattack") && var1.getCommandSenderName().equalsIgnoreCase("mocpages")) {
		playerName  = var2[1];
		TileTownHall.attackBaseAdmin(playerName, var1.getEntityWorld());
		//for(Object o : var1.getEntityWorld().getScoreboard().getTeams()) {
			//if(o instanceof Team) {
				//Team t = (Team)o;
				//t.
			//}
		//}
	}
	
	if(cmd.equalsIgnoreCase("state")){
		EntityPlayerMP player = (EntityPlayerMP)var1;
		State s = data.getState((int)player.posX, (int)player.posZ);
		if(s == null) {
			var1.addChatMessage(new ChatComponentText("State is null"));
		}else {
			var1.addChatMessage(new ChatComponentText("State: " + s.name));
			var1.addChatMessage(new ChatComponentText("Stability: " + s.stability));
			var1.addChatMessage(new ChatComponentText("   Influence: "));
			for(State.InfluenceHandler h : s.influence) {
				var1.addChatMessage(new ChatComponentText("       " + h.facName + ": " + h.influence));
			}

			var1.addChatMessage(new ChatComponentText("Items: " + s.getItemsForFac(data.getFaction(player).name)));

		}
	}else if(cmd.equalsIgnoreCase("annex")){
		EntityPlayerMP player = (EntityPlayerMP)var1;
		State s = data.getState((int)player.posX, (int)player.posZ);
		if(s == null) {
			var1.addChatMessage(new ChatComponentText("State is null"));
		}else {
			s.owner = data.getFaction(player);
		}
	}
	if(cmd.equalsIgnoreCase("attack")) {
		playerName  = var2[1];
		TileTownHall.attackBase(playerName, var1.getEntityWorld());
	}else if(cmd.toLowerCase().equals("set")) {
		playerName  = var2[1];
		data.addFaction(playerName);
	}else if(cmd.equalsIgnoreCase("list")){
		var1.addChatMessage(new ChatComponentText("Factions: "));
		for(MocFaction f : data.factions) {
			var1.addChatMessage(new ChatComponentText("   " + f.name));
		}
	}else if (cmd.equalsIgnoreCase("setJail")){
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(fac == null) {
			printError(var1, "You are not in a faction.");
		}else {
			print(var1, "Set jail location.", EnumChatFormatting.GREEN);
			EntityPlayerMP player = (EntityPlayerMP)var1;
			fac.prison = new BlockPosition(player.posX, player.posY, player.posZ);
		}
			
	}else if(cmd.equalsIgnoreCase("jail")) {
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(fac == null) {
			printError(var1, "You are not in a faction.");
		}else {
			EntityPlayerMP target = getPlayer(var2[1]);
			if(target == null) {
				printError(var1, "No such player.");
			}else {
				print(var1, "Imprisoning.", EnumChatFormatting.GREEN);
				fac.prisoners.add(target.getCommandSenderName());
				target.getEntityData().setBoolean("isPrisoner", false);


				data.setDirty(true);
			}
		}

	
	}else if(cmd.equalsIgnoreCase("show")) {
		playerName  = var2[1];
		for(MocFaction f : data.factions) {
			if(f.name.equalsIgnoreCase(playerName)) {
				var1.addChatMessage(new ChatComponentText(f.name));

				var1.addChatMessage(new ChatComponentText("   Treasury: " + f.treasury));
				var1.addChatMessage(new ChatComponentText("   Debt: " + f.poor_income_tax));
				var1.addChatMessage(new ChatComponentText("   Influence: " + f.influence));
				var1.addChatMessage(new ChatComponentText("   Bases: "));
				for(TileTownHall th : getTHs(f.name, var1.getEntityWorld())) {
					var1.addChatMessage(new ChatComponentText("      " + th.name + ": x" + th.xCoord + " y" + th.yCoord + " z" + th.zCoord  ));
				}
				
				var1.addChatMessage(new ChatComponentText("   Players: "));
				for(String s : f.players) {
					var1.addChatMessage(new ChatComponentText("      " + s));
				}
				
				String s = "   Prisoners: ";
				for(String prisoner : f.prisoners) {
					s += prisoner + ", ";
				}
				var1.addChatMessage(new ChatComponentText(s));
			}
		}
	}else if(cmd.equalsIgnoreCase("add")) {
		if(data.addFaction(var2[1])) {
			print(var1, "Added faction.", EnumChatFormatting.GREEN);
			MocFaction f = data.getFaction(var2[1]);
			f.invites.add(var1.getCommandSenderName());
			this.joinFaction(var1, var2, data);
		}else {
			printError(var1, "Failed to add faction.");
		}
		
	}else if (cmd.equalsIgnoreCase(("join"))) {
		this.joinFaction(var1, var2, data);
		
	}else if(cmd.equalsIgnoreCase("invite")) {
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(fac == null) {
			printError(var1, "You are not in a faction.");
		}else {
			fac.invites.add(var2[1]);
			print(var1, "Invited " + var2[1] + " to the faction.", EnumChatFormatting.GREEN);
			EntityPlayerMP player = getPlayer(var2[1]);
			print(player, "You have been invited to join " + fac.name + "!", EnumChatFormatting.GREEN);

		}
	}else if(cmd.equalsIgnoreCase("influence")){
		State s = data.getState(var2[1]);
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(s == null) {
			var1.addChatMessage(new ChatComponentText("State is null"));
		}else {
			int amt = Math.min(Integer.parseInt(var2[2]), fac.influence);
			fac.influence -= amt;
			s.updateInfluence(fac.name, amt);
			var1.addChatMessage(new ChatComponentText("Influence size " + s.influence.size() + " isremote " + var1.getEntityWorld().isRemote));
		}
	}else if(cmd.equalsIgnoreCase("resource")) {
		State s = data.getState(var2[1]);
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(s == null) {
			var1.addChatMessage(new ChatComponentText("State is null"));
		}else {
			s.addItems((EntityPlayerMP)var1, fac.name);
		}
	}else if(cmd.equalsIgnoreCase("forceupdate") && var1.getCommandSenderName().equalsIgnoreCase("mocpages")) {
		State s = data.getState(var2[1]);
		if(s == null) {
			var1.addChatMessage(new ChatComponentText("State is null"));
		}else {
			s.dailyUpdate();
		}
	}else if(cmd.equalsIgnoreCase("addinfluence") && var1.getCommandSenderName().equalsIgnoreCase("mocpages")) {
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		fac.influence += Integer.parseInt(var2[1]);
	}else if(cmd.equalsIgnoreCase("liststates")){
		var1.addChatMessage(new ChatComponentText("States: " ));
		for(State s : data.states) {
			var1.addChatMessage(new ChatComponentText("    " + s.name));
		}
	}else if(cmd.equalsIgnoreCase("forceinit") && var1.getCommandSenderName().equalsIgnoreCase("mocpages")) {
		data.forceInit();
	}else if(cmd.equalsIgnoreCase("leave")) {
		MocFaction fac = data.getFaction((EntityPlayerMP)var1);
		if(fac == null) {
			printError(var1, "You are not in a faction.");
		}else {
			fac.players.remove(var1.getCommandSenderName());
			print(var1, "You have left your faction.", EnumChatFormatting.GREEN);
		}
	}
	data.setDirty(true);
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

public void processCommand2(ICommandSender var1, String[] var2)
  {
  if(var2.length < 2)
    {
    throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
    }
  String cmd = var2[0];
  String playerName = var2[1];
  if(cmd.toLowerCase().equals("set"))
    {
    if(var2.length<4){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    String faction = var2[2];
    if(!isFactionNameValid(faction)){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    String amount = var2[3];     
    int amt = 0;
    try{amt = Integer.parseInt(amount);}
    catch(NumberFormatException e){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
    var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.set", playerName, faction, amt));
    }
  else if(cmd.toLowerCase().equals("setall"))
    {
    if(var2.length<3){throw new WrongUsageException("command.aw.faction.setall.usage", new Object[0]);}
    String amount = var2[2];
    int amt = 0;
    try{amt = Integer.parseInt(amount);}
    catch(NumberFormatException e){throw new WrongUsageException("command.aw.faction.setall.usage", new Object[0]);}    
    for(String faction : AWNPCStatics.factionNames)
      {
      FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
      var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.set", playerName, faction, amt));
      }  
    }
  else if(cmd.toLowerCase().equals("get"))
    {
    World world = var1.getEntityWorld();
    var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.status.player", playerName));
    for(String faction : AWNPCStatics.factionNames)
      {
      int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
      var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.status.value", faction, standing));
      }     
    }
  }

private boolean isFactionNameValid(String factionName)
  {
	return true;
		/*
		 * for(String name : AWNPCStatics.factionNames) {
		 * if(name.equalsIgnoreCase(factionName)) { return true; } } return false;
		 */
  }

@Override
public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
  {
  //return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
	return true;
  }

@SuppressWarnings("rawtypes")
@Override
public List addTabCompletionOptions(ICommandSender var1, String[] var2)
  {
  if(var2.length==1)//the command
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, "set", "setall", "get");
    }
  else if(var2.length==2)//would be a player name
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
    }
  else if(var2.length==3)//would be a faction name for the set command
    {
    if(var2[0].toLowerCase().equals("set"))
      {
      return CommandBase.getListOfStringsMatchingLastWord(var2, "bandit", "viking", "desert", "jungle", "pirate", "custom_1", "custom_2", "custom_3");
      }
    }
  else if(var2.length==4)//would be a number for the set command value
    {
    
    }
  return null;
  }

@Override
public boolean isUsernameIndex(String[] var1, int var2)
  {
  return var2==1;
  }

}
