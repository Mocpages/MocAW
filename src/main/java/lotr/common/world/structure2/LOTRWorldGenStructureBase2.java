/*      */ package lotr.common.world.structure2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;

/*      */ import lotr.common.LOTRFoods;
/*      */ import lotr.common.LOTRMod;
import lotr.common.block.LOTRBlockFlowerPot;
import lotr.common.block.LOTRBlockGate;
import lotr.common.block.LOTRBlockMug;
import lotr.common.entity.LOTREntities;
/*      */ import lotr.common.entity.LOTREntityNPCRespawner;
/*      */ import lotr.common.entity.item.LOTREntityBanner;
/*      */ import lotr.common.entity.item.LOTREntityBannerWall;
/*      */ import lotr.common.entity.item.LOTREntityRugBase;
import lotr.common.entity.npc.LOTREntityNPC;
/*      */ import lotr.common.item.LOTRItemBanner;
/*      */ import lotr.common.item.LOTRItemMug;
/*      */ import lotr.common.item.LOTRItemMug.Vessel;
/*      */ import lotr.common.recipe.LOTRBrewingRecipes;
/*      */ import lotr.common.tileentity.LOTRTileEntityAnimalJar;
/*      */ import lotr.common.tileentity.LOTRTileEntityArmorStand;
/*      */ import lotr.common.tileentity.LOTRTileEntityBarrel;
/*      */ import lotr.common.tileentity.LOTRTileEntityDwarvenDoor;
/*      */ import lotr.common.tileentity.LOTRTileEntityKebabStand;
import lotr.common.tileentity.LOTRTileEntityMobSpawner;
/*      */ import lotr.common.tileentity.LOTRTileEntityPlate;
import lotr.common.tileentity.LOTRTileEntitySpawnerChest;
/*      */ import lotr.common.tileentity.LOTRTileEntityWeaponRack;
import lotr.common.util.LOTRLog;
/*      */ import lotr.common.world.biome.LOTRBiome;
/*      */ import lotr.common.world.structure.LOTRChestContents;
import lotr.common.world.structure.LOTRStructures;
/*      */ import lotr.common.world.structure2.scan.LOTRStructureScan;
import lotr.common.world.village.LOTRVillageGen;
/*      */ import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
/*      */ import net.minecraft.entity.EntityCreature;
/*      */ import net.minecraft.entity.EntityLeashKnot;
/*      */ import net.minecraft.entity.EntityLiving;
/*      */ import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
/*      */ import net.minecraft.init.Blocks;
/*      */ import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
/*      */ import net.minecraft.item.ItemStack;
/*      */ import net.minecraft.nbt.NBTTagCompound;
/*      */ import net.minecraft.tileentity.TileEntity;
/*      */ import net.minecraft.tileentity.TileEntityFlowerPot;
/*      */ import net.minecraft.tileentity.TileEntitySign;
/*      */ import net.minecraft.tileentity.TileEntitySkull;
/*      */ import net.minecraft.util.Direction;
/*      */ import net.minecraft.util.MathHelper;
/*      */ import net.minecraft.util.WeightedRandom;
/*      */ import net.minecraft.world.World;
/*      */ import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
/*      */ import net.minecraft.world.gen.structure.StructureBoundingBox;
/*      */ import net.minecraftforge.common.util.ForgeDirection;

