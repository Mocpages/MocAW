package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteQuarry extends GuiWorksiteBase
{

public GuiWorksiteQuarry(ContainerBase par1Container)
  {
  super(par1Container);
  }

@Override
public void initElements()
  {
  addLabels();
  addSideSelectButton();
  addCreativeButton();
  //addBoundsAdjustButton();
  }

protected void addCreativeButton()
{
if(!this.player.capabilities.isCreativeMode) {return;}
Button button = new Button(108, ySize-8-12, 50, 12, "Admin Options")
  {
  @Override
  protected void onPressed(){
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CREATIVE_CONTROLS, container.worksite.xCoord, container.worksite.yCoord, container.worksite.zCoord);
    }
  };
addGuiElement(button);
}

@Override
public void setupElements()
  {
  
  }


}
