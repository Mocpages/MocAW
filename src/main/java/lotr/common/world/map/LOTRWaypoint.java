/*     */ package lotr.common.world.map;

/*     */
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;

/*     */ import lotr.common.LOTRLevelData;
/*     */ import lotr.common.LOTRMod;
/*     */ import lotr.common.fac.LOTRFaction;
/*     */ import lotr.common.world.genlayer.LOTRGenLayerWorld;
import lotr.common.world.map.LOTRRoads.RoadPoint;
//import lotr.common.world.map.LOTRWaypoint.Region;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.util.StatCollector;
/*     */ import net.minecraft.world.World;

/*     */
/*     */ public enum LOTRWaypoint/*     */ implements LOTRAbstractWaypoint
/*     */ {
	/* 17 */ HIMLING(Region.OCEAN, LOTRFaction.UNALIGNED, 485, 523),
	/* 18 */ TOL_FUIN(Region.OCEAN, LOTRFaction.UNALIGNED, 357, 542),
	/* 19 */ TOL_MORWEN(Region.OCEAN, LOTRFaction.UNALIGNED, 87, 698),
	/*     */
	/* 21 */ MENELTARMA_SUMMIT(Region.MENELTARMA, LOTRFaction.UNALIGNED, 64, 1733, true),
	/*     */
	/* 23 */ HOBBITON(Region.SHIRE, LOTRFaction.HOBBIT, 815, 727),
	/* 24 */ BRANDYWINE_BRIDGE(Region.SHIRE, LOTRFaction.UNALIGNED, 853, 725),
	/* 25 */ SARN_FORD(Region.SHIRE, LOTRFaction.UNALIGNED, 883, 802),
	/* 26 */ LONGBOTTOM(Region.SHIRE, LOTRFaction.HOBBIT, 820, 765),
	/* 27 */ MICHEL_DELVING(Region.SHIRE, LOTRFaction.HOBBIT, 796, 739),
	/* 28 */ WILLOWBOTTOM(Region.SHIRE, LOTRFaction.HOBBIT, 845, 752),
	/* 29 */ BUCKLEBURY(Region.SHIRE, LOTRFaction.HOBBIT, 857, 734),
	/* 30 */ WHITFURROWS(Region.SHIRE, LOTRFaction.HOBBIT, 843, 727),
	/* 31 */ FROGMORTON(Region.SHIRE, LOTRFaction.HOBBIT, 831, 728),
	/* 32 */ OATBARTON(Region.SHIRE, LOTRFaction.HOBBIT, 822, 701),
	/* 33 */ SCARY(Region.SHIRE, LOTRFaction.HOBBIT, 840, 713),
	/* 34 */ NEEDLEHOLE(Region.SHIRE, LOTRFaction.HOBBIT, 806, 708),
	/* 35 */ LITTLE_DELVING(Region.SHIRE, LOTRFaction.HOBBIT, 785, 718),
	/* 36 */ WAYMEET(Region.SHIRE, LOTRFaction.UNALIGNED, 807, 733),
	/* 37 */ TUCKBOROUGH(Region.SHIRE, LOTRFaction.HOBBIT, 815, 741),
	/* 38 */ NOBOTTLE(Region.SHIRE, LOTRFaction.HOBBIT, 797, 710),
	/* 39 */ TIGHFIELD(Region.SHIRE, LOTRFaction.HOBBIT, 778, 712),
	/* 40 */ BROCKENBORINGS(Region.SHIRE, LOTRFaction.HOBBIT, 833, 710),
	/* 41 */ DEEPHALLOW(Region.SHIRE, LOTRFaction.HOBBIT, 850, 749),
	/* 42 */ STOCK(Region.SHIRE, LOTRFaction.HOBBIT, 849, 737),
	/* 43 */ BYWATER(Region.SHIRE, LOTRFaction.HOBBIT, 820, 730),
	/* 44 */ OVERHILL(Region.SHIRE, LOTRFaction.HOBBIT, 817, 720),
	/* 45 */ HAYSEND(Region.SHIRE, LOTRFaction.HOBBIT, 858, 747),
	/* 46 */ HAY_GATE(Region.SHIRE, LOTRFaction.HOBBIT, 856, 728),
	/* 47 */ GREENHOLM(Region.SHIRE, LOTRFaction.HOBBIT, 762, 745),
	/*     */
	/* 49 */ WITHYWINDLE_VALLEY(Region.OLD_FOREST, LOTRFaction.UNALIGNED, 881, 749),
	/*     */
	/* 51 */ FORLOND(Region.LINDON, LOTRFaction.HIGH_ELF, 526, 718),
	/* 52 */ HARLOND(Region.LINDON, LOTRFaction.HIGH_ELF, 605, 783),
	/* 53 */ MITHLOND_NORTH(Region.LINDON, LOTRFaction.HIGH_ELF, 669, 717),
	/* 54 */ MITHLOND_SOUTH(Region.LINDON, LOTRFaction.HIGH_ELF, 679, 729),
	/* 55 */ FORLINDON(Region.LINDON, LOTRFaction.HIGH_ELF, 532, 638),
	/* 56 */ HARLINDON(Region.LINDON, LOTRFaction.HIGH_ELF, 611, 878),
	/* 57 */ TOWER_HILLS(Region.LINDON, LOTRFaction.HIGH_ELF, 710, 742),
	/* 58 */ AMON_EREB(Region.LINDON, LOTRFaction.HIGH_ELF, 500, 708),
	/*     */
	/* 60 */ BELEGOST(Region.BLUE_MOUNTAINS, LOTRFaction.UNALIGNED, 622, 600),
	/* 61 */ NOGROD(Region.BLUE_MOUNTAINS, LOTRFaction.UNALIGNED, 626, 636),
	/* 62 */ MOUNT_RERIR(Region.BLUE_MOUNTAINS, LOTRFaction.UNALIGNED, 592, 525),
	/* 63 */ MOUNT_DOLMED(Region.BLUE_MOUNTAINS, LOTRFaction.UNALIGNED, 599, 627),
	/* 64 */ THORIN_HALLS(Region.BLUE_MOUNTAINS, LOTRFaction.BLUE_MOUNTAINS, 641, 671),
	/* 65 */ ARVEDUI_MINES(Region.BLUE_MOUNTAINS, LOTRFaction.UNALIGNED, 663, 489),
	/* 66 */ THRAIN_HALLS(Region.BLUE_MOUNTAINS, LOTRFaction.BLUE_MOUNTAINS, 669, 793),
	/*     */
	/* 68 */ NORTH_DOWNS(Region.ERIADOR, LOTRFaction.UNALIGNED, 930, 626),
	/* 69 */ SOUTH_DOWNS(Region.ERIADOR, LOTRFaction.UNALIGNED, 960, 768),
	/* 70 */ ERYN_VORN(Region.ERIADOR, LOTRFaction.UNALIGNED, 747, 957),
	/* 71 */ THARBAD(Region.ERIADOR, LOTRFaction.UNALIGNED, 979, 878),
	/* 72 */ FORNOST(Region.ERIADOR, LOTRFaction.UNALIGNED, 897, 652),
	/* 73 */ ANNUMINAS(Region.ERIADOR, LOTRFaction.UNALIGNED, 814, 661),
	/* 74 */ GREENWAY_CROSSROADS(Region.ERIADOR, LOTRFaction.UNALIGNED, 920, 810),
	/* 75 */ FORSAKEN_INN(Region.ERIADOR, LOTRFaction.UNALIGNED, 950, 743),
	/*     */
	/* 77 */ BREE(Region.BREE_LAND, LOTRFaction.UNALIGNED, 917, 734),
	/* 78 */ STADDLE(Region.BREE_LAND, LOTRFaction.UNALIGNED, 923, 736),
	/* 79 */ COMBE(Region.BREE_LAND, LOTRFaction.UNALIGNED, 927, 731),
	/* 80 */ ARCHET(Region.BREE_LAND, LOTRFaction.UNALIGNED, 929, 726),
	/*     */
	/* 82 */ WEATHERTOP(Region.LONE_LANDS, LOTRFaction.UNALIGNED, 998, 723),
	/* 83 */ LAST_BRIDGE(Region.LONE_LANDS, LOTRFaction.UNALIGNED, 1088, 714),
	/* 84 */ OLD_ELF_WAY(Region.LONE_LANDS, LOTRFaction.UNALIGNED, 1028, 847),
	/*     */
	/* 86 */ RIVENDELL(Region.RIVENDELL_VALE, LOTRFaction.HIGH_ELF, 1173, 721),
	/* 87 */ FORD_BRUINEN(Region.RIVENDELL_VALE, LOTRFaction.HIGH_ELF, 1163, 723),
	/*     */
	/* 89 */ THE_TROLLSHAWS(Region.TROLLSHAWS, LOTRFaction.UNALIGNED, 1130, 703),
	/*     */
	/* 91 */ CARN_DUM(Region.ANGMAR, LOTRFaction.ANGMAR, 1010, 503),
	/*     */
	/* 93 */ WEST_GATE(Region.EREGION, LOTRFaction.UNALIGNED, 1134, 873),
	/* 94 */ OST_IN_EDHIL(Region.EREGION, LOTRFaction.UNALIGNED, 1112, 870),
	/*     */
	/* 96 */ NORTH_DUNLAND(Region.DUNLAND, LOTRFaction.DUNLAND, 1073, 946),
	/* 97 */ SOUTH_DUNLAND(Region.DUNLAND, LOTRFaction.DUNLAND, 1070, 1027),
	/* 98 */ FORDS_OF_ISEN(Region.DUNLAND, LOTRFaction.UNALIGNED, 1102, 1087),
	/* 99 */ DWARROWVALE(Region.DUNLAND, LOTRFaction.UNALIGNED, 1080, 990),
	/* 100 */ WULFBURG(Region.DUNLAND, LOTRFaction.UNALIGNED, 1077, 1098),
	/*     */
	/* 102 */ LOND_DAER(Region.ENEDWAITH, LOTRFaction.UNALIGNED, 867, 1004),
	/* 103 */ ENEDWAITH_ROAD(Region.ENEDWAITH, LOTRFaction.UNALIGNED, 1025, 1050),
	/* 104 */ MOUTHS_ISEN(Region.ENEDWAITH, LOTRFaction.UNALIGNED, 871, 1127),
	/*     */
	/* 106 */ ISENGARD(Region.NAN_CURUNIR, LOTRFaction.UNALIGNED, 1102, 1058),
	/*     */
	/* 108 */ CAPE_OF_FOROCHEL(Region.FORODWAITH, LOTRFaction.UNALIGNED, 786, 390),
	/* 109 */ SOUTH_FOROCHEL(Region.FORODWAITH, LOTRFaction.UNALIGNED, 825, 459),
	/* 110 */ WITHERED_HEATH(Region.FORODWAITH, LOTRFaction.UNALIGNED, 1441, 556),
	/*     */
	/* 112 */ MOUNT_GUNDABAD(Region.MISTY_MOUNTAINS, LOTRFaction.GUNDABAD, 1195, 592),
	/* 113 */ MOUNT_GRAM(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1106, 589),
	/* 114 */ HIGH_PASS(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1222, 706),
	/* 115 */ MOUNT_CARADHRAS(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1166, 845),
	/* 116 */ MOUNT_CELEBDIL(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1158, 864),
	/* 117 */ MOUNT_FANUIDHOL(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1191, 854),
	/* 118 */ MOUNT_METHEDRAS(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1111, 1031),
	/* 119 */ GOBLIN_TOWN(Region.MISTY_MOUNTAINS, LOTRFaction.GUNDABAD, 1220, 696),
	/* 120 */ EAGLES_EYRIE(Region.MISTY_MOUNTAINS, LOTRFaction.UNALIGNED, 1246, 685),
	/*     */
	/* 122 */ DAINS_HALLS(Region.GREY_MOUNTAINS, LOTRFaction.UNALIGNED, 1262, 554),
	/* 123 */ SCATHA(Region.GREY_MOUNTAINS, LOTRFaction.UNALIGNED, 1335, 562),
	/*     */
	/* 125 */ CARROCK(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1281, 681),
	/* 126 */ OLD_FORD(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1284, 702),
	/* 127 */ GLADDEN_FIELDS(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1294, 790),
	/* 128 */ DIMRILL_DALE(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1177, 864),
	/* 129 */ FIELD_OF_CELEBRANT(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1281, 960),
	/* 130 */ RAUROS(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1345, 1130),
	/* 131 */ BEORN(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1302, 680),
	/* 132 */ FOREST_GATE(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1303, 655),
	/* 133 */ FRAMSBURG(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1251, 590),
	/* 134 */ ANDUIN_CROSSROADS(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1285, 905),
	/* 135 */ EAST_RHOVANION_ROAD(Region.VALES_OF_ANDUIN, LOTRFaction.UNALIGNED, 1354, 966),
	/*     */
	/* 137 */ THRANDUIL_HALLS(Region.WOODLAND_REALM, LOTRFaction.WOOD_ELF, 1420, 633),
	/*     */
	/* 139 */ DOL_GULDUR(Region.MIRKWOOD, LOTRFaction.DOL_GULDUR, 1339, 894),
	/* 140 */ MIRKWOOD_MOUNTAINS(Region.MIRKWOOD, LOTRFaction.UNALIGNED, 1430, 672),
	/* 141 */ RHOSGOBEL(Region.MIRKWOOD, LOTRFaction.UNALIGNED, 1343, 762),
	/* 142 */ ENCHANTED_RIVER(Region.MIRKWOOD, LOTRFaction.UNALIGNED, 1396, 650),
	/*     */
	/* 144 */ RIVER_GATE(Region.WILDERLAND, LOTRFaction.UNALIGNED, 1474, 696),
	/* 145 */ EAST_BIGHT(Region.WILDERLAND, LOTRFaction.UNALIGNED, 1437, 824),
	/* 146 */ OLD_RHOVANION(Region.WILDERLAND, LOTRFaction.UNALIGNED, 1524, 870),
	/* 147 */ DORWINION_CROSSROADS(Region.WILDERLAND, LOTRFaction.UNALIGNED, 1680, 882),
	/*     */
	/* 149 */ EREBOR(Region.DALE, LOTRFaction.DALE, 1463, 609),
	/* 150 */ DALE_CITY(Region.DALE, LOTRFaction.DALE, 1464, 615),
	/* 151 */ LONG_LAKE(Region.DALE, LOTRFaction.DALE, 1461, 632),
	/* 152 */ RUNNING_FORD(Region.DALE, LOTRFaction.UNALIGNED, 1534, 749),
	/* 153 */ REDWATER_FORD(Region.DALE, LOTRFaction.UNALIGNED, 1651, 690),
	/* 154 */ DALE_CROSSROADS(Region.DALE, LOTRFaction.UNALIGNED, 1567, 680),
	/* 155 */ DALE_PORT(Region.DALE, LOTRFaction.UNALIGNED, 1657, 768),
	/*     */
	/* 157 */ WEST_PEAK(Region.IRON_HILLS, LOTRFaction.UNALIGNED, 1588, 608),
	/* 158 */ EAST_PEAK(Region.IRON_HILLS, LOTRFaction.UNALIGNED, 1729, 610),
	/*     */
	/* 160 */ CARAS_GALADHON(Region.LOTHLORIEN, LOTRFaction.UNALIGNED, 1242, 902),
	/* 161 */ CERIN_AMROTH(Region.LOTHLORIEN, LOTRFaction.UNALIGNED, 1230, 897),
	/* 162 */ NIMRODEL(Region.LOTHLORIEN, LOTRFaction.UNALIGNED, 1198, 894),
	/*     */
	/* 164 */ DERNDINGLE(Region.FANGORN, LOTRFaction.FANGORN, 1163, 1030),
	/* 165 */ WELLINGHALL(Region.FANGORN, LOTRFaction.FANGORN, 1153, 1014),
	/* 166 */ TREEBEARD_HILL(Region.FANGORN, LOTRFaction.FANGORN, 1200, 1030),
	/*     */
	/* 168 */ WOLD(Region.ROHAN, LOTRFaction.UNALIGNED, 1285, 1015),
	/* 169 */ EDORAS(Region.ROHAN, LOTRFaction.ROHAN, 1190, 1148),
	/* 170 */ HELMS_DEEP(Region.ROHAN, LOTRFaction.UNALIGNED, 1128, 1115),
	/* 171 */ HELMS_CROSSROADS(Region.ROHAN, LOTRFaction.UNALIGNED, 1136, 1108),
	/* 172 */ URUK_HIGHLANDS(Region.ROHAN, LOTRFaction.UNALIGNED, 1131, 1057),
	/* 173 */ MERING_STREAM(Region.ROHAN, LOTRFaction.UNALIGNED, 1299, 1202),
	/* 174 */ ENTWADE(Region.ROHAN, LOTRFaction.UNALIGNED, 1239, 1104),
	/* 175 */ EASTMARK(Region.ROHAN, LOTRFaction.UNALIGNED, 1286, 1130),
	/* 176 */ ALDBURG(Region.ROHAN, LOTRFaction.UNALIGNED, 1223, 1178),
	/* 177 */ GRIMSLADE(Region.ROHAN, LOTRFaction.UNALIGNED, 1153, 1122),
	/* 178 */ CORSAIRS_LANDING(Region.ROHAN, LOTRFaction.UNALIGNED, 992, 1113),
	/* 179 */ FRECA_HOLD(Region.ROHAN, LOTRFaction.UNALIGNED, 1099, 1144),
	/*     */
	/* 181 */ DUNHARROW(Region.WHITE_MOUNTAINS, LOTRFaction.UNALIGNED, 1175, 1154),
	/* 182 */ TARLANG(Region.WHITE_MOUNTAINS, LOTRFaction.UNALIGNED, 1205, 1213),
	/* 183 */ RAS_MORTHIL(Region.WHITE_MOUNTAINS, LOTRFaction.UNALIGNED, 845, 1332),
	/*     */
	/* 185 */ MINAS_TIRITH(Region.GONDOR, LOTRFaction.GONDOR, 1419, 1247),
	/* 186 */ CAIR_ANDROS(Region.GONDOR, LOTRFaction.GONDOR, 1427, 1207),
	/* 187 */ HALIFIRIEN(Region.GONDOR, LOTRFaction.UNALIGNED, 1309, 1205),
	/* 188 */ CALENHAD(Region.GONDOR, LOTRFaction.UNALIGNED, 1330, 1212),
	/* 189 */ MINRIMMON(Region.GONDOR, LOTRFaction.UNALIGNED, 1350, 1219),
	/* 190 */ ERELAS(Region.GONDOR, LOTRFaction.UNALIGNED, 1367, 1222),
	/* 191 */ NARDOL(Region.GONDOR, LOTRFaction.UNALIGNED, 1384, 1228),
	/* 192 */ EILENACH(Region.GONDOR, LOTRFaction.UNALIGNED, 1402, 1228),
	/* 193 */ AMON_DIN(Region.GONDOR, LOTRFaction.UNALIGNED, 1416, 1231),
	/* 194 */ OSGILIATH_WEST(Region.GONDOR, LOTRFaction.UNALIGNED, 1428, 1246),
	/*     */
	/* 196 */ OSGILIATH_EAST(Region.ITHILIEN, LOTRFaction.UNALIGNED, 1435, 1246),
	/* 197 */ EMYN_ARNEN(Region.ITHILIEN, LOTRFaction.UNALIGNED, 1437, 1267),
	/* 198 */ HENNETH_ANNUN(Region.ITHILIEN, LOTRFaction.GONDOR, 1443, 1192),
	/* 199 */ CROSSROADS_ITHILIEN(Region.ITHILIEN, LOTRFaction.UNALIGNED, 1450, 1236),
	/* 200 */ NORTH_ITHILIEN(Region.ITHILIEN, LOTRFaction.UNALIGNED, 1447, 1151),
	/* 201 */ CROSSINGS_OF_POROS(Region.ITHILIEN, LOTRFaction.UNALIGNED, 1442, 1370),
	/*     */
	/* 203 */ PELARGIR(Region.LEBENNIN, LOTRFaction.GONDOR, 1390, 1348),
	/* 204 */ LINHIR(Region.LEBENNIN, LOTRFaction.UNALIGNED, 1292, 1342),
	/* 205 */ ANDUIN_MOUTH(Region.LEBENNIN, LOTRFaction.UNALIGNED, 1273, 1369),
	/*     */
	/* 207 */ IMLOTH_MELUI(Region.LOSSARNACH, LOTRFaction.UNALIGNED, 1397, 1254),
	/* 208 */ CROSSINGS_ERUI(Region.LOSSARNACH, LOTRFaction.UNALIGNED, 1412, 1272),
	/*     */
	/* 210 */ CALEMBEL(Region.LAMEDON, LOTRFaction.GONDOR, 1235, 1248),
	/* 211 */ ETHRING(Region.LAMEDON, LOTRFaction.UNALIGNED, 1256, 1259),
	/*     */
	/* 213 */ ERECH(Region.BLACKROOT_VALE, LOTRFaction.GONDOR, 1186, 1205),
	/*     */
	/* 215 */ GREEN_HILLS(Region.PINNATH_GELIN, LOTRFaction.UNALIGNED, 1045, 1273),
	/*     */
	/* 217 */ DOL_AMROTH(Region.DOR_EN_ERNIL, LOTRFaction.GONDOR, 1158, 1333),
	/* 218 */ EDHELLOND(Region.DOR_EN_ERNIL, LOTRFaction.GONDOR, 1189, 1293),
	/* 219 */ TARNOST(Region.DOR_EN_ERNIL, LOTRFaction.UNALIGNED, 1241, 1300),
	/*     */
	/* 221 */ TOLFALAS_ISLAND(Region.TOLFALAS, LOTRFaction.UNALIGNED, 1240, 1414),
	/*     */
	/* 223 */ AMON_HEN(Region.EMYN_MUIL, LOTRFaction.UNALIGNED, 1335, 1131),
	/* 224 */ AMON_LHAW(Region.EMYN_MUIL, LOTRFaction.UNALIGNED, 1372, 1120),
	/* 225 */ ARGONATH(Region.EMYN_MUIL, LOTRFaction.UNALIGNED, 1347, 1112),
	/*     */
	/* 227 */ NORTH_UNDEEP(Region.BROWN_LANDS, LOTRFaction.UNALIGNED, 1319, 988),
	/* 228 */ SOUTH_UNDEEP(Region.BROWN_LANDS, LOTRFaction.UNALIGNED, 1335, 1024),
	/*     */
	/* 230 */ MORANNON(Region.DAGORLAD, LOTRFaction.UNALIGNED, 1470, 1131),
	/*     */
	/* 232 */ UDUN(Region.MORDOR, LOTRFaction.MORDOR, 1470, 1145),
	/* 233 */ MOUNT_DOOM(Region.MORDOR, LOTRFaction.MORDOR, 1533, 1204),
	/* 234 */ BARAD_DUR(Region.MORDOR, LOTRFaction.MORDOR, 1573, 1196),
	/* 235 */ MINAS_MORGUL(Region.MORDOR, LOTRFaction.MORDOR, 1461, 1239),
	/* 236 */ DURTHANG(Region.MORDOR, LOTRFaction.MORDOR, 1464, 1159),
	/* 237 */ CARACH_ANGREN(Region.MORDOR, LOTRFaction.MORDOR, 1493, 1166),
	/* 238 */ CIRITH_UNGOL(Region.MORDOR, LOTRFaction.MORDOR, 1479, 1225),
	/* 239 */ MORIGOST(Region.MORDOR, LOTRFaction.MORDOR, 1558, 1286),
	/* 240 */ NARGROTH(Region.MORDOR, LOTRFaction.MORDOR, 1640, 1248),
	/* 241 */ AMON_ANGREN(Region.MORDOR, LOTRFaction.MORDOR, 1663, 1245),
	/* 242 */ SEREGOST(Region.MORDOR, LOTRFaction.MORDOR, 1682, 1214),
	/* 243 */ FELLBEASTS(Region.MORDOR, LOTRFaction.MORDOR, 1754, 1164),
	/* 244 */ EASTERN_GUARD(Region.MORDOR, LOTRFaction.MORDOR, 1840, 1137),
	/*     */
	/* 246 */ NURNEN_NORTHERN_SHORE(Region.NURN, LOTRFaction.MORDOR, 1696, 1324),
	/* 247 */ NURNEN_SOUTHERN_SHORE(Region.NURN, LOTRFaction.MORDOR, 1718, 1369),
	/* 248 */ NURNEN_WESTERN_SHORE(Region.NURN, LOTRFaction.MORDOR, 1650, 1363),
	/* 249 */ NURNEN_EASTERN_SHORE(Region.NURN, LOTRFaction.MORDOR, 1758, 1316),
	/* 250 */ THAURBAND(Region.NURN, LOTRFaction.MORDOR, 1643, 1354),
	/*     */
	/* 252 */ VALLEY_OF_SPIDERS(Region.NAN_UNGOL, LOTRFaction.MORDOR, 1512, 1400),
	/*     */
	/* 254 */ DORWINION_PORT(Region.DORWINION, LOTRFaction.UNALIGNED, 1784, 863),
	/* 255 */ DORWINION_COURT(Region.DORWINION, LOTRFaction.DORWINION, 1758, 939),
	/* 256 */ DORWINION_FORD(Region.DORWINION, LOTRFaction.UNALIGNED, 1776, 986),
	/* 257 */ DORWINION_HILLS(Region.DORWINION, LOTRFaction.UNALIGNED, 1733, 950),
	/*     */
	/* 259 */ RHUN_ROAD_WAY(Region.RHUN, LOTRFaction.UNALIGNED, 2228, 835),
	/* 260 */ BALCARAS(Region.RHUN, LOTRFaction.UNALIGNED, 2231, 1058),
	/* 261 */ KHAND_NORTH_ROAD(Region.RHUN, LOTRFaction.UNALIGNED, 1932, 1331),
	/*     */
	/* 263 */ RHUN_CAPITAL(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1867, 984),
	/* 264 */ KHAMUL_TOWER(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1888, 878),
	/* 265 */ MORDOR_FORD(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1834, 1112),
	/* 266 */ RHUN_NORTH_CITY(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1903, 914),
	/* 267 */ BAZYLAN(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1921, 889),
	/* 268 */ BORDER_TOWN(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1794, 979),
	/* 269 */ RHUN_SEA_CITY(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1837, 956),
	/* 270 */ RHUN_NORTH_FORD(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1942, 811),
	/* 271 */ RHUN_NORTHEAST(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 2045, 815),
	/* 272 */ RHUN_SOUTH_PASS(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1869, 1055),
	/* 273 */ RHUN_EAST_CITY(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 2010, 962),
	/* 274 */ RHUN_EAST_TOWN(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1983, 936),
	/* 275 */ RHUN_SOUTHEAST(Region.RHUN_KHAGANATE, LOTRFaction.UNALIGNED, 1900, 1141),
	/*     */
	/* 277 */ BARAZ_DUM(Region.RED_MOUNTAINS, LOTRFaction.UNALIGNED, 2326, 800),
	/*     */
	/* 279 */ CROSSINGS_OF_HARAD(Region.HARONDOR, LOTRFaction.UNALIGNED, 1503, 1544),
	/*     */
	/* 281 */ HARNEN_SEA_TOWN(Region.HARNEDOR, LOTRFaction.NEAR_HARAD, 1343, 1561),
	/* 282 */ HARNEN_ROAD_TOWN(Region.HARNEDOR, LOTRFaction.NEAR_HARAD, 1518, 1563),
	/* 283 */ HARNEN_BLACK_TOWN(Region.HARNEDOR, LOTRFaction.NEAR_HARAD, 1566, 1482),
	/* 284 */ CROSSINGS_OF_LITHNEN(Region.HARNEDOR, LOTRFaction.NEAR_HARAD, 1539, 1545),
	/* 285 */ HARNEN_RIVER_TOWN(Region.HARNEDOR, LOTRFaction.NEAR_HARAD, 1447, 1558),
	/*     */
	/* 287 */ KHAND_FORD(Region.LOSTLADEN, LOTRFaction.UNALIGNED, 1778, 1432),
	/*     */
	/* 289 */ UMBAR_CITY(Region.UMBAR, LOTRFaction.NEAR_HARAD, 1214, 1689),
	/* 290 */ UMBAR_GATE(Region.UMBAR, LOTRFaction.UNALIGNED, 1252, 1698),
	/* 291 */ GATE_HERUMOR(Region.UMBAR, LOTRFaction.NEAR_HARAD, 1097, 1721),
	/*     */
	/* 293 */ CEDAR_ROAD(Region.SOUTHRON_COASTS, LOTRFaction.UNALIGNED, 1034, 1848),
	/* 294 */ FERTILE_VALLEY(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1169, 1821),
	/* 295 */ GARDENS_BERUTHIEL(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1245, 1781),
	/* 296 */ AIN_AL_HARAD(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1265, 1737),
	/* 297 */ GATE_FUINUR(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1218, 1631),
	/* 298 */ COAST_FORTRESS(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1245, 1582),
	/* 299 */ SANDHILL_TOWN(Region.SOUTHRON_COASTS, LOTRFaction.UNALIGNED, 1277, 1600),
	/* 300 */ COAST_RIVER_TOWN(Region.SOUTHRON_COASTS, LOTRFaction.UNALIGNED, 1080, 1837),
	/* 301 */ COAST_CITY(Region.SOUTHRON_COASTS, LOTRFaction.NEAR_HARAD, 1165, 1742),
	/*     */
	/* 303 */ DESERT_TOWN(Region.HARAD_DESERT, LOTRFaction.UNALIGNED, 1563, 1611),
	/* 304 */ SOUTH_DESERT_TOWN(Region.HARAD_DESERT, LOTRFaction.UNALIGNED, 1141, 1976),
	/* 305 */ DESERT_RIVER_TOWN(Region.HARAD_DESERT, LOTRFaction.UNALIGNED, 1191, 1984),
	/*     */
	/* 307 */ GULF_OF_HARAD(Region.GULF_HARAD, LOTRFaction.NEAR_HARAD, 1724, 1982),
	/* 308 */ GULF_CITY(Region.GULF_HARAD, LOTRFaction.NEAR_HARAD, 1640, 1922),
	/* 309 */ GULF_FORD(Region.GULF_HARAD, LOTRFaction.UNALIGNED, 1686, 2032),
	/* 310 */ GULF_TRADE_TOWN(Region.GULF_HARAD, LOTRFaction.UNALIGNED, 1692, 2001),
	/* 311 */ GULF_NORTH_TOWN(Region.GULF_HARAD, LOTRFaction.NEAR_HARAD, 1626, 1874),
	/* 312 */ GULF_EAST_BAY(Region.GULF_HARAD, LOTRFaction.UNALIGNED, 2036, 2081),
	/* 313 */ GULF_EAST_PORT(Region.GULF_HARAD, LOTRFaction.UNALIGNED, 1847, 2049),
	/*     */
	/* 315 */ MOUNT_SAND(Region.HARAD_MOUNTAINS, LOTRFaction.UNALIGNED, 959, 1899),
	/* 316 */ MOUNT_GREEN(Region.HARAD_MOUNTAINS, LOTRFaction.UNALIGNED, 884, 2372),
	/* 317 */ MOUNT_THUNDER(Region.HARAD_MOUNTAINS, LOTRFaction.UNALIGNED, 1019, 2590),
	/*     */
	/* 319 */ GREAT_PLAINS_NORTH(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1308, 2067),
	/* 320 */ GREAT_PLAINS_SOUTH(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1462, 2452),
	/* 321 */ GREAT_PLAINS_WEST(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1048, 2215),
	/* 322 */ GREAT_PLAINS_EAST(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1637, 2176),
	/* 323 */ GREEN_VALLEY(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1557, 2622),
	/* 324 */ HARAD_LAKES(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1774, 2310),
	/* 325 */ LAKE_HARAD(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1100, 2592),
	/* 326 */ HARADUIN_MOUTH(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1846, 2838),
	/* 327 */ ISLE_MIST(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1533, 3573),
	/* 328 */ TAURELONDE(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 901, 2613),
	/* 329 */ HARAD_HORN(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1105, 3778),
	/* 330 */ TOWN_BONES(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1832, 2188),
	/* 331 */ HARADUIN_BRIDGE(Region.FAR_HARAD, LOTRFaction.UNALIGNED, 1621, 2673),
	/*     */
	/* 333 */ JUNGLE_CITY_TRADE(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 952, 2656),
	/* 334 */ JUNGLE_CITY_OLD(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1084, 2670),
	/* 335 */ JUNGLE_CITY_NORTH(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1419, 2604),
	/* 336 */ JUNGLE_CITY_EAST(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1594, 2766),
	/* 337 */ JUNGLE_CITY_CAPITAL(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1380, 2861),
	/* 338 */ JUNGLE_CITY_DEEP(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1184, 3237),
	/* 339 */ JUNGLE_CITY_WATCH(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1590, 2940),
	/* 340 */ JUNGLE_CITY_CAVES(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1257, 3054),
	/* 341 */ JUNGLE_CITY_STONE(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1236, 2787),
	/* 342 */ JUNGLE_LAKES(Region.FAR_HARAD_JUNGLE, LOTRFaction.UNALIGNED, 1550, 2856),
	/*     */
	/* 344 */ TROLL_ISLAND(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 1966, 2342),
	/* 345 */ BLACK_COAST(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 1936, 2496),
	/* 346 */ BLOOD_RIVER(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 1897, 2605),
	/* 347 */ SHADOW_POINT(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 1952, 2863),
	/*     */ /*
				 * BOSTON(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 396, 554, false, true),
				 * NYC(Region.PERTOROGWAITH, LOTRFaction.UNALIGNED, 382, 560, false, true),
				 * PARIS(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 698, 518, false, true),
				 * CALAIS(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 697, 507, false, true),
				 * CAMBRAI(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 704, 508, false, true),
				 * BAYONNE(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 684, 543, false, true),
				 * TOULOUSE(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 694, 542, false, true),
				 * MARSEILLE(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 710, 544, false, true),
				 * BORDEAUX(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 685, 537, false, true),
				 * ROCHELLE(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 684, 530, false, true),
				 * DIJON(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 712, 525, false, true),
				 * 
				 * BARCELONA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 696, 555, false, true),
				 * SARAGOSSA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 684, 554, false, true),
				 * VALENCIA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 688, 566, false, true),
				 * BURGOS(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 674, 549, false, true),
				 * MADRID(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 673, 563, false, true),
				 * CORUNNA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 654, 545, false, true),
				 * LISBON(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 650, 568, false, true),
				 * BADAJOZ(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 660, 568, false, true),
				 * CORDOVA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 671, 573, false, true),
				 * SEVILLE(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 664, 576, false, true),
				 * CADIZ(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 664, 581, false, true),
				 * CARTAGENA(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 684, 574, false, true),
				 * GIBRALTER(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 666, 582, false, true),
				 * 
				 * 349 MONS(Region.LOSSARNACH , LOTRFaction.UNALIGNED, 708, 504, false, true),
				 * OLD_JUNGLE_RUIN(Region.PERTOROGWAITH_FOREST, LOTRFaction.UNALIGNED, 1834,
				 * 2523); 18
				 */
	OLD_JUNGLE_RUIN(Region.PERTOROGWAITH_FOREST, LOTRFaction.UNALIGNED, 1834, 2523);
	/*     */

	/*     */ private Region region;
	/*     */
	/*     */ public LOTRFaction faction;
	/*     */
	/*     */ private int imgX;
	/*     */
	/*     */ private int imgY;
	/*     */
	/*     */ private int xCoord;
	/*     */
	/*     */ private int zCoord;
	/*     */
	/*     */ private boolean isHidden;
	private boolean moc;
	/*     */ public ArrayList<LOTRRoads> roads = new ArrayList<LOTRRoads>();

	LOTRWaypoint(Region r, LOTRFaction f, int x, int y) {
		/* 366 */ this.region = r;
		/* 367 */ this.region.waypoints.add(this);
		/* 368 */ this.faction = f;
		/* 369 */ this.imgX = x;
		/* 370 */ this.imgY = y;
		/* 371 */ this.xCoord = mapToWorldX(x);
		/* 372 */ this.zCoord = mapToWorldZ(y);
		/* 373 */ this.isHidden = true;
		this.moc = false;

	}

	/*     */ LOTRWaypoint(Region r, LOTRFaction f, int x, int y, boolean hide) {
		/* 366 */ this.region = r;
		/* 367 */ this.region.waypoints.add(this);
		/* 368 */ this.faction = f;
		/* 369 */ this.imgX = x;
		/* 370 */ this.imgY = y;
		/* 371 */ this.xCoord = mapToWorldX(x);
		/* 372 */ this.zCoord = mapToWorldZ(y);
		/* 373 */ this.isHidden = hide;
		this.moc = false;
		/*     */ }

	/*     */
	LOTRWaypoint(Region r, LOTRFaction f, int x, int y, boolean hide, boolean moc) {
		/* 366 */ this.region = r;
		/* 367 */ this.region.waypoints.add(this);
		/* 368 */ this.faction = f;
		/* 369 */ this.imgX = x;
		/* 370 */ this.imgY = y;
		/* 371 */ this.xCoord = mapToWorldX(x);
		/* 372 */ this.zCoord = mapToWorldZ(y);
		/* 373 */ this.isHidden = hide;
		this.moc = moc;
		/*     */ }

	/*     */
	/*     */
	/* 378 */ public static int mapToWorldX(double x) {
		return (int) Math.round((x - 810.0D + 0.5D) * LOTRGenLayerWorld.scale);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 383 */ public static int mapToWorldZ(double z) {
		return (int) Math.round((z - 730.0D + 0.5D) * LOTRGenLayerWorld.scale);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 388 */ public static int mapToWorldR(double r) {
		return (int) Math.round(r * LOTRGenLayerWorld.scale);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 393 */ public static int worldToMapX(double x) {
		return (int) Math.round(x / LOTRGenLayerWorld.scale - 0.5D + 810.0D);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 398 */ public static int worldToMapZ(double z) {
		return (int) Math.round(z / LOTRGenLayerWorld.scale - 0.5D + 730.0D);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/* 403 */ public static int worldToMapR(double r) {
		return (int) Math.round(r / LOTRGenLayerWorld.scale);
	}

	/*     */
	/*     */
	/*     */ public enum Region
	/*     */ {
		/* 408 */ OCEAN, /* 409 */ MENELTARMA, /* 410 */ SHIRE, /* 411 */ OLD_FOREST, /* 412 */ LINDON,
		/* 413 */ BLUE_MOUNTAINS, /* 414 */ ERIADOR, /* 415 */ BREE_LAND, /* 416 */ MIDGEWATER, /* 417 */ LONE_LANDS,
		/* 418 */ RIVENDELL_VALE, /* 419 */ TROLLSHAWS, /* 420 */ COLDFELLS, /* 421 */ ETTENMOORS, /* 422 */ ANGMAR,
		/* 423 */ EREGION, /* 424 */ DUNLAND, /* 425 */ ENEDWAITH, /* 426 */ NAN_CURUNIR, /* 427 */ FORODWAITH,
		/* 428 */ MISTY_MOUNTAINS, /* 429 */ GREY_MOUNTAINS, /* 430 */ VALES_OF_ANDUIN, /* 431 */ WOODLAND_REALM,
		/* 432 */ MIRKWOOD, /* 433 */ WILDERLAND, /* 434 */ DALE, /* 435 */ IRON_HILLS, /* 436 */ LOTHLORIEN,
		/* 437 */ FANGORN, /* 438 */ ROHAN, /* 439 */ WHITE_MOUNTAINS, /* 440 */ PUKEL, /* 441 */ GONDOR,
		/* 442 */ ITHILIEN, /* 443 */ LEBENNIN, /* 444 */ LOSSARNACH, /* 445 */ LAMEDON, /* 446 */ BLACKROOT_VALE,
		/* 447 */ PINNATH_GELIN, /* 448 */ DOR_EN_ERNIL, /* 449 */ TOLFALAS, /* 450 */ EMYN_MUIL, /* 451 */ NINDALF,
		/* 452 */ BROWN_LANDS, /* 453 */ DAGORLAD, /* 454 */ MORDOR, /* 455 */ NURN, /* 456 */ NAN_UNGOL,
		/* 457 */ DORWINION, /* 458 */ RHUN, /* 459 */ RHUN_KHAGANATE, /* 460 */ TOL_RHUNAER, /* 461 */ RED_MOUNTAINS,
		/* 462 */ HARONDOR, /* 463 */ HARNEDOR, /* 464 */ LOSTLADEN, /* 465 */ UMBAR, /* 466 */ SOUTHRON_COASTS,
		/* 467 */ HARAD_DESERT, /* 468 */ GULF_HARAD, /* 469 */ HARAD_MOUNTAINS, /* 470 */ FAR_HARAD,
		/* 471 */ FAR_HARAD_JUNGLE, /* 472 */ PERTOROGWAITH, /* 473 */ PERTOROGWAITH_FOREST;
		/*     */
		/* 475 */ Region() {
			this.waypoints = new ArrayList();
		}

		/*     */
		/*     */
		/*     */ public List<LOTRWaypoint> waypoints;
		/*     */ }

	/*     */
	/* 481 */ public int getX() {
		return this.imgX;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 487 */ public int getY() {
		return this.imgY;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 493 */ public int getXCoord() {
		return this.xCoord;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 499 */ public int getZCoord() {
		return this.zCoord;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 505 */ public int getYCoord(World world, int i, int k) {
		return LOTRMod.getTrueTopBlock(world, i, k);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 511 */ public int getYCoordDisplay() {
		return -1;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 517 */ public String getCodeName() {
		return name();
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 523 */ public String getDisplayName() {
		return StatCollector.translateToLocal("lotr.waypoint." + getCodeName());
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 529 */ public String getLoreText(EntityPlayer entityplayer) {
		return StatCollector.translateToLocal("lotr.waypoint." + getCodeName() + ".info");
	}

	/*     */
	/*     */
	/*     */
	/*     */ public boolean isRoadPointEqual(RoadPoint rp, LOTRWaypoint wp) {
		if (rp.isWaypoint && rp.x == wp.xCoord && rp.z == wp.zCoord) {
			return true;
		}
		return false;
	}

	public boolean hasWP(LOTRRoads.RoadPoint[] roadPoints, LOTRWaypoint wp) {
		for (RoadPoint point : roadPoints) {
			if (point.isWaypoint && point.wp == wp) {
				System.out.println("FOUND WP " + point.wp.getDisplayName());
				return true;
			} else if (point.wp != null) {
				System.out.println("Looking for WP " + wp.getDisplayName() + " found wp " + point.wp.getDisplayName());
			}
		}
		return false;
	}

	/* 535 */ public boolean hasPlayerUnlocked(EntityPlayer entityplayer) {
		if (!this.moc) {
			return false;
		}
		for (LOTRWaypoint w : this.values()) {
			if (entityplayer.getDistance(w.getXCoord(), entityplayer.posY, w.getZCoord()) <= 100) {
				for (LOTRRoads road : LOTRRoads.allRoads) {
					boolean hasSP = false;
					boolean hasSelf = false;
					if (hasWP(road.roadPoints, w) && hasWP(road.roadPoints, this)) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 541 */ public boolean isUnlockable(EntityPlayer entityplayer) {
		return (this.faction == LOTRFaction.UNALIGNED
				|| LOTRLevelData.getData(entityplayer).getAlignment(this.faction) >= 0.0F);
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 547 */ public boolean isHidden() {
		// System.out.println("Hidden: " + this.moc);
		// return this.isHidden;
		return !this.moc;
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/* 553 */ public int getID() {
		return ordinal();
	}

	/*     */
	/*     */
	/*     */
	/*     */ public static LOTRWaypoint waypointForName(String name) {
		/* 558 */ for (LOTRWaypoint wp : values()) {
			/*     */
			/* 560 */ if (wp.getCodeName().equals(name))
			/*     */ {
				/* 562 */ return wp;
				/*     */ }
			/*     */ }
		/* 565 */ return null;
		/*     */ }

	/*     */
	/*     */
	/*     */ public static List<LOTRAbstractWaypoint> listAllWaypoints() {
		/* 570 */ List<LOTRAbstractWaypoint> list = new ArrayList();
		/* 571 */ list.addAll(Arrays.asList(values()));
		/* 572 */ return list;
		/*     */ }

	/*     */
	/*     */
	/*     */ public static Region regionForName(String name) {
		/* 577 */ for (Region region : Region.values()) {
			/*     */
			/* 579 */ if (region.name().equals(name))
			/*     */ {
				/* 581 */ return region;
				/*     */ }
			/*     */ }
		/* 584 */ return null;
		/*     */ }

	/*     */
	/*     */
	/*     */ public static Region regionForID(int id) {
		/* 589 */ for (Region region : Region.values()) {
			/*     */
			/* 591 */ if (region.ordinal() == id)
			/*     */ {
				/* 593 */ return region;
				/*     */ }
			/*     */ }
		/* 596 */ return null;
		/*     */ }
	/*     */

}

/*
 * Location: C:\Users\tani\Desktop\minecraft-modding\LOTRMod
 * v35.3\!\lotr\common\world\map\LOTRWaypoint.class Java compiler version: 6
 * (50.0) JD-Core Version: 1.0.7
 */