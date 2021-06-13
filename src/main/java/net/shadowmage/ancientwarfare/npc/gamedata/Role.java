package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public abstract class Role {
	public ArrayList<ItemHelper> items = new ArrayList<ItemHelper>();
	public String name;
	
	public Role(String n) {
		name = n;
	}
	
	public void addItem(ItemStack item, int slot) {

		NBTTagCompound tag;// = stack.getTagCompound();
		if(item.hasTagCompound()) {
			tag = item.getTagCompound();
		}else {
			tag = new NBTTagCompound();
		}
		tag.setString("role", name);
		item.setTagCompound(tag);
		items.add(new ItemHelper(item, slot));
	}
	
	public void cleanItems(EntityPlayer p) {
		for(int i = 0; i< p.inventory.getSizeInventory(); i++) {
			if(p.inventory.getStackInSlot(i).getTagCompound().hasKey("role")) {
				if(p.inventory.getStackInSlot(i).getTagCompound().getString("role") != name) {
					p.inventory.setInventorySlotContents(i, null);
				}
			}
		}
	}
	
	public void addItems(EntityPlayer p) {
		for(ItemHelper i : items) {
			p.inventory.setInventorySlotContents(i.slot, i.item);
		}
	}
	
	public void onUpdate(EntityPlayer p) {
		cleanItems(p);
	}
	
	public class ItemHelper{
		ItemStack item;
		int slot;
		
		public ItemHelper(ItemStack i, int s) {
			item = i;
			slot = s;
		}
	}
}
