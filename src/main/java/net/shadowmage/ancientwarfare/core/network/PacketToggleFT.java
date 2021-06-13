package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.server.CommandTeleport;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.npc.gamedata.FTData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;

public class PacketToggleFT extends PacketBase
{

String playerName;
double xGoal;
double zGoal;
boolean inFT;

public PacketToggleFT(String playerName, double x, double z, boolean ft)
  {
  this.playerName = playerName;
  this.xGoal = x;
  this.zGoal = z;
  inFT=ft;
  }

public PacketToggleFT(){
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {  
	//this.player.setPosition(player.posX, 1000, player.posZ);
  StringTools.writeString(data, playerName);
  data.writeDouble(xGoal);
  data.writeDouble(zGoal);
  data.writeBoolean(inFT);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  playerName = StringTools.readString(data);
  xGoal = data.readDouble();
  zGoal = data.readDouble();
  inFT = data.readBoolean();
  }

@Override
protected void execute(){
	System.out.println("executing " + player.worldObj.isRemote);
	if(inFT) {
		System.out.println("time to yeet");
		this.player.setPosition(player.posX, 1000, player.posZ);
		System.out.println("yote " + player.posY);
		String[] args = { Double.toString(player.posX), Integer.toString(Integer.MAX_VALUE), Double.toString(player.posZ) };
		(new CommandTeleport()).processCommand(player, args);
		//this.player.moveEntity(0, 1000, 0);
		//this.player.posY = 1000;
		this.player.capabilities.setFlySpeed(0.001F);
		this.player.fallDistance = 0;
		MocData data = ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,player.worldObj, MocData.class));
		FTData d = data.getFTFor(player.getDisplayName());
		if(this.xGoal != 0 || this.zGoal != 0) {
			System.out.println("Setting goal pos x " + xGoal + " z " + zGoal);
			d.xGoal = this.xGoal;
			d.zGoal = this.zGoal;
			data.changeXZ(xGoal, zGoal, player.getDisplayName());
		}
	}else if(this.player.posY >=500){
		System.out.println("deyeeting");
		this.player.fallDistance = 0;
		this.player.capabilities.setFlySpeed(0.0F);
		double y = this.player.worldObj.getTopSolidOrLiquidBlock((int)player.posX, (int)player.posZ)+1;
		String[] args = { Double.toString(player.posX), Double.toString(y), Double.toString(player.posZ) };
		(new CommandTeleport()).processCommand(player, args);

	}
  }
}