/*      */ 
/*      */ public abstract class LOTRWorldGenStructureBase2 extends WorldGenerator {
	/*      */   public boolean restrictions = true;

	/*      */   protected boolean notifyChanges;

	/*   49 */   public EntityPlayer usingPlayer = null;
	public boolean shouldFindSurface = false;
	public LOTRVillageGen.AbstractInstance villageInstance;
	public LOTRStructureTimelapse.ThreadTimelapse threadTimelapse;
	protected int originX;
	protected int originY;
	protected int originZ;
	private int rotationMode;
	private StructureBoundingBox sbb;
	private LOTRStructureScan currentStrScan;
	private Map<String, BlockAliasPool> scanAliases;
	private Map<String, Float> scanAliasChances;
	public final boolean generate(World world, Random random, int i, int j, int k) { 
		return true;
		//return generateWithSetRotation(world, random, i, j, k, random.nextInt(4));
	}
	public abstract boolean generateWithSetRotation(World paramWorld, Random paramRandom, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
	protected void setupRandomBlocks(Random random) {}
	public int usingPlayerRotation() { return LOTRStructures.getRotationFromPlayer(this.usingPlayer);
	}
	public int getRotationMode() { return this.rotationMode;
	}
	protected void setOriginAndRotation(World world, int i, int j, int k, int rotation, int shift) { setOriginAndRotation(world, i, j, k, rotation, shift, 0);
	}
	protected void setOriginAndRotation(World world, int i, int j, int k, int rotation, int shift, int shiftX) { j--;
	this.rotationMode = rotation;
	switch (getRotationMode()) { case 0: k += shift;
	i += shiftX;
	break;
	case 1: i -= shift;
	k += shiftX;
	break;
	case 2: k -= shift;
	i -= shiftX;
	break;
	case 3: i += shift;
	k -= shiftX;
	break;
	}
	this.originX = i;
	this.originY = j;
	this.originZ = k;
	if (this.shouldFindSurface) { this.shouldFindSurface = false;
	findSurface(world, -shiftX, -shift);
	}
	}
	protected void findSurface(World world, int i, int k) { for (int j = 8;
			getY(j) >= 0;
			j--) { if (isSurface(world, i, j, k)) { this.originY = getY(j);
			break;
			}
	}
	}
	public void setStructureBB(StructureBoundingBox box) { this.sbb = box;
	}
	public boolean hasSBB() { return (this.sbb != null);
	}
	private boolean isInSBB(int i, int j, int k) { return (this.sbb == null) ? true : this.sbb.isVecInside(i, j, k);
	}

	protected void setBlockAndMetadata(World world, int i, int j, int k, Block block, int meta) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	meta = rotateMeta(block, meta);
	//setBlockAndNotifyAdequately(world, i, j, k, block, meta);
	if (meta != 0 && (block instanceof net.minecraft.block.BlockChest || block instanceof lotr.common.block.LOTRBlockChest || block instanceof lotr.common.block.LOTRBlockSpawnerChest || block instanceof net.minecraft.block.BlockFurnace || block instanceof lotr.common.block.LOTRBlockHobbitOven || block instanceof lotr.common.block.LOTRBlockForgeBase)) world.setBlockMetadataWithNotify(i, j, k, meta, this.notifyChanges ? 3 : 2);
	if (block != Blocks.air && this.threadTimelapse != null) this.threadTimelapse.onBlockSet();
	}

	/*      */   protected int rotateMeta(Block block, int meta) { if (block instanceof net.minecraft.block.BlockRotatedPillar) { int i = meta & 0x3;
	int j = meta & 0xC;
	if (j == 0) return meta;
	if (this.rotationMode == 0 || this.rotationMode == 2) return meta;
	if (j == 4) { j = 8;
	}
	else if (j == 8) { j = 4;
	}
	return j | i;
	}
	if (block instanceof net.minecraft.block.BlockStairs) { int i = meta & 0x3;
	int j = meta & 0x4;
	for (int l = 0;
			l < this.rotationMode;
			l++) { if (i == 2) { i = 1;
			}
			else if (i == 1) { i = 3;
			}
			else if (i == 3) { i = 0;
			}
			else if (i == 0) { i = 2;
			}
	}
	return j | i;
	}
	if (block instanceof LOTRBlockMug || block instanceof net.minecraft.block.BlockTripWireHook || block instanceof net.minecraft.block.BlockAnvil) { int i = meta;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return i;
	}
	if (block instanceof lotr.common.block.LOTRBlockArmorStand) { int i = meta & 0x3;
	int j = meta & 0x4;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return j | i;
	}
	if (block instanceof lotr.common.block.LOTRBlockWeaponRack) { int i = meta & 0x3;
	int j = meta & 0x4;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return j | i;
	}
	if (block == Blocks.wall_sign || block instanceof net.minecraft.block.BlockLadder || block instanceof net.minecraft.block.BlockFurnace || block instanceof net.minecraft.block.BlockChest || block instanceof lotr.common.block.LOTRBlockChest || block instanceof lotr.common.block.LOTRBlockBarrel || block instanceof lotr.common.block.LOTRBlockHobbitOven || block instanceof lotr.common.block.LOTRBlockForgeBase || block instanceof lotr.common.block.LOTRBlockKebabStand) { if (meta == 0 && (block instanceof net.minecraft.block.BlockFurnace || block instanceof net.minecraft.block.BlockChest || block instanceof lotr.common.block.LOTRBlockChest || block instanceof lotr.common.block.LOTRBlockHobbitOven || block instanceof lotr.common.block.LOTRBlockForgeBase)) return meta;
	int i = meta;
	for (int l = 0; l < this.rotationMode; l++) { 
			i = Direction.facingToDirection[i];
			i = Direction.rotateRight[i];
			i = Direction.directionToFacing[i];
	}
	return i;
	}
	if (block == Blocks.standing_sign) { 
		int i = meta;
		i += this.rotationMode * 4;
		return 15;
	}
	if (block instanceof net.minecraft.block.BlockBed) { int i = meta;
	boolean flag = (meta >= 8);
	if (flag) i -= 8;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	if (flag) i += 8;
	return i;
	}
	if (block instanceof net.minecraft.block.BlockTorch) { if (meta == 5) return 5;
	int i = meta;
	for (int l = 0;
			l < this.rotationMode;
			l++) { if (i == 4) { i = 1;
			}
			else if (i == 1) { i = 3;
			}
			else if (i == 3) { i = 2;
			}
			else if (i == 2) { i = 4;
			}
	}
	return i;
	}
	if (block instanceof net.minecraft.block.BlockDoor) { if ((meta & 0x8) != 0) return meta;
	int i = meta & 0x3;
	int j = meta & 0x4;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return j | i;
	}
	if (block instanceof net.minecraft.block.BlockTrapDoor) { int i = meta & 0x3;
	int j = meta & 0x4;
	int k = meta & 0x8;
	for (int l = 0;
			l < this.rotationMode;
			l++) { if (i == 0) { i = 3;
			}
			else if (i == 1) { i = 2;
			}
			else if (i == 2) { i = 0;
			}
			else if (i == 3) { i = 1;
			}
	}
	return k | j | i;
	}
	if (block instanceof net.minecraft.block.BlockFenceGate) { int i = meta & 0x3;
	int j = meta & 0x4;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return j | i;
	}
	if (block instanceof net.minecraft.block.BlockPumpkin) { int i = meta;
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return i;
	}
	if (block instanceof net.minecraft.block.BlockSkull) { if (meta < 2) return meta;
	int i = Direction.facingToDirection[meta];
	for (int l = 0;
			l < this.rotationMode;
			l++) i = Direction.rotateRight[i];
	return Direction.directionToFacing[i];
	}
	if (block instanceof lotr.common.block.LOTRBlockGate) { int i = meta & 0x7;
	int j = meta & 0x8;
	if (i != 0 && i != 1) for (int l = 0;
			l < this.rotationMode;
			l++) { i = Direction.facingToDirection[i];
			i = Direction.rotateRight[i];
			i = Direction.directionToFacing[i];
	}
	return j | i;
	}
	if (block instanceof net.minecraft.block.BlockLever) { int i = meta & 0x7;
	int j = meta & 0x8;
	if (i == 0 || i == 7) { for (int l = 0;
			l < this.rotationMode;
			l++)
		/*      */           i = (i == 0) ? 7 : 0;
	}
	else if (i == 5 || i == 6) { for (int l = 0;
			l < this.rotationMode;
			l++)
		/*      */           i = (i == 5) ? 6 : 5;
	}
	else { for (int l = 0;
			l < this.rotationMode;
			l++) { if (i == 4) { i = 1;
			}
			else if (i == 1) { i = 3;
			}
			else if (i == 3) { i = 2;
			}
			else if (i == 2) { i = 4;
			}
	}
	}
	return j | i;
	}
	if (block instanceof net.minecraft.block.BlockButton) { int i = meta;
	int j = meta & 0x8;
	for (int l = 0;
			l < this.rotationMode;
			l++) { if (i == 4) { i = 1;
			}
			else if (i == 1) { i = 3;
			}
			else if (i == 3) { i = 2;
			}
			else if (i == 2) { i = 4;
			}
	}
	return j | i;
	}
	return meta;
	}

	/*      */   protected Block getBlock(World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k))
		/*      */       return Blocks.air;
	return world.getBlock(i, j, k);
	}

	/*      */   protected int getMeta(World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k))
		/*      */       return 0;
	return world.getBlockMetadata(i, j, k);
	}

	/*      */   protected int getTopBlock(World world, int i, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	if (!isInSBB(i, 0, k))
		/*      */       return 0;
	return world.getTopSolidOrLiquidBlock(i, k) - this.originY;
	}

	/*      */   protected BiomeGenBase getBiome(World world, int i, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	if (!isInSBB(i, 0, k))
		/*      */       return null;
	return world.getBiomeGenForCoords(i, k);
	}

	protected boolean isAir(World world, int i, int j, int k) { 
		return (getBlock(world, i, j, k).getMaterial() == Material.air);
	}

	/*   63 */   public LOTRWorldGenStructureBase2(boolean flag) { super(flag);

	/* 1329 */     this.scanAliases = new HashMap();

	/* 1330 */     this.scanAliasChances = new HashMap();
	this.notifyChanges = flag;
	}
	protected boolean isOpaque(World world, int i, int j, int k) { return getBlock(world, i, j, k).isOpaqueCube();
	}
	protected boolean isReplaceable(World world, int i, int j, int k) { return getBlock(world, i, j, k).isReplaceable(world, getX(i, k), getY(j), getZ(i, k));
	}
	protected boolean isSideSolid(World world, int i, int j, int k, ForgeDirection side) { return getBlock(world, i, j, k).isSideSolid(world, getX(i, k), getY(j), getZ(i, k), side);
	}
	protected TileEntity getTileEntity(World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return null;
	return world.getTileEntity(i, j, k);
	}
	protected void placeChest(World world, Random random, int i, int j, int k, int meta, LOTRChestContents contents) { placeChest(world, random, i, j, k, meta, contents, -1);
	}
	protected void placeChest(World world, Random random, int i, int j, int k, int meta, LOTRChestContents contents, int amount) { placeChest(world, random, i, j, k, Blocks.chest, meta, contents, amount);
	}
	protected void placeChest(World world, Random random, int i, int j, int k, Block chest, int meta, LOTRChestContents contents) { placeChest(world, random, i, j, k, chest, meta, contents, -1);
	}
	protected void placeChest(World world, Random random, int i, int j, int k, Block chest, int meta, LOTRChestContents contents, int amount) { setBlockAndMetadata(world, i, j, k, chest, meta);
	fillChest(world, random, i, j, k, contents, amount);
	}
	protected void fillChest(World world, Random random, int i, int j, int k, LOTRChestContents contents, int amount) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	LOTRChestContents.fillChest(world, random, i, j, k, contents, amount);
	}
	protected void putInventoryInChest(World world, int i, int j, int k, IInventory inv) { TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof IInventory) { IInventory blockInv = (IInventory)tileentity;
	for (int l = 0;
			l < blockInv.getSizeInventory();
			l++) { if (l >= inv.getSizeInventory()) break;
			blockInv.setInventorySlotContents(l, inv.getStackInSlot(l));
	}
	}
	}
	protected void placeOrcTorch(World world, int i, int j, int k) { setBlockAndMetadata(world, i, j, k, LOTRMod.orcTorch, 0);
	setBlockAndMetadata(world, i, j + 1, k, LOTRMod.orcTorch, 1);
	}
	protected void placeMobSpawner(World world, int i, int j, int k, Class entityClass) { setBlockAndMetadata(world, i, j, k, LOTRMod.mobSpawner, 0);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityMobSpawner) ((LOTRTileEntityMobSpawner)tileentity).setEntityClassID(LOTREntities.getEntityIDFromClass(entityClass));
	}
	protected void placeSpawnerChest(World world, int i, int j, int k, Block block, int meta, Class entityClass) { placeSpawnerChest(world, null, i, j, k, block, meta, entityClass, null);
	}
	protected void placeSpawnerChest(World world, Random random, int i, int j, int k, Block block, int meta, Class entityClass, LOTRChestContents contents) { placeSpawnerChest(world, random, i, j, k, block, meta, entityClass, contents, -1);
	}

	/*      */   protected void placeSpawnerChest(World world, Random random, int i, int j, int k, Block block, int meta, Class entityClass, LOTRChestContents contents, int amount) { setBlockAndMetadata(world, i, j, k, block, meta);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntitySpawnerChest) ((LOTRTileEntitySpawnerChest)tileentity).setMobID(entityClass);
	if (contents != null) fillChest(world, random, i, j, k, contents, amount);
	}

	/*      */   protected void placePlate(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList) { placePlate_list(world, random, i, j, k, plateBlock, foodList, false);
	}

	/*      */   protected void placePlateWithCertainty(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList) { placePlate_list(world, random, i, j, k, plateBlock, foodList, true);
	}

	/* 1334 */   protected void loadStrScan(String name) { this.currentStrScan = LOTRStructureScan.getScanByName(name);

	/* 1335 */     if (this.currentStrScan == null)
	/*      */     {
		/* 1337 */       LOTRLog.logger.error("LOTR: Structure Scan for name " + name + " does not exist!!!");

	/*      */     }

	/* 1339 */     this.scanAliases.clear();
	}

	/*      */   protected void placePlate_list(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList, boolean certain) { ItemStack food = foodList.getRandomFoodForPlate(random);
	if (random.nextInt(4) == 0) food.stackSize += 1 + random.nextInt(3);
	placePlate_item(world, random, i, j, k, plateBlock, food, certain);
	}

	/*      */   protected void placePlate_item(World world, Random random, int i, int j, int k, Block plateBlock, ItemStack foodItem, boolean certain) { placePlate_do(world, random, i, j, k, plateBlock, foodItem, certain);
	}

	/*      */   private void placePlate_do(World world, Random random, int i, int j, int k, Block plateBlock, ItemStack foodItem, boolean certain) { if (!certain && random.nextBoolean()) return;
	setBlockAndMetadata(world, i, j, k, plateBlock, 0);
	if (certain || random.nextBoolean()) { TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityPlate) { LOTRTileEntityPlate plate = (LOTRTileEntityPlate)tileentity;
	plate.setFoodItem(foodItem);
	}
	}
	}

	/*      */   protected void placeBarrel(World world, Random random, int i, int j, int k, int meta, LOTRFoods foodList) { placeBarrel(world, random, i, j, k, meta, foodList.getRandomBrewableDrink(random));
	}

	/*      */   protected void placeBarrel(World world, Random random, int i, int j, int k, int meta, ItemStack drink) { setBlockAndMetadata(world, i, j, k, LOTRMod.barrel, meta);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityBarrel) { LOTRTileEntityBarrel barrel = (LOTRTileEntityBarrel)tileentity;
	barrel.barrelMode = 2;
	drink = drink.copy();
	LOTRItemMug.setStrengthMeta(drink, MathHelper.getRandomIntegerInRange(random, 1, 3));
	LOTRItemMug.setVessel(drink, LOTRItemMug.Vessel.MUG, true);
	drink.stackSize = MathHelper.getRandomIntegerInRange(random, LOTRBrewingRecipes.BARREL_CAPACITY / 2, LOTRBrewingRecipes.BARREL_CAPACITY);
	
	//barrel.setInventorySlotContents(9, drink);
	}
	}

	/*      */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, LOTRFoods foodList) { placeMug(world, random, i, j, k, meta, foodList.getRandomPlaceableDrink(random), foodList);
	}

	/*      */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, ItemStack drink, LOTRFoods foodList) { placeMug(world, random, i, j, k, meta, drink, foodList.getPlaceableDrinkVessels());
	}

	/*      */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, ItemStack drink, Vessel[] vesselTypes) { LOTRItemMug.Vessel vessel = vesselTypes[random.nextInt(vesselTypes.length)];
	setBlockAndMetadata(world, i, j, k, vessel.getBlock(), meta);
	if (random.nextInt(3) != 0) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	drink = drink.copy();
	drink.stackSize = 1;
	if (drink.getItem() instanceof LOTRItemMug && ((LOTRItemMug)drink.getItem()).isBrewable) LOTRItemMug.setStrengthMeta(drink, MathHelper.getRandomIntegerInRange(random, 1, 3));
	LOTRItemMug.setVessel(drink, vessel, true);
	LOTRBlockMug.setMugItem(world, i, j, k, drink, vessel);
	}
	}
	protected void placeKebabStand(World world, Random random, int i, int j, int k, Block block, int meta) { setBlockAndMetadata(world, i, j, k, block, meta);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityKebabStand) { LOTRTileEntityKebabStand stand = (LOTRTileEntityKebabStand)tileentity;
	int kebab = MathHelper.getRandomIntegerInRange(random, 1, 8);
	stand.generateCookedKebab(kebab);
	}
	}
	protected void plantFlower(World world, Random random, int i, int j, int k) { ItemStack itemstack = getRandomFlower(world, random);
	setBlockAndMetadata(world, i, j, k, Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());
	}
	protected void placeFlowerPot(World world, int i, int j, int k, ItemStack itemstack) { boolean vanilla = (itemstack == null || itemstack.getItem() == Item.getItemFromBlock(Blocks.cactus));
	if (vanilla) { setBlockAndMetadata(world, i, j, k, Blocks.flower_pot, 0);
	}
	else { setBlockAndMetadata(world, i, j, k, LOTRMod.flowerPot, 0);
	}
	int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	if (itemstack != null) if (vanilla) { TileEntity te = world.getTileEntity(i, j, k);
	if (te instanceof TileEntityFlowerPot) { TileEntityFlowerPot pot = (TileEntityFlowerPot)te;
	pot.func_145964_a(itemstack.getItem(), itemstack.getItemDamage());
	pot.markDirty();
	}
	}
	else { LOTRBlockFlowerPot.setPlant(world, i, j, k, itemstack);
	}
	}
	protected ItemStack getRandomFlower(World world, Random random) { BiomeGenBase biome = getBiome(world, 0, 0);
	if (biome instanceof LOTRBiome) { BiomeGenBase.FlowerEntry fe = ((LOTRBiome)biome).getRandomFlower(random);
	return new ItemStack(fe.block, 1, fe.metadata);
	}
	if (random.nextBoolean()) return new ItemStack(Blocks.yellow_flower, 0);
	return new ItemStack(Blocks.red_flower, 0);
	}
	protected ItemStack getRandomTallGrass(World world, Random random) { BiomeGenBase biome = getBiome(world, 0, 0);
	if (biome instanceof LOTRBiome) { LOTRBiome.GrassBlockAndMeta gbm = ((LOTRBiome)biome).getRandomGrass(random);
	return new ItemStack(gbm.block, 1, gbm.meta);
	}
	return new ItemStack(Blocks.tallgrass, 1, 1);
	}
	protected void plantTallGrass(World world, Random random, int i, int j, int k) { ItemStack itemstack = getRandomTallGrass(world, random);
	setBlockAndMetadata(world, i, j, k, Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());
	}
	protected void spawnItemFrame(World world, int i, int j, int k, int direction, ItemStack itemstack) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	for (int l = 0;
			l < this.rotationMode;
			l++) direction = Direction.rotateRight[direction];
	EntityItemFrame frame = new EntityItemFrame(world, i, j, k, direction);
	frame.setDisplayedItem(itemstack);
	world.spawnEntityInWorld(frame);
	}
	protected void placeArmorStand(World world, int i, int j, int k, int direction, ItemStack[] armor) { setBlockAndMetadata(world, i, j, k, LOTRMod.armorStand, direction);
	setBlockAndMetadata(world, i, j + 1, k, LOTRMod.armorStand, direction | 0x4);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityArmorStand) { LOTRTileEntityArmorStand armorStand = (LOTRTileEntityArmorStand)tileentity;
	if (armor != null) for (int l = 0;
			l < armor.length;
			l++) { ItemStack armorPart = armor[l];
			//if (armorPart == null) { armorStand.setInventorySlotContents(l, null);
			//}
			//else { armorStand.setInventorySlotContents(l, armor[l].copy());
			//}
	}
	}
	}
	protected void placeWeaponRack(World world, int i, int j, int k, int meta, ItemStack weapon) { setBlockAndMetadata(world, i, j, k, LOTRMod.weaponRack, meta);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof LOTRTileEntityWeaponRack) { LOTRTileEntityWeaponRack weaponRack = (LOTRTileEntityWeaponRack)tileentity;
	if (weapon != null) weaponRack.setWeaponItem(weapon.copy());
	}
	}
	protected void placeBanner(World world, int i, int j, int k, LOTRItemBanner.BannerType bt, int direction) { placeBanner(world, i, j, k, bt, direction, false, 0);
	}
	protected void placeBanner(World world, int i, int j, int k, LOTRItemBanner.BannerType bt, int direction, boolean protection, int r) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	for (int l = 0;
			l < this.rotationMode;
			l++) direction = Direction.rotateRight[direction];
	LOTREntityBanner banner = new LOTREntityBanner(world);
	banner.setLocationAndAngles(i + 0.5D, j, k + 0.5D, direction * 90.0F, 0.0F);
	banner.setBannerType(bt);
	if (protection) { banner.setStructureProtection(true);
	banner.setSelfProtection(false);
	}
	if (r > 0) { if (r > 64) throw new RuntimeException("WARNING: Banner protection range " + r + " is too large!");
	banner.setCustomRange(r);
	}
	world.spawnEntityInWorld(banner);
	}
	protected void placeWallBanner(World world, int i, int j, int k, LOTRItemBanner.BannerType bt, int direction) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	for (int l = 0;
			l < this.rotationMode;
			l++) direction = Direction.rotateRight[direction];
	LOTREntityBannerWall banner = new LOTREntityBannerWall(world, i, j, k, direction);
	banner.setBannerType(bt);
	world.spawnEntityInWorld(banner);
	}
	protected void setGrassToDirt(World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	world.getBlock(i, j, k).onPlantGrow(world, i, j, k, i, j, k);
	}
	protected void setBiomeTop(World world, int i, int j, int k) { BiomeGenBase biome = getBiome(world, i, k);
	Block topBlock = biome.topBlock;
	int topMeta = 0;
	if (biome instanceof LOTRBiome) topMeta = ((LOTRBiome)biome).topBlockMeta;
	setBlockAndMetadata(world, i, j, k, topBlock, topMeta);
	}
	protected void setBiomeFiller(World world, int i, int j, int k) { BiomeGenBase biome = getBiome(world, i, k);
	Block fillerBlock = biome.fillerBlock;
	int fillerMeta = 0;
	if (biome instanceof LOTRBiome) fillerMeta = ((LOTRBiome)biome).fillerBlockMeta;
	setBlockAndMetadata(world, i, j, k, fillerBlock, fillerMeta);
	}
	protected void setAir(World world, int i, int j, int k) { setBlockAndMetadata(world, i, j, k, Blocks.air, 0);
	}
	protected void placeSkull(World world, Random random, int i, int j, int k) { placeSkull(world, i, j, k, random.nextInt(16));
	}
	protected void placeSkull(World world, int i, int j, int k, int dir) { setBlockAndMetadata(world, i, j, k, Blocks.skull, 1);
	TileEntity tileentity = getTileEntity(world, i, j, k);
	if (tileentity instanceof TileEntitySkull) { TileEntitySkull skull = (TileEntitySkull)tileentity;
	dir += this.rotationMode * 4;
	dir %= 16;
	skull.func_145903_a(dir);
	}
	}
	protected void placeSign(World world, int i, int j, int k, Block block, int meta, String[] text) { setBlockAndMetadata(world, i, j, k, block, meta);
	TileEntity te = getTileEntity(world, i, j, k);
	if (te instanceof TileEntitySign) { TileEntitySign sign = (TileEntitySign)te;
	for (int l = 0;
			l < sign.signText.length;
			l++) sign.signText[l] = text[l];
	}
	}
	protected void placeAnimalJar(World world, int i, int j, int k, Block block, int meta, EntityLiving creature) { setBlockAndMetadata(world, i, j, k, block, meta);
	TileEntity te = getTileEntity(world, i, j, k);
	if (te instanceof LOTRTileEntityAnimalJar) { LOTRTileEntityAnimalJar jar = (LOTRTileEntityAnimalJar)te;
	NBTTagCompound nbt = new NBTTagCompound();
	if (creature != null) { int i1 = getX(i, k);
	int j1 = getY(j);
	int k1 = getZ(i, k);
	creature.setPosition(i1 + 0.5D, j1, k1 + 0.5D);
	creature.onSpawnWithEgg(null);
	if (creature.writeToNBTOptional(nbt)) jar.setEntityData(nbt);
	}
	}
	}

	/*
	 * //protected void placeIthildinDoor(World world, int i, int j, int k, Block
	 * block, int meta, LOTRBlockGate.DoorSize doorSize) { int i1 = getX(i, k); int
	 * j1 = getY(j); int k1 = getZ(i, k); int xzFactorX = (meta == 2) ? -1 : ((meta
	 * == 3) ? 1 : 0); int xzFactorZ = (meta == 4) ? 1 : ((meta == 5) ? -1 : 0); for
	 * (int y = 0; y < doorSize.height; y++) { for (int xz = 0; xz < doorSize.width;
	 * xz++) { int i2 = i + xz * xzFactorX; int j2 = j + y; int k2 = k + xz *
	 * xzFactorZ; setBlockAndMetadata(world, i2, j2, k2, block, meta);
	 * LOTRTileEntityDwarvenDoor door =
	 * (LOTRTileEntityDwarvenDoor)getTileEntity(world, i2, j2, k2); if (door !=
	 * null) { door.setDoorSizeAndPos(doorSize, xz, y); door.setDoorBasePos(i1, j1,
	 * k1); } } } }
	 */
	protected void spawnNPCAndSetHome(EntityCreature entity, World world, int i, int j, int k, int homeDistance) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	entity.setLocationAndAngles(i + 0.5D, j, k + 0.5D, 0.0F, 0.0F);
	entity.onSpawnWithEgg(null);
	if (entity instanceof LOTREntityNPC) ((LOTREntityNPC)entity).isNPCPersistent = true;
	world.spawnEntityInWorld(entity);
	entity.setHomeArea(i, j, k, homeDistance);
	}
	protected void leashEntityTo(EntityCreature entity, World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	EntityLeashKnot leash = EntityLeashKnot.func_110129_a(world, i, j, k);
	entity.setLeashedToEntity(leash, true);
	}
	protected void placeNPCRespawner(LOTREntityNPCRespawner entity, World world, int i, int j, int k) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	entity.setLocationAndAngles(i + 0.5D, j, k + 0.5D, 0.0F, 0.0F);
	world.spawnEntityInWorld(entity);
	}
	protected void placeRug(LOTREntityRugBase rug, World world, int i, int j, int k, float rotation) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	if (!isInSBB(i, j, k)) return;
	float f = rotation;
	switch (this.rotationMode) { case 0: f += 0.0F;
	break;
	case 1: f += 270.0F;
	break;
	case 2: f += 180.0F;
	break;
	case 3: f += 90.0F;
	break;
	}
	f %= 360.0F;
	rug.setLocationAndAngles(i + 0.5D, j, k + 0.5D, f, 0.0F);
	world.spawnEntityInWorld(rug);
	}
	protected boolean generateSubstructure(LOTRWorldGenStructureBase2 str, World world, Random random, int i, int j, int k, int r) { return generateSubstructureWithRestrictionFlag(str, world, random, i, j, k, r, this.restrictions);
	}
	protected boolean generateSubstructureWithRestrictionFlag(LOTRWorldGenStructureBase2 str, World world, Random random, int i, int j, int k, int r, boolean isRestrict) { int i1 = i;
	int k1 = k;
	i = getX(i1, k1);
	k = getZ(i1, k1);
	j = getY(j);
	r += this.rotationMode;
	r %= 4;
	str.restrictions = isRestrict;
	str.usingPlayer = this.usingPlayer;
	str.villageInstance = this.villageInstance;
	str.threadTimelapse = this.threadTimelapse;
	str.setStructureBB(this.sbb);
	return str.generateWithSetRotation(world, random, i, j, k, r);
	}
	private static class BlockAliasPool
	/*      */   {
		/*      */     private static class BlockMetaEntry extends WeightedRandom.Item
		/*      */     {
			/* 1351 */       public final Block block;
			public BlockMetaEntry(int w, Block b, int m) { super(w);

			/* 1352 */         this.block = b;

			/* 1353 */         this.meta = m;
			}

			/*      */ 
			/*      */       
			/*      */       public final int meta;
		}

		/* 1357 */     private List<BlockMetaEntry> entries = new ArrayList();

		/*      */     
		/*      */     private int totalWeight;

		/*      */     
		/*      */     public void addEntry(int w, Block b, int m) {
			/* 1362 */       this.entries.add(new BlockMetaEntry(w, b, m));

			/* 1363 */       this.totalWeight = WeightedRandom.getTotalWeight(this.entries);

		/*      */     }

		/*      */ 
		/*      */ 
		/*      */     
		/* 1368 */     public BlockMetaEntry getEntry(Random random) { return (BlockMetaEntry)WeightedRandom.getRandomItem(random, this.entries, this.totalWeight);
		}
		private BlockAliasPool() {}
	}
	private static class BlockMetaEntry extends WeightedRandom.Item { public final Block block;
	public final int meta;

	/*      */     public BlockMetaEntry(int w, Block b, int m) {
		/*      */       super(w);

		/*      */       this.block = b;

		/*      */       this.meta = m;

	/*      */     }
	}

	/* 1374 */   protected void associateBlockAlias(String alias, Block block) { addBlockAliasOption(alias, 1, block);
	}

	/*      */ 
	/*      */ 
	/*      */ 
	/*      */   
	/* 1379 */   protected void addBlockAliasOption(String alias, int weight, Block block) { addBlockMetaAliasOption(alias, weight, block, -1);
	}

	/*      */ 
	/*      */ 
	/*      */ 
	/*      */   
	/* 1384 */   protected void associateBlockMetaAlias(String alias, Block block, int meta) { addBlockMetaAliasOption(alias, 1, block, meta);
	}

	/*      */ 
	/*      */ 
	/*      */   
	/*      */   protected void addBlockMetaAliasOption(String alias, int weight, Block block, int meta) {
		/* 1389 */     BlockAliasPool pool = (BlockAliasPool)this.scanAliases.get(alias);

		/* 1390 */     if (pool == null) {
			/*      */       
			/* 1392 */       pool = new BlockAliasPool();

			/* 1393 */       this.scanAliases.put(alias, pool);

		/*      */     }

		/* 1395 */     pool.addEntry(1, block, meta);

	/*      */   }

	/*      */ 
	/*      */ 
	/*      */   
	/* 1400 */   protected void setBlockAliasChance(String alias, float chance) { this.scanAliasChances.put(alias, Float.valueOf(chance));
	}

	/*      */ 
	/*      */ 
	/*      */   
	/*      */   protected void clearScanAlias(String alias) {
		/* 1405 */     this.scanAliases.remove(alias);

		/* 1406 */     this.scanAliasChances.remove(alias);

	/*      */   }

	/*      */ 
	/*      */   
	/*      */   protected void generateStrScan(World world, Random random, int i, int j, int k) {
		/* 1411 */     for (int pass = 0;
				pass <= 1;
				pass++) {
			/*      */       
			/* 1413 */       for (LOTRStructureScan.ScanStepBase step : this.currentStrScan.scanSteps) {
				/*      */         
				/* 1415 */         int i1 = i - step.x;

				/* 1416 */         int j1 = j + step.y;

				/* 1417 */         int k1 = k + step.z;

				/*      */         
				/* 1419 */         Block aliasBlock = null;

				/* 1420 */         int aliasMeta = -1;

				/* 1421 */         if (step.hasAlias()) {
					/*      */           
					/* 1423 */           String alias = step.getAlias();

					/* 1424 */           BlockAliasPool pool = (BlockAliasPool)this.scanAliases.get(alias);

					/* 1425 */           if (pool == null)
					/*      */           {
						/* 1427 */             throw new IllegalArgumentException("No block associated to alias " + alias + " !");

					/*      */           }

					/*      */           
					/* 1430 */           BlockAliasPool.BlockMetaEntry e = pool.getEntry(random);

					/* 1431 */           aliasBlock = e.block;

					/* 1432 */           aliasMeta = e.meta;

					/*      */           
					/* 1434 */           if (this.scanAliasChances.containsKey(alias)) {
						/*      */             
						/* 1436 */             float chance = ((Float)this.scanAliasChances.get(alias)).floatValue();

						/* 1437 */             if (random.nextFloat() >= chance) {
							/*      */               continue;

						/*      */             }

					/*      */           }

				/*      */         }

				/*      */         
				/* 1443 */         Block block = step.getBlock(aliasBlock);

				/* 1444 */         int meta = step.getMeta(aliasMeta);

				/*      */         
				/* 1446 */         boolean inThisPass = false;

				/* 1447 */         if (block.getMaterial().isOpaque() || block == Blocks.air) {
					/*      */           
					/* 1449 */           inThisPass = (pass == 0);

				/*      */         }

				/*      */         else {
					/*      */           
					/* 1453 */           inThisPass = (pass == 1);

				/*      */         }

				/*      */         
				/* 1456 */         if (inThisPass) {
					/*      */           
					/* 1458 */           if (step.findLowest)
					/*      */           {
						/* 1460 */             while (getY(j1) > 0 && !getBlock(world, i1, j1 - 1, k1).getMaterial().blocksMovement())
						/*      */             {
							/* 1462 */               j1--;

						/*      */             }

					/*      */           }

					/*      */           
					/* 1466 */           if (step instanceof LOTRStructureScan.ScanStepSkull) {
						/*      */             
						/* 1468 */             placeSkull(world, random, i1, j1, k1);

						/*      */             
						/*      */             continue;

					/*      */           }

					/* 1472 */           setBlockAndMetadata(world, i1, j1, k1, block, meta);

					/* 1473 */           if ((step.findLowest || j1 <= 1) && block.isOpaqueCube())
					/*      */           {
						/* 1475 */             setGrassToDirt(world, i1, j1 - 1, k1);

					/*      */           }

					/*      */           
					/* 1478 */           if (step.fillDown)
					/*      */           {
						/* 1480 */             for (int j2 = j1 - 1;
								!isOpaque(world, i1, j2, k1) && getY(j2) >= 0;
								j2--) {
							/*      */               
							/* 1482 */               setBlockAndMetadata(world, i1, j2, k1, block, meta);

							/* 1483 */               if (block.isOpaqueCube())
							/*      */               {
								/* 1485 */                 setGrassToDirt(world, i1, j2 - 1, k1);

							/*      */               }

						/*      */             }

					/*      */           }

				/*      */         }

			/*      */       }

		/*      */     }

		/*      */ 
		/*      */     
		/* 1494 */     this.currentStrScan = null;

		/* 1495 */     this.scanAliases.clear();

	/*      */   }

	/*      */ 
	/*      */   
	/*      */   protected int getX(int x, int z) {
		/* 1500 */     switch (this.rotationMode) {
		/*      */       
		/*      */       case 0:
			/* 1503 */         return this.originX - x;

		/*      */       case 1:
			/* 1505 */         return this.originX - z;

		/*      */       case 2:
			/* 1507 */         return this.originX + x;

		/*      */       case 3:
			/* 1509 */         return this.originX + z;

		/*      */     }

		/* 1511 */     return this.originX;

	/*      */   }

	/*      */ 
	/*      */   
	/*      */   protected int getZ(int x, int z) {
		/* 1516 */     switch (this.rotationMode) {
		/*      */       
		/*      */       case 0:
			/* 1519 */         return this.originZ + z;

		/*      */       case 1:
			/* 1521 */         return this.originZ - x;

		/*      */       case 2:
			/* 1523 */         return this.originZ - z;

		/*      */       case 3:
			/* 1525 */         return this.originZ + x;

		/*      */     }

		/* 1527 */     return this.originZ;

	/*      */   }

	/*      */ 
	/*      */ 
	/*      */   
	/* 1532 */   protected int getY(int y) { return this.originY + y;
	}

	/*      */ 
	/*      */ 
	/*      */   
	/*      */   protected final boolean isSurface(World world, int i, int j, int k) {
		/* 1537 */     int i1 = i;

		/* 1538 */     int k1 = k;

		/* 1539 */     i = getX(i1, k1);

		/* 1540 */     k = getZ(i1, k1);

		/* 1541 */     j = getY(j);

		/*      */     
		/* 1543 */     if (isSurfaceStatic(world, i, j, k))
		/*      */     {
			/* 1545 */       return true;

		/*      */     }

		/* 1547 */     if (this.villageInstance != null && this.villageInstance.isVillageSurface(world, i, j, k))
		/*      */     {
			/* 1549 */       return true;

		/*      */     }

		/*      */     
		/* 1552 */     return false;

	/*      */   }

	/*      */ 
	/*      */   
	/*      */   public static boolean isSurfaceStatic(World world, int i, int j, int k) {
		/* 1557 */     Block block = world.getBlock(i, j, k);

		/* 1558 */     BiomeGenBase biome = world.getBiomeGenForCoords(i, k);

		/*      */     
		/* 1560 */     if (block instanceof net.minecraft.block.BlockSlab && !block.isOpaqueCube())
		/*      */     {
			/* 1562 */       return isSurfaceStatic(world, i, j - 1, k);

		/*      */     }

		/*      */     
		/* 1565 */     Block above = world.getBlock(i, j + 1, k);

		/* 1566 */     if (above.getMaterial().isLiquid())
		/*      */     {
			/* 1568 */       return false;

		/*      */     }

		/*      */     
		/* 1571 */     if (block == biome.topBlock || block == biome.fillerBlock)
		/*      */     {
			/* 1573 */       return true;

		/*      */     }

		/* 1575 */     if (block == Blocks.grass || block == Blocks.dirt || block == Blocks.gravel || block == LOTRMod.dirtPath)
		/*      */     {
			/* 1577 */       return true;

		/*      */     }

		/* 1579 */     if (block == LOTRMod.mudGrass || block == LOTRMod.mud)
		/*      */     {
			/* 1581 */       return true;

		/*      */     }

		/* 1583 */     if (block == Blocks.sand || block == LOTRMod.whiteSand)
		/*      */     {
			/* 1585 */       return true;

		/*      */     }

		/* 1587 */     if (block == LOTRMod.mordorDirt || block == LOTRMod.mordorGravel)
		/*      */     {
			/* 1589 */       return true;

		/*      */     }

		/*      */     
		/* 1592 */     return false;

	/*      */   }

/*      */ }



/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\common\world\structure2\LOTRWorldGenStructureBase2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */