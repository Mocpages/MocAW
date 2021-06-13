package net.shadowmage.ancientwarfare.npc.entity.faction;

import java.util.ArrayList;
import java.util.List;

import mcheli.MCH_MOD;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionFindCommander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionPilot;

public abstract class NpcFactionPilot extends NpcFactionMounted{

	public NpcAIFactionPilot pilotAI;
	private Ticket loaderTicket;
	
public NpcFactionPilot(World par1World)
  {
  super(par1World);
  init(ForgeChunkManager.requestTicket(MCH_MOD.instance, worldObj, Type.ENTITY));
//  this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));  
  
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity entity)
      {
      return isHostileTowards(entity);
      }    
    };
    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (pilotAI=new NpcAIFactionPilot(this)));
  this.tasks.addTask(1, new NpcAIFactionFindCommander(this));  
  this.tasks.addTask(1, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f));   
  this.tasks.addTask(3, new NpcAIAttackMeleeLongRange(this));

  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));      
  
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }


@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  if(tag.hasKey("pilotAI")){pilotAI.readFromNBT(tag.getCompoundTag("pilotAI"));}
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  if(pilotAI!=null){tag.setTag("pilotAI", pilotAI.writeToNBT(new NBTTagCompound()));}
  }


//Chunk loading code courtesy of HBM's nuclear tech mod https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT/
	public void init(Ticket ticket) {
		if(!worldObj.isRemote) {
            if(ticket != null) {
                if(loaderTicket == null) {
                	loaderTicket = ticket;
                	loaderTicket.bindEntity(this);
                	loaderTicket.getModData();
                }
                ForgeChunkManager.forceChunk(loaderTicket, new ChunkCoordIntPair(chunkCoordX, chunkCoordZ));
            }
        }
	}
	
	List<ChunkCoordIntPair> loadedChunks = new ArrayList<ChunkCoordIntPair>();

	public void loadNeighboringChunks(int newChunkX, int newChunkZ)
	{
		if(!worldObj.isRemote && loaderTicket != null)
		{
			for(ChunkCoordIntPair chunk : loadedChunks)
			{
				ForgeChunkManager.unforceChunk(loaderTicket, chunk);
			}

			loadedChunks.clear();
			loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ + 1));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ - 1));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ - 1));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ + 1));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ + 1));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ));
			loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ - 1));

			for(ChunkCoordIntPair chunk : loadedChunks)
			{
				ForgeChunkManager.forceChunk(loaderTicket, chunk);
			}
		}
	}


}
