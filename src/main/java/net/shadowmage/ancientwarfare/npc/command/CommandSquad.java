package net.shadowmage.ancientwarfare.npc.command;

import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedSoldier;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.RoleOfficer;
import net.shadowmage.ancientwarfare.npc.gamedata.Squad;
import net.shadowmage.ancientwarfare.npc.gamedata.Squad.PlayerRole;
import scala.util.Random;

public class CommandSquad extends CommandBase{

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
		return "squad";
	}

	@Override
	public String getCommandUsage(ICommandSender var1)
	{
		return "Yeet";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCommandAliases()
	{
		return null;
	}

	public void printError(ICommandSender tgt, String text) {
		print(tgt, text, EnumChatFormatting.RED);
	}


	public static void print(ICommandSender player, String message, EnumChatFormatting color) {
		ChatComponentText text = new ChatComponentText(message);
		text.getChatStyle().setColor(color);
		player.addChatMessage(text);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2){
		String cmd = var2[0];
		MocData data = AWGameData.INSTANCE.getData(MocData.name,var1.getEntityWorld(), MocData.class);

		if(cmd.equalsIgnoreCase("create")) {
			if(var2.length != 3) {
				printError(var1, "Usage: /squad create [type] [name]");
				return;
			}
			if(var1 instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) var1;
				if(data.createSquad(player, var2[1], var2[2])) {
					print(var1, "Squad created!", EnumChatFormatting.GREEN);
				}else {
					printError(var1, "You are already in a squad.");
				}

			}
		}else if (cmd.equalsIgnoreCase("show")) {
			Squad s = data.getSquad((EntityPlayerMP)var1);
			if(s == null) {
				printError(var1, "You are not in a squad.");
			}else {
				print(var1, "Squad: " + s.name, EnumChatFormatting.WHITE);

				print(var1, "Members: " , EnumChatFormatting.WHITE);
				int i = 1;
				for(PlayerRole p : s.players) {
					print(var1, "    " +i + ". " +  p.getPlayerName() + " [" + p.getRoleName() + "]", EnumChatFormatting.WHITE);
					i++;
				}
				
				print(var1, "Pending Invites: ", EnumChatFormatting.WHITE);
				for(String inv : s.invitations) {
					print(var1, "    " +  inv, EnumChatFormatting.WHITE);
				}
			}
		}else if (cmd.equalsIgnoreCase("invite")) {
			if(var2.length <= 1) {
				printError(var1, "Usage: /squad invite [tgt username]");
				return;
			}
			
			Squad s = data.getSquad((EntityPlayerMP)var1);
			if(s == null) {
				printError(var1, "You are not in a squad.");
				return;
			}
			
			EntityPlayer target = var1.getEntityWorld().getPlayerEntityByName(var2[1]);
			if(target == null) {
				printError(var1, "Player " + var2[1] +" does not exist.");
				return;
			}
			
			s.invitations.add(var2[1]);
			print(var1, "Invited player " + var2[1] + " to the squad.", EnumChatFormatting.GREEN);
		}else if (cmd.equalsIgnoreCase("join")) {
			if(var2.length <= 1) {
				printError(var1, "Usage: /squad join [tgt squad name]");
				return;
			}
			
			Squad s = data.getSquad(var2[1]);
			if(s == null) {
				printError(var1, "Squad " + var2[1] +" does not exist.");
				return;
			}
			
			if(s.invitations.contains(var1.getCommandSenderName())) {
				print(var1, "Joined squad " + var2[1], EnumChatFormatting.GREEN);
			}else {
				printError(var1, "You are not invited to this squad.");
				return;
			}
		}else if (cmd.equalsIgnoreCase("loadout")) {
			if(var1 instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)var1;
				if(var2.length <= 1) {
					printError(var1, "Usage: /squad loadout [role]");
					return;
				}
				if(var2[1].equalsIgnoreCase("officer")) {
					RoleOfficer.role.addItems(player);
					data.getSquad(player).getPRForPlayer(player).setRole(RoleOfficer.role);
				}else {
					//RoleOfficer.role.addItems(player);
					data.getSquad(player).getPRForPlayer(player).setRole(null);
				}
			}
		}else if(cmd.equalsIgnoreCase("raid")) {
			if(var1 instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)var1;
				Random r = new Random();
				double radius = 100;
				double angle = r.nextDouble() * Math.PI * 2; //random number between 0 and 2pi
				double dx = radius * Math.cos(angle);
				double dz = radius * Math.sin(angle);
				
				NpcBanditMountedSoldier pilot = new NpcBanditMountedSoldier(player.worldObj);
				pilot.setPosition(player.posX+dx, 150, player.posZ+dz);
				pilot.ordersStack = new ItemStack(GameData.itemRegistry.get("mcheli:" + var2[1]));
				pilot.pilotAI.target = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
				pilot.pilotAI.goalAltitude = 60;
				player.worldObj.spawnEntityInWorld(pilot);
			}
			
		}else if(cmd.equalsIgnoreCase("attack")) {
			EntityPlayer p = (EntityPlayer)var1;
			int range = 25;
			for(Object o : p.worldObj.getEntitiesWithinAABBExcludingEntity(p, p.boundingBox.expand(range, range, range))) {
				if(o instanceof NpcBanditMountedSoldier) {
					NpcBanditMountedSoldier pilot = (NpcBanditMountedSoldier)o;
					pilot.pilotAI.target = Vec3.createVectorHelper(Integer.parseInt(var2[1]), 0, Integer.parseInt(var2[2]));
					pilot.pilotAI.goalAltitude = Integer.parseInt(var2[3]);
					pilot.pilotAI.bombsDropped = false;
					pilot.pilotAI.setFullAmmo();
				}
			}
		}else if(cmd.equalsIgnoreCase("warp")) {
			EntityPlayerMP player = (EntityPlayerMP)var1;
			
			NpcBanditMountedSoldier pilot = new NpcBanditMountedSoldier(player.worldObj);
			pilot.setPosition(player.posX, 150, player.posZ);
			pilot.ordersStack = new ItemStack(GameData.itemRegistry.get("mcheli:dc3"));
			pilot.pilotAI.target = Vec3.createVectorHelper(Integer.parseInt(var2[1]), 0, Integer.parseInt(var2[2]));
			pilot.pilotAI.goalAltitude = 150;
			player.worldObj.spawnEntityInWorld(pilot);
			
			pilot.pilotAI.targetPlayer = player;

		}
			
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
	{
		return true; //par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2)
	{ 
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2)
	{
		return false;
	}

}
