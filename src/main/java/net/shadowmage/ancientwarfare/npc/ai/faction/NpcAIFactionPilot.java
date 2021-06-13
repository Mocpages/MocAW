package net.shadowmage.ancientwarfare.npc.ai.faction;

import java.util.ArrayList;
import java.util.List;

import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.plane.MCP_EntityPlane;
import mcheli.plane.MCP_ItemPlane;
import mcheli.weapon.MCH_WeaponBomb;
import mcheli.weapon.MCH_WeaponMachineGun1;
import mcheli.weapon.MCH_WeaponMachineGun2;
import mcheli.weapon.MCH_WeaponSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionPilot;

public class NpcAIFactionPilot extends NpcAI
{

	AttributeModifier followRangeModifier;
	AttributeModifier moveSpeedModifier;
	boolean wasPlaneKilled = false;
	MCH_EntityAircraft aircraft;
	List<EntityAITaskEntry> horseAI = new ArrayList<EntityAITaskEntry>();
	public double  goalAltitude = 20;
	public Vec3 target = Vec3.createVectorHelper(0, 0, 0);
	public boolean bombsDropped = false;
	public double goalRoll = 0;
	public double goalPitch = 0;
	
	public EntityPlayer targetPlayer = null;
	
	public NpcAIFactionPilot(NpcBase npc){
		super(npc);
		this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", 1.5d, 2);
		this.moveSpeedModifier.setSaved(false);
		this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
		this.followRangeModifier.setSaved(false);
	}

	@Override
	public boolean shouldExecute()
	{
		return !wasPlaneKilled; //&& (npc.ridingEntity==null || aircraft!=npc.ridingEntity);
	}

	@Override
	public void startExecuting()
	{  
		if(aircraft==null && !wasPlaneKilled)
		{
			if(npc.ridingEntity instanceof MCH_EntityAircraft){
				aircraft = (MCH_EntityAircraft)npc.ridingEntity;
			}
			else{
				if(npc.ordersStack != null && npc.ordersStack.getItem() instanceof MCP_ItemPlane) {
					spawnAircraft(npc.ordersStack);
					if(targetPlayer != null) {
						//targetPlayer.setLocationAndAngles(aircraft.posX, aircraft.posY, aircraft.posZ, aircraft.rotationPitch, aircraft.rotationYaw);
						//targetPlayer.mountEntity(aircraft);
						aircraft.interactFirstSeat(targetPlayer);
					}
				}
			}
		}
		else if(aircraft!=null && aircraft.isDead)
		{
			wasPlaneKilled=true;
			aircraft=null;
		}
	}

	@Override
	public void updateTask(){
		super.updateTask();
		
		if(aircraft == null || aircraft.isDead || aircraft.isDestroyed()) {
			npc.setDead();
		}
		if(aircraft != null) {
			try {
				if(target.xCoord != 0 || target.zCoord != 0) {
					updateAircraft();
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("Not updating");
		}
	}
	
	private void updateAircraft() {
		if(aircraft == null) {return;}
		
		((NpcFactionPilot)npc).loadNeighboringChunks((int)(npc.posX / 16), (int)(npc.posZ / 16));
		
		aircraft.setCurrentThrottle(1.0);
		aircraft.foldLandingGear();
		
		if(aircraft.rotationRoll > goalRoll) {
			aircraft.moveLeft = true;
			aircraft.moveRight = false;
		}else if(aircraft.rotationRoll < goalRoll) {
			aircraft.moveLeft = false;
			aircraft.moveRight = true;
		}
		
	//	updateDogfight();
		
		//aircraft.rotationRoll = (float) goalRoll;
		aircraft.onUpdateAngles(1.0f);
		try {
			double dif = aircraft.posY - goalAltitude;
			if(Math.abs(dif) >= 3) {
				if(dif > 0 ) { //actual alt is above goal
					aircraft.rotationPitch = 15;
				}else { //Alt is too low
					aircraft.rotationPitch = -15;
				}
			}else {
				aircraft.rotationPitch = 0;
				aircraft.motionY = 0;
			}
			if(!bombsDropped) {
				//aircraft.rotationYaw = -(float)getBearingToVec3(target);
			}
		}catch(NoSuchFieldError e){
			e.printStackTrace();
		}
		
		if(this.targetPlayer != null && this.getPlayerDropTarget() <= 10) {
			if(this.targetPlayer.isRiding()) {
				targetPlayer.mountEntity((Entity)null);
				aircraft.dropEntityParachute(targetPlayer);
				aircraft.setDead();
				npc.setDead();
			}
		}else {
			aircraft.rotationYaw = -(float)getBearingToVec3(target);
			//System.out.println("Player: " + targetPlayer.getDisplayName() + " range " + getRangeTarget());
		}
		//MCH_WeaponSet ws = aircraft.getCurrentWeapon(npc);
		
		//if(ws.getCurrentWeapon() instanceof MCH_WeaponBomb) {
			//System.out.println("Is bomb");
			//if(getRangeTarget() <= 10 || bombsDropped) {
				//System.out.println("Using wep");
			//	aircraft.useCurrentWeapon(npc);
			//	bombsDropped = true;
				
		//	}
		//}
	}
	
	private void updateDogfight() {
		List<MCH_EntityAircraft> targets = getTargets();
		if(targets.size() == 0) {return;}
		MCH_EntityAircraft targetAC = targets.get(0);
		double aspect = getAspect(targetAC, aircraft);
		double mod = aspect - 180;
		
		double aspect2 = getAspect(aircraft, targetAC);
		double mod2 = aspect2 - 180;
		
		if(false) {//(Math.abs(mod) <= 45) { //engaged defensive
			if(mod > 0) {
				goalRoll = 45;
			}else {
				goalRoll = -45;
			}
			aircraft.motionY = 0;
			//targetAC.print("Aspect hot");
		}else { //if(Math.abs(mod2) <= 45) { //Engaged offensive
			if(Math.abs(mod2) <= 6) {
				goalRoll = 0;
				if(aircraft.getDistanceToEntity(targetAC) <= 100*100) {
					MCH_WeaponSet ws = aircraft.getCurrentWeapon(npc);
					if(ws.getCurrentWeapon() instanceof MCH_WeaponMachineGun1 || ws.getCurrentWeapon() instanceof MCH_WeaponMachineGun2) {
						//ws.setAmmoNum(ws.getAmmoNumMax());
						aircraft.useCurrentWeapon(npc);
						targetAC.print("Firing " + ws.getName() + " ammo " + ws.getAmmoNum());
					}else {
						int id = aircraft.getCurrentWeaponID(npc);
						targetAC.print("Wep: " + ws.getWeapon(0).displayName + " id " + id + " " + ws.getCurrentWeapon().name);
						aircraft.updateWeaponID(aircraft.getSeatIdByEntity(npc), 0);
					}
				}else {
					targetAC.print("Dist " + targetAC.getDistanceToEntity(npc));
				}
			}else if(mod2 > 0) {
				goalRoll = 45;
			}else {
				goalRoll = -45;
			}
			
			//targetAC.print(("Aspect2 " + mod2 + " goal roll " + goalRoll + " roll "  + aircraft.getRotRoll()));
		//	targetAC.print("Range: " + Math.sqrt(targetAC.getDistanceToEntity(aircraft)));
				double angle = getAltAngle(aircraft, targetAC);
				double pA = angle + aircraft.rotationPitch;
			//	targetAC.print("angle: " + angle + " aspect " + pA);
				
				if(pA < 0) {
					//aircraft.moveUp = false;
				//	aircraft.moveDown = true;
					
					//aircraft.moveUp = true;
					//aircraft.moveDown = false;
				}else if(pA > 0) {
				//	aircraft.moveUp = true;
				//	aircraft.moveDown = false;
					
					//aircraft.moveUp = false;
					//aircraft.moveDown = true;
				}else {
				//	aircraft.moveUp = false;
				//	aircraft.moveDown = false;
				}
				
				goalPitch = -angle;
				aircraft.rotationPitch = (float) goalPitch;
			}
		
		//else {
		//	goalRoll = 0;
		//	targetAC.print("Aspect: " + aspect);
		//}
		
	}
	
	private double getAltAngle(MCH_EntityAircraft a, MCH_EntityAircraft b) {
		double dist = Math.sqrt(a.getDistanceToEntity(b));
		double dY = b.posY - a.posY;
		return Math.toDegrees(Math.atan(dY / dist));
	}
	
	private double getAspect(MCH_EntityAircraft a, MCH_EntityAircraft b) {
		return getAngle(a, b) - a.rotationYaw;
	}
	
	private double getAngle(MCH_EntityAircraft a, MCH_EntityAircraft b) {
		return a.getBearingToEntity(b);
	}
	
	private List<MCH_EntityAircraft> getTargets(){
		int range = 350;
		List<MCH_EntityAircraft> targets = new ArrayList<MCH_EntityAircraft>();
		for(Object o : aircraft.worldObj.getEntitiesWithinAABBExcludingEntity(aircraft, aircraft.boundingBox.expand(range, range,range))) {
			if(o instanceof MCH_EntityAircraft) {
				MCH_EntityAircraft ac = (MCH_EntityAircraft)o;
				if(ac.getFirstMountPlayer() != null || ac.isAirBorne) {
					targets.add(ac);
				}
			}
		}
		return targets;
	}
	
	public double getRangeTarget() {
		double delta_x = target.xCoord- aircraft.target.xCoord;
		double delta_z = target.zCoord - aircraft.target.zCoord;
		return Math.sqrt(delta_x * delta_x + delta_z * delta_z);
	}
	
	public double getPlayerDropTarget() {
		double delta_x = target.xCoord- aircraft.posX;
		double delta_z = target.zCoord - aircraft.posZ;
		return Math.sqrt(delta_x * delta_x + delta_z * delta_z);
	}
	
	public void setFullAmmo() {
		MCH_WeaponSet ws = aircraft.getCurrentWeapon(npc);
		ws.setAmmoNum(ws.getAmmoNumMax());
	}

	public double getBearingToVec3(Vec3 v) {
		
		double delta_x = v.xCoord- aircraft.posX;
		double delta_z = v.zCoord - aircraft.posZ;
		double angle = Math.atan2(delta_x, delta_z);
		angle = Math.toDegrees(angle);
		if(angle < 0) { angle += 360;}
		
		return angle;
	}
	
	private void updateSeat() {
		aircraft.onMountPlayerSeat(aircraft.getSeatByEntity(npc), npc);
	}
	
	private void spawnAircraft(ItemStack stack)
	{
		
		MCP_EntityPlane ac;// = new MCP_EntityPlane(npc.worldObj);
		MCP_ItemPlane item = (MCP_ItemPlane) stack.getItem();
		ac = item.createAircraft(npc.worldObj, npc.posX, npc.posY, npc.posZ, stack);
		ac.initRotationYaw(npc.rotationYaw);
		ac.getAcDataFromItem(stack);
		npc.worldObj.spawnEntityInWorld(ac);
		
		
		this.aircraft = ac;
		

		npc.mountEntity(ac);
		onMountHorse();
		updateSeat();
		aircraft.initCurrentWeapon(npc);
		aircraft.rotationYaw = -(float)getBearingToVec3(target);
		setFullAmmo();
	}

	public void onKilled()
	{
		if(aircraft!=null){
			onDismountHorse();
		}
		aircraft = null;
		
	}

	private void onMountHorse()
	{

	}

	private void onDismountHorse()
	{

	}



	public void readFromNBT(NBTTagCompound tag)
	{
		wasPlaneKilled = tag.getBoolean("wasHorseKilled");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("wasHorseKilled", wasPlaneKilled);
		return tag;
	}

}
