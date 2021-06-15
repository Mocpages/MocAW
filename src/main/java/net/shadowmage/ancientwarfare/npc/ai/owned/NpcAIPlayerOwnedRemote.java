package net.shadowmage.ancientwarfare.npc.ai.owned;

import mcheli.MCH_Camera;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedRemote extends NpcAI {
    public MCH_Camera camera = new MCH_Camera(npc.worldObj, npc, npc.posX, npc.posY, npc.posZ);
    public EntityPlayer controller;

    public NpcAIPlayerOwnedRemote(NpcBase npc) {
        super(npc);
        this.setMutexBits(ATTACK+MOVE);
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if(!npc.getIsAIEnabled()){return false;}
        return controller != null;
    }


    @Override
    public void startExecuting(){

    }

    @Override
    public void updateTask(){

    }
}
