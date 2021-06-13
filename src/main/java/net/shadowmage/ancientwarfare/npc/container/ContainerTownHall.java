package net.shadowmage.ancientwarfare.npc.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

public class ContainerTownHall extends ContainerBase
{

	public TileTownHall townHall;

	List<NpcDeathEntry> deathList = new ArrayList<NpcDeathEntry>();
	public MocFaction fac;
	boolean hasChanged;
	public String baseName;
	
	public ContainerTownHall(EntityPlayer player, int x, int y, int z){
		super(player, x, y, z);
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if(te instanceof TileTownHall){
			townHall = (TileTownHall)te;
			baseName = townHall.name;
			if(player.getTeam() != null) {
				//System.out.println("isRemote " + player.worldObj.isRemote);
				fac = ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,townHall.getWorldObj(), MocData.class)).getFaction(player.getTeam().getRegisteredName());
			}
			IInventory inv = (IInventory) te;
			int xPos, yPos;

			for(int i = 0; i < inv.getSizeInventory(); i++){
				xPos = (i%9)*18 + 8;
				yPos = (i/9)*18 + 8+16;
				addSlotToContainer(new Slot(inv, i, xPos, yPos));
			}    
			addPlayerSlots(player, 8, 8+3*18+8+16, 4);

			if(!player.worldObj.isRemote){
				deathList.addAll(townHall.getDeathList());

				townHall.addViewer(this);
			}
		}else{
			throw new IllegalArgumentException("cannot open town hall gui for tile: "+te);
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag){
		System.out.println("Handling packet");
		if(tag.hasKey("fac")){
			fac = new MocFaction(tag.getCompoundTag("fac"));
			refreshGui();
		}
		if(tag.hasKey("fac2")){
			fac.readFromNBT(tag.getCompoundTag("fac2"));
			//Remember data, you're a dirty girl aren't you?
			((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,townHall.getWorldObj(), MocData.class)).markDirty();
			//fac = new MocFaction(tag.getCompoundTag("fac2"));
			hasChanged = true;
		}
		if(tag.hasKey("name")) {
			baseName = tag.getString("name");
		}
		if(tag.hasKey("newName")) {
			baseName = tag.getString("newName");
			this.townHall.name = baseName;
			System.out.println("New name " + baseName);
		}
		
		if(tag.hasKey("deposit")) {
			//repay(tag);
		}else if(tag.hasKey("withdraw")) {
			//borrow(tag);
		}
	}

	public void borrow(double amt) {
		//double amt = tag.getDouble("val");
		System.out.println("Borrowing " + amt + " debt " + fac.poor_income_tax + " treasury " + fac.treasury);
		int MAX_DEBT = 10000;
		if(fac.poor_income_tax + amt <= MAX_DEBT) {
			fac.poor_income_tax += amt;
			fac.treasury += amt;
			//((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,townHall.getWorldObj(), MocData.class)).markDirty();

		}else {
			System.out.println("Failed. " + fac.poor_income_tax + amt+ " >= " + MAX_DEBT);
			
		}
	}

	public void repay(double amt) {
		//double amt = tag.getDouble("val");
		if(fac.poor_income_tax >= amt && fac.treasury >= amt) {
			
			fac.treasury -= amt;
			fac.poor_income_tax -= amt;
			//((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,townHall.getWorldObj(), MocData.class)).markDirty();

		}
		
	}

	@Override
	public void sendInitData(){
		sendDeathListToClient();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer){  
		super.onContainerClosed(par1EntityPlayer);
		townHall.removeViewer(this);

		if(hasChanged && !player.worldObj.isRemote){
			
		}
	}

	public void updateName(String newName) {
		this.townHall.name = newName;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("newName", newName);
		sendDataToServer(tag);
		System.out.println("New name = " + newName);
	}
	
	public void onTownHallDeathListUpdated(){
		this.deathList.clear();
		this.deathList.addAll(townHall.getDeathList());
		sendDeathListToClient();
	}

	private void sendDeathListToClient(){
		NBTTagCompound facTag = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		if(fac != null) {
			fac.writeToNBT(facTag);
			tag.setTag("fac", facTag);
		}
		tag.setString("name", baseName);
		sendDataToClient(tag);
	}

	public List<NpcDeathEntry> getDeathList(){
		return deathList;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
	{
		ItemStack slotStackCopy = null;
		Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack())
		{
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			if(slotClickedIndex < townHall.getSizeInventory())//book slot
			{      
				if(!this.mergeItemStack(slotStack, townHall.getSizeInventory(), townHall.getSizeInventory()+36, false))//merge into player inventory
				{
					return null;
				}
			}
			else
			{
				if(!this.mergeItemStack(slotStack, 0, townHall.getSizeInventory(), false))//merge into player inventory
				{
					return null;
				}
			}
			if (slotStack.stackSize == 0)
			{
				theSlot.putStack((ItemStack)null);
			}
			else
			{
				theSlot.onSlotChanged();
			}
			if (slotStack.stackSize == slotStackCopy.stackSize)
			{
				return null;
			}
			theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

}
