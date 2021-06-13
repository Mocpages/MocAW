package net.shadowmage.ancientwarfare.npc.economy;

import net.minecraft.item.Item;

public class Commodity {
	public int category;
	public int utility;
	public Item item;
	
	public Commodity(int c, int u, Item i) {
		category = c;
		utility = u;
		item = i;
	}
}
