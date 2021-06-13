package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileBuilding extends TileEntity implements IOwnable, IInteractableTile{

	private String ownerName = "";
	private int mode = 0; // -1 = building set; 0 = building not set; 1 = setting pos 1; 2 = setting pos 2
	public BlockPosition pos1 = new BlockPosition(this.xCoord, yCoord, zCoord);
	public BlockPosition pos2 = new BlockPosition(xCoord, yCoord, zCoord);

	@Override
	public boolean canUpdate(){
		return true;
	}


	@Override
	public boolean onBlockClicked(EntityPlayer player){
		if(!player.worldObj.isRemote){
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SETUP_BUILDING, xCoord, yCoord, zCoord);
		}

		return false;
	}
	
	AxisAlignedBB getBuildingBB() {
		return AxisAlignedBB.getBoundingBox(pos1.x, pos1.y, pos1.z, pos2.x+1, pos2.y+1, pos2.z+1);
	}
	
	public boolean doesCollide(TileBuilding b) {
		AxisAlignedBB bb = this.getBuildingBB();
		return bb.intersectsWith(b.getBuildingBB());
	}
	
	//true if we are colliding false otherwise
	public boolean checkCollisions() {
		for(Object o: worldObj.loadedTileEntityList) {
			if(o != this && o instanceof TileBuilding) { //&& ((TileBuilding)o).getDistanceFrom(xCoord, yCoord, zCoord) <= 64 * 64) {
				//System.out.println("found building");
				if(doesCollide((TileBuilding)o)) {
					//System.out.println("Woo!");
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isContained() {
		int minX = Math.min(pos1.x, pos2.x);
		int maxX = Math.max(pos1.x, pos2.x);
		int minZ = Math.min(pos1.z, pos2.z);
		int maxZ = Math.max(pos1.z, pos2.z);
		int minY = Math.min(pos1.y, pos2.y);
		int maxY = Math.max(pos1.y, pos2.y);
		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				for(int y = minY; y <= maxY; y++) {
					//if(y == maxY) {//If we're in the top tier, check the block above us
					//	if(worldObj.getBlock(x, y+1, z) == Blocks.air) { return false;}
					//}
					if(y == minY) {//If we're in the bottom then check the floor
						if(worldObj.getBlock(x, y-1, z) == Blocks.air) { return false;}
					}
					
					if(y >= worldObj.getTopSolidOrLiquidBlock(x, z) ) { return false;}
					if(x == maxX) {//Check x+1 block
						if(worldObj.getBlock(x+1, y, z) == Blocks.air) { return false;}
					}
					if(x == minX) {//Check x-1 block
						if(worldObj.getBlock(x-1, y, z) == Blocks.air) { return false;}
					}
					
					if(z == maxZ) {//Check z+1
						if(worldObj.getBlock(x, y, z+1) == Blocks.air) { return false;}
					}
					if(z == minZ) {//Check z-1
						if(worldObj.getBlock(x, y, z-1) == Blocks.air) { return false;}
					}
				}
			}
		}
		return true;
	}
	
	
	//check if the block is inside its own bounds
	public boolean isInBounds() {
		int minX = Math.min(pos1.x, pos2.x);
		int maxX = Math.max(pos1.x, pos2.x);
		int minZ = Math.min(pos1.z, pos2.z);
		int maxZ = Math.max(pos1.z, pos2.z);
		int minY = Math.min(pos1.y, pos2.y);
		int maxY = Math.max(pos1.y, pos2.y);
		
		if(minX <= this.xCoord && maxX >= this.xCoord) {
			if(minY <= this.yCoord && maxY >= this.yCoord) {
				if(minZ <= this.zCoord && maxZ >= this.zCoord) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isValid() {
		if(checkCollisions()) {return false;}
		if(!isContained()) {return false;}
		if(!isInBounds()) {return false;}
		return true;
	}


	@Override
	public void updateEntity(){
		if(worldObj.isRemote){return;}
	}

	@Override
	public void setOwnerName(String name) {
		this.ownerName = name;

	}


	@Override
	public String getOwnerName() {
		return ownerName;
	}

	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	  {
	  super.readFromNBT(tag);
	  if(tag.hasKey("pos1"))
	    {
	    pos1 = new BlockPosition();
	    pos1.read(tag.getCompoundTag("pos1"));
	    }
	  if(tag.hasKey("pos2"))
	    {
	    pos2 = new BlockPosition();
	    pos2.read(tag.getCompoundTag("pos2"));
	    }
	  if(pos2==null){pos2=new BlockPosition(xCoord, yCoord, zCoord+1);}
	  if(pos1==null){pos1=new BlockPosition(xCoord, yCoord, zCoord+1);}
	  }

	@Override
	public void writeToNBT(NBTTagCompound tag)
	  {
	  super.writeToNBT(tag);
	  if(pos1!=null)
	    {
	    NBTTagCompound innerTag = new NBTTagCompound();
	    pos1.writeToNBT(innerTag);
	    tag.setTag("pos1", innerTag);
	    }
	  if(pos2!=null)
	    {
	    NBTTagCompound innerTag = new NBTTagCompound();
	    pos2.writeToNBT(innerTag);
	    tag.setTag("pos2", innerTag);
	    }
	  }

	@Override
	public Packet getDescriptionPacket()
	  {
	  NBTTagCompound tag = new NBTTagCompound();
	  if(pos1!=null){
	    NBTTagCompound innerTag = new NBTTagCompound();
	    pos1.writeToNBT(innerTag);
	    tag.setTag("pos1", innerTag);
	    }
	  if(pos2!=null){
	    NBTTagCompound innerTag = new NBTTagCompound();
	    pos2.writeToNBT(innerTag);
	    tag.setTag("pos2", innerTag);
	    }  

	  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
	  }

	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
	  super.onDataPacket(net, pkt);
	  NBTTagCompound tag = pkt.func_148857_g();
	  if(tag.hasKey("pos1")){
	    pos1 = new BlockPosition();
	    pos1.read(tag.getCompoundTag("pos1"));
	    }
	  if(tag.hasKey("pos2")){
	    pos2 = new BlockPosition();
	    pos2.read(tag.getCompoundTag("pos2"));
	    }
	  }
}
