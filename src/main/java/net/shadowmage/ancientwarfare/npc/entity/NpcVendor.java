package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;

public interface NpcVendor {
	
	public float getPrice(ItemStack item, NpcPlayerOwned npc);
	public boolean buy(ItemStack stack, NpcPlayerOwned npc);
	public InventoryBackpack getSaleInventory();
	public NpcPlayerOwned getNpc();
}
