package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class Battle {
    public static final int SECOND = 20;
    public static final int MINUTE = 60 * SECOND;
    public int battleTimer = 15 * MINUTE;
    public World worldObj;
    public Base base;

    public Battle(World w, Base b){
        worldObj = w;
        base = b;
    }

    public void printTimer(){
        if(battleTimer > MINUTE && battleTimer % MINUTE == 0){
            printToAll("The battle of " + base.name + " will begin in " + battleTimer / MINUTE + " minutes.", EnumChatFormatting.YELLOW);
        }else if(battleTimer >= 0 && battleTimer % (15 * SECOND) == 0){
            printToAll("The battle of " + base.name + " will begin in " + battleTimer / SECOND + " seconds.", EnumChatFormatting.RED);
        }else if(0 < battleTimer && battleTimer <= 10*SECOND && battleTimer % SECOND == 0){
            printToAll("The battle of " + base.name + " will begin in " + battleTimer / SECOND + " seconds.", EnumChatFormatting.DARK_RED);
        }
    }



    public void printToAll(String s, EnumChatFormatting color) {
        System.out.println("TEST " + s);
        for(Object o : this.worldObj.playerEntities) {
            if(o instanceof EntityPlayer) {
                print((EntityPlayer)o, s, color);
            }
        }
    }

    public static void print(EntityPlayer player, String message, EnumChatFormatting color) {
        ChatComponentText text = new ChatComponentText(message);
        text.getChatStyle().setColor(color);
        player.addChatComponentMessage(text);
    }


    public void onUpdate(){

        System.out.println("Battle ticking");
        printTimer();
        battleTimer --;

    }

}
