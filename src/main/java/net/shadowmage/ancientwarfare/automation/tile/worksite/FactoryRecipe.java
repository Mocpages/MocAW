package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class FactoryRecipe {
	public InventoryBackpack output;
	public InventoryBackpack input;
	public InventoryBackpack constructionItems;

	
	public FactoryRecipe() {
		output = new InventoryBackpack(27);
		input = new InventoryBackpack(27);
		constructionItems = new InventoryBackpack(27);
	}
	
	public void readFromNBT(NBTTagCompound tag){
		InventoryTools.readInventoryFromNBT(output, tag.getCompoundTag("output"));
		InventoryTools.readInventoryFromNBT(input, tag.getCompoundTag("output"));
		InventoryTools.readInventoryFromNBT(constructionItems, tag.getCompoundTag("output"));
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound invTag = new NBTTagCompound();
		InventoryTools.writeInventoryToNBT(output, invTag);
		tag.setTag("output", invTag);
		
		NBTTagCompound invTag2 = new NBTTagCompound();
		InventoryTools.writeInventoryToNBT(input, invTag2);
		tag.setTag("input", invTag2);
		
		NBTTagCompound invTag3 = new NBTTagCompound();
		InventoryTools.writeInventoryToNBT(constructionItems, invTag3);
		tag.setTag("constructionItems", invTag3);
	}
	
	public boolean isInOutput(ItemStack stack) {
		return InventoryTools.getCountOf(output, -1, stack) > 0;
	}
	
	public static FactoryRecipe getDistillery() {
		FactoryRecipe recipe = new FactoryRecipe();
		
		//Add items to the input
		InventoryTools.mergeItemStack(recipe.input, new ItemStack(Items.sugar, 5), 0);
		
		//Add items to the output
		//InventoryTools.mergeItemStack(recipe.output, new ItemStack(AWNpcItemLoader.rum, 5), 0);
		
		//Add items to the construction cost TODO
		InventoryTools.mergeItemStack(recipe.constructionItems, new ItemStack(Items.sugar, 5), 0);
		InventoryTools.mergeItemStack(recipe.constructionItems, new ItemStack(Items.sugar, 5), 0);
		
		//connn = new ItemStack[] {new ItemStack(Items.iron_ingot, 60), new ItemStack(Items.iron_ingot, 60)};
		//FactoryRecipe recipe = new FactoryRecipe(new ItemStack(AWNpcItemLoader.fabric, 45), innies, connn) ;
		return recipe;
	}
	
	public static ArrayList<FactoryRecipe> getAllRecipes(){
		ArrayList<FactoryRecipe> recipes = new ArrayList<FactoryRecipe>();
		
		recipes.add(getDistillery());
		
		return recipes;
	}
	
	//public static FactoryRecipe getWeaver() {
	//	innies = new ItemStack[]{new ItemStack(AWNpcItemLoader.cotton, 18), new ItemStack(AWNpcItemLoader.dye, 2)};
		//FactoryRecipe weaver = new FactoryRecipe(new ItemStack(AWNpcItemLoader.fabric, 45), innies, connn) ;

	//}
	//Weaver
}
