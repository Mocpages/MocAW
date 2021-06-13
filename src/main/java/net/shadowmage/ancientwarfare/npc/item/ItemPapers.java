package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemPapers extends Item{
	//String player = null;
	IIcon open = null;
	IIcon closed = null;
	
	public ItemPapers(String name){
	  super();
	  this.setCreativeTab(AWNpcItemLoader.npcTab);
	  
	  this.setUnlocalizedName(name);
	  this.setTextureName("ancientwarfare:npc/papers_closed");
	  
	  
	  }
	
	@Override
    public void registerIcons(IIconRegister reg){
		open = reg.registerIcon("ancientwarfare:npc/papers_open");
		closed = reg.registerIcon("ancientwarfare:npc/papers_closed");
	}


	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int i){
		if(i == 1) {
			return open;
		}else {
			return closed;
		}
    }

	@Override
    public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean isHeld) {
		if(isHeld) {
			stack.setItemDamage(1);
		}else {
			stack.setItemDamage(0);
		}
	}


	
}
