package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;

public class Base {
    ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
    public String name;
    public String owner;
    public ArrayList<String> members = new ArrayList<String>();
    public BlockPosition spawn;

    public Base(String n, String o){
        name = n;
        owner = o;
    }


    public Base(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public void claimChunk(double x, double z){
        ChunkCoordIntPair coords = new ChunkCoordIntPair((int)x / 16, (int)z / 16);
        if(!isChunkClaimed((int)x/16, (int)z/16)){chunks.add(coords);}
    }

    public boolean isAdjacent(int x, int z){
        if(isChunkClaimed(x+1, z) || isChunkClaimed(x-1, z) || isChunkClaimed(x, z+1) || isChunkClaimed(x, z-1)){
            return true;
        }
        return false;
    }

    public boolean isChunkClaimed(int x, int z){
        for(ChunkCoordIntPair c : chunks){
            if(c.chunkXPos == x && c.chunkZPos == z){
                return true;
            }
        }
        return false;
    }

    public boolean canPlayerAccess(EntityPlayer player){
        if(members.contains(player.getCommandSenderName())){return true;} //If they're on the base whiteliset they're in the base access list, full stop
        MocData data = AWGameData.INSTANCE.getData(MocData.name, player.getEntityWorld(), MocData.class);
        MocFaction ownerFac = data.getFaction(owner);
        if(ownerFac == null){return false;} //This should never happen but knowing players a base owner will leave their faction and it'll be a fuck
        if(ownerFac.players.contains(player.getCommandSenderName())){return true;} //If player is in the same fac as our owner they auto have access
        return false; //else they're neither in fac or whitelist so they can get fucked lol
    }

    public void readFromNBT(NBTTagCompound tag) {

    }


    public void writeToNBT(NBTTagCompound tag) {

    }

}
