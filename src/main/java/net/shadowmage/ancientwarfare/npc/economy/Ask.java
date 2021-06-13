package net.shadowmage.ancientwarfare.npc.economy;

import net.minecraft.item.Item;

public class Ask {
	private BankAccount account;
	public Item item;
	public int qty;
	public double pricePer;
	
	public Ask(BankAccount act, Item i, int q, double price) {
		account = act;
		item = i;
		qty = q;
		pricePer = price;
	}
	
	public Check writeCheck() {
		return Check.write(account, pricePer);
	}
}
