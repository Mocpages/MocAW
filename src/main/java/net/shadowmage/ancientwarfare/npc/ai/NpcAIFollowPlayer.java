package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;

public class NpcAIFollowPlayer extends NpcAI
{

private Entity target;
private double attackIgnoreDistance = 0.01d; //4.d*4.d;
private double followStopDistance = 0.001d; //4.d*4.d;

public NpcAIFollowPlayer(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK);
  }

/**
 * Returns whether the EntityAIBase should begin execution.
 */
public boolean shouldExecute()
  {
  //System.out.println("YEET");
  target = this.npc.getFollowingEntity();  
  if(target == null)
    {
	//  System.out.println("Target is null");
    return false;
    }
  if(npc.getAttackTarget()!=null)
    {
    if(npc.getDistanceSqToEntity(target)<attackIgnoreDistance && npc.getDistanceSqToEntity(npc.getAttackTarget())<attackIgnoreDistance)
      {
    //  System.out.println("Moc you got fucked");
      return false;
      }
    }
  //System.out.println("executing");
  return true;
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
public boolean continueExecuting()
  {
  target = this.npc.getFollowingEntity();  
  if(target == null)
    {
//System.out.println("lol");
    return false;
    }
  if(npc.getAttackTarget()!=null)
    {
    if(npc.getDistanceSqToEntity(target)<attackIgnoreDistance && npc.getDistanceSqToEntity(npc.getAttackTarget())<attackIgnoreDistance)
      {
      return false;
      }
    }
  return true;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
public void startExecuting()
  {
  moveRetryDelay=0;
  this.npc.addAITask(TASK_FOLLOW);
  }

/**
 * Resets the task
 */
public void resetTask()
  {
  this.target = null;
  moveRetryDelay=0;
  this.npc.removeAITask(TASK_FOLLOW + TASK_MOVE);
 // this.npc.setCustomNameTag("arms");
  }

/**
 * Updates the task
 */
public void updateTask()
  {
  // npc.renderMode = 1;
	EntityLivingBase e = npc.getFollowingEntity();
    NpcBase f = null;
	if(e instanceof NpcBase) {
		f= (NpcBase)e;
	}
	if(f == null) {return;}
  this.npc.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, (float)this.npc.getVerticalFaceSpeed());
  //PathPoint p = null;
  //PathNavigate nav = f.getNavigator();
  //if(nav != null) {
	//  PathEntity path = nav.getPath();
	 // if(path!= null) {
	//	  try {
		//	  p = path.getPathPointFromIndex(path.getCurrentPathIndex());
		 // }catch(Exception exc) {
			//  exc.printStackTrace();
		  //}
	  //}
  //}
  double[] goal;
  //if(p != null) {
	  //goal = NpcCommand.getRelOffset(p.xCoord, p.zCoord, npc.angle, npc.xOff, npc.zOff);
  //}
  goal = NpcCommand.getRelOffset(f.posX, f.posZ, npc.angle, npc.xOff, npc.zOff);
  double dist = npc.getDistanceSq(goal[0], npc.posY, goal[1]);
  
  if(dist > followStopDistance){
	//  System.out.println("Woo");
    this.npc.addAITask(TASK_MOVE);
    
  //  npc.setCustomNameTag("shoulder");
   // this.moveToPosition(goal[0], npc.posY, goal[1], dist);
	if(f != null) {
		
		dist = npc.getDistanceSqToEntity(f);
	    if(dist > 1.0 && f != null) {
	    //	npc.speed = 1.0;
	    }else if(f !=null){
	    //	f.speed = npc.speed = 0.75d;
	    	//System.out.println("dist: " + dist);
	    	//if(npc.worldObj.getBlock((int)goal[0], (int)f.posY, (int)goal[1]) == Blocks.air || npc.worldObj.getBlock((int)goal[0], (int)f.posY, (int)goal[1]) == null) {
	    	//	npc.setPosition(goal[0], f.posY, goal[1]);
	    	//}
	    //	npc.speed = 1.0;
	    	//npc.getFollowingEntity().setAIMoveSpeed(0.16F);
	    }
	}
    
    int y = npc.worldObj.getTopSolidOrLiquidBlock((int)goal[0], (int)goal[1]);
    this.moveToPosition(goal[0], y, goal[1], dist);
//    System.out.println("Moving to " + goal[0] + " " + goal[1]);
    double[] lookPos = NpcCommand.getRelOffset(npc.getFollowingEntity().posX, npc.getFollowingEntity().posZ, npc.angle, npc.xOff + 100, npc.zOff);
    npc.getLookHelper().setLookPosition(lookPos[0], npc.posY + npc.getEyeHeight(), lookPos[1], npc.getVerticalFaceSpeed(), npc.getVerticalFaceSpeed());
    
    }
  else
    {
	  System.out.println("Not moving fuck you");
    this.npc.removeAITask(TASK_MOVE);   
    this.npc.getNavigator().clearPathEntity();
    }  
  }

}

