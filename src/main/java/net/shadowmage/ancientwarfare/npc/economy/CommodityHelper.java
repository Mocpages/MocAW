package net.shadowmage.ancientwarfare.npc.economy;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CommodityHelper {
	public static final int TRANSPORT = 0;
	public static final int FOOD = 1;
	public static final int DRINK = 2;
	public static final int CLOTHING = 3;
	public static final int ENTERTAINMENT = 4;
	public static final int FURNITURE = 5;
	public static final int MISC = 6;
	
	static Item shoes = Items.leather_boots;
	static Commodity shoesCommodity = new Commodity(TRANSPORT, 1, shoes);
	
	static Item fruit = Items.apple;
	static Commodity fruitCommodity = new Commodity(FOOD, 1, fruit);
	
	static Item newspaper = Items.paper;
	static Commodity newspaperCommodity = new Commodity(ENTERTAINMENT, 1, newspaper);
	
	
	public static ArrayList<Commodity> getCommodities(){
		ArrayList<Commodity> commodities = new ArrayList<Commodity>();
		
		commodities.add(shoesCommodity);
		commodities.add(fruitCommodity);
		commodities.add(newspaperCommodity);
		
		return commodities;
	}
	
}
