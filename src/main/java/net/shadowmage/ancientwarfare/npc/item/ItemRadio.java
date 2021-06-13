package net.shadowmage.ancientwarfare.npc.item;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedSoldier;

public class ItemRadio extends Item{


	public ItemRadio(String name){
		super();
		this.setCreativeTab(AWNpcItemLoader.npcTab);
		this.setUnlocalizedName(name);
		this.setTextureName("ancientwarfare:npc/radio");

	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 40;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		
		return itemstack;
	}
	
	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.addChatComponentMessage(new ChatComponentText("Flying home."));
			itemstack.stackSize --;
			
			
			ChunkCoordinates coords = player.getBedLocation(world.provider.dimensionId);
			
			NpcBanditMountedSoldier pilot = new NpcBanditMountedSoldier(player.worldObj);
			pilot.setPosition(player.posX, 150, player.posZ);
			pilot.ordersStack = new ItemStack(GameData.itemRegistry.get("mcheli:dc3"));
			player.addChatComponentMessage(new ChatComponentText("x: " + coords.posX + " z " + coords.posZ));
			pilot.pilotAI.target = Vec3.createVectorHelper(coords.posX , 0, coords.posZ );
			pilot.pilotAI.goalAltitude = 150;
			player.worldObj.spawnEntityInWorld(pilot);
			pilot.pilotAI.targetPlayer = player;
		}
		return itemstack;
	}

	@Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
		//if(world.isRemote) {
		//	player.addChatComponentMessage(new ChatComponentText("remote."));
		//	return;
		//}
		//if(useCount >= getMaxItemUseDuration(stack)) {
		//	player.addChatComponentMessage(new ChatComponentText("wo."));
		//}else {
		//	player.addChatComponentMessage(new ChatComponentText("Fuck " + useCount));
		//}
		
	}

	
	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) { return EnumAction.bow; }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_){
		return true;
	}
}
