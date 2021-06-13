package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerCreative;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;

public class GuiCreativeControls extends GuiContainerBase
{
ContainerCreative container;
public GuiCreativeControls(ContainerBase container)
  {
  super(container, 178, 192, defaultBackground);
  this.container = (ContainerCreative)container;
  this.ySize = this.container.guiHeight;
  }

@Override
public void initElements()
  {
  }

@Override
public void setupElements()
  {
  }

@Override
public void handleKeyboardInput()
  {
  // TODO Auto-generated method stub
  super.handleKeyboardInput();
  }

@Override
protected boolean checkHotbarKeys(int keyCode)//this code handles whether to allow the backpack to be moved from its slot via the number keys
  {
  boolean callSuper = true;
  for(int slot = 0; slot<9; slot++)
    {
    if(keyCode == this.mc.gameSettings.keyBindsHotbar[slot].getKeyCode() && slot==container.backpackSlotIndex)
      {
      callSuper=false;
      }
    }
  if(callSuper)
    {
    return super.checkHotbarKeys(keyCode);
    }
  return false;
  }

}

