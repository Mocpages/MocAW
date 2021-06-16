package net.shadowmage.ancientwarfare.npc.ai;

import lotr.common.item.LOTRItemMug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIDrinkingContest extends NpcAI{
    public EntityPlayer targetPlayer;
    public int timer = 0;

    public NpcAIDrinkingContest(NpcBase npc) {
        super(npc);
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }

    @Override
    public boolean continueExecuting() {
        if(!npc.getIsAIEnabled()){return false;}
        return true;
    }

    public void startExecuting() {
        timer = 5 * 20;
    }

    @Override
    public void updateTask(){
        if(targetPlayer == null){ return;}
        if(timer == 0){return;}
        timer --;
        if(timer == 3 * 20){targetPlayer.addChatComponentMessage(new ChatComponentText("3"));}
        if(timer == 2 * 20){targetPlayer.addChatComponentMessage(new ChatComponentText("2"));}
        if(timer == 1 * 20){targetPlayer.addChatComponentMessage(new ChatComponentText("1"));}

        if(timer > 1){return;}

        ItemStack stack = targetPlayer.getHeldItem();
        if(stack == null){return;}
        Item item = stack.getItem();
        if(item instanceof LOTRItemMug){
            LOTRItemMug mug = (LOTRItemMug) item;
            double alc = 10* mug.alcoholicity * mug.getStrength(stack);
            if(alc <= 15.0){
               // targetPlayer.addChatComponentMessage(new ChatComponentText("Alc: " + alc));
            }else{
              //  targetPlayer.addChatComponentMessage(new ChatComponentText("Alc 2: " + alc));
                mug.func_77654_b(stack, targetPlayer.worldObj, targetPlayer); //onEaten

            }
        }
    }
}
