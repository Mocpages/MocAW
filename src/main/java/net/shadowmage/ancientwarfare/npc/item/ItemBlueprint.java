package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.Item;

public class ItemBlueprint extends Item{
	String type;
	
	
	public ItemBlueprint(String name, String t){
	  super();
	  this.setCreativeTab(AWNpcItemLoader.npcTab);
	  this.setUnlocalizedName(name);
	  this.setTextureName("ancientwarfare:automation/blueprint");
	  this.type = t;
	  
	  }
	
	public String getType() {
		return type;
		
	}
	
	
}
