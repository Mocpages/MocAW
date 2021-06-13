package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.container.ContainerBuildingSetup;

public class GuiSetupBuilding extends GuiContainerBase
{

boolean noTargetMode = false;
ContainerBuildingSetup container;

boolean boundsAdjusted = false, targetsAdjusted = false;
byte[] checkedMap = new byte[16*16];
NumberInput x1, y1, z1, x2, y2, z2;


public GuiSetupBuilding(ContainerBase container)
  {
  super(container);
  this.container = (ContainerBuildingSetup) container;
  this.shouldCloseOnVanillaKeys = true;
  //if(!this.container.worksite.userAdjustableBlocks()){noTargetMode=true;}
  }

@Override
public void initElements(){
	//System.out.println("Yeet");
  //read initial checked-map from container
	

    Label label;
    label = new Label(8, 16, "Test");
    addGuiElement(label);
  }

@Override
public void setupElements()
  {
  this.clearElements();  
  
  Label label;
  label = new Label(8, 18, "X1");
  addGuiElement(label);
  
  x1 = new NumberInput(20, 16, 30, this.container.min.x, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.min.x = (int)value;
    }
  };
  
  x1.setIntegerValue();
  x1.setAllowNegative();
  x1.setValue(container.min.x);
  addGuiElement(x1);
  
  label = new Label(50, 18, "Y1");
  addGuiElement(label);
  
  y1 = new NumberInput(64, 16, 30, this.container.min.y, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.min.y = (int)value;
    }
  };
  y1.setIntegerValue();
  y1.setAllowNegative();
  y1.setValue(container.min.y);
  addGuiElement(y1);
  
  label = new Label(94, 18, "Z1");
  addGuiElement(label);
  z1 = new NumberInput(108, 16, 30, this.container.min.z, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.min.z = (int)value;
    }
  };
  z1.setIntegerValue();
  z1.setAllowNegative();
  z1.setValue(container.min.z);
  addGuiElement(z1);
  
  
  label = new Label(8, 38, "X2");
  addGuiElement(label);
  
  //X2 set
  x2 = new NumberInput(20, 40, 30, this.container.max.x, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.max.x = (int)value;
    }
  };
  
  x2.setIntegerValue();
  x2.setAllowNegative();
  x2.setValue(container.max.x);
  addGuiElement(x2);
  
  label = new Label(50, 38, "Y2");
  addGuiElement(label);
  
  y2 = new NumberInput(64, 40, 30, this.container.max.y, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.max.y = (int)value;
    }
  };
  y2.setIntegerValue();
  y2.setAllowNegative();
  y2.setValue(container.max.y);
  addGuiElement(y2);
  
  label = new Label(94, 38, "Z2");
  addGuiElement(label);
  z2 = new NumberInput(108, 40, 30, this.container.max.z, this)
  {
  @Override
  public void onValueUpdated(float value)
    {
    container.max.z = (int)value;
    }
  };
  z2.setIntegerValue();
  z2.setAllowNegative();
  z2.setValue(container.max.z);
  addGuiElement(z2);
  
  label = new Label(8, 8, "Set up building:");
  addGuiElement(label);
  }

@Override
public void handlePacketData(NBTTagCompound data){
  if(data.hasKey("pos1")){
	 BlockPosition pos = new BlockPosition(data.getCompoundTag("pos1"));
	 x1.setValue(pos.x);
	 y1.setValue(pos.y);
	 z1.setValue(pos.z);
	 refreshGui();
    }
  if(data.hasKey("pos2")){
		 BlockPosition pos = new BlockPosition(data.getCompoundTag("pos2"));
		 x2.setValue(pos.x);
		 y2.setValue(pos.y);
		 z2.setValue(pos.z);
		 refreshGui();
	    }
  }

@Override
protected boolean onGuiCloseRequested()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("guiClosed", true); 
  tag.setTag("min", container.min.writeToNBT(new NBTTagCompound()));
  tag.setTag("max", container.max.writeToNBT(new NBTTagCompound()));
  
  container.building.pos1 = container.min;
  container.building.pos2 = container.max;
  sendDataToContainer(tag);
  return super.onGuiCloseRequested();
  }

}
