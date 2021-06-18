package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.gamedata.Base;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;

import java.util.List;

public class CommandBase  implements ICommand {

    @Override
    public String getCommandName() {
        return "base";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    public static void print(ICommandSender player, String message, EnumChatFormatting color) {
        System.out.println("Attempting print: " + message);
        ChatComponentText text = new ChatComponentText(message);
        text.getChatStyle().setColor(color);
        player.addChatMessage(text);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(!(sender instanceof EntityPlayer)){return;}
        EntityPlayer player = (EntityPlayer)sender;
        MocData data = AWGameData.INSTANCE.getData(MocData.name,player.getEntityWorld(), MocData.class);
        MocFaction fac = data.getFaction(player);
        if(fac == null){
            print(player, "Error: you are not in a faction!", EnumChatFormatting.RED);
            return;
        }

        if(args[0].equalsIgnoreCase("create")){
            Base b = new Base(args[1], player.getCommandSenderName());
            b.claimChunk(player.posX, player.posZ);
            fac.bases.add(b);
            print(player,"Created base.", EnumChatFormatting.GREEN);
        }else if(args[0].equalsIgnoreCase("claim")){
            Base b = fac.getBase(args[1]);
            if(b == null){
                print(player, "Error: No base with name " + args[1] + " in faction " + fac.name, EnumChatFormatting.RED);
                return;
            }
            if(b.isAdjacent((int)player.posX/16, (int)player.posZ/16)) {
                b.claimChunk((int)player.posX, (int)player.posZ);
                print(player, "Claimed chunk.", EnumChatFormatting.GREEN);
            }else{
                print(player, "Error: Chunk is not adjacent!", EnumChatFormatting.RED);
            }

        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
