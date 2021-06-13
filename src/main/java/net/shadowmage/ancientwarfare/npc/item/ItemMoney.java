package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.Item;

public class ItemMoney extends Item{
	
	
	public ItemMoney(String name){
	  super();
	  this.setCreativeTab(AWNpcItemLoader.npcTab);
	  this.setUnlocalizedName(name);
	  this.setTextureName("ancientwarfare:npc/money");
	  
	  }
}
