/*     */ package lotr.common.world.structure;
/*     */ import java.util.Random;

/*     */ import lotr.common.LOTRFoods;
/*     */ import lotr.common.LOTRMod;
/*     */ import lotr.common.block.LOTRBlockFlowerPot;
/*     */ import lotr.common.block.LOTRBlockMug;
/*     */ import lotr.common.entity.LOTREntities;
/*     */ import lotr.common.entity.LOTREntityNPCRespawner;
/*     */ import lotr.common.entity.item.LOTREntityBanner;
/*     */ import lotr.common.entity.item.LOTREntityBannerWall;
/*     */ import lotr.common.item.LOTRItemBanner;
/*     */ import lotr.common.item.LOTRItemMug;
/*     */ import lotr.common.item.LOTRItemMug.Vessel;
/*     */ import lotr.common.recipe.LOTRBrewingRecipes;
/*     */ import lotr.common.tileentity.LOTRTileEntityArmorStand;
/*     */ import lotr.common.tileentity.LOTRTileEntityBarrel;
/*     */ import lotr.common.tileentity.LOTRTileEntityMobSpawner;
/*     */ import lotr.common.tileentity.LOTRTileEntityPlate;
/*     */ import lotr.common.tileentity.LOTRTileEntitySpawnerChest;
/*     */ import lotr.common.world.structure2.LOTRStructureTimelapse;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.item.EntityItemFrame;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.tileentity.TileEntitySkull;
/*     */ import net.minecraft.util.MathHelper;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraft.world.gen.feature.WorldGenerator;
/*     */ 
/*     */ public abstract class LOTRWorldGenStructureBase extends WorldGenerator {
/*  33 */   public EntityPlayer usingPlayer = null;
/*     */   public boolean restrictions = true;
/*     */   protected boolean notifyChanges;
/*     */   public LOTRStructureTimelapse.ThreadTimelapse threadTimelapse;
/*     */   
/*     */   public LOTRWorldGenStructureBase(boolean flag) {
/*  39 */     super(flag);
/*  40 */     this.notifyChanges = flag;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  45 */   protected int usingPlayerRotation() { return MathHelper.floor_double((this.usingPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3; }
/*     */ 
/*     */ 
/*     */ 
/*     */   @Override
/*     */   protected void setBlockAndNotifyAdequately(World world, int i, int j, int k, Block block, int meta) {
/*  51 */     //super.setBlockAndNotifyAdequately(world, i, j, k, block, meta);
/*     */     
/*  53 */     //if (block != Blocks.air && this.threadTimelapse != null)
/*     */     //{
/*  55 */     //  this.threadTimelapse.onBlockSet();
/*     */     //}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  61 */   protected void setBlockMetadata(World world, int i, int j, int k, int meta) { 
					world.setBlockMetadataWithNotify(i, j, k, meta, this.notifyChanges ? 3 : 2); }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void placeOrcTorch(World world, int i, int j, int k) {
/*  66 */     setBlockAndNotifyAdequately(world, i, j, k, LOTRMod.orcTorch, 0);
/*  67 */     setBlockAndNotifyAdequately(world, i, j + 1, k, LOTRMod.orcTorch, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeMobSpawner(World world, int i, int j, int k, Class entityClass) {
/*  72 */     setBlockAndNotifyAdequately(world, i, j, k, LOTRMod.mobSpawner, 0);
/*  73 */     TileEntity tileentity = world.getTileEntity(i, j, k);
/*  74 */     if (tileentity instanceof LOTRTileEntityMobSpawner)
/*     */     {
/*  76 */       ((LOTRTileEntityMobSpawner)tileentity).setEntityClassID(LOTREntities.getEntityIDFromClass(entityClass));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeSpawnerChest(World world, int i, int j, int k, Block block, int meta, Class entityClass) {
/*  82 */     setBlockAndNotifyAdequately(world, i, j, k, block, 0);
/*  83 */     setBlockMetadata(world, i, j, k, meta);
/*  84 */     TileEntity tileentity = world.getTileEntity(i, j, k);
/*  85 */     if (tileentity instanceof LOTRTileEntitySpawnerChest)
/*     */     {
/*  87 */       ((LOTRTileEntitySpawnerChest)tileentity).setMobID(entityClass);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  93 */   protected void placePlate(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList) { placePlate_do(world, random, i, j, k, plateBlock, foodList, false); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   protected void placePlateWithCertainty(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList) { placePlate_do(world, random, i, j, k, plateBlock, foodList, true); }
/*     */ 
/*     */ 
/*     */   
/*     */   private void placePlate_do(World world, Random random, int i, int j, int k, Block plateBlock, LOTRFoods foodList, boolean certain) {
/* 103 */     if (!certain && random.nextBoolean()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 108 */     setBlockAndNotifyAdequately(world, i, j, k, plateBlock, 0);
/* 109 */     if (certain || random.nextBoolean()) {
/*     */       
/* 111 */       TileEntity tileentity = world.getTileEntity(i, j, k);
/* 112 */       if (tileentity != null && tileentity instanceof LOTRTileEntityPlate) {
/*     */         
/* 114 */         LOTRTileEntityPlate plate = (LOTRTileEntityPlate)tileentity;
/* 115 */         ItemStack food = foodList.getRandomFoodForPlate(random);
/* 116 */         if (random.nextInt(4) == 0)
/*     */         {
/* 118 */           food.stackSize += 1 + random.nextInt(3);
/*     */         }
/* 120 */         plate.setFoodItem(food);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 127 */   protected void placeBarrel(World world, Random random, int i, int j, int k, int meta, LOTRFoods foodList) { placeBarrel(world, random, i, j, k, meta, foodList.getRandomBrewableDrink(random)); }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void placeBarrel(World world, Random random, int i, int j, int k, int meta, ItemStack drink) {
/* 132 */     setBlockAndNotifyAdequately(world, i, j, k, LOTRMod.barrel, meta);
/* 133 */     TileEntity tileentity = world.getTileEntity(i, j, k);
/* 134 */     if (tileentity instanceof LOTRTileEntityBarrel) {
/*     */       
/* 136 */       LOTRTileEntityBarrel barrel = (LOTRTileEntityBarrel)tileentity;
/* 137 */       barrel.barrelMode = 2;
/*     */       
/* 139 */       drink = drink.copy();
/* 140 */       LOTRItemMug.setStrengthMeta(drink, MathHelper.getRandomIntegerInRange(random, 1, 3));
/* 141 */       LOTRItemMug.setVessel(drink, LOTRItemMug.Vessel.MUG, true);
/* 142 */       drink.stackSize = MathHelper.getRandomIntegerInRange(random, LOTRBrewingRecipes.BARREL_CAPACITY / 2, LOTRBrewingRecipes.BARREL_CAPACITY);
/* 143 */       barrel.func_70299_a(9, drink);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 149 */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, LOTRFoods foodList) { placeMug(world, random, i, j, k, meta, foodList.getRandomPlaceableDrink(random), foodList); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 154 */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, ItemStack drink, LOTRFoods foodList) { placeMug(world, random, i, j, k, meta, drink, foodList.getPlaceableDrinkVessels()); }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void placeMug(World world, Random random, int i, int j, int k, int meta, ItemStack drink, Vessel[] vesselTypes) {
/* 159 */     LOTRItemMug.Vessel vessel = vesselTypes[random.nextInt(vesselTypes.length)];
/* 160 */     setBlockAndNotifyAdequately(world, i, j, k, vessel.getBlock(), meta);
/*     */     
/* 162 */     if (random.nextInt(3) != 0) {
/*     */       
/* 164 */       drink = drink.copy();
/* 165 */       drink.stackSize = 1;
/* 166 */       if (drink.getItem() instanceof LOTRItemMug && ((LOTRItemMug)drink.getItem()).isBrewable)
/*     */       {
/* 168 */         LOTRItemMug.setStrengthMeta(drink, MathHelper.getRandomIntegerInRange(random, 1, 3));
/*     */       }
/* 170 */       LOTRItemMug.setVessel(drink, vessel, true);
/* 171 */       LOTRBlockMug.setMugItem(world, i, j, k, drink, vessel);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeFlowerPot(World world, int i, int j, int k, ItemStack itemstack) {
/* 177 */     setBlockAndNotifyAdequately(world, i, j, k, LOTRMod.flowerPot, 0);
/* 178 */     LOTRBlockFlowerPot.setPlant(world, i, j, k, itemstack);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void spawnItemFrame(World world, int i, int j, int k, int direction, ItemStack itemstack) {
/* 183 */     EntityItemFrame frame = new EntityItemFrame(world, i, j, k, direction);
/* 184 */     frame.setDisplayedItem(itemstack);
/* 185 */     world.spawnEntityInWorld(frame);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeArmorStand(World world, int i, int j, int k, int direction, ItemStack[] armor) {
/* 190 */     setBlockAndNotifyAdequately(world, i, j, k, LOTRMod.armorStand, direction);
/* 191 */     setBlockAndNotifyAdequately(world, i, j + 1, k, LOTRMod.armorStand, direction | 0x4);
/* 192 */     TileEntity tileentity = world.getTileEntity(i, j, k);
/* 193 */     if (tileentity != null && tileentity instanceof LOTRTileEntityArmorStand) {
/*     */       
/* 195 */       LOTRTileEntityArmorStand armorStand = (LOTRTileEntityArmorStand)tileentity;
/* 196 */       for (int l = 0; l < armor.length; l++) {
/*     */         
/* 198 */         ItemStack armorPart = armor[l];
/* 199 */         if (armorPart == null) {
/*     */           
/* 201 */           armorStand.func_70299_a(l, null);
/*     */         }
/*     */         else {
/*     */           
/* 205 */           armorStand.func_70299_a(l, armor[l].copy());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeBanner(World world, int i, int j, int k, int direction, LOTRItemBanner.BannerType type) {
/* 213 */     LOTREntityBanner banner = new LOTREntityBanner(world);
/* 214 */     banner.setLocationAndAngles(i + 0.5D, j, k + 0.5D, direction * 90.0F, 0.0F);
/* 215 */     banner.setBannerType(type);
/* 216 */     world.spawnEntityInWorld(banner);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeWallBanner(World world, int i, int j, int k, int direction, LOTRItemBanner.BannerType type) {
/* 221 */     LOTREntityBannerWall banner = new LOTREntityBannerWall(world, i, j, k, direction);
/* 222 */     banner.setBannerType(type);
/* 223 */     world.spawnEntityInWorld(banner);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void placeNPCRespawner(LOTREntityNPCRespawner entity, World world, int i, int j, int k) {
/* 228 */     entity.setLocationAndAngles(i + 0.5D, j, k + 0.5D, 0.0F, 0.0F);
/* 229 */     world.spawnEntityInWorld(entity);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 234 */   protected void setGrassToDirt(World world, int i, int j, int k) { world.getBlock(i, j, k).onPlantGrow(world, i, j, k, i, j, k); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 239 */   protected void setAir(World world, int i, int j, int k) { setBlockAndNotifyAdequately(world, i, j, k, Blocks.air, 0); }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void placeSkull(World world, Random random, int i, int j, int k) {
/* 244 */     setBlockAndNotifyAdequately(world, i, j, k, Blocks.skull, 1);
/* 245 */     TileEntity tileentity = world.getTileEntity(i, j, k);
/* 246 */     if (tileentity != null && tileentity instanceof TileEntitySkull) {
/*     */       
/* 248 */       TileEntitySkull skull = (TileEntitySkull)tileentity;
/* 249 */       skull.func_145903_a(random.nextInt(16));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\common\world\structure\LOTRWorldGenStructureBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */