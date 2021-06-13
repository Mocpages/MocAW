package net.shadowmage.ancientwarfare.npc.entity.faction;

import mcheli.container.MCH_EntityContainer;
import mcheli.parachute.MCH_EntityParachute;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class NpcBanditMountedSoldier extends NpcFactionPilot
{

public NpcBanditMountedSoldier(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.cavalry";
  }

	@Override
	public void onDeath(DamageSource source) {
		// player: source.getSourceOfDamage()
		//System.out.println("Wo");
		MCH_EntityContainer container = new MCH_EntityContainer(worldObj, posX, 255, posZ);
		container.rotationYaw = 0;
		InventoryTools.mergeItemStack(container, new ItemStack(Items.apple), 1);
		
		//worldObj.spawnEntityInWorld(container);
		
		MCH_EntityParachute parachute = new MCH_EntityParachute(worldObj, posX, 255, posZ);
		parachute.user = container;
		parachute.rotationYaw = 0;
		parachute.setType(3);
		//super.worldObj.spawnEntityInWorld(parachute);
		
		
		//ChatComponentText text = new ChatComponentText("Spawning crate at " + (int)posX + " " + (int)posZ);
		//text.getChatStyle().setColor(EnumChatFormatting.RED);
	//	MinecraftServer.getServer().addChatMessage(text);
	}

}
