package net.shadowmage.ancientwarfare.npc.economy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BankAccount {
	private double balance;
	protected Bank bank;
	

	public BankAccount(Bank b) {
		balance = 0.0;
		bank = b;
	}
	
	public BankAccount(NBTTagCompound tag, Bank b) {
		readFromNBT(tag);
		bank = b;
	}
	
	public abstract boolean isOwner(EntityLiving e);
	
	public void setBalance(double b) {
		balance=b;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void deposit(double d) {
		balance += d;
		bank.deposit(d);
	}
	
	//Returns the amount actually withdrawn - EG if we attempt to overdraw
	public double withdraw(double d) {
		if(d > balance) {
			d = balance;
		}
		d = bank.withdraw(d); //In case the bank doesn't have enough capital to cover this withdrawal
		balance -= d;
		return d;
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("balance", balance);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		balance = tag.getDouble("balance");
	}

	public void updateInterest(double interest) {
		balance += balance * interest;
	}
}
