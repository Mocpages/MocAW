/*     */ package lotr.common.world.structure;

/*     */ import java.util.HashMap;
import java.util.LinkedHashMap;

import codechicken.lib.math.MathHelper;
import cpw.mods.fml.common.FMLLog;
import lotr.common.LOTRConfig;
/*     */ import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.mapgen.dwarvenmine.LOTRMapGenDwarvenMine;
import lotr.common.world.mapgen.tpyr.LOTRMapGenTauredainPyramid;
import lotr.common.world.structure2.LOTRStructureTimelapse;
/*     */ import lotr.common.world.structure2.LOTRWorldGenGondorStructure;
/*     */ import lotr.common.world.structure2.LOTRWorldGenStructureBase2;
/*     */ import lotr.common.world.village.LOTRVillageGen;
/*     */ import lotr.common.world.village.LOTRVillageGenDunedain;
/*     */ import lotr.common.world.village.LOTRVillageGenGondor;
/*     */ import lotr.common.world.village.LOTRVillageGenGulfHarad;
/*     */ import lotr.common.world.village.LOTRVillageGenHaradNomad;
/*     */ import lotr.common.world.village.LOTRVillageGenHarnedor;
/*     */ import lotr.common.world.village.LOTRVillageGenRhun;
/*     */ import lotr.common.world.village.LOTRVillageGenRohan;
/*     */ import lotr.common.world.village.LOTRVillageGenSouthron;
import lotr.common.world.village.LOTRVillageGenTauredain;
/*     */ import lotr.common.world.village.LOTRVillageGenUmbar;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraft.world.gen.feature.WorldGenerator;

