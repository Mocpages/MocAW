package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIChargeAttack extends NpcAI{

	Entity target;
	int attackDelay = 0;
	Vec3 movementVec;

	public NpcAIChargeAttack(NpcBase npc){
		super(npc);
		this.setMutexBits(ATTACK+MOVE);
	}

	@Override
	public boolean shouldExecute(){
		return npc.getIsAIEnabled() && npc.getAttackTarget()!=null && !npc.getAttackTarget().isDead;
	}

	@Override
	public boolean continueExecuting(){
		return npc.getIsAIEnabled() && target!=null && target==npc.getAttackTarget() && !npc.getAttackTarget().isDead;
	}

	@Override
	public void startExecuting(){
		npc.addAITask(TASK_ATTACK);
		target = npc.getAttackTarget();
		attackDelay = 0;
		moveRetryDelay = 0;
	}

	@Override
	public void updateTask(){
		npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
		
		double distanceToEntity = this.npc.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
		double attackDistance = (double)((this.npc.width * this.npc.width * 2.0F * 2.0F) + (target.width * target.width * 2.0F * 2.0F));
		if(distanceToEntity>100){
			npc.addAITask(TASK_MOVE);
			Vec3 vec = Vec3.createVectorHelper(target.posX - npc.posX, target.posY - npc.posY, target.posZ - npc.posZ).normalize();
			double goalDist = 10;
			vec.xCoord *= goalDist;
			vec.yCoord *= goalDist;
			vec.zCoord *= goalDist;
			double d = npc.getDistance(vec.xCoord, vec.yCoord, vec.zCoord);
			//super.moveToPosition(vec.xCoord, vec.yCoord, vec.zCoord, d);
			moveToEntity(target, distanceToEntity);
		}else{
			npc.removeAITask(TASK_MOVE);
			attackTarget();
		}
	}

	private void attackTarget(){
		attackDelay--;
		if(attackDelay <= 1 && movementVec == null) {
			movementVec = Vec3.createVectorHelper(target.posX-npc.posX, target.posY-npc.posY, target.posZ-npc.posZ).normalize();
			movementVec.xCoord *= 0.2;
			movementVec.yCoord *= 0.2;
			movementVec.zCoord *= 0.2;
		}
		if(attackDelay <= 0 && movementVec != null) {
			npc.addVelocity(movementVec.xCoord, movementVec.yCoord, movementVec.zCoord);
			if(npc.getDistanceSqToEntity(target) <= 1) {
				npc.swingItem();
				npc.attackEntityAsMob(target);
				int xp = AWNPCStatics.npcXpFromAttack;
				npc.addExperience(xp);
				attackDelay = 200;
				movementVec = null;
			}
		}
		
		if(attackDelay<=-20){
			movementVec = null;
			this.attackDelay=200;//TODO set attack delay from npc-attributes? 
			
		}
	}

	@Override
	public void resetTask(){
		super.resetTask();
		npc.removeAITask(TASK_MOVE + TASK_ATTACK);
		target = null;
		movementVec = null;
		attackDelay = 0;
		moveRetryDelay = 0;
	}
}
