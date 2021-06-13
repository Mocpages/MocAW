package net.shadowmage.ancientwarfare.npc.npc_command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcCommand
{

public static enum CommandType
{
MOVE,
ATTACK,//attack click on entity
ATTACK_AREA,//attack click on block
GUARD,//attack click on friendly player or npc
SET_HOME,
SET_UPKEEP,
CLEAR_HOME,
CLEAR_UPKEEP,
CLEAR_COMMAND;
}

/**
 * client-side handle command. called from command baton key handler
 * @param cmd
 */
public static void handleCommandClient(CommandType type, MovingObjectPosition hit)
  {
  if(hit!=null && hit.typeOfHit!=MovingObjectType.MISS)
    {
    if(hit.typeOfHit==MovingObjectType.ENTITY && hit.entityHit!=null)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.entityHit);
      NetworkHandler.sendToServer(pkt);
      }
    else if(hit.typeOfHit==MovingObjectType.BLOCK)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.blockX, hit.blockY, hit.blockZ);
      NetworkHandler.sendToServer(pkt);
      }    
    }
  }

/**
 * server side handle command. called from packet triggered from client key input while baton is equipped
 */
public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int x, int y, int z)
  {
  Command cmd = null;
  if(block)
    {
    cmd = new Command(type, x, y, z);
    }
  else
    {
    cmd = new Command(type, x);
    }
  List<NpcBase> targets = new ArrayList<NpcBase>();
  ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
  
  double angle = getAngle(player.posX, x, player.posZ, z);
  //y = player.worldObj.getTopSolidOrLiquidBlock((int)x, (int)z);
  targets.get(0).handlePlayerCommand(new Command(CommandType.ATTACK_AREA, x, y, z));
  NpcBase npc = targets.get(0);
  double[] lookPos = NpcCommand.getRelOffset(npc.posX, npc.posZ, angle, npc.xOff + 100, npc.zOff);
  npc.getLookHelper().setLookPosition(lookPos[0], npc.posY + npc.getEyeHeight(), lookPos[1], npc.getVerticalFaceSpeed(), npc.getVerticalFaceSpeed());

  handleFormation(npc, targets,angle, 2, 0);
  
  }

public static void handleFormation(NpcBase leader, List<NpcBase> targets, double angle, int files, double zOffset) { //Files are actually ranks, idk why
	if(targets.size() <= files) { //we only need one rank!
		handleRow(leader, targets,angle, 0);
	}else {
		//NpcBase guide = targets.get(0);
		handleRow(leader, targets.subList(0, files),angle, zOffset); //handle this rank
		targets = targets.subList(files, targets.size()); //get all the remaining ranks
		
		//set guide for next rank
		if(targets.size() == 0) { return;}
		NpcBase rowLead = targets.get(0);
		rowLead.setFollowingEntity(leader);
		rowLead.xOff = 0;
		rowLead.zOff = zOffset -1.25;
		rowLead.angle = angle;
		handleFormation(leader, targets, angle, files, zOffset -1.25);
	}
}

public static void handleRow(NpcBase leader, List<NpcBase> targets, double angle, double zOffset) {
	int i = 1;
	for(NpcBase n : targets) {
		if(n != leader) {
			//System.out.println("Updating NPC");
			//n.setAIMoveSpeed(guide.getAIMoveSpeed() * 2);
			n.rotationYaw = (float) angle;
			n.setFollowingEntity(leader);
			n.xOff = -1.25 * i;
			n.zOff = zOffset;
			n.angle = angle;
			i++;
		}
	}
}
	
	

public static double[] getRelOffset(double x, double z, double angle, double offX, double offZ) {
	double xPrime = x * Math.cos(angle) + z * Math.sin(angle);
	double zPrime = -x  * Math.sin(angle) + z * Math.cos(angle);
	
	zPrime += offZ;
	xPrime += offX;

	double x2 = xPrime * Math.cos(angle) - zPrime * Math.sin(angle);
	double z2 = xPrime  * Math.sin(angle) + zPrime * Math.cos(angle);
	
	return new double[] {x2,z2};
}

public static double getAngle(double x1, double x2, double z1, double z2) {
	double cx = (x1 + x2) / 2;
	double cz = (z1 + z2) / 2;
	double angle = Math.atan2(z2 - cz, x2 - cx);
	return angle;
}

public static final class Command
{
public CommandType type;
public int x, y, z;
public boolean blockTarget;

UUID entityID;
Entity entity;

public Command(){}

public Command(NBTTagCompound tag)
  {
  readFromNBT(tag);
  }

public Command(CommandType type, int x, int y, int z)
  {
  this.type = type;
  this.x = x;
  this.y = y;
  this.z = z;
  this.blockTarget = true;  
  }

public Command(CommandType type, int entityID)
  {
  this.type = type;
  this.x = entityID;
  this.y=this.z=0;
  this.blockTarget = false;
  }

public Command copy()
  {
  Command cmd = new Command();
  cmd.type = this.type;
  cmd.x=this.x;
  cmd.y=this.y;
  cmd.z=this.z;
  cmd.entity=this.entity;
  cmd.entityID=this.entityID;
  cmd.blockTarget=this.blockTarget;
  return cmd;
  }

public final void readFromNBT(NBTTagCompound tag)
  {
  type = CommandType.values()[tag.getInteger("type")];
  blockTarget = tag.getBoolean("block");
  x = tag.getInteger("x");
  y = tag.getInteger("y");
  z = tag.getInteger("z");
  if(tag.hasKey("idmsb") && tag.hasKey("idlsb"))
    {
    entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
    }
  }

public final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", type.ordinal());
  tag.setBoolean("block", blockTarget);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  if(entityID!=null)
    {
    tag.setLong("idmsb", entityID.getMostSignificantBits());
    tag.setLong("idlsb", entityID.getLeastSignificantBits());
    }
  return tag;
  }

/**
 * should be called by packet prior to passing command into npc processing
 */
public void findEntity(World world)
  {
  if(blockTarget){return;}
  if(entity!=null){return;}
  if(entityID==null)
    {
    entity = world.getEntityByID(x);
    if(entity!=null)
      {
      entityID = entity.getPersistentID();
      }
    }
  else
    {
    entity = WorldTools.getEntityByUUID(world, entityID.getMostSignificantBits(), entityID.getLeastSignificantBits());
    }
  }

public Entity getEntityTarget(World world)
  {
  if(blockTarget){return null;}
  if(entity!=null)
    {
    return entity;
    }
  else
    {
    findEntity(world);    
    } 
  return entity;
  }

}


}
