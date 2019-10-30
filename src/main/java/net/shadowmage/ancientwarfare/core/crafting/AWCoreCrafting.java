package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public class AWCoreCrafting
{

/**
 * load any recipes for CORE module (research book, engineering station, research station)
 */
public static void loadRecipes()
  {
  RecipeSorter.register("ancientwarfare:researched", RecipeResearched.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
  CraftingManager.getInstance().addRecipe(new ItemStack(AWItems.researchBook), new Object[]{"ILL", "PPP", "ILL", 'I', Items.iron_ingot, 'L', Items.leather, 'P', Items.paper});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.engineeringStation), new Object[]{"IWI", "IPI", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.researchStation), new Object[]{"IWI", "GPG", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest, 'G', Items.gold_ingot});
    
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerWood),  "engineering",
      "_s_",
      "msm",
      "_s_",
      'm', Blocks.planks,
      's', Items.stick);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerStone),  "engineering",
      "_s_",
      "msm",
      "_s_",
      'm', Blocks.stone,
      's', Items.stick);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerIron),  "engineering",
      "_s_",
      "msm",
      "_s_",
      'm', Items.iron_ingot,
      's', Items.stick);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerGold),  "engineering",
      "_s_",
      "msm",
      "_s_",
      'm', Items.gold_ingot,
      's', Items.stick);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerDiamond),  "engineering",
      "_s_",
      "msm",
      "_s_",
      'm', Items.diamond,
      's', Items.stick);

  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillWood),  "engineering",
      "__f",
      "_s_",
      "m__",
      'm', Blocks.planks,
      's', Items.stick,
      'f', Items.feather);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillStone),  "engineering",
      "__f",
      "_s_",
      "m__",
      'm', Blocks.stone,
      's', Items.stick,
      'f', Items.feather);
  
  AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillIron),  "engineering",
      "__f",
      "_s_",
      "m__",
      'm', Items.iron_ingot,
      's', Items.stick,
      'f', Items.feather);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillGold),  "engineering",
      "__f",
      "_s_",
      "m__",
      'm', Items.gold_ingot,
      's', Items.stick,
      'f', Items.feather);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillDiamond),  "engineering",
      "__f",
      "_s_",
      "m__",
      'm', Items.diamond,
      's', Items.stick,
      'f', Items.feather);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.steel_ingot),  "refining",
      "c",
      "i",
      'c', Items.coal,
      'i', Items.iron_ingot);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack,1,0),  "trade",
      "lwl",
      "www",
      "lwl",
      'l', Items.leather,
      'w', Blocks.wool);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack,1,1),  "trade",
      "lwl",
      "wcw",
      "lwl",
      'l', Items.leather,
      'w', Blocks.wool,
      'c', Blocks.chest);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack,1,2),  "trade",
      "lcl",
      "wcw",
      "lwl",
      'l', Items.leather,
      'w', Blocks.wool,
      'c', Blocks.chest);
  
   AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack,1,3),  "trade",
      "lcl",
      "wcw",
      "lcl",
      'l', Items.leather,
      'w', Blocks.wool,
      'c', Blocks.chest);
  
  }

}
