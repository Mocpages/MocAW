package net.shadowmage.ancientwarfare.npc.entity.faction;

import lotr.common.item.LOTRItemMug;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDrinkingContest;

public class NpcBanditPriest extends NpcFaction
{
public NpcAIDrinkingContest drink;
public NpcBanditPriest(World par1World) {
  super(par1World);

  this.tasks.addTask(103, drink = new NpcAIDrinkingContest(this));

}

@Override
protected boolean interact(EntityPlayer player){
  if(player.worldObj.isRemote){return false;}

  if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof LOTRItemMug){
    drink.targetPlayer = player;
    return true;
  }else{
    return super.interact(player);
  }
}


@Override
public String getNpcType()
  {
  return "bandit.priest";
  }

}
