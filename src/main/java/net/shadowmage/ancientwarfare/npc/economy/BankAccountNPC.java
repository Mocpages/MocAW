package net.shadowmage.ancientwarfare.npc.economy;

import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class BankAccountNPC extends BankAccount{
	UUID entityID;
	
	public BankAccountNPC(NpcPlayerOwned npc, Bank b) {
		super(b);
		entityID = npc.getPersistentID();
	}
	
	public BankAccountNPC(NBTTagCompound tag, Bank b) {
		super(tag, b);
		readFromNBT(tag);
	}

	@Override
	public boolean isOwner(EntityLiving e) {
		return e.getPersistentID().equals(entityID);
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(entityID!=null){
		    tag.setLong("idmsb", entityID.getMostSignificantBits());
		    tag.setLong("idlsb", entityID.getLeastSignificantBits());
	    }
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("idmsb") && tag.hasKey("idlsb")){
		   entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
		}
	}

}
