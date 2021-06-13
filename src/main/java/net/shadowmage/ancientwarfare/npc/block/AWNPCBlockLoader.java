package net.shadowmage.ancientwarfare.npc.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.npc.tile.TileBuilding;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class AWNPCBlockLoader
{

public static final BlockTownHall townHall = new BlockTownHall("town_hall");
public static final BlockBuilding building = new BlockBuilding("building");

public static void load()
  {
  GameRegistry.registerBlock(townHall, ItemBlockOwnedRotatable.class, "town_hall");
  GameRegistry.registerTileEntity(TileTownHall.class, "town_hall_tile");
  townHall.iconMap.setIcon(townHall, RelativeSide.TOP, "ancientwarfare:npc/town_hall_top");
  townHall.iconMap.setIcon(townHall, RelativeSide.BOTTOM, "ancientwarfare:npc/town_hall_bottom");
  townHall.iconMap.setIcon(townHall, RelativeSide.LEFT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.RIGHT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.FRONT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.REAR, "ancientwarfare:npc/town_hall_side");
  
  GameRegistry.registerBlock(building, ItemBlockOwnedRotatable.class, "building");
  GameRegistry.registerTileEntity(TileBuilding.class, "building_tile");
  building.iconMap.setIcon(building, RelativeSide.TOP, "ancientwarfare:automation/quarry_rear");
  building.iconMap.setIcon(building, RelativeSide.BOTTOM, "ancientwarfare:automation/quarry_rear");
  building.iconMap.setIcon(building, RelativeSide.LEFT, "ancientwarfare:automation/quarry_rear");
  building.iconMap.setIcon(building, RelativeSide.RIGHT, "ancientwarfare:automation/quarry_rear");
  building.iconMap.setIcon(building, RelativeSide.FRONT, "ancientwarfare:automation/building");
  building.iconMap.setIcon(building, RelativeSide.REAR, "ancientwarfare:automation/quarry_rear");
  }

}
