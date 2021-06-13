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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;

public class ItemDossier extends Item{
	//String player = null;
	IIcon dossier = null;
	IIcon blackmail = null;
	
	public ItemDossier(String name){
	  super();
	  this.setCreativeTab(AWNpcItemLoader.npcTab);
	  
	  this.setUnlocalizedName(name);
	  this.setTextureName("ancientwarfare:npc/dossier");
	  
	  
	  }
	
	@Override
    public void registerIcons(IIconRegister reg){
		dossier = reg.registerIcon("ancientwarfare:npc/dossier");
		blackmail = reg.registerIcon("ancientwarfare:npc/blackmail");
	}


	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int i){
		if(i == 1) {
			return blackmail;
		}else {
			return dossier;
		}
    }
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining){
        Item i = stack.getItem();
        if(!(i instanceof ItemDossier)) {return getIcon(stack, renderPass);}
        
        if(!isBlackmail(stack)) {
        	return dossier;
        }else {
        	return blackmail;
        }
        
    }
	
	public boolean isBlackmail(ItemStack d) {
		if(!d.hasTagCompound()) {return false;}
		NBTTagCompound tag = d.getTagCompound();
		if(!tag.hasKey("player")) {return false;}
		return true;
	}
	
	public String getTarget(ItemStack d) {
		if(!d.hasTagCompound()) {return "";}
		NBTTagCompound tag = d.getTagCompound();
		if(!tag.hasKey("player")) {return "";}
		return tag.getString("player");
	}
	
	
	public double getBearingToEntity(Entity a, Entity e) {
		
		double delta_x = e.posX- a.posX;
		double delta_z = a.posZ - e.posZ;
		double angle = Math.atan2(delta_x, delta_z);
		angle = Math.toDegrees(angle);
		if(angle < 0) { angle += 360;}
		
		return angle;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(player.worldObj.isRemote) {return stack;}
		NBTTagCompound nbt;
		if (stack.hasTagCompound()){
			nbt = stack.getTagCompound();
		}
		else{
			nbt = new NBTTagCompound();
		}

		if(nbt.hasKey("player")) {
			return stack;
		}


		if(!player.isSneaking()) {
		    MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, 2, 0);//TODO set range from config;
		    if(pos!=null && pos.typeOfHit==MovingObjectType.ENTITY && pos.entityHit instanceof EntityPlayer) {
		    	EntityPlayer target = (EntityPlayer)pos.entityHit;
		    	nbt.setString("player", target.getCommandSenderName());
		    	player.addChatComponentMessage(new ChatComponentText("Stole blackmail on " + target.getDisplayName()));
		    	
		    }
		}else {
			player.addChatComponentMessage(new ChatComponentText("Set blackmail to self."));
			nbt.setString("player", player.getCommandSenderName());
			stack.setItemDamage(1);
		}

		stack.setTagCompound(nbt);
		return stack;
	}

	@Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List info, boolean p_77624_4_) {
		String s = "";
		if(!isBlackmail(p_77624_1_)) {
			s = " None.";
		}else {
			s = getTarget(p_77624_1_);
		}
		info.add("Player: " + s);
	}

	
}
