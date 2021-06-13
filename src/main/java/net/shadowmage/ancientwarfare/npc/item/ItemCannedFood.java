package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.ItemFood;

public class ItemCannedFood extends ItemFood{
	
	public ItemCannedFood(String name){
		  super(8, 13, false);
		  this.setCreativeTab(AWNpcItemLoader.npcTab);
		  this.setUnlocalizedName(name);
		  this.setTextureName("ancientwarfare:npc/cannedfood");	  
	}
}
