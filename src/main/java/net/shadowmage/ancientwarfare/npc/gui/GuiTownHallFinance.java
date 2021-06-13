package net.shadowmage.ancientwarfare.npc.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;


public class GuiTownHallFinance extends GuiContainerBase
{

	GuiTownHallInventory parent;
	CompositeScrolled area;
	int val;
	public GuiTownHallFinance(GuiTownHallInventory parent)
	{
		super((ContainerBase)parent.inventorySlots);
		this.parent = parent;
	}

	@Override
	public void initElements(){
		area = new CompositeScrolled(this, 0, 40, xSize, ySize-40);
		addGuiElement(area);
		
		
		Label label;
		String s = "null " + (parent.container.fac == null);
		//Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(s));
		if(parent.container.fac != null) {
			//System.out.println("not null");
			label = new Label(8, 8, "Faction: " + parent.container.fac.name);
			addGuiElement(label);
		}else {
			label = new Label(8, 8, "Faction is null. This should NEVER happen; get Moc.");
			addGuiElement(label);
		}
		
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}


	@Override
	public void setupElements()
	{
		area.clearElements();
		Label label;
		if(parent.container.fac != null) {
			//System.out.println("not null");
			MocFaction fac = parent.container.fac;
			int height = 8;
			label = new Label(8, height, "Treasury: " + fac.treasury);
			area.addGuiElement(label);
			height += 9;
			
			Button button = new Button(8, height, 55, 12, "Purchase")
			{
				@Override
				protected void onPressed(){
					NBTTagCompound tag = new NBTTagCompound();
					tag.setBoolean("purchase", true);
					sendDataToContainer(tag);
				}
			};
			area.addGuiElement(button);
			height += 12;
			
			
			
			area.addGuiElement(new Line(0, height+1, xSize, height+1, 1, 0x000000ff));
			height+=2;

			label = new Label(8, height, "Debt: " + fac.poor_income_tax);
			area.addGuiElement(label);
			height += 9;
			button = new Button(8, height, 55, 12, "Borrow")
			{
				@Override
				protected void onPressed(){
					parent.container.borrow(val);
					refreshGui();
				}
			};
			area.addGuiElement(button);
			
			button = new Button(8+64, height, 55, 12, "Repay")
			{
				@Override
				protected void onPressed(){
					NBTTagCompound tag = new NBTTagCompound();
					//.setBoolean("deposit", true);
					//tag.setInteger("val",val);
					parent.container.repay(val);
				//	sendDataToContainer(tag);
					refreshGui();
				}
			};
			area.addGuiElement(button);
			
			height += 12;
			label = new Label(8, height, "Amt: ");
			area.addGuiElement(label);
			FacNumberInput input = new FacNumberInput(8+50, height, 60, val, this, fac){
				@Override
				public void onValueUpdated(float value)
				{
					val=(int) value;
				}
			};
			area.addGuiElement(input);
			height += 12;
		}
		area.setAreaSize(0);
	}

	@Override
	protected boolean onGuiCloseRequested()
	{
		Minecraft.getMinecraft().displayGuiScreen(parent);
		NBTTagCompound outer = new NBTTagCompound();
		NBTTagCompound fac = new NBTTagCompound();
		 parent.container.fac.writeToNBT(fac);
	    outer.setTag("fac2",fac);
	    sendDataToContainer(outer);
		parent.container.addSlots();
		parent.refreshGui();
		return false;
	}
	private class FacNumberInput extends NumberInput{
		MocFaction fac;
		public FacNumberInput(int topLeftX, int topLeftY, int width,float defaultText, IWidgetSelection selector, MocFaction f){
			super(topLeftX, topLeftY, width, defaultText, selector);
			this.fac = f;
		}
	}
}
