package net.shadowmage.ancientwarfare.npc.economy;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class Bank {
	private double interestRate;
	private double bankCapital;
	private ArrayList<BankAccount> accounts;
	private ArrayList<Check> checks = new ArrayList<Check>();
	public boolean bankRun = false;
	
	public Bank() {
		interestRate = 0.0;
		bankCapital = 0.0;
		accounts = new ArrayList<BankAccount>();
	}

	public Bank(NBTTagCompound tag) {
		accounts = new ArrayList<BankAccount>();
		readFromNBT(tag);
	}

	public void setInterest(double i) {
		interestRate = i;
	}
	
	public double getInterest() {
		return interestRate;
	}
	
	public void addCheck(Check c) { checks.add(c);}
	
	public void resolveChecks() {
		for(Check c : checks) {
			c.resolve();
		}
	}
	
	public double withdraw(double amt) {
		if(amt <= bankCapital) {
			bankCapital -= amt;
			return amt;
		}else { //We're about to default
			amt = bankCapital;
			bankCapital = 0; 
			bankRun = true;
			return amt;
		}
	}
	
	public void deposit(double amt) {
		bankCapital += amt;
	}

	public BankAccount getAccountForNPC(NpcPlayerOwned npc) {
		for(BankAccount acct : accounts) {
			if(acct.isOwner(npc)) {
				return acct;
			}
		}
		return null;
	}
	
	public void openAccountForNPC(NpcPlayerOwned npc) {
		if(getAccountForNPC(npc)==null) {
			accounts.add(new BankAccountNPC(npc, this));
		}else {
			System.out.println("Attempted to open account where one already exists!");
		}
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		interestRate = tag.getDouble("interest");
		bankCapital = tag.getDouble("capital");
		
		NBTTagList acctList = tag.getTagList("acctList", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < acctList.tagCount(); i++) {
			accounts.add(new BankAccountNPC(acctList.getCompoundTagAt(i), this));
		}
	}


	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("interest", interestRate);
		tag.setDouble("capital", bankCapital);
		
		NBTTagList acctList = new NBTTagList();
		NBTTagCompound acctTag;  
		for(BankAccount acct : accounts) {
			acctTag = new NBTTagCompound();
			acct.writeToNBT(acctTag);
			acctList.appendTag(acctTag);
		}
		tag.setTag("acctList", acctList);
	}

	public void updateInterest() {
		for(BankAccount acct : accounts) {
			acct.updateInterest(interestRate);
		}
		
	}
}
