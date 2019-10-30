package net.shadowmage.ancientwarfare.structure.town;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldTownGenerator implements IWorldGenerator
{

private static WorldTownGenerator instance = new WorldTownGenerator();
private WorldTownGenerator(){}
public static WorldTownGenerator instance(){return instance;}

@Override
public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
  {
  if(!AWStructureStatics.enableTownGeneration){return;}
  if(random.nextFloat() < AWStructureStatics.townGenerationChance)
    {
    WorldGenTickHandler.instance().addChunkForTownGeneration(world, chunkX, chunkZ);    
    }
  }

public void attemptGeneration(World world, Random rng, int blockX, int blockZ)
  {
  TownBoundingArea area = TownPlacementValidator.findGenerationPosition(world, blockX, blockZ);
  if(area==null)
    {
    return;
    }
  TownTemplate template = TownTemplateManager.instance().selectTemplateForGeneration(world, blockX, blockZ, area);
  if(template==null)
    {
    return;
    }
  if(area.getChunkWidth() - 1 > template.getMaxSize())//shrink width down to town max size
    {
    area.chunkMaxX = area.chunkMinX + template.getMaxSize();
    }
  if(area.getChunkLength() - 1 > template.getMaxSize())//shrink length down to town max size
    {
    area.chunkMaxZ = area.chunkMinZ + template.getMaxSize();
    }
  if(!TownPlacementValidator.validateAreaForPlacement(world, area))
    {
    return;
    }//cannot validate the area until bounds are possibly shrunk by selected template
  
  /**
   * add the town to the generated structure map, as a -really- large structure entry
   */
  StructureMap map = AWGameData.INSTANCE.getData(StructureMap.NAME, world, StructureMap.class);
  StructureBB bb = new StructureBB(new BlockPosition(area.getBlockMinX(), area.getMinY(), area.getBlockMinZ()), new BlockPosition(area.getBlockMaxX(), area.getMaxY(), area.getBlockMaxZ()));
  StructureEntry entry = new StructureEntry(bb, template.getTownTypeName(), template.getClusterValue());
  map.setGeneratedAt(world, area.getCenterX(), area.getSurfaceY(), area.getCenterZ(), 0, entry, false);
  map.markDirty();
  
  /**
   * add the town to generated town map, to eliminate towns generating too close to eachother
   */
  TownMap tm = AWGameData.INSTANCE.getPerWorldData(TownMap.NAME, world, TownMap.class);
  tm.setGenerated(world, bb); 
  
  /**
   * and finally initialize generation.  The townGenerator will do borders, walls, roads, and add any structures to the world-gen tick handler for generation.
   */
  TownGenerator generator = new TownGenerator(world, area, template);
  generator.generate();
  }

}
