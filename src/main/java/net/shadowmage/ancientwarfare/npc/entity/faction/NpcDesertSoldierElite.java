package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIChargeAttack;

public class NpcDesertSoldierElite extends NpcFactionSoldier{
public NpcDesertSoldierElite(World par1World){
  super(par1World);
  this.tasks.addTask(2, new NpcAIChargeAttack(this));

  }

@Override
public String getNpcType()
  {
  return "desert.soldier.elite";
  }

}
