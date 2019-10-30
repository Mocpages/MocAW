package net.shadowmage.ancientwarfare.nei_plugin;

import codechicken.nei.api.API;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;

public class AWNeiProxyActual extends AWNeiProxyBase
{

@Override
public void load()
  {
  AWNeiRecipeHandler handler = new AWNeiRecipeHandler();
  API.registerRecipeHandler((ICraftingHandler)handler);
  API.registerUsageHandler((IUsageHandler)handler);
  }

}
