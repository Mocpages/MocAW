package net.shadowmage.ancientwarfare.npc.economy;

import net.minecraft.item.Item;

public class CommodityItem{
	public Item item;
	
	public CommodityItem(Item i) {
		item = i;
	}
	
	
	public boolean equals(Object o) {
		//System.out.println("Comparing");
		if(o instanceof CommodityItem) {
		//	System.out.println("Is commodity");
			CommodityItem c = (CommodityItem)o;
			return c.item == this.item;
		}
		return false;
	}
}
