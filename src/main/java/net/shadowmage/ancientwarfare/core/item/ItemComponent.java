package net.shadowmage.ancientwarfare.core.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

public class ItemComponent extends Item
{

/**
 * automation module
 */
public static int WOODEN_GEAR_SET = 0;
public static int IRON_GEAR_SET = 1;
public static int STEEL_GEAR_SET = 2;
public static int WOODEN_BUSHINGS = 3;
public static int IRON_BEARINGS = 4;
public static int STEEL_BEARINGS = 5;
public static int WOODEN_TORQUE_SHAFT = 6;
public static int IRON_TORQUE_SHAFT = 7;
public static int STEEL_TORQUE_SHAFT = 8;

/**
 * npc module
 * TODO
 */
public static int NPC_FOOD_BUNDLE = 100;

HashMap<Integer, String> subItems = new HashMap<Integer, String>();
HashMap<Integer, IIcon> subItemIcons = new HashMap<Integer, IIcon>();

public ItemComponent(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setHasSubtypes(true);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {
  return super.getUnlocalizedName(par1ItemStack)+"."+par1ItemStack.getItemDamage();
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void getSubItems(Item item, CreativeTabs p_150895_2_, List list)
  {
  for(Integer num : subItems.keySet())
    {
    list.add(new ItemStack(item,1,num));
    }
  }

@Override
public IIcon getIconFromDamage(int par1)
  {
  return subItemIcons.get(par1);
  }

@Override
public void registerIcons(IIconRegister par1IconRegister)
  {
  for(Integer num : subItems.keySet())
    {
    subItemIcons.put(num, par1IconRegister.registerIcon(subItems.get(num)));
    }
  }

public void addSubItem(int num, String texture)
  {
  subItems.put(num, texture);
  }

}
