package net.shadowmage.ancientwarfare.npc.economy;

import net.minecraft.nbt.NBTTagCompound;

public class UtilityHandler {
	public int transport,food,drink,clothing,entertainment,furniture,misc;
	
	public UtilityHandler() {
		transport= 0;
		food=0;
		drink=0;
		clothing=0;
		entertainment=0;
		furniture=0;
		misc=0;
	}
	
	public UtilityHandler(int t, int f, int d, int c, int e, int fu, int m) {
		transport = t;
		food = f;
		drink = d;
		clothing = c;
		entertainment = e;
		furniture = f;
		misc = m;
	}
	
	public void increaseCategory(int category, int amt) {
		switch(category) {
			case CommodityHelper.TRANSPORT:
				transport += amt;
				break;
			case CommodityHelper.FOOD:
				food += amt;
				break;
			case CommodityHelper.DRINK:
				drink += amt;
				break;
			case CommodityHelper.CLOTHING:
				clothing += amt;
				break;
			case CommodityHelper.ENTERTAINMENT:
				entertainment += amt;
				break;
			case CommodityHelper.FURNITURE:
				furniture += amt;
				break;
			case CommodityHelper.MISC:
				misc += amt;
				break;
		}
	}
	
	public double sqrt(int a) {
		return Math.pow(a, 0.5);
	}
	
	public double getUtility() {
		return sqrt(transport) + sqrt(food) + sqrt(drink) + sqrt(clothing) + sqrt(entertainment) + sqrt(furniture) + sqrt(misc);
	}
	
	public double getUtilityChange(int category, int amt) {
		double currentUtility = getUtility();
		UtilityHandler newUtility =  new UtilityHandler(this.transport, this.food, this.drink,this.clothing,this.entertainment,this.furniture,this.misc);
		newUtility.increaseCategory(category, amt);
		return newUtility.getUtility() - currentUtility;
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("transport", transport);
		tag.setInteger("food", food);
		tag.setInteger("drink", drink);
		tag.setInteger("clothing", clothing);
		tag.setInteger("entertainment", entertainment);
		tag.setInteger("furniture", furniture);
		tag.setInteger("misc", misc);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		transport = tag.getInteger("transport");
		food = tag.getInteger("food");
		drink = tag.getInteger("drink");
		clothing = tag.getInteger("clothing");
		entertainment = tag.getInteger("entertainment");
		furniture = tag.getInteger("furniture");
		misc = tag.getInteger("misc");
	}
}