/*     */
/*     */ public class LOTRStructures {
	/* 21 */ private static HashMap<Integer, IStructureProvider> idToClassMapping = new HashMap();
	/* 22 */ private static HashMap<Integer, String> idToStringMapping = new HashMap();
	/* 23 */ public static HashMap<Integer, StructureColorInfo> structureItemSpawners = new LinkedHashMap();

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */ private static void registerStructure(int id, IStructureProvider str, String name, int colorBG,
			int colorFG, boolean hide) {
		/* 33 */ idToClassMapping.put(Integer.valueOf(id), str);
		/* 34 */ idToStringMapping.put(Integer.valueOf(id), name);
		/* 35 */ structureItemSpawners.put(Integer.valueOf(id),
				new StructureColorInfo(id, colorBG, colorFG, str.isVillage(), hide));
		/*     */ }

	/*     */
	/*     */
	/*     */ public static class StructureColorInfo
	/*     */ {
		/*     */ public final int spawnedID;
		/*     */ public final int colorBackground;
		/*     */ public final int colorForeground;
		/*     */ public final boolean isVillage;
		/*     */ public final boolean isHidden;

		/*     */
		/*     */ public StructureColorInfo(int i, int colorBG, int colorFG, boolean vill, boolean hide) {
			/* 48 */ this.spawnedID = i;
			/* 49 */ this.colorBackground = colorBG;
			/* 50 */ this.colorForeground = colorFG;
			/* 51 */ this.isVillage = vill;
			/* 52 */ this.isHidden = hide;
			/*     */ }
		/*     */ }

	/*     */
	/*     */
	/*     */
	/* 58 */ public static IStructureProvider getStructureForID(int ID) {
		return (IStructureProvider) idToClassMapping.get(Integer.valueOf(ID));
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 63 */ public static String getNameFromID(int ID) {
		return (String) idToStringMapping.get(Integer.valueOf(ID));
	}

	/*     */
	/*     */
	/*     */
	/*     */ public static void registerStructures() {
		/* 68 */ /*
					 * registerStructure(1,
					 * lotr.common.world.structure2.LOTRWorldGenHobbitHole.class, "HobbitHole",
					 * 2727977, 8997164); 69 registerStructure(2,
					 * lotr.common.world.structure2.LOTRWorldGenHobbitTavern.class, "HobbitTavern",
					 * 9324081, 15975807); 70 registerStructure(3,
					 * LOTRWorldGenHobbitPicnicBench.class, "HobbitPicnicBench", 7032622, 13882323);
					 * 71 registerStructure(4,
					 * lotr.common.world.structure2.LOTRWorldGenHobbitWindmill.class,
					 * "HobbitWindmill", 9324081, 15975807); 72 registerStructure(5,
					 * lotr.common.world.structure2.LOTRWorldGenHobbitFarm.class, "HobbitFarm",
					 * 9324081, 15975807); 73 registerStructure(6,
					 * lotr.common.world.structure2.LOTRWorldGenHayBales.class, "HayBale", 14863437,
					 * 11499334); 74 registerStructure(7,
					 * lotr.common.world.structure2.LOTRWorldGenHobbitHouse.class, "HobbitHouse",
					 * 9324081, 15975807);
					 * 
					 * 76 registerStructure(20,
					 * lotr.common.world.structure2.LOTRWorldGenBlueMountainsHouse.class,
					 * "BlueMountainsHouse", 10397380, 7633815); 77 registerStructure(21,
					 * LOTRWorldGenBlueMountainsStronghold.class, "BlueMountainsStronghold",
					 * 10397380, 7633815); 78 registerStructure(22,
					 * lotr.common.world.structure2.LOTRWorldGenBlueMountainsSmithy.class,
					 * "BlueMountainsSmithy", 10397380, 7633815);
					 * 
					 * 80 registerStructure(30, LOTRWorldGenHighElvenTurret.class,
					 * "HighElvenTurret", 13419962, 11380637); 81 registerStructure(31,
					 * LOTRWorldGenRuinedHighElvenTurret.class, "RuinedHighElvenTurret", 13419962,
					 * 11380637); 82 registerStructure(32, LOTRWorldGenHighElvenHall.class,
					 * "HighElvenHall", 13419962, 11380637); 83 registerStructure(33,
					 * LOTRWorldGenUnderwaterElvenRuin.class, "UnderwaterElvenRuin", 13419962,
					 * 11380637); 84 registerStructure(34,
					 * lotr.common.world.structure2.LOTRWorldGenHighElvenForge.class,
					 * "HighElvenForge", 13419962, 11380637); 85 registerStructure(35,
					 * lotr.common.world.structure2.LOTRWorldGenRuinedEregionForge.class,
					 * "RuinedEregionForge", 13419962, 11380637); 86 registerStructure(36,
					 * lotr.common.world.structure2.LOTRWorldGenHighElvenTower.class,
					 * "HighElvenTower", 13419962, 11380637); 87 registerStructure(37,
					 * lotr.common.world.structure2.LOTRWorldGenTowerHillsTower.class,
					 * "TowerHillsTower", 16250346, 14211019); 88 registerStructure(38,
					 * lotr.common.world.structure2.LOTRWorldGenHighElfHouse.class, "HighElfHouse",
					 * 13419962, 11380637); 89 registerStructure(39,
					 * lotr.common.world.structure2.LOTRWorldGenRivendellHouse.class,
					 * "RivendellHouse", 13419962, 11380637); 90 registerStructure(40,
					 * LOTRWorldGenRivendellHall.class, "RivendellHall", 13419962, 11380637); 91
					 * registerStructure(41,
					 * lotr.common.world.structure2.LOTRWorldGenRivendellForge.class,
					 * "RivendellForge", 13419962, 11380637);
					 * 
					 * 93 registerStructure(50, LOTRWorldGenRuinedDunedainTower.class,
					 * "RuinedDunedainTower", 8947848, 6052956); 94 registerStructure(51,
					 * lotr.common.world.structure2.LOTRWorldGenRuinedHouse.class, "RuinedHouse",
					 * 8355197, 6838845); 95 registerStructure(52,
					 * lotr.common.world.structure2.LOTRWorldGenRangerTent.class, "RangerTent",
					 * 3755037, 4142111); 96 registerStructure(53,
					 * lotr.common.world.structure2.LOTRWorldGenNumenorRuin.class, "NumenorRuin",
					 * 8947848, 6052956); 97 registerStructure(54,
					 * lotr.common.world.structure2.LOTRWorldGenBDBarrow.class, "BDBarrow", 6586202,
					 * 6505786); 98 registerStructure(55,
					 * lotr.common.world.structure2.LOTRWorldGenRangerWatchtower.class,
					 * "RangerWatchtower", 5982252, 13411436); 99 registerStructure(56,
					 * lotr.common.world.structure2.LOTRWorldGenBurntHouse.class, "BurntHouse",
					 * 1117449, 3288357); 100 registerStructure(57,
					 * lotr.common.world.structure2.LOTRWorldGenRottenHouse.class, "RottenHouse",
					 * 3026204, 5854007); 101 registerStructure(58,
					 * lotr.common.world.structure2.LOTRWorldGenRangerHouse.class, "RangerHouse",
					 * 5982252, 13411436); 102 registerStructure(59,
					 * lotr.common.world.structure2.LOTRWorldGenRangerLodge.class, "RangerLodge",
					 * 5982252, 13411436); 103 registerStructure(60,
					 * lotr.common.world.structure2.LOTRWorldGenRangerStables.class,
					 * "RangerStables", 5982252, 13411436); 104 registerStructure(61,
					 * lotr.common.world.structure2.LOTRWorldGenRangerSmithy.class, "RangerSmithy",
					 * 5982252, 13411436); 105 registerStructure(62,
					 * lotr.common.world.structure2.LOTRWorldGenRangerWell.class, "RangerWell",
					 * 5982252, 13411436); 106 registerStructure(63,
					 * lotr.common.world.structure2.LOTRWorldGenRangerVillageLight.class,
					 * "RangerVillageLight", 5982252, 13411436); 107 registerVillage(64, new
					 * LOTRVillageGenDunedain(LOTRBiome.angle, 1.0F), "DunedainVillage", 5982252,
					 * 13411436, new IVillageProperties<LOTRVillageGenDunedain.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenDunedain.Instance instance) { 112
					 * instance.villageType = LOTRVillageGenDunedain.VillageType.VILLAGE; } }); 115
					 * registerStructure(65,
					 * lotr.common.world.structure2.LOTRWorldGenRangerCamp.class, "RangerCamp",
					 * 3755037, 4142111);
					 * 
					 * 117 registerStructure(80, LOTRWorldGenOrcDungeon.class, "OrcDungeon",
					 * 8947848, 6052956); 118 registerStructure(81,
					 * lotr.common.world.structure2.LOTRWorldGenGundabadTent.class, "GundabadTent",
					 * 2301210, 131586); 119 registerStructure(82,
					 * lotr.common.world.structure2.LOTRWorldGenGundabadForgeTent.class,
					 * "GundabadForgeTent", 2301210, 131586); 120 registerStructure(83,
					 * lotr.common.world.structure2.LOTRWorldGenGundabadCamp.class, "GundabadCamp",
					 * 2301210, 131586);
					 * 
					 * 122 registerStructure(100, LOTRWorldGenAngmarTower.class, "AngmarTower",
					 * 3815994, 1644825); 123 registerStructure(101, LOTRWorldGenAngmarShrine.class,
					 * "AngmarShrine", 3815994, 1644825); 124 registerStructure(102,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarWargPit.class,
					 * "AngmarWargPit", 3815994, 1644825); 125 registerStructure(103,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarTent.class, "AngmarTent",
					 * 2301210, 131586); 126 registerStructure(104,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarForgeTent.class,
					 * "AngmarForgeTent", 3815994, 1644825); 127 registerStructure(105,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarCamp.class, "AngmarCamp",
					 * 2301210, 131586);
					 * 
					 * 129 registerStructure(110,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarHillmanHouse.class,
					 * "AngmarHillmanHouse", 6705465, 3813154); 130 registerStructure(111,
					 * lotr.common.world.structure2.LOTRWorldGenAngmarHillmanChieftainHouse.class,
					 * "AngmarHillmanChieftainHouse", 6705465, 3813154); 131 registerStructure(112,
					 * lotr.common.world.structure2.LOTRWorldGenRhudaurCastle.class,
					 * "RhudaurCastle", 3815994, 1644825);
					 * 
					 * 133 registerStructure(120, LOTRWorldGenWoodElfPlatform.class,
					 * "WoodElfLookoutPlatform", 2498840, 4932405); 134 registerStructure(121,
					 * lotr.common.world.structure2.LOTRWorldGenWoodElfHouse.class, "WoodElfHouse",
					 * 2498840, 1004574); 135 registerStructure(122, LOTRWorldGenWoodElfTower.class,
					 * "WoodElfTower", 12692892, 9733494); 136 registerStructure(123,
					 * LOTRWorldGenRuinedWoodElfTower.class, "RuinedWoodElfTower", 12692892,
					 * 9733494); 137 registerStructure(124,
					 * lotr.common.world.structure2.LOTRWorldGenWoodElvenForge.class,
					 * "WoodElvenForge", 12692892, 9733494);
					 * 
					 * 139 registerStructure(130,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurAltar.class,
					 * "DolGuldurAltar", 4408654, 2040101); 140 registerStructure(131,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurTower.class,
					 * "DolGuldurTower", 4408654, 2040101); 141 registerStructure(132,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurSpiderPit.class,
					 * "DolGuldurSpiderPit", 4408654, 2040101); 142 registerStructure(133,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurTent.class,
					 * "DolGuldurTent", 2301210, 131586); 143 registerStructure(134,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurForgeTent.class,
					 * "DolGuldurForgeTent", 4408654, 2040101); 144 registerStructure(135,
					 * lotr.common.world.structure2.LOTRWorldGenDolGuldurCamp.class,
					 * "DolGuldurCamp", 2301210, 131586);
					 * 
					 * 146 registerStructure(140,
					 * lotr.common.world.structure2.LOTRWorldGenDaleWatchtower.class,
					 * "DaleWatchtower", 13278568, 6836795); 147 registerStructure(141,
					 * lotr.common.world.structure2.LOTRWorldGenDaleFortress.class, "DaleFortress",
					 * 13278568, 6836795); 148 registerStructure(142,
					 * lotr.common.world.structure2.LOTRWorldGenDaleHouse.class, "DaleHouse",
					 * 13278568, 6836795); 149 registerStructure(143,
					 * lotr.common.world.structure2.LOTRWorldGenDaleSmithy.class, "DaleSmithy",
					 * 13278568, 6836795); 150 registerStructure(144,
					 * lotr.common.world.structure2.LOTRWorldGenDaleVillageTower.class,
					 * "DaleVillageTower", 13278568, 6836795); 151 registerStructure(145,
					 * lotr.common.world.structure2.LOTRWorldGenDaleBakery.class, "DaleBakery",
					 * 13278568, 6836795);
					 * 
					 * 153 registerStructure(150,
					 * lotr.common.world.structure2.LOTRWorldGenDwarvenMineEntrance.class,
					 * "DwarvenMineEntrance", 4935761, 2961971); 154 registerStructure(151,
					 * lotr.common.world.structure2.LOTRWorldGenDwarvenTower.class, "DwarvenTower",
					 * 4935761, 2961971); 155 registerStructure(152,
					 * lotr.common.world.structure2.LOTRWorldGenDwarfHouse.class, "DwarfHouse",
					 * 4935761, 2961971); 156 registerStructure(153,
					 * lotr.common.world.structure2.LOTRWorldGenDwarvenMineEntranceRuined.class,
					 * "DwarvenMineEntranceRuined", 4935761, 2961971); 157 registerStructure(154,
					 * lotr.common.world.structure2.LOTRWorldGenDwarfSmithy.class, "DwarfSmithy",
					 * 4935761, 2961971); 158 registerStructure(155,
					 * lotr.common.world.structure2.LOTRWorldGenRuinedDwarvenTower.class,
					 * "DwarvenTowerRuined", 4935761, 2961971);
					 * 
					 * 160 registerStructure(200,
					 * lotr.common.world.structure2.LOTRWorldGenElfHouse.class, "ElfHouse",
					 * 15325615, 2315809); 161 registerStructure(201,
					 * LOTRWorldGenElfLordHouse.class, "ElfLordHouse", 15325615, 2315809); 162
					 * registerStructure(202,
					 * lotr.common.world.structure2.LOTRWorldGenGaladhrimForge.class,
					 * "GaladhrimForge", 14407118, 10854552);
					 * 
					 * 164 registerStructure(300,
					 * lotr.common.world.structure2.LOTRWorldGenMeadHall.class, "RohanMeadHall",
					 * 5982252, 13411436); 165 registerStructure(301,
					 * lotr.common.world.structure2.LOTRWorldGenRohanWatchtower.class,
					 * "RohanWatchtower", 5982252, 13411436); 166 registerStructure(302,
					 * LOTRWorldGenRohanBarrow.class, "RohanBarrow", 9016133, 16775901); 167
					 * registerStructure(303,
					 * lotr.common.world.structure2.LOTRWorldGenRohanFortress.class,
					 * "RohanFortress", 5982252, 13411436); 168 registerStructure(304,
					 * lotr.common.world.structure2.LOTRWorldGenRohanHouse.class, "RohanHouse",
					 * 5982252, 13411436); 169 registerStructure(305,
					 * lotr.common.world.structure2.LOTRWorldGenRohanSmithy.class, "RohanSmithy",
					 * 5982252, 13411436); 170 registerStructure(306,
					 * lotr.common.world.structure2.LOTRWorldGenRohanVillageFarm.class,
					 * "RohanVillageFarm", 7648578, 8546111); 171 registerStructure(307,
					 * lotr.common.world.structure2.LOTRWorldGenRohanStables.class, "RohanStables",
					 * 5982252, 13411436); 172 registerStructure(308,
					 * lotr.common.world.structure2.LOTRWorldGenRohanBarn.class, "RohanBarn",
					 * 5982252, 13411436); 173 registerStructure(309,
					 * lotr.common.world.structure2.LOTRWorldGenRohanWell.class, "RohanWell",
					 * 5982252, 13411436); 174 registerStructure(310,
					 * lotr.common.world.structure2.LOTRWorldGenRohanVillageGarden.class,
					 * "RohanVillageGarden", 7648578, 8546111); 175 registerStructure(311,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Blacksmith.class,
					 * "RohanMarketBlacksmith", 2960684, 13411436); 176 registerStructure(312,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Farmer.class,
					 * "RohanMarketFarmer", 15066597, 13411436); 177 registerStructure(313,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Lumber.class,
					 * "RohanMarketLumber", 5981994, 13411436); 178 registerStructure(314,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Builder.class,
					 * "RohanMarketBuilder", 7693401, 13411436); 179 registerStructure(315,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Brewer.class,
					 * "RohanMarketBrewer", 13874218, 13411436); 180 registerStructure(316,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Butcher.class,
					 * "RohanMarketButcher", 16358066, 13411436); 181 registerStructure(317,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Fish.class,
					 * "RohanMarketFish", 9882879, 13411436); 182 registerStructure(318,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Baker.class,
					 * "RohanMarketBaker", 14725995, 13411436); 183 registerStructure(319,
					 * lotr.common.world.structure2.LOTRWorldGenRohanMarketStall.Orcharder.class,
					 * "RohanMarketOrcharder", 9161006, 13411436); 184 registerStructure(320,
					 * lotr.common.world.structure2.LOTRWorldGenRohanVillagePasture.class,
					 * "RohanVillagePasture", 7648578, 8546111); 185 registerStructure(321,
					 * lotr.common.world.structure2.LOTRWorldGenRohanVillageSign.class,
					 * "RohanVillageSign", 5982252, 13411436); 186 registerStructure(322,
					 * lotr.common.world.structure2.LOTRWorldGenRohanGatehouse.class,
					 * "RohanGatehouse", 5982252, 13411436); 187 registerVillage(323, new
					 * LOTRVillageGenRohan(LOTRBiome.rohan, 1.0F), "RohanVillage", 5982252,
					 * 13411436, new IVillageProperties<LOTRVillageGenRohan.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenRohan.Instance instance) { 192
					 * instance.villageType = LOTRVillageGenRohan.VillageType.VILLAGE; } }); 195
					 * registerVillage(324, new LOTRVillageGenRohan(LOTRBiome.rohan, 1.0F),
					 * "RohanFortVillage", 5982252, 13411436, new
					 * IVillageProperties<LOTRVillageGenRohan.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenRohan.Instance instance) { 200
					 * instance.villageType = LOTRVillageGenRohan.VillageType.FORT; } });
					 * 
					 * 204 registerStructure(350,
					 * lotr.common.world.structure2.LOTRWorldGenUrukTent.class, "UrukTent", 2301210,
					 * 131586); 205 registerStructure(351, LOTRWorldGenRuinedRohanWatchtower.class,
					 * "RuinedRohanWatchtower", 1117449, 3288357); 206 registerStructure(352,
					 * lotr.common.world.structure2.LOTRWorldGenUrukForgeTent.class,
					 * "UrukForgeTent", 3682596, 2038547); 207 registerStructure(353,
					 * lotr.common.world.structure2.LOTRWorldGenUrukWargPit.class, "UrukWargPit",
					 * 3682596, 2038547); 208 registerStructure(354,
					 * lotr.common.world.structure2.LOTRWorldGenUrukCamp.class, "UrukCamp", 2301210,
					 * 131586);
					 * 
					 * 210 registerStructure(380,
					 * lotr.common.world.structure2.LOTRWorldGenDunlendingHouse.class,
					 * "DunlendingHouse", 6705465, 3813154); 211 registerStructure(381,
					 * lotr.common.world.structure2.LOTRWorldGenDunlendingTavern.class,
					 * "DunlendingTavern", 6705465, 3813154); 212 registerStructure(382,
					 * LOTRWorldGenDunlendingCampfire.class, "DunlendingCampfire", 9539472,
					 * 6837299); 213 registerStructure(383,
					 * lotr.common.world.structure2.LOTRWorldGenDunlandHillFort.class,
					 * "DunlandHillFort", 6705465, 3813154);
					 * 
					 * 215 registerStructure(400,
					 * lotr.common.world.structure2.LOTRWorldGenBeaconTower.class, "BeaconTower",
					 * 14869218, 11513775); 216 registerStructure(401,
					 * lotr.common.world.structure2.LOTRWorldGenGondorWatchfort.class,
					 * "GondorWatchfort", 14869218, 2367263); 217 registerStructure(402,
					 * lotr.common.world.structure2.LOTRWorldGenGondorSmithy.class, "GondorSmithy",
					 * 14869218, 2367263); 218 registerStructure(403,
					 * lotr.common.world.structure2.LOTRWorldGenGondorTurret.class, "GondorTurret",
					 * 14869218, 11513775); 219 registerStructure(404,
					 * lotr.common.world.structure2.LOTRWorldGenIthilienHideout.class,
					 * "IthilienHideout", 8882055, 7365464); 220 registerStructure(405,
					 * lotr.common.world.structure2.LOTRWorldGenGondorHouse.class, "GondorHouse",
					 * 14869218, 9861961); 221 registerStructure(406,
					 * lotr.common.world.structure2.LOTRWorldGenGondorCottage.class,
					 * "GondorCottage", 14869218, 9861961); 222 registerStructure(407,
					 * lotr.common.world.structure2.LOTRWorldGenGondorStoneHouse.class,
					 * "GondorStoneHouse", 14869218, 2367263); 223 registerStructure(408,
					 * lotr.common.world.structure2.LOTRWorldGenGondorWatchtower.class,
					 * "GondorWatchtower", 14869218, 11513775); 224 registerStructure(409,
					 * lotr.common.world.structure2.LOTRWorldGenGondorStables.class,
					 * "GondorStables", 14869218, 9861961); 225 registerStructure(410,
					 * lotr.common.world.structure2.LOTRWorldGenGondorBarn.class, "GondorBarn",
					 * 14869218, 9861961); 226 registerStructure(411,
					 * lotr.common.world.structure2.LOTRWorldGenGondorFortress.class,
					 * "GondorFortress", 14869218, 2367263); 227 registerStructure(412,
					 * lotr.common.world.structure2.LOTRWorldGenGondorTavern.class, "GondorTavern",
					 * 14869218, 9861961); 228 registerStructure(413,
					 * lotr.common.world.structure2.LOTRWorldGenGondorWell.class, "GondorWell",
					 * 14869218, 11513775); 229 registerStructure(414,
					 * lotr.common.world.structure2.LOTRWorldGenGondorVillageFarm.Crops.class,
					 * "GondorFarmCrops", 7047232, 15066597); 230 registerStructure(415,
					 * lotr.common.world.structure2.LOTRWorldGenGondorVillageFarm.Animals.class,
					 * "GondorFarmAnimals", 7047232, 15066597); 231 registerStructure(416,
					 * lotr.common.world.structure2.LOTRWorldGenGondorVillageFarm.Tree.class,
					 * "GondorFarmTree", 7047232, 15066597); 232 registerStructure(417,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Greengrocer.class,
					 * "GondorMarketGreengrocer", 8567851, 9861961); 233 registerStructure(418,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Lumber.class,
					 * "GondorMarketLumber", 5981994, 9861961); 234 registerStructure(419,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Mason.class,
					 * "GondorMarketMason", 10526621, 9861961); 235 registerStructure(420,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Brewer.class,
					 * "GondorMarketBrewer", 13874218, 9861961); 236 registerStructure(421,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Flowers.class,
					 * "GondorMarketFlowers", 16243515, 9861961); 237 registerStructure(422,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Butcher.class,
					 * "GondorMarketButcher", 14521508, 9861961); 238 registerStructure(423,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Fish.class,
					 * "GondorMarketFish", 6862591, 9861961); 239 registerStructure(424,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Farmer.class,
					 * "GondorMarketFarmer", 14401433, 9861961); 240 registerStructure(425,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Blacksmith.class,
					 * "GondorMarketBlacksmith", 2960684, 9861961); 241 registerStructure(426,
					 * lotr.common.world.structure2.LOTRWorldGenGondorMarketStall.Baker.class,
					 * "GondorMarketBaker", 13543009, 9861961); 242 registerStructure(427,
					 * lotr.common.world.structure2.LOTRWorldGenGondorVillageSign.class,
					 * "GondorVillageSign", 5982252, 13411436); 243 registerStructure(428,
					 * lotr.common.world.structure2.LOTRWorldGenGondorBath.class, "GondorBath",
					 * 14869218, 2367263); 244 registerStructure(429,
					 * lotr.common.world.structure2.LOTRWorldGenGondorGatehouse.class,
					 * "GondorGatehouse", 14869218, 2367263); 245 registerStructure(430,
					 * lotr.common.world.structure2.LOTRWorldGenGondorLampPost.class,
					 * "GondorLampPost", 14869218, 11513775); 246 registerStructure(431,
					 * lotr.common.world.structure2.LOTRWorldGenGondorTownGarden.class,
					 * "GondorTownGarden", 7047232, 15066597); 247 registerStructure(432,
					 * lotr.common.world.structure2.LOTRWorldGenGondorTownTrees.class,
					 * "GondorTownTrees", 7047232, 15066597); 248 registerStructure(433,
					 * lotr.common.world.structure2.LOTRWorldGenGondorTownBench.class,
					 * "GondorTownBench", 14869218, 11513775); 249 registerVillage(434, new
					 * LOTRVillageGenGondor(LOTRBiome.gondor,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.GONDOR, 1.0F), "GondorVillage",
					 * 14869218, 2367263, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 254
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 257
					 * registerVillage(435, new LOTRVillageGenGondor(LOTRBiome.gondor,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.GONDOR, 1.0F), "GondorTown",
					 * 14869218, 2367263, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 262
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 265
					 * registerVillage(436, new LOTRVillageGenGondor(LOTRBiome.gondor,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.GONDOR, 1.0F), "GondorFortVillage",
					 * 14869218, 2367263, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 270
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 274 registerStructure(450,
					 * lotr.common.world.structure2.LOTRWorldGenRuinedBeaconTower.class,
					 * "RuinedBeaconTower", 14869218, 11513775); 275 registerStructure(451,
					 * LOTRWorldGenRuinedGondorTower.class, "RuinedGondorTower", 14869218,
					 * 11513775); 276 registerStructure(452,
					 * lotr.common.world.structure2.LOTRWorldGenGondorObelisk.class,
					 * "GondorObelisk", 14869218, 11513775); 277 registerStructure(453,
					 * LOTRWorldGenGondorRuin.class, "GondorRuin", 14869218, 11513775);
					 * 
					 * 279 registerStructure(500,
					 * lotr.common.world.structure2.LOTRWorldGenDolAmrothStables.class,
					 * "DolAmrothStables", 15002613, 2709918); 280 registerStructure(501,
					 * lotr.common.world.structure2.LOTRWorldGenDolAmrothWatchtower.class,
					 * "DolAmrothWatchtower", 14869218, 11513775); 281 registerStructure(502,
					 * lotr.common.world.structure2.LOTRWorldGenDolAmrothWatchfort.class,
					 * "DolAmrothWatchfort", 15002613, 2709918); 282 registerVillage(503, new
					 * LOTRVillageGenGondor(LOTRBiome.dorEnErnil,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.DOL_AMROTH, 1.0F),
					 * "DolAmrothVillage", 15002613, 2709918, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 287
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 290
					 * registerVillage(504, new LOTRVillageGenGondor(LOTRBiome.dorEnErnil,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.DOL_AMROTH, 1.0F), "DolAmrothTown",
					 * 15002613, 2709918, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 295
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 298
					 * registerVillage(505, new LOTRVillageGenGondor(LOTRBiome.dorEnErnil,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.DOL_AMROTH, 1.0F),
					 * "DolAmrothFortVillage", 15002613, 2709918, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 303
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 307 registerStructure(510,
					 * lotr.common.world.structure2.LOTRWorldGenLossarnachFortress.class,
					 * "LossarnachFortress", 14869218, 15138816); 308 registerStructure(511,
					 * lotr.common.world.structure2.LOTRWorldGenLossarnachWatchtower.class,
					 * "LossarnachWatchtower", 14869218, 11513775); 309 registerStructure(512,
					 * lotr.common.world.structure2.LOTRWorldGenLossarnachWatchfort.class,
					 * "LossarnachWatchfort", 14869218, 15138816); 310 registerVillage(513, new
					 * LOTRVillageGenGondor(LOTRBiome.lossarnach,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LOSSARNACH, 1.0F),
					 * "LossarnachVillage", 14869218, 15138816, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 315
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 318
					 * registerVillage(514, new LOTRVillageGenGondor(LOTRBiome.lossarnach,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LOSSARNACH, 1.0F),
					 * "LossarnachTown", 14869218, 15138816, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 323
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 326
					 * registerVillage(515, new LOTRVillageGenGondor(LOTRBiome.lossarnach,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LOSSARNACH, 1.0F),
					 * "LossarnachFortVillage", 14869218, 15138816, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 331
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 335 registerStructure(520,
					 * lotr.common.world.structure2.LOTRWorldGenLebenninFortress.class,
					 * "LebenninFortress", 14869218, 621750); 336 registerStructure(521,
					 * lotr.common.world.structure2.LOTRWorldGenLebenninWatchtower.class,
					 * "LebenninWatchtower", 14869218, 11513775); 337 registerStructure(522,
					 * lotr.common.world.structure2.LOTRWorldGenLebenninWatchfort.class,
					 * "LebenninWatchfort", 14869218, 621750); 338 registerVillage(523, new
					 * LOTRVillageGenGondor(LOTRBiome.lebennin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LEBENNIN, 1.0F), "LebenninVillage",
					 * 14869218, 621750, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 343
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 346
					 * registerVillage(524, new LOTRVillageGenGondor(LOTRBiome.lebennin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LEBENNIN, 1.0F), "LebenninTown",
					 * 14869218, 621750, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 351
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 354
					 * registerVillage(525, new LOTRVillageGenGondor(LOTRBiome.lebennin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LEBENNIN, 1.0F),
					 * "LebenninFortVillage", 14869218, 621750, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 359
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 363 registerStructure(530,
					 * lotr.common.world.structure2.LOTRWorldGenPelargirFortress.class,
					 * "PelargirFortress", 14869218, 2917253); 364 registerStructure(531,
					 * lotr.common.world.structure2.LOTRWorldGenPelargirWatchtower.class,
					 * "PelargirWatchtower", 14869218, 11513775); 365 registerStructure(532,
					 * lotr.common.world.structure2.LOTRWorldGenPelargirWatchfort.class,
					 * "PelargirWatchfort", 14869218, 2917253); 366 registerVillage(533, new
					 * LOTRVillageGenGondor(LOTRBiome.pelargir,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PELARGIR, 1.0F), "PelargirVillage",
					 * 14869218, 2917253, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 371
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 374
					 * registerVillage(534, new LOTRVillageGenGondor(LOTRBiome.pelargir,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PELARGIR, 1.0F), "PelargirTown",
					 * 14869218, 2917253, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 379
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 382
					 * registerVillage(535, new LOTRVillageGenGondor(LOTRBiome.pelargir,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PELARGIR, 1.0F),
					 * "PelargirFortVillage", 14869218, 2917253, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 387
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 391 registerStructure(540,
					 * lotr.common.world.structure2.LOTRWorldGenPinnathGelinFortress.class,
					 * "PinnathGelinFortress", 14869218, 1401651); 392 registerStructure(541,
					 * lotr.common.world.structure2.LOTRWorldGenPinnathGelinWatchtower.class,
					 * "PinnathGelinWatchtower", 14869218, 11513775); 393 registerStructure(542,
					 * lotr.common.world.structure2.LOTRWorldGenPinnathGelinWatchfort.class,
					 * "PinnathGelinWatchfort", 14869218, 1401651); 394 registerVillage(543, new
					 * LOTRVillageGenGondor(LOTRBiome.pinnathGelin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PINNATH_GELIN, 1.0F),
					 * "PinnathGelinVillage", 14869218, 1401651, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 399
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 402
					 * registerVillage(544, new LOTRVillageGenGondor(LOTRBiome.pinnathGelin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PINNATH_GELIN, 1.0F),
					 * "PinnathGelinTown", 14869218, 1401651, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 407
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 410
					 * registerVillage(545, new LOTRVillageGenGondor(LOTRBiome.pinnathGelin,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.PINNATH_GELIN, 1.0F),
					 * "PinnathGelinFortVillage", 14869218, 1401651, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 415
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 419 registerStructure(550,
					 * lotr.common.world.structure2.LOTRWorldGenBlackrootFortress.class,
					 * "BlackrootFortress", 14869218, 2367263); 420 registerStructure(551,
					 * lotr.common.world.structure2.LOTRWorldGenBlackrootWatchtower.class,
					 * "BlackrootWatchtower", 14869218, 11513775); 421 registerStructure(552,
					 * lotr.common.world.structure2.LOTRWorldGenBlackrootWatchfort.class,
					 * "BlackrootWatchfort", 14869218, 2367263); 422 registerVillage(553, new
					 * LOTRVillageGenGondor(LOTRBiome.blackrootVale,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.BLACKROOT_VALE, 1.0F),
					 * "BlackrootVillage", 14869218, 2367263, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 427
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 430
					 * registerVillage(554, new LOTRVillageGenGondor(LOTRBiome.blackrootVale,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.BLACKROOT_VALE, 1.0F),
					 * "BlackrootTown", 14869218, 2367263, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 435
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 438
					 * registerVillage(555, new LOTRVillageGenGondor(LOTRBiome.blackrootVale,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.BLACKROOT_VALE, 1.0F),
					 * "BlackrootFortVillage", 14869218, 2367263, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 443
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 447 registerStructure(560,
					 * lotr.common.world.structure2.LOTRWorldGenLamedonFortress.class,
					 * "LamedonFortress", 14869218, 1784649); 448 registerStructure(561,
					 * lotr.common.world.structure2.LOTRWorldGenLamedonWatchtower.class,
					 * "LamedonWatchtower", 14869218, 11513775); 449 registerStructure(562,
					 * lotr.common.world.structure2.LOTRWorldGenLamedonWatchfort.class,
					 * "LamedonWatchfort", 14869218, 1784649); 450 registerVillage(563, new
					 * LOTRVillageGenGondor(LOTRBiome.lamedon,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LAMEDON, 1.0F), "LamedonVillage",
					 * 14869218, 1784649, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 455
					 * instance.villageType = LOTRVillageGenGondor.VillageType.VILLAGE; } }); 458
					 * registerVillage(564, new LOTRVillageGenGondor(LOTRBiome.lamedon,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LAMEDON, 1.0F), "LamedonTown",
					 * 14869218, 1784649, new IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 463
					 * instance.villageType = LOTRVillageGenGondor.VillageType.TOWN; } }); 466
					 * registerVillage(565, new LOTRVillageGenGondor(LOTRBiome.lamedon,
					 * LOTRWorldGenGondorStructure.GondorFiefdom.LAMEDON, 1.0F),
					 * "LamedonFortVillage", 14869218, 1784649, new
					 * IVillageProperties<LOTRVillageGenGondor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGondor.Instance instance) { 471
					 * instance.villageType = LOTRVillageGenGondor.VillageType.FORT; } });
					 * 
					 * 475 registerStructure(600, LOTRWorldGenMordorTower.class, "MordorTower",
					 * 2631720, 328965); 476 registerStructure(601,
					 * lotr.common.world.structure2.LOTRWorldGenMordorTent.class, "MordorTent",
					 * 2301210, 131586); 477 registerStructure(602,
					 * lotr.common.world.structure2.LOTRWorldGenMordorForgeTent.class,
					 * "MordorForgeTent", 2631720, 328965); 478 registerStructure(603,
					 * lotr.common.world.structure2.LOTRWorldGenMordorWargPit.class,
					 * "MordorWargPit", 2631720, 328965); 479 registerStructure(604,
					 * lotr.common.world.structure2.LOTRWorldGenMordorCamp.class, "MordorCamp",
					 * 2301210, 131586); 480 registerStructure(605,
					 * lotr.common.world.structure2.LOTRWorldGenBlackUrukFort.class,
					 * "BlackUrukFort", 2631720, 328965);
					 * 
					 * 482 registerStructure(650, LOTRWorldGenNurnWheatFarm.class, "NurnWheatFarm",
					 * 4469796, 328965); 483 registerStructure(651,
					 * LOTRWorldGenOrcSlaverTower.class, "OrcSlaverTower", 1117449, 3288357);
					 * 
					 * 485 registerStructure(670,
					 * lotr.common.world.structure2.LOTRWorldGenMordorSpiderPit.class,
					 * "MordorSpiderPit", 1511181, 12917534);
					 * 
					 * 487 registerStructure(700,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionGarden.class,
					 * "DorwinionGarden", 16572875, 13418417); 488 registerStructure(701,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionTent.class,
					 * "DorwinionTent", 6706573, 15058766); 489 registerStructure(702,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionCaptainTent.class,
					 * "DorwinionCaptainTent", 6706573, 15058766); 490 registerStructure(703,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionHouse.class,
					 * "DorwinionHouse", 7167128, 15390149); 491 registerStructure(704,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionBrewery.class,
					 * "DorwinionBrewery", 7167128, 15390149); 492 registerStructure(705,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionElfHouse.class,
					 * "DorwinionElfHouse", 7167128, 15390149); 493 registerStructure(706,
					 * lotr.common.world.structure2.LOTRWorldGenDorwinionBath.class,
					 * "DorwinionBath", 7167128, 15390149);
					 * 
					 * 495 registerStructure(750,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingHouse.class,
					 * "EasterlingHouse", 12693373, 7689786); 496 registerStructure(751,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingStables.class,
					 * "EasterlingStables", 12693373, 7689786); 497 registerStructure(752,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingTownHouse.class,
					 * "EasterlingTownHouse", 6304287, 12693373); 498 registerStructure(753,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingLargeTownHouse.class,
					 * "EasterlingLargeTownHouse", 6304287, 12693373); 499 registerStructure(754,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingFortress.class,
					 * "EasterlingFortress", 6304287, 12693373); 500 registerStructure(755,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingTower.class,
					 * "EasterlingTower", 6304287, 12693373); 501 registerStructure(756,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingSmithy.class,
					 * "EasterlingSmithy", 6304287, 12693373); 502 registerStructure(757,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Blacksmith.
					 * class, "EasterlingMarketBlacksmith", 2960684, 12693373); 503
					 * registerStructure(758,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Lumber.class,
					 * "EasterlingMarketLumber", 5981994, 12693373); 504 registerStructure(759,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Mason.class,
					 * "EasterlingMarketMason", 7039594, 12693373); 505 registerStructure(760,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Butcher.class,
					 * "EasterlingMarketButcher", 12544103, 12693373); 506 registerStructure(761,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Brewer.class,
					 * "EasterlingMarketBrewer", 11891243, 12693373); 507 registerStructure(762,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Fish.class,
					 * "EasterlingMarketFish", 4882395, 12693373); 508 registerStructure(763,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Baker.class,
					 * "EasterlingMarketBaker", 14725995, 12693373); 509 registerStructure(764,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Hunter.class,
					 * "EasterlingMarketHunter", 4471854, 12693373); 510 registerStructure(765,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Farmer.class,
					 * "EasterlingMarketFarmer", 8893759, 12693373); 511 registerStructure(766,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingMarketStall.Gold.class,
					 * "EasterlingMarketGold", 16237060, 12693373); 512 registerStructure(767,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingTavern.class,
					 * "EasterlingTavern", 12693373, 7689786); 513 registerStructure(768,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingTavernTown.class,
					 * "EasterlingTavernTown", 6304287, 12693373); 514 registerStructure(769,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingStatue.class,
					 * "EasterlingStatue", 12693373, 7689786); 515 registerStructure(770,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingGarden.class,
					 * "EasterlingGarden", 4030994, 12693373); 516 registerStructure(771,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingVillageSign.class,
					 * "EasterlingVillageSign", 12693373, 7689786); 517 registerStructure(772,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingWell.class,
					 * "EasterlingWell", 12693373, 7689786); 518 registerStructure(773,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingVillageFarm.Crops.class,
					 * "EasterlingFarmCrops", 4030994, 12693373); 519 registerStructure(774,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingVillageFarm.Animals.class,
					 * "EasterlingFarmAnimals", 4030994, 12693373); 520 registerStructure(775,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingVillageFarm.Tree.class,
					 * "EasterlingFarmTree", 4030994, 12693373); 521 registerStructure(776,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingGatehouse.class,
					 * "EasterlingGatehouse", 6304287, 12693373); 522 registerStructure(777,
					 * lotr.common.world.structure2.LOTRWorldGenEasterlingLamp.class,
					 * "EasterlingLamp", 6304287, 12693373); 523 registerVillage(778, new
					 * LOTRVillageGenRhun(LOTRBiome.rhunLand, 1.0F, true), "EasterlingVillage",
					 * 6304287, 12693373, new IVillageProperties<LOTRVillageGenRhun.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenRhun.Instance instance) { 528
					 * instance.villageType = LOTRVillageGenRhun.VillageType.VILLAGE; } }); 531
					 * registerVillage(779, new LOTRVillageGenRhun(LOTRBiome.rhunLand, 1.0F, true),
					 * "EasterlingTown", 6304287, 12693373, new
					 * IVillageProperties<LOTRVillageGenRhun.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenRhun.Instance instance) { 536
					 * instance.villageType = LOTRVillageGenRhun.VillageType.TOWN; } }); 539
					 * registerVillage(780, new LOTRVillageGenRhun(LOTRBiome.rhunLand, 1.0F, true),
					 * "EasterlingFortVillage", 6304287, 12693373, new
					 * IVillageProperties<LOTRVillageGenRhun.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenRhun.Instance instance) { 544
					 * instance.villageType = LOTRVillageGenRhun.VillageType.FORT; } });
					 * 
					 * 548 registerStructure(1000, LOTRWorldGenHaradObelisk.class, "HaradObelisk",
					 * 10854007, 15590575); 549 registerStructure(1001,
					 * lotr.common.world.structure2.LOTRWorldGenHaradPyramid.class, "HaradPyramid",
					 * 10854007, 15590575); 550 registerStructure(1002,
					 * lotr.common.world.structure2.LOTRWorldGenMumakSkeleton.class,
					 * "MumakSkeleton", 14737111, 16250349); 551 registerStructure(1003,
					 * lotr.common.world.structure2.LOTRWorldGenHaradRuinedFort.class,
					 * "HaradRuinedFort", 10854007, 15590575);
					 * 
					 * 553 registerStructure(1050,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorHouse.class,
					 * "HarnedorHouse", 4994339, 12814421); 554 registerStructure(1051,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorSmithy.class,
					 * "HarnedorSmithy", 4994339, 12814421); 555 registerStructure(1052,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorTavern.class,
					 * "HarnedorTavern", 4994339, 12814421); 556 registerStructure(1053,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorMarket.class,
					 * "HarnedorMarket", 4994339, 12814421); 557 registerStructure(1054,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorTower.class,
					 * "HarnedorTower", 4994339, 12814421); 558 registerStructure(1055,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorFort.class, "HarnedorFort",
					 * 4994339, 12814421); 559 registerStructure(1056,
					 * lotr.common.world.structure2.LOTRWorldGenNearHaradTent.class,
					 * "NearHaradTent", 13519170, 1775897); 560 registerStructure(1057,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorFarm.class, "HarnedorFarm",
					 * 10073953, 12814421); 561 registerStructure(1058,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorPasture.class,
					 * "HarnedorPasture", 10073953, 12814421); 562 registerVillage(1059, new
					 * LOTRVillageGenHarnedor(LOTRBiome.harnedor, 1.0F), "HarnedorVillage", 4994339,
					 * 12814421, new IVillageProperties<LOTRVillageGenHarnedor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenHarnedor.Instance instance) { 567
					 * instance.villageType = LOTRVillageGenHarnedor.VillageType.VILLAGE; } }); 570
					 * registerStructure(1060,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorStables.class,
					 * "HarnedorStables", 4994339, 12814421); 571 registerStructure(1061,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorVillageSign.class,
					 * "HarnedorVillageSign", 4994339, 12814421); 572 registerVillage(1062, new
					 * LOTRVillageGenHarnedor(LOTRBiome.harnedor, 1.0F), "HarnedorFortVillage",
					 * 4994339, 12814421, new IVillageProperties<LOTRVillageGenHarnedor.Instance>()
					 * {
					 * 
					 * public void apply(LOTRVillageGenHarnedor.Instance instance) { 577
					 * instance.villageType = LOTRVillageGenHarnedor.VillageType.FORTRESS; } });
					 * 
					 * 581 registerStructure(1080,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorHouseRuined.class,
					 * "HarnedorHouseRuined", 5519919, 10059372); 582 registerStructure(1081,
					 * lotr.common.world.structure2.LOTRWorldGenHarnedorTavernRuined.class,
					 * "HarnedorTavernRuined", 5519919, 10059372); 583 registerVillage(1082, (new
					 * LOTRVillageGenHarnedor(LOTRBiome.harondor, 1.0F)).setRuined(),
					 * "HarnedorVillageRuined", 5519919, 10059372, new
					 * IVillageProperties<LOTRVillageGenHarnedor.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenHarnedor.Instance instance) { 588
					 * instance.villageType = LOTRVillageGenHarnedor.VillageType.VILLAGE; } });
					 * 
					 * 592 registerStructure(1100,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronHouse.class,
					 * "SouthronHouse", 15063989, 10052655); 593 registerStructure(1101,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTavern.class,
					 * "SouthronTavern", 15063989, 10052655); 594 registerStructure(1102,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronSmithy.class,
					 * "SouthronSmithy", 15063989, 10052655); 595 registerStructure(1103,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTower.class,
					 * "SouthronTower", 15063989, 10052655); 596 registerStructure(1104,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronMansion.class,
					 * "SouthronMansion", 15063989, 10052655); 597 registerStructure(1105,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronStables.class,
					 * "SouthronStables", 15063989, 10052655); 598 registerStructure(1106,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronFarm.class, "SouthronFarm",
					 * 9547581, 10052655); 599 registerStructure(1107,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronFortress.class,
					 * "SouthronFortress", 15063989, 10052655); 600 registerStructure(1108,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronWell.class, "SouthronWell",
					 * 15063989, 10052655); 601 registerStructure(1109,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronBazaar.class,
					 * "SouthronBazaar", 15063989, 10052655); 602 registerStructure(1110,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronPasture.class,
					 * "SouthronPasture", 9547581, 10052655); 603 registerStructure(1111,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronVillageSign.class,
					 * "SouthronVillageSign", 15063989, 10052655); 604 registerVillage(1112, new
					 * LOTRVillageGenSouthron(LOTRBiome.nearHaradFertile, 1.0F), "SouthronVillage",
					 * 15063989, 10052655, new IVillageProperties<LOTRVillageGenSouthron.Instance>()
					 * {
					 * 
					 * public void apply(LOTRVillageGenSouthron.Instance instance) { 609
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.VILLAGE; } }); 612
					 * registerStructure(1113,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronStatue.class,
					 * "SouthronStatue", 15063989, 10052655); 613 registerStructure(1114,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronBarracks.class,
					 * "SouthronBarracks", 15063989, 10052655); 614 registerStructure(1115,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTraining.class,
					 * "SouthronTraining", 15063989, 10052655); 615 registerStructure(1116,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronFortGate.class,
					 * "SouthronFortGate", 15063989, 10052655); 616 registerVillage(1117, new
					 * LOTRVillageGenSouthron(LOTRBiome.nearHaradFertile, 1.0F),
					 * "SouthronFortVillage", 15063989, 10052655, new
					 * IVillageProperties<LOTRVillageGenSouthron.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenSouthron.Instance instance) { 621
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.FORT; } }); 624
					 * registerStructure(1118,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronLamp.class, "SouthronLamp",
					 * 15063989, 10052655); 625 registerStructure(1119,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTownTree.class,
					 * "SouthronTownTree", 9547581, 10052655); 626 registerStructure(1120,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTownFlowers.class,
					 * "SouthronTownFlowers", 9547581, 10052655); 627 registerVillage(1121, new
					 * LOTRVillageGenSouthron(LOTRBiome.nearHaradFertile, 1.0F), "SouthronTown",
					 * 15063989, 10052655, new IVillageProperties<LOTRVillageGenSouthron.Instance>()
					 * {
					 * 
					 * public void apply(LOTRVillageGenSouthron.Instance instance) { 632
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.TOWN; } }); 635
					 * registerStructure(1122,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTownGate.class,
					 * "SouthronTownGate", 15063989, 10052655); 636 registerStructure(1123,
					 * lotr.common.world.structure2.LOTRWorldGenSouthronTownCorner.class,
					 * "SouthronTownCorner", 15063989, 10052655);
					 * 
					 * 638 registerStructure(1140,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainMercTent.class,
					 * "MoredainMercTent", 12845056, 2949120); 639 registerStructure(1141,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainMercCamp.class,
					 * "MoredainMercCamp", 12845056, 2949120);
					 * 
					 * 641 registerStructure(1150,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarHouse.class, "UmbarHouse",
					 * 14407104, 3354926); 642 registerStructure(1151,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTavern.class, "UmbarTavern",
					 * 14407104, 3354926); 643 registerStructure(1152,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarSmithy.class, "UmbarSmithy",
					 * 14407104, 3354926); 644 registerStructure(1153,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTower.class, "UmbarTower",
					 * 14407104, 3354926); 645 registerStructure(1154,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarMansion.class, "UmbarMansion",
					 * 14407104, 3354926); 646 registerStructure(1155,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarStables.class, "UmbarStables",
					 * 14407104, 3354926); 647 registerStructure(1156,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarFarm.class, "UmbarFarm",
					 * 9547581, 3354926); 648 registerStructure(1157,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarFortress.class,
					 * "UmbarFortress", 14407104, 3354926); 649 registerStructure(1158,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarWell.class, "UmbarWell",
					 * 14407104, 3354926); 650 registerStructure(1159,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarBazaar.class, "UmbarBazaar",
					 * 14407104, 3354926); 651 registerStructure(1160,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarPasture.class, "UmbarPasture",
					 * 9547581, 3354926); 652 registerStructure(1161,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarVillageSign.class,
					 * "UmbarVillageSign", 14407104, 3354926); 653 registerVillage(1162, new
					 * LOTRVillageGenUmbar(LOTRBiome.umbar, 1.0F), "UmbarVillage", 14407104,
					 * 3354926, new IVillageProperties<LOTRVillageGenUmbar.InstanceUmbar>() {
					 * 
					 * public void apply(LOTRVillageGenUmbar.InstanceUmbar instance) { 658
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.VILLAGE; } }); 661
					 * registerStructure(1163,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarStatue.class, "UmbarStatue",
					 * 14407104, 3354926); 662 registerStructure(1164,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarBarracks.class,
					 * "UmbarBarracks", 14407104, 3354926); 663 registerStructure(1165,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTraining.class,
					 * "UmbarTraining", 14407104, 3354926); 664 registerStructure(1166,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarFortGate.class,
					 * "UmbarFortGate", 14407104, 3354926); 665 registerVillage(1167, new
					 * LOTRVillageGenUmbar(LOTRBiome.umbar, 1.0F), "UmbarFortVillage", 14407104,
					 * 3354926, new IVillageProperties<LOTRVillageGenSouthron.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenSouthron.Instance instance) { 670
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.FORT; } }); 673
					 * registerStructure(1168,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarLamp.class, "UmbarLamp",
					 * 14407104, 3354926); 674 registerStructure(1169,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTownTree.class,
					 * "UmbarTownTree", 9547581, 3354926); 675 registerStructure(1170,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTownFlowers.class,
					 * "UmbarTownFlowers", 9547581, 3354926); 676 registerVillage(1171, new
					 * LOTRVillageGenUmbar(LOTRBiome.umbar, 1.0F), "UmbarTown", 14407104, 3354926,
					 * new IVillageProperties<LOTRVillageGenSouthron.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenSouthron.Instance instance) { 681
					 * instance.villageType = LOTRVillageGenSouthron.VillageType.TOWN; } }); 684
					 * registerStructure(1172,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTownGate.class,
					 * "UmbarTownGate", 14407104, 3354926); 685 registerStructure(1173,
					 * lotr.common.world.structure2.LOTRWorldGenUmbarTownCorner.class,
					 * "UmbarTownCorner", 14407104, 3354926);
					 * 
					 * 687 registerStructure(1180,
					 * lotr.common.world.structure2.LOTRWorldGenCorsairCove.class, "CorsairCove",
					 * 8355711, 1644825); 688 registerStructure(1181,
					 * lotr.common.world.structure2.LOTRWorldGenCorsairTent.class, "CorsairTent",
					 * 5658198, 657930); 689 registerStructure(1182,
					 * lotr.common.world.structure2.LOTRWorldGenCorsairCamp.class, "CorsairCamp",
					 * 5658198, 657930);
					 * 
					 * 691 registerStructure(1200,
					 * lotr.common.world.structure2.LOTRWorldGenNomadTent.class, "NomadTent",
					 * 16775927, 8345150); 692 registerStructure(1201,
					 * lotr.common.world.structure2.LOTRWorldGenNomadTentLarge.class,
					 * "NomadTentLarge", 16775927, 8345150); 693 registerStructure(1202,
					 * lotr.common.world.structure2.LOTRWorldGenNomadChieftainTent.class,
					 * "NomadChieftainTent", 16775927, 8345150); 694 registerStructure(1203,
					 * lotr.common.world.structure2.LOTRWorldGenNomadWell.class, "NomadWell",
					 * 5478114, 15391151); 695 registerVillage(1204, new
					 * LOTRVillageGenHaradNomad(LOTRBiome.nearHaradSemiDesert, 1.0F),
					 * "NomadVillageSmall", 16775927, 8345150, new
					 * IVillageProperties<LOTRVillageGenHaradNomad.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenHaradNomad.Instance instance) { 700
					 * instance.villageType = LOTRVillageGenHaradNomad.VillageType.SMALL; } }); 703
					 * registerVillage(1205, new
					 * LOTRVillageGenHaradNomad(LOTRBiome.nearHaradSemiDesert, 1.0F),
					 * "NomadVillageBig", 16775927, 8345150, new
					 * IVillageProperties<LOTRVillageGenHaradNomad.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenHaradNomad.Instance instance) { 708
					 * instance.villageType = LOTRVillageGenHaradNomad.VillageType.BIG; } }); 711
					 * registerStructure(1206,
					 * lotr.common.world.structure2.LOTRWorldGenNomadBazaarTent.class,
					 * "NomadBazaarTent", 16775927, 8345150);
					 * 
					 * 713 registerStructure(1250,
					 * lotr.common.world.structure2.LOTRWorldGenGulfWarCamp.class, "GulfWarCamp",
					 * 12849937, 4275226); 714 registerStructure(1251,
					 * lotr.common.world.structure2.LOTRWorldGenGulfHouse.class, "GulfHouse",
					 * 9335899, 5654831); 715 registerStructure(1252,
					 * lotr.common.world.structure2.LOTRWorldGenGulfAltar.class, "GulfAltar",
					 * 12849937, 4275226); 716 registerStructure(1253,
					 * lotr.common.world.structure2.LOTRWorldGenGulfSmithy.class, "GulfSmithy",
					 * 9335899, 5654831); 717 registerStructure(1254,
					 * lotr.common.world.structure2.LOTRWorldGenGulfBazaar.class, "GulfBazaar",
					 * 9335899, 5654831); 718 registerStructure(1255,
					 * lotr.common.world.structure2.LOTRWorldGenGulfTotem.class, "GulfTotem",
					 * 12849937, 4275226); 719 registerStructure(1256,
					 * lotr.common.world.structure2.LOTRWorldGenGulfPyramid.class, "GulfPyramid",
					 * 15721151, 12873038); 720 registerStructure(1257,
					 * lotr.common.world.structure2.LOTRWorldGenGulfFarm.class, "GulfFarm", 9547581,
					 * 12849937); 721 registerStructure(1258,
					 * lotr.common.world.structure2.LOTRWorldGenGulfTower.class, "GulfTower",
					 * 12849937, 4275226); 722 registerStructure(1259,
					 * lotr.common.world.structure2.LOTRWorldGenGulfTavern.class, "GulfTavern",
					 * 9335899, 5654831); 723 registerStructure(1260,
					 * lotr.common.world.structure2.LOTRWorldGenGulfVillageSign.class,
					 * "GulfVillageSign", 14737111, 16250349); 724 registerStructure(1261,
					 * lotr.common.world.structure2.LOTRWorldGenGulfVillageLight.class,
					 * "GulfVillageLight", 14737111, 16250349); 725 registerVillage(1262, new
					 * LOTRVillageGenGulfHarad(LOTRBiome.gulfHarad, 1.0F), "GulfVillage", 9335899,
					 * 5654831, new IVillageProperties<LOTRVillageGenGulfHarad.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGulfHarad.Instance instance) { 730
					 * instance.villageType = LOTRVillageGenGulfHarad.VillageType.VILLAGE; } }); 733
					 * registerStructure(1263,
					 * lotr.common.world.structure2.LOTRWorldGenGulfPasture.class, "GulfPasture",
					 * 9547581, 12849937); 734 registerVillage(1264, new
					 * LOTRVillageGenGulfHarad(LOTRBiome.gulfHarad, 1.0F), "GulfTown", 15721151,
					 * 12873038, new IVillageProperties<LOTRVillageGenGulfHarad.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGulfHarad.Instance instance) { 739
					 * instance.villageType = LOTRVillageGenGulfHarad.VillageType.TOWN; } }); 742
					 * registerVillage(1265, new LOTRVillageGenGulfHarad(LOTRBiome.gulfHarad, 1.0F),
					 * "GulfFortVillage", 12849937, 4275226, new
					 * IVillageProperties<LOTRVillageGenGulfHarad.Instance>() {
					 * 
					 * public void apply(LOTRVillageGenGulfHarad.Instance instance) { 747
					 * instance.villageType = LOTRVillageGenGulfHarad.VillageType.FORT; } });
					 * 
					 * 751 registerStructure(1500,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainHutVillage.class,
					 * "MoredainHutVillage", 8873812, 12891279); 752 registerStructure(1501,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainHutChieftain.class,
					 * "MoredainHutChieftain", 8873812, 12891279); 753 registerStructure(1502,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainHutTrader.class,
					 * "MoredainHutTrader", 8873812, 12891279); 754 registerStructure(1503,
					 * lotr.common.world.structure2.LOTRWorldGenMoredainHutHunter.class,
					 * "MoredainHutHunter", 8873812, 12891279);
					 * 
					 * 756 registerStructure(1550,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainPyramid.class,
					 * "TauredainPyramid", 6513746, 4803646); 757 registerStructure(1551,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainHouseSimple.class,
					 * "TauredainHouseSimple", 4796447, 8021303); 758 registerStructure(1552,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainHouseStilts.class,
					 * "TauredainHouseStilts", 4796447, 8021303); 759 registerStructure(1553,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainWatchtower.class,
					 * "TauredainWatchtower", 4796447, 8021303); 760 registerStructure(1554,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainHouseLarge.class,
					 * "TauredainHouseLarge", 4796447, 14593598); 761 registerStructure(1555,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainChieftainPyramid.class,
					 * "TauredainChieftainPyramid", 6513746, 4803646); 762 registerStructure(1556,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainVillageTree.class,
					 * "TauredainVillageTree", 9285414, 4796447); 763 registerStructure(1557,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainVillageFarm.class,
					 * "TauredainVillageFarm", 9285414, 4796447); 764 registerVillage(1558, new
					 * LOTRVillageGenTauredain(LOTRBiome.tauredainClearing, 1.0F),
					 * "TauredainVillage", 6840658, 5979708, new
					 * IVillageProperties<LOTRVillageGenTauredain.Instance>() { public void
					 * apply(LOTRVillageGenTauredain.Instance instance) {} });
					 * 
					 * 
					 * 
					 * 771 registerStructure(1559,
					 * lotr.common.world.structure2.LOTRWorldGenTauredainSmithy.class,
					 * "TauredainSmithy", 4796447, 8021303);
					 * 
					 * 773 registerStructure(1700,
					 * lotr.common.world.structure2.LOTRWorldGenHalfTrollHouse.class,
					 * "HalfTrollHouse", 10058344, 5325111); 774 registerStructure(1701,
					 * lotr.common.world.structure2.LOTRWorldGenHalfTrollWarlordHouse.class,
					 * "HalfTrollWarlordHouse", 10058344, 5325111);
					 * 
					 * 776 registerStructure(1994,
					 * lotr.common.world.structure2.LOTRWorldGenTicketBooth.class, "TicketBooth",
					 * 15313961, 1118481, true);
					 * 
					 * 778 LOTRMapGenDwarvenMine.register(); 779
					 * LOTRMapGenTauredainPyramid.register();
					 */
		/*     */ }

	/*     */
	/*     */
	/*     */
	/* 784 */ private static void registerStructure(int id, Class<? extends WorldGenerator> strClass, String name,
			int colorBG, int colorFG) {
		registerStructure(id, strClass, name, colorBG, colorFG, false);
	}

	/*     */
	/*     */
	/*     */
	/*     */ private static void registerStructure(int id, final Class<? extends WorldGenerator> strClass, String name,
			int colorBG, int colorFG, boolean hide) {
		/* 789 */ IStructureProvider strProvider = new IStructureProvider()
		/*     */ {
			/*     */
			/*     */ public boolean generateStructure(World world, EntityPlayer entityplayer, int i, int j, int k)
			/*     */ {
				/* 794 */ WorldGenerator generator = null;
				/*     */
				/*     */ try {
					/* 797 */ generator = (WorldGenerator) strClass.getConstructor(new Class[] { boolean.class })
							.newInstance(new Object[] { Boolean.valueOf(true) });
					/*     */ }
				/* 799 */ catch (Exception e) {
					/*     */
					/* 801 */ FMLLog.warning("Failed to build LOTR structure " + strClass.getName(), new Object[0]);
					/* 802 */ e.printStackTrace();
					/*     */ }
				/*     */
				/* 805 */ if (generator != null) {
					/*     */
					/* 807 */ boolean timelapse = LOTRConfig.strTimelapse;
					/*     */
					/* 809 */ if (generator instanceof LOTRWorldGenStructureBase2) {
						/*     */
						/* 811 */ LOTRWorldGenStructureBase2 strGen = (LOTRWorldGenStructureBase2) generator;
						/* 812 */ strGen.restrictions = false;
						/* 813 */ strGen.usingPlayer = entityplayer;
						/* 814 */ if (timelapse) {
							/*     */
							/* 816 */ LOTRStructureTimelapse.start(strGen, world, i, j, k);
							/* 817 */ return true;
							/*     */ }
						/*     */
						/*     */
						/* 821 */ return strGen.generateWithSetRotation(world, world.rand, i, j, k,
								strGen.usingPlayerRotation());
						/*     */ }
					/*     */
					/* 824 */ if (generator instanceof LOTRWorldGenStructureBase) {
						/*     */
						/* 826 */ LOTRWorldGenStructureBase strGen = (LOTRWorldGenStructureBase) generator;
						/* 827 */ strGen.restrictions = false;
						/* 828 */ strGen.usingPlayer = entityplayer;
						/* 829 */ if (timelapse) {
							/*     */
							/* 831 */ //LOTRStructureTimelapse.start(strGen, world, i, j, k);
							/* 832 */ return true;
							/*     */ }
						/*     */
						/*     */
						/* 836 */ //return strGen.generate(world, world.rand, i, j, k);
						/*     */ }
					/*     */ }
				/*     */
				/*     */
				/* 841 */ return false;
				/*     */ }

			/*     */
			/*     */
			/*     */
			/*     */ public boolean isVillage() {
				/* 847 */ return false;
				/*     */ }
			/*     */ };
		/*     */
		/* 851 */ registerStructure(id, strProvider, name, colorBG, colorFG, hide);
		/*     */ }

	/*     */
	/*     */
	/*     */ private static void registerVillage(int id, final LOTRVillageGen village, String name, int colorBG,
			int colorFG, final IVillageProperties properties) {
		/* 856 */ IStructureProvider strProvider = new IStructureProvider()
		/*     */ {
			/*     */
			/*     */ public boolean generateStructure(World world, EntityPlayer entityplayer, int i, int j, int k)
			/*     */ {
				/* 861 */ //LOTRVillageGen.AbstractInstance instance = village.createAndSetupVillageInstance(world, i, k,world.rand);
				/* 862 */ //instance.setRotation((LOTRStructures.getRotationFromPlayer(entityplayer) + 2) % 4);
				/* 863 */ //properties.apply(instance);
				/* 864 */ //village.generateCompleteVillageInstance(instance, world, i, k);
				/* 865 */ return true;
				/*     */ }

			/*     */
			/*     */
			/*     */
			/*     */ public boolean isVillage() {
				/* 871 */ return true;
				/*     */ }
			/*     */ };
		/*     */
		/* 875 */ registerStructure(id, strProvider, name, colorBG, colorFG, false);
		/*     */ }

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 885 */ public static int getRotationFromPlayer(EntityPlayer entityplayer) {
		return MathHelper.floor_double((entityplayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
	}

	/*     */
	/*     */ private static interface IVillageProperties<V> {
		/*     */ void apply(V param1V);
		/*     */ }

	/*     */
	/*     */ public static interface IStructureProvider {
		/*     */ boolean generateStructure(World param1World, EntityPlayer param1EntityPlayer, int param1Int1,
				int param1Int2, int param1Int3);

		/*     */
		/*     */ boolean isVillage();
		/*     */ }
	/*     */ }

/*
 * Location: C:\Users\tani\Desktop\minecraft-modding\LOTRMod
 * v35.3\!\lotr\common\world\structure\LOTRStructures.class Java compiler
 * version: 6 (50.0) JD-Core Version: 1.0.7
 */