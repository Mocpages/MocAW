/*     */ package lotr.common.world.biome;

import lotr.common.LOTRMod;
import lotr.common.world.LOTRChunkProvider;
import lotr.common.world.LOTRWorldChunkManager;
import lotr.common.world.biome.variant.LOTRBiomeVariant;
import lotr.common.world.feature.*;
import lotr.common.world.map.LOTRRoads;
import lotr.common.world.structure.LOTRWorldGenMarshHut;
import lotr.common.world.structure.LOTRWorldGenOrcDungeon;
import lotr.common.world.structure2.LOTRWorldGenGrukHouse;
import lotr.common.world.structure2.LOTRWorldGenTicketBooth;
import lotr.common.world.village.LOTRVillageGen;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LOTRBiomeDecorator {
    private World worldObj;
    private Random rand;
    private int chunkX;
    private int chunkZ;
    private LOTRBiome biome;
    private List<OreGenerant> biomeSoils;
    private List<OreGenerant> biomeOres;
    private List<OreGenerant> biomeGems;
    public float biomeOreFactor;
    public float biomeGemFactor;
    protected WorldGenerator clayGen;
    private WorldGenerator sandGen;
    private WorldGenerator whiteSandGen;
    private WorldGenerator quagmireGen;
    private WorldGenerator surfaceGravelGen;
    private WorldGenerator flowerGen;
    private WorldGenerator logGen;
    private WorldGenerator mushroomBrownGen;
    private WorldGenerator mushroomRedGen;
    private WorldGenerator caneGen;
    private WorldGenerator reedGen;
    private WorldGenerator dryReedGen;
    private WorldGenerator cornGen;
    private WorldGenerator pumpkinGen;
    private WorldGenerator waterlilyGen;
    private WorldGenerator cobwebGen;
    private WorldGenerator stalactiteGen;
    private WorldGenerator vinesGen;
    private WorldGenerator cactusGen;
    private WorldGenerator melonGen;
    public int sandPerChunk;
    public int clayPerChunk;
    public int quagmirePerChunk;
    public int treesPerChunk;

    /*     */   public int willowPerChunk;
    /*     */   public int logsPerChunk;
    /*     */   public int vinesPerChunk;
    /*     */   public int flowersPerChunk;
    /*     */   public int doubleFlowersPerChunk;
    /*     */   public int grassPerChunk;
    /*     */   public int doubleGrassPerChunk;
    /*     */   public boolean enableFern;
    /*     */   public boolean enableSpecialGrasses;
    /*     */   public int deadBushPerChunk;
    /*     */   public int waterlilyPerChunk;
    /*     */   public int mushroomsPerChunk;
    /*     */   public boolean enableRandomMushroom;
    /*     */   public int canePerChunk;
    /*     */   public int reedPerChunk;
    /*     */   public float dryReedChance;
    /*     */   public int cornPerChunk;
    /*     */   public int cactiPerChunk;
    /*     */   public float melonPerChunk;
    /*     */   public boolean generateWater;
    /*     */   public boolean generateLava;
    /*     */   public boolean generateCobwebs;
    /*     */   public boolean generateAthelas;
    /*     */   public boolean whiteSand;
    /*     */   private int treeClusterSize;
    /*     */   private int treeClusterChance;
    /*     */   private WorldGenerator orcDungeonGen;
    /*     */   private WorldGenerator trollHoardGen;
    /*     */   public boolean generateOrcDungeon;
    /*     */   public boolean generateTrollHoard;
    /*     */   private List<LOTRTreeType.WeightedTreeType> treeTypes;
    /*     */   private Random structureRand;
    /*     */   private List<RandomStructure> randomStructures;
    /*     */   private List<LOTRVillageGen> villages;
    /*     */
    /*     */   private class OreGenerant { public OreGenerant(WorldGenerator gen, float f, int min, int max) {
        /*  38 */       this.oreGen = gen;
        /*  39 */       this.oreChance = f;
        /*  40 */       this.minHeight = min;
        /*  41 */       this.maxHeight = max;
        /*     */     }
        /*     */
        /*     */
        /*     */     private WorldGenerator oreGen;
        /*     */
        /*     */     private float oreChance;
        /*     */
        /*     */     private int minHeight;
        /*     */     private int maxHeight; }
    /*     */
    /*     */
    /*  53 */   public void addSoil(WorldGenerator gen, float f, int min, int max) { this.biomeSoils.add(new OreGenerant(gen, f, min, max)); }
    /*     */
    /*     */
    /*     */
    /*     */
    /*  58 */   public void addOre(WorldGenerator gen, float f, int min, int max) { this.biomeOres.add(new OreGenerant(gen, f, min, max)); }
    /*     */
    /*     */
    /*     */
    /*     */
    /*  63 */   public void addGem(WorldGenerator gen, float f, int min, int max) { this.biomeGems.add(new OreGenerant(gen, f, min, max)); }
    /*     */
    /*     */
    /*     */
    /*     */   public void clearOres() {
        /*  68 */     this.biomeSoils.clear();
        /*  69 */     this.biomeOres.clear();
        /*  70 */     this.biomeGems.clear();
        /*     */   }
    /*     */
    /*     */   private void addDefaultOres()
    /*     */   {
        /*  75 */     addSoil(new WorldGenMinable(Blocks.dirt, 32), 40.0F, 0, 256);
        /*  76 */     addSoil(new WorldGenMinable(Blocks.gravel, 32), 20.0F, 0, 256);
        /*     */
        /*  78 */     addOre(new WorldGenMinable(Blocks.coal_ore, 16), 40.0F, 0, 128);
        /*  79 */     addOre(new WorldGenMinable(LOTRMod.oreCopper, 8), 16.0F, 0, 128);
        /*  80 */     addOre(new WorldGenMinable(LOTRMod.oreTin, 8), 16.0F, 0, 128);
        /*  81 */     addOre(new WorldGenMinable(Blocks.iron_ore, 8), 20.0F, 0, 64);
        /*     */
        /*  83 */     addOre(new WorldGenMinable(LOTRMod.oreSulfur, 8), 2.0F, 0, 64);
        /*  84 */     addOre(new WorldGenMinable(LOTRMod.oreSaltpeter, 8), 2.0F, 0, 64);
        /*  85 */     addOre(new WorldGenMinable(LOTRMod.oreSalt, 12), 2.0F, 0, 64);
        /*     */
        /*  87 */     addOre(new WorldGenMinable(Blocks.gold_ore, 8), 2.0F, 0, 32);
        /*  88 */     addOre(new WorldGenMinable(LOTRMod.oreSilver, 8), 3.0F, 0, 32);
        /*     */
        /*  90 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 1, 6, Blocks.stone), 2.0F, 0, 64);
        /*  91 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 0, 6, Blocks.stone), 2.0F, 0, 64);
        /*  92 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 4, 5, Blocks.stone), 1.5F, 0, 48);
        /*  93 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 6, 5, Blocks.stone), 1.5F, 0, 48);
        /*  94 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 2, 4, Blocks.stone), 1.0F, 0, 32);
        /*  95 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 3, 4, Blocks.stone), 1.0F, 0, 32);
        /*  96 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 7, 4, Blocks.stone), 0.75F, 0, 24);
        /*  97 */     addGem(new WorldGenMinable(LOTRMod.oreGem, 5, 4, Blocks.stone), 0.5F, 0, 16); }
                    public LOTRBiomeDecorator(LOTRBiome lotrbiome) { this.biomeSoils = new ArrayList(); this.biomeOres = new ArrayList(); this.biomeGems = new ArrayList();
        /*     */     this.biomeOreFactor = 1.0F;
        /*     */     this.biomeGemFactor = 0.5F;
        /* 100 */     this.clayGen = new LOTRWorldGenSand(Blocks.clay, 5, 1);
        /* 101 */     this.sandGen = new LOTRWorldGenSand(Blocks.sand, 7, 2);
        /* 102 */     this.whiteSandGen = new LOTRWorldGenSand(LOTRMod.whiteSand, 7, 2);
        /* 103 */     this.quagmireGen = new LOTRWorldGenSand(LOTRMod.quagmire, 7, 2);
        /* 104 */     this.surfaceGravelGen = new LOTRWorldGenSurfaceGravel();
        /*     */
        /* 106 */     this.flowerGen = new LOTRWorldGenBiomeFlowers();
        /* 107 */     this.logGen = new LOTRWorldGenLogs();
        /* 108 */     this.mushroomBrownGen = new WorldGenFlowers(Blocks.brown_mushroom);
        /* 109 */     this.mushroomRedGen = new WorldGenFlowers(Blocks.red_mushroom);
        /* 110 */     this.caneGen = new WorldGenReed();
        /* 111 */     this.reedGen = new LOTRWorldGenReeds(LOTRMod.reeds);
        /* 112 */     this.dryReedGen = new LOTRWorldGenReeds(LOTRMod.driedReeds);
        /* 113 */     this.cornGen = new LOTRWorldGenCorn();
        /* 114 */     this.pumpkinGen = new WorldGenPumpkin();
        /* 115 */     this.waterlilyGen = new WorldGenWaterlily();
        /* 116 */     this.cobwebGen = new LOTRWorldGenCaveCobwebs();
        /* 117 */     this.stalactiteGen = new LOTRWorldGenStalactites();
        /* 118 */     this.vinesGen = new WorldGenVines();
        /* 119 */     this.cactusGen = new WorldGenCactus();
        /* 120 */     this.melonGen = new WorldGenMelon();
        /*     */
        /* 122 */     this.sandPerChunk = 4;
        /* 123 */     this.clayPerChunk = 3;
        /* 124 */     this.quagmirePerChunk = 0;
        /* 125 */     this.treesPerChunk = 0;
        /* 126 */     this.willowPerChunk = 0;
        /* 127 */     this.logsPerChunk = 0;
        /* 128 */     this.vinesPerChunk = 0;
        /* 129 */     this.flowersPerChunk = 2;
        /* 130 */     this.doubleFlowersPerChunk = 0;
        /* 131 */     this.grassPerChunk = 1;
        /* 132 */     this.doubleGrassPerChunk = 0;
        /* 133 */     this.enableFern = false;
        /* 134 */     this.enableSpecialGrasses = true;
        /* 135 */     this.deadBushPerChunk = 0;
        /* 136 */     this.waterlilyPerChunk = 0;
        /* 137 */     this.mushroomsPerChunk = 0;
        /* 138 */     this.enableRandomMushroom = true;
        /* 139 */     this.canePerChunk = 0;
        /* 140 */     this.reedPerChunk = 1;
        /* 141 */     this.dryReedChance = 0.1F;
        /* 142 */     this.cornPerChunk = 0;
        /* 143 */     this.cactiPerChunk = 0;
        /* 144 */     this.melonPerChunk = 0.0F;
        /* 145 */     this.generateWater = true;
        /* 146 */     this.generateLava = true;
        /* 147 */     this.generateCobwebs = true;
        /* 148 */     this.generateAthelas = false;
        /* 149 */     this.whiteSand = false;
        /*     */
        /*     */
        /* 152 */     this.treeClusterChance = -1;
        /*     */
        /* 154 */     this.orcDungeonGen = new LOTRWorldGenOrcDungeon(false);
        /* 155 */     this.trollHoardGen = new LOTRWorldGenTrollHoard();
        /* 156 */     this.generateOrcDungeon = false;
        /* 157 */     this.generateTrollHoard = false;
        /*     */
        /* 159 */     this.treeTypes = new ArrayList();
        /*     */
        /* 161 */     this.structureRand = new Random();
        /* 162 */     this.randomStructures = new ArrayList();
        /* 163 */     this.villages = new ArrayList();
        /*     */
        /*     */
        /*     */
        /* 167 */     this.biome = lotrbiome;
        /* 168 */     addDefaultOres(); }
    /*     */
    /*     */
    /*     */
    /*     */
    /* 173 */   public void addTree(LOTRTreeType type, int weight) { this.treeTypes.add(new LOTRTreeType.WeightedTreeType(type, weight)); }
    /*     */
    /*     */
    /*     */
    /*     */
    /* 178 */   public void clearTrees() { this.treeTypes.clear(); }
    /*     */
    /*     */
    /*     */
    /*     */   public LOTRTreeType getRandomTree(Random random) {
        /* 183 */     if (this.treeTypes.isEmpty())
            /*     */     {
            /* 185 */       return LOTRTreeType.OAK;
            /*     */     }
        /*     */
        /* 188 */     WeightedRandom.Item item = WeightedRandom.getRandomItem(random, this.treeTypes);
        /* 189 */     return ((LOTRTreeType.WeightedTreeType)item).treeType;
        /*     */   }
    /*     */
    /*     */
    /*     */   public LOTRTreeType getRandomTreeForVariant(Random random, LOTRBiomeVariant variant) {
        /* 194 */     if (variant.treeTypes.isEmpty())
            /*     */     {
            /* 196 */       return getRandomTree(random);
            /*     */     }
        /*     */
        /*     */
        /* 200 */     float f = variant.variantTreeChance;
        /* 201 */     if (random.nextFloat() < f)
            /*     */     {
            /* 203 */       return variant.getRandomTree(random);
            /*     */     }
        /*     */
        /*     */
        /* 207 */     return getRandomTree(random);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public void genTree(World world, Random random, int i, int j, int k) {
        /* 214 */     WorldGenAbstractTree worldGenAbstractTree = this.biome.getTreeGen(world, random, i, j, k);
        /* 215 */     worldGenAbstractTree.generate(world, random, i, j, k);
        /*     */   }
    /*     */
    /*     */
    /*     */   public void setTreeCluster(int size, int chance) {
        /* 220 */     this.treeClusterSize = size;
        /* 221 */     this.treeClusterChance = chance;
        /*     */   }
    /*     */
    /*     */
    /*     */
    /* 226 */   public void resetTreeCluster() { setTreeCluster(0, -1); }
    /*     */
    /*     */
    /*     */   private class RandomStructure
            /*     */   {
        /*     */     public WorldGenerator structureGen;
        /*     */
        /*     */     public int chunkChance;
        /*     */
        /*     */     public RandomStructure(WorldGenerator w, int i) {
            /* 236 */       this.structureGen = w;
            /* 237 */       this.chunkChance = i;
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */
    /* 243 */   public void addRandomStructure(WorldGenerator structure, int chunkChance) { this.randomStructures.add(new RandomStructure(structure, chunkChance)); }
    /*     */
    /*     */
    /*     */
    /*     */
    /* 248 */   public void clearRandomStructures() { this.randomStructures.clear(); }
    /*     */
    /*     */
    /*     */
    /*     */
    /* 253 */   public void addVillage(LOTRVillageGen village) { this.villages.add(village); }
    /*     */
    /*     */
    /*     */
    /*     */
    /* 258 */   public void clearVillages() { this.villages.clear(); }
    /*     */
    /*     */
    /*     */
    /*     */   public void checkForVillages(World world, int i, int k, LOTRChunkProvider.ChunkFlags chunkFlags) {
        /* 263 */     chunkFlags.isVillage = false;
        /* 264 */     chunkFlags.isFlatVillage = false;
        /*     */
        /* 266 */     for (LOTRVillageGen village : this.villages) {
            /*     */
            /* 268 */       List<LOTRVillageGen.AbstractInstance> instances = village.getNearbyVillagesAtPosition(world, i, k);
            /* 269 */       if (!instances.isEmpty()) {
                /*     */
                /* 271 */         chunkFlags.isVillage = true;
                /* 272 */         for (LOTRVillageGen.AbstractInstance inst : instances) {
                    /*     */
                    /* 274 */           if (inst.isFlat())
                        /*     */           {
                        /* 276 */             chunkFlags.isFlatVillage = true;
                        /*     */           }
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public int getVariantTreesPerChunk(LOTRBiomeVariant variant) {
        /* 285 */     int trees = this.treesPerChunk;
        /* 286 */     if (variant.treeFactor > 1.0F)
            /*     */     {
            /* 288 */       trees = Math.max(trees, 1);
            /*     */     }
        /* 290 */     return Math.round(trees * variant.treeFactor);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public void decorate(World world, Random random, int i, int k) {
        /* 296 */     this.worldObj = world;
        /* 297 */     this.rand = random;
        /* 298 */     this.chunkX = i;
        /* 299 */     this.chunkZ = k;
        /* 300 */     decorate();
        /*     */   }
    /*     */
    /*     */
    /*     */   private void decorate() {
        /* 305 */     LOTRBiomeVariant biomeVariant = ((LOTRWorldChunkManager)this.worldObj.getWorldChunkManager()).getBiomeVariantAt(this.chunkX + 8, this.chunkZ + 8);
        /*     */
        /* 307 */     generateOres();
        /*     */
        /* 309 */     biomeVariant.decorateVariant(this.worldObj, this.rand, this.chunkX, this.chunkZ, this.biome);
        /*     */
        /* 311 */     if (this.rand.nextBoolean() && this.generateCobwebs) {
            /*     */
            /* 313 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 314 */       int j = this.rand.nextInt(60);
            /* 315 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 316 */       this.cobwebGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 319 */     for (int l = 0; l < 3; l++) {
            /*     */
            /* 321 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 322 */       int j = this.rand.nextInt(60);
            /* 323 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 324 */       this.stalactiteGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 327 */     for (int l = 0; l < this.quagmirePerChunk; l++) {
            /*     */
            /* 329 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 330 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 331 */       this.quagmireGen.generate(this.worldObj, this.rand, i, this.worldObj.getTopSolidOrLiquidBlock(i, k), k);
            /*     */     }
        /*     */
        /* 334 */     for (int l = 0; l < this.sandPerChunk; l++) {
            /*     */
            /* 336 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 337 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 338 */       WorldGenerator biomeSandGenerator = this.sandGen;
            /* 339 */       if (this.whiteSand)
                /*     */       {
                /* 341 */         biomeSandGenerator = this.whiteSandGen;
                /*     */       }
            /* 343 */       this.sandGen.generate(this.worldObj, this.rand, i, this.worldObj.getTopSolidOrLiquidBlock(i, k), k);
            /*     */     }
        /*     */
        /* 346 */     for (int l = 0; l < this.clayPerChunk; l++) {
            /*     */
            /* 348 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 349 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 350 */       this.clayGen.generate(this.worldObj, this.rand, i, this.worldObj.getTopSolidOrLiquidBlock(i, k), k);
            /*     */     }
        /*     */
        /* 353 */     if (this.rand.nextInt(60) == 0) {
            /*     */
            /* 355 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 356 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;

            /* 357 */       this.surfaceGravelGen.generate(this.worldObj, this.rand, i, 0, k);
            /*     */     }
        /*     */
        /* 360 */     if (!biomeVariant.disableStructures)
            /*     */     {
            /* 362 */       if (Math.abs(this.chunkX) > 32 && Math.abs(this.chunkZ) > 32) {
                /*     */
                /* 364 */         long seed = (this.chunkX * 1879267) ^ this.chunkZ * 67209689L;
                /* 365 */         seed = seed * seed * 5829687L + seed * 2876L;
                /* 366 */         this.structureRand.setSeed(seed);
                /*     */
                /* 368 */         boolean roadNear = (LOTRRoads.isRoadNear(this.chunkX + 8, this.chunkZ + 8, 16) >= 0.0F);
                /* 369 */         if (!roadNear)
                    /*     */         {
                    /* 371 */           for (RandomStructure randomstructure : this.randomStructures) {
                        /*     */
                        /* 373 */             if (this.structureRand.nextInt(randomstructure.chunkChance) == 0) {
                            /*     */
                            /* 375 */               int i = this.chunkX + this.rand.nextInt(16) + 8;
                            /* 376 */               int k = this.chunkZ + this.rand.nextInt(16) + 8;
                            /* 377 */               int j = this.worldObj.getTopSolidOrLiquidBlock(i, k);

                            /* 378 */               //randomstructure.generate(this.worldObj, this.rand, i, j, k);
                            /*     */             }
                        /*     */           }
                    /*     */         }
                /*     */
                /* 383 */         for (LOTRVillageGen village : this.villages)
                    /*     */         {
                    /* 385 */           village.generateInChunk(this.worldObj, this.chunkX, this.chunkZ);
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */
        /* 390 */     if (LOTRWorldGenMarshHut.generatesAt(this.worldObj, this.chunkX, this.chunkZ)) {
            /*     */
            /* 392 */       int i = this.chunkX + 8;
            /* 393 */       int k = this.chunkZ + 8;
            /* 394 */       int j = this.worldObj.getTopSolidOrLiquidBlock(i, k);
            /* 395 */       LOTRWorldGenMarshHut lOTRWorldGenMarshHut = new LOTRWorldGenMarshHut();
            /* 396 */       lOTRWorldGenMarshHut.restrictions = false;
            /* 397 */       lOTRWorldGenMarshHut.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /* 399 */     if (LOTRWorldGenGrukHouse.generatesAt(this.worldObj, this.chunkX, this.chunkZ)) {
            /*     */
            /* 401 */       int i = this.chunkX + 8;
            /* 402 */       int k = this.chunkZ + 8;
            /* 403 */       int j = this.worldObj.getTopSolidOrLiquidBlock(i, k);
            /* 404 */       LOTRWorldGenGrukHouse lOTRWorldGenGrukHouse = new LOTRWorldGenGrukHouse(false);
            /* 405 */       lOTRWorldGenGrukHouse.restrictions = false;
            /* 406 */       lOTRWorldGenGrukHouse.generateWithSetRotation(this.worldObj, this.rand, i, j, k, 2);
            /*     */     }
        /* 408 */     if (LOTRWorldGenTicketBooth.generatesAt(this.worldObj, this.chunkX, this.chunkZ)) {
            /*     */
            /* 410 */       int i = this.chunkX + 8;
            /* 411 */       int k = this.chunkZ + 8;
            /* 412 */       int j = worldObj.getTopSolidOrLiquidBlock(i, k);
            /* 413 */       LOTRWorldGenTicketBooth lOTRWorldGenTicketBooth = new LOTRWorldGenTicketBooth(false);
            /* 414 */       lOTRWorldGenTicketBooth.restrictions = false;
            /* 415 */       lOTRWorldGenTicketBooth.generateWithSetRotation(this.worldObj, this.rand, i, j, k, 3);
            /*     */     }
        /*     */
        /* 418 */     int trees = getVariantTreesPerChunk(biomeVariant);
        /* 419 */     if (this.rand.nextFloat() < this.biome.getTreeIncreaseChance() * biomeVariant.treeFactor)
            /*     */     {
            /* 421 */       trees++;
            /*     */     }
        /*     */
        /* 424 */     float reciprocalTreeFactor = 1.0F / Math.max(biomeVariant.treeFactor, 0.001F);
        /* 425 */     int cluster = Math.round(this.treeClusterChance * reciprocalTreeFactor);
        /* 426 */     if (cluster > 0) {
            /*     */
            /* 428 */       Random chunkRand = new Random();
            /* 429 */       long seed = (this.chunkX / this.treeClusterSize * 3129871) ^ (this.chunkZ / this.treeClusterSize) * 116129781L;
            /* 430 */       seed = seed * seed * 42317861L + seed * 11L;
            /* 431 */       chunkRand.setSeed(seed);
            /* 432 */       if (chunkRand.nextInt(cluster) == 0)
                /*     */       {
                /* 434 */         trees += 6 + this.rand.nextInt(5);
                /*     */       }
            /*     */     }
        /*     */
        /* 438 */     for (int l = 0; l < trees; l++) {
            /*     */
            /* 440 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 441 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /*     */
            /* 443 */       WorldGenAbstractTree worldGenAbstractTree = getRandomTreeForVariant(this.rand, biomeVariant).create(false, this.rand);
            /* 444 */       worldGenAbstractTree.generate(this.worldObj, this.rand, i, this.worldObj.getHeightValue(i, k), k);
            /*     */     }
        /*     */
        /* 447 */     for (int l = 0; l < this.willowPerChunk; l++) {
            /*     */
            /* 449 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 450 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 451 */       WorldGenAbstractTree worldGenAbstractTree = LOTRTreeType.WILLOW_WATER.create(false, this.rand);
            /* 452 */       worldGenAbstractTree.generate(this.worldObj, this.rand, i, this.worldObj.getHeightValue(i, k), k);
            /*     */     }
        /*     */
        /* 455 */     if (trees > 0) {
            /*     */
            /* 457 */       float fallenLeaves = trees / 2.0F;
            /* 458 */       int fallenLeavesI = (int)fallenLeaves;
            /* 459 */       float fallenLeavesR = fallenLeaves - fallenLeavesI;
            /* 460 */       if (this.rand.nextFloat() < fallenLeavesR)
                /*     */       {
                /* 462 */         fallenLeavesI++;
                /*     */       }
            /* 464 */       for (int l = 0; l < fallenLeaves; l++) {
                /*     */
                /* 466 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 467 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 468 */         new LOTRWorldGenFallenLeaves().generate(this.worldObj, this.rand, i, this.worldObj.getTopSolidOrLiquidBlock(i, k), k);
                /*     */       }
            /*     */     }
        /*     */
        /* 472 */     if (trees > 0) {
            /*     */
            /* 474 */       float bushes = trees / 3.0F;
            /* 475 */       int bushesI = (int)bushes;
            /* 476 */       float bushesR = bushes - bushesI;
            /* 477 */       if (this.rand.nextFloat() < bushesR)
                /*     */       {
                /* 479 */         bushesI++;
                /*     */       }
            /* 481 */       for (int l = 0; l < bushes; l++) {
                /*     */
                /* 483 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 484 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 485 */         new LOTRWorldGenBushes().generate(this.worldObj, this.rand, i, this.worldObj.getTopSolidOrLiquidBlock(i, k), k);
                /*     */       }
            /*     */     }
        /*     */
        /* 489 */     for (int l = 0; l < this.logsPerChunk; l++) {
            /*     */
            /* 491 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 492 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 493 */       this.logGen.generate(this.worldObj, this.rand, i, this.worldObj.getHeightValue(i, k), k);
            /*     */     }
        /*     */
        /* 496 */     for (int l = 0; l < this.vinesPerChunk; l++) {
            /*     */
            /* 498 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 499 */       int j = 64;
            /* 500 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 501 */       this.vinesGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 504 */     int flowers = this.flowersPerChunk;
        /* 505 */     flowers = Math.round(flowers * biomeVariant.flowerFactor);
        /* 506 */     for (int l = 0; l < flowers; l++) {
            /*     */
            /* 508 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 509 */       int j = this.rand.nextInt(128);
            /* 510 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 511 */       this.flowerGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 514 */     int doubleFlowers = this.doubleFlowersPerChunk;
        /* 515 */     doubleFlowers = Math.round(doubleFlowers * biomeVariant.flowerFactor);
        /* 516 */     for (int l = 0; l < doubleFlowers; l++) {
            /*     */
            /* 518 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 519 */       int j = this.rand.nextInt(128);
            /* 520 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 521 */       WorldGenerator doubleFlowerGen = this.biome.getRandomWorldGenForDoubleFlower(this.rand);
            /* 522 */       doubleFlowerGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 525 */     int grasses = this.grassPerChunk;
        /* 526 */     grasses = Math.round(grasses * biomeVariant.grassFactor);
        /* 527 */     for (int l = 0; l < grasses; l++) {
            /*     */
            /* 529 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 530 */       int j = this.rand.nextInt(128);
            /* 531 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 532 */       WorldGenerator grassGen = this.biome.getRandomWorldGenForGrass(this.rand);
            /* 533 */       grassGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 536 */     int doubleGrasses = this.doubleGrassPerChunk;
        /* 537 */     doubleGrasses = Math.round(doubleGrasses * biomeVariant.grassFactor);
        /* 538 */     for (int l = 0; l < doubleGrasses; l++) {
            /*     */
            /* 540 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 541 */       int j = this.rand.nextInt(128);
            /* 542 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 543 */       WorldGenerator grassGen = this.biome.getRandomWorldGenForDoubleGrass(this.rand);
            /* 544 */       grassGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 547 */     for (int l = 0; l < this.deadBushPerChunk; l++) {
            /*     */
            /* 549 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 550 */       int j = this.rand.nextInt(128);
            /* 551 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 552 */       (new WorldGenDeadBush(Blocks.deadbush)).generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 555 */     for (int l = 0; l < this.waterlilyPerChunk; l++) {
            /*     */
            /* 557 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 558 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /*     */
            /*     */       int j;
            /* 561 */       for (j = this.rand.nextInt(128); j > 0 && this.worldObj.getBlock(i, j - 1, k) == Blocks.air; j--);
            /*     */
            /*     */
            /*     */
            /*     */
            /* 566 */       this.waterlilyGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 569 */     for (int l = 0; l < this.mushroomsPerChunk; l++) {
            /*     */
            /* 571 */       if (this.rand.nextInt(4) == 0) {
                /*     */
                /* 573 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 574 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 575 */         int j = this.worldObj.getHeightValue(i, k);

                /* 576 */      //  this.mushroomBrownGen(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */
            /* 579 */       if (this.rand.nextInt(8) == 0) {
                /*     */
                /* 581 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 582 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 583 */         int j = this.worldObj.getHeightValue(i, k);
                /* 584 */        // this.mushroomRedGen(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 588 */     if (this.enableRandomMushroom) {
            /*     */
            /* 590 */       if (this.rand.nextInt(4) == 0) {
                /*     */
                /* 592 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 593 */         int j = this.rand.nextInt(128);
                /* 594 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 595 */         this.mushroomBrownGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */
            /* 598 */       if (this.rand.nextInt(8) == 0) {
                /*     */
                /* 600 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 601 */         int j = this.rand.nextInt(128);
                /* 602 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 603 */         this.mushroomRedGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 607 */     for (int l = 0; l < this.canePerChunk; l++) {
            /*     */
            /* 609 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 610 */       int j = this.rand.nextInt(128);
            /* 611 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 612 */       this.caneGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 615 */     for (int l = 0; l < 10; l++) {
            /*     */
            /* 617 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 618 */       int j = this.rand.nextInt(128);
            /* 619 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 620 */       this.caneGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 623 */     for (int l = 0; l < this.reedPerChunk; l++) {
            /*     */
            /* 625 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 626 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /*     */
            /*     */       int j;
            /* 629 */       for (j = this.rand.nextInt(128); j > 0 && this.worldObj.getBlock(i, j - 1, k) == Blocks.air; j--);
            /*     */
            /*     */
            /*     */
            /*     */
            /* 634 */       if (this.rand.nextFloat() < this.dryReedChance) {
                /*     */
                /* 636 */         this.dryReedGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */       else {
                /*     */
                /* 640 */         this.reedGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 644 */     for (int l = 0; l < this.cornPerChunk; l++) {
            /*     */
            /* 646 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 647 */       int j = this.rand.nextInt(128);
            /* 648 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 649 */       this.cornGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 652 */     for (int l = 0; l < this.cactiPerChunk; l++) {
            /*     */
            /* 654 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 655 */       int j = this.rand.nextInt(128);
            /* 656 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 657 */       this.cactusGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 660 */     if (this.melonPerChunk > 0.0F) {
            /*     */
            /* 662 */       int melonInt = MathHelper.floor_double(this.melonPerChunk);
            /* 663 */       float melonF = this.melonPerChunk - melonInt;
            /* 664 */       for (int l = 0; l < melonInt; l++) {
                /*     */
                /* 666 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 667 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 668 */         int j = this.worldObj.getHeightValue(i, k);

                /* 669 */         this.melonGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /* 671 */       if (this.rand.nextFloat() < melonF) {
                /*     */
                /* 673 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 674 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 675 */         int j = this.worldObj.getHeightValue(i, k);
                /* 676 */         this.melonGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 680 */     if (this.flowersPerChunk > 0 && this.rand.nextInt(32) == 0) {
            /*     */
            /* 682 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 683 */       int j = this.rand.nextInt(128);
            /* 684 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 685 */       this.pumpkinGen.generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 688 */     if (this.flowersPerChunk > 0 && this.rand.nextInt(4) == 0) {
            /*     */
            /* 690 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 691 */       int j = this.rand.nextInt(128);
            /* 692 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 693 */       (new LOTRWorldGenBerryBush()).generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 696 */     if (this.generateAthelas && this.rand.nextInt(30) == 0) {
            /*     */
            /* 698 */       int i = this.chunkX + this.rand.nextInt(16) + 8;
            /* 699 */       int j = this.rand.nextInt(128);
            /* 700 */       int k = this.chunkZ + this.rand.nextInt(16) + 8;
            /* 701 */       (new WorldGenFlowers(LOTRMod.athelas)).generate(this.worldObj, this.rand, i, j, k);
            /*     */     }
        /*     */
        /* 704 */     if (this.generateWater) {
            /*     */
            /* 706 */       LOTRWorldGenStreams lOTRWorldGenStreams = new LOTRWorldGenStreams(Blocks.flowing_water);
            /*     */
            /* 708 */       for (int l = 0; l < 50; l++) {
                /*     */
                /* 710 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 711 */         int j = this.rand.nextInt(this.rand.nextInt(120) + 8);
                /* 712 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 713 */         lOTRWorldGenStreams.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*
            /* 716 */       /*if (this.biome.minHeight > 1.0F)
                *//*     *//*       {
                *//* 718 *//*         for (int l = 0; l < 50; l++) {
                    *//*     *//*
                    *//* 720 *//*           int i = this.chunkX + this.rand.nextInt(16) + 8;
                    *//* 721 *//*           int j = 100 + this.rand.nextInt(150);
                    *//* 722 *//*           int k = this.chunkZ + this.rand.nextInt(16) + 8;
                    *//* 723 *//*           lOTRWorldGenStreams.generate(this.worldObj, this.rand, i, j, k);
                    *//*     *//*         }
                *//*     *//*       }*/
            /*     */     }
        /*     */
        /* 728 */     if (this.generateLava) {
            /*     */
            /* 730 */       LOTRWorldGenStreams lOTRWorldGenStreams = new LOTRWorldGenStreams(Blocks.flowing_lava);
            /*     */
            /* 732 */       int lava = 20;
            /* 733 */       if (this.biome instanceof LOTRBiomeGenMordor)
                /*     */       {
                /* 735 */         lava = 50;
                /*     */       }
            /*     */
            /* 738 */       for (int l = 0; l < lava; l++) {
                /*     */
                /* 740 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 741 */         int j = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
                /* 742 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 743 */         lOTRWorldGenStreams.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 747 */     if (this.generateOrcDungeon)
            /*     */     {
            /* 749 */       for (int l = 0; l < 6; l++) {
                /*     */
                /* 751 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 752 */         int j = this.rand.nextInt(128);
                /* 753 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;
                /* 754 */         this.orcDungeonGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 758 */     if (this.generateTrollHoard)
            /*     */     {
            /* 760 */       for (int l = 0; l < 2; l++) {
                /*     */
                /* 762 */         int i = this.chunkX + this.rand.nextInt(16) + 8;
                /* 763 */         int j = MathHelper.getRandomIntegerInRange(this.rand, 36, 90);
                /* 764 */         int k = this.chunkZ + this.rand.nextInt(16) + 8;

                /* 765 */         this.trollHoardGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */
        /* 769 */     if (biomeVariant.boulderGen != null)
            /*     */     {
            /* 771 */       if (this.rand.nextInt(biomeVariant.boulderChance) == 0) {
                /*     */
                /* 773 */         int boulders = MathHelper.getRandomIntegerInRange(this.rand, 1, biomeVariant.boulderMax);
                /* 774 */         for (int l = 0; l < boulders; l++) {
                    /*     */
                    /* 776 */           int i = this.chunkX + this.rand.nextInt(16) + 8;
                    /* 777 */           int k = this.chunkZ + this.rand.nextInt(16) + 8;
                                        //BOULDERS
                    /* 778 */           //biomeVariant.generate(this.worldObj, this.rand, i, this.worldObj.getHeightValue(i, k), k);
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void generateOres() {
        /* 786 */     for (OreGenerant soil : this.biomeSoils)
            /*     */     {
            /* 788 */       genStandardOre(soil.oreChance, soil.oreGen, soil.minHeight, soil.maxHeight);
            /*     */     }
        /* 790 */     for (OreGenerant ore : this.biomeOres) {
            /*     */
            /* 792 */       float f = ore.oreChance * this.biomeOreFactor;
            /* 793 */       genStandardOre(f, ore.oreGen, ore.minHeight, ore.maxHeight);
            /*     */     }
        /* 795 */     for (OreGenerant gem : this.biomeGems) {
            /*     */
            /* 797 */       float f = gem.oreChance * this.biomeGemFactor;
            /* 798 */       genStandardOre(f, gem.oreGen, gem.minHeight, gem.maxHeight);
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void genStandardOre(float ores, WorldGenerator oreGen, int minHeight, int maxHeight) {
        /* 804 */     while (ores > 0.0F) {
            /*     */       boolean generate;
            /*     */
            /* 807 */       if (ores >= 1.0F) {
                /*     */
                /* 809 */         generate = true;
                /*     */       }
            /*     */       else {
                /*     */
                /* 813 */         generate = (this.rand.nextFloat() < ores);
                /*     */       }
            /* 815 */       ores--;
            /*     */
            /* 817 */       if (generate) {
                /*     */
                /* 819 */         int i = this.chunkX + this.rand.nextInt(16);
                /* 820 */         int j = MathHelper.getRandomIntegerInRange(this.rand, minHeight, maxHeight);
                /* 821 */         int k = this.chunkZ + this.rand.nextInt(16);

                /* 822 */      //    oreGen.generate(this.worldObj, this.rand, i, j, k);
                /*     */       }
            /*     */     }
        /*     */   } }


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\common\world\biome\LOTRBiomeDecorator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */