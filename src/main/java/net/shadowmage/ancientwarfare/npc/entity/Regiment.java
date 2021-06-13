package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

public class Regiment {
	public ItemStack batonStack;
	public World worldObj;
	public double angle;
	public ArrayList<NpcBase> troops = new ArrayList<NpcBase>();
	public NpcCombat commander;
	public int frontage;
	
	
	public Regiment(ItemStack baton, World w, NpcCombat cdr) {
		batonStack = baton;
		worldObj = w;
		commander = cdr;
		loadNpcs();
	}
	
	public void loadNpcs(){
    	ItemCommandBaton.getCommandedEntities(this.worldObj, batonStack, troops);
	}
	
	
	
	
	public static void handleFormation(List<NpcBase> targets, double angle, int files) {
		if(targets.size() <= files) { //we only need one rank!
			handleRow(targets,angle);
		}else {
			NpcBase guide = targets.get(0);
			handleRow(targets.subList(0, files),angle); //handle this rank
			targets = targets.subList(files, targets.size()); //get all the remaining ranks
			
			//set guide for next rank
			if(targets.size() == 0) { return;}
			NpcBase rowLead = targets.get(0);
			rowLead.setFollowingEntity(guide);
			rowLead.xOff = 0;
			rowLead.zOff = -1.25;
			rowLead.angle = angle;
			handleFormation(targets, angle, files);
		}
	}

	public static void handleRow(List<NpcBase> targets, double angle) {
		NpcBase guide = targets.get(0);
		for(NpcBase n : targets) {
			if(n != guide) {
				
				//n.setAIMoveSpeed(guide.getAIMoveSpeed() * 2);
				n.rotationYaw = (float) angle;
				n.setFollowingEntity(guide);
				n.xOff = -1.25;
				n.zOff = 0;
				n.angle = angle;
				guide = n;
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

	public void handleOrder(int x, int y, int z) {
		
		
	}


}
