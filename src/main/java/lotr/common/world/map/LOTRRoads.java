package lotr.common.world.map;

import cpw.mods.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.tuple.Pair;



public class LOTRRoads
{
	public static List<LOTRRoads> allRoads = new ArrayList();
	private static RoadPointDatabase roadPointDatabase = new RoadPointDatabase(); public RoadPoint[] roadPoints;
	public List<RoadPoint> endpoints;
	private String roadName;

	private static class RoadPointDatabase { 
		private Map<Pair<Integer, Integer>, List<LOTRRoads.RoadPoint>> pointMap = new HashMap();

		private static final int COORD_LOOKUP_SIZE = 1000;

		public List<LOTRRoads.RoadPoint> getPointsForCoords(int x, int z) {
			int x1 = x / 1000;
			int z1 = z / 1000;
			return getRoadList(x1, z1, false);
		}


		public void add(LOTRRoads.RoadPoint point) {
			int x = (int)Math.round(point.x / 1000.0D);
			int z = (int)Math.round(point.z / 1000.0D);


			int overlap = 1;
			for (int i = -overlap; i <= overlap; i++) {

				for (int k = -overlap; k <= overlap; k++) {

					int xKey = x + i;
					int zKey = z + k;

					getRoadList(xKey, zKey, true).add(point);
				} 
			} 
		}

		private List<LOTRRoads.RoadPoint> getRoadList(int xKey, int zKey, boolean addToMap) {
			Pair<Integer, Integer> key = Pair.of(Integer.valueOf(xKey), Integer.valueOf(zKey));
			List<LOTRRoads.RoadPoint> list = (List)this.pointMap.get(key);
			if (list == null) {

				list = new ArrayList<LOTRRoads.RoadPoint>();
				if (addToMap)
				{
					this.pointMap.put(key, list);
				}
			} 
			return list;
		}

		private RoadPointDatabase() {} }

	private LOTRRoads(String name, RoadPoint... ends) { 
		this.endpoints = new ArrayList();
		this.roadName = name;
		for (RoadPoint e : ends)
		{
			this.endpoints.add(e);
		} 
	}


	public String getDisplayName() { return StatCollector.translateToLocal("lotr.road." + this.roadName); }
	public static void createRoads() { 
		FMLLog.info("LOTRRoads: Creating roads", new Object[0]); 
		long time = System.nanoTime(); 
		allRoads.clear(); 
		roadPointDatabase = new RoadPointDatabase(); 

		/*
		 * registerRoad("Test", new Object[] { LOTRWaypoint.BOSTON, LOTRWaypoint.NYC });
		 * //registerRoad("FR1", new Object[] { LOTRWaypoint.PARIS, LOTRWaypoint.CAMBRAI
		 * }); //registerRoad("FR2", new Object[] { LOTRWaypoint.CAMBRAI,
		 * LOTRWaypoint.CALAIS}); registerRoad("FR3", new Object[] { LOTRWaypoint.PARIS,
		 * LOTRWaypoint.CAMBRAI, LOTRWaypoint.CALAIS}); int[] toulouseMarseille=
		 * {700,547}; int[]crs={707,542}; registerRoad("FR4", new Object[] {
		 * LOTRWaypoint.BARCELONA, toulouseMarseille, crs, LOTRWaypoint.MARSEILLE});
		 * registerRoad("FR5", new Object[] { crs, LOTRWaypoint.DIJON,
		 * LOTRWaypoint.PARIS}); registerRoad("FR5", new Object[] { toulouseMarseille,
		 * LOTRWaypoint.TOULOUSE, LOTRWaypoint.PARIS}); registerRoad("FR7", new Object[]
		 * { LOTRWaypoint.TOULOUSE, LOTRWaypoint.BORDEAUX}); int[] test8 = {686,532};
		 * registerRoad("FR6", new Object[] { LOTRWaypoint.BAYONNE,
		 * LOTRWaypoint.BORDEAUX, test8, LOTRWaypoint.ROCHELLE, LOTRWaypoint.PARIS});
		 * int[] test = {690,558}; registerRoad("SP1", new Object[] {
		 * LOTRWaypoint.BARCELONA, test, LOTRWaypoint.VALENCIA}); int[]test2 =
		 * {679,547}; int[]test3 = {688,555}; registerRoad("SP2", new Object[] {
		 * LOTRWaypoint.BARCELONA, test3, LOTRWaypoint.SARAGOSSA, test2}); int[]test4 =
		 * {670,554};
		 * 
		 * registerRoad("SP3", new Object[] { LOTRWaypoint.BURGOS,LOTRWaypoint.MADRID});
		 * int[]test5={681,556}; int[]test6={685,567}; int[]test7={679,566};
		 * registerRoad("SP4", new Object[] { LOTRWaypoint.BAYONNE, test2,
		 * LOTRWaypoint.BURGOS, test4, LOTRWaypoint.MADRID}); int[]test10={654,554};
		 * int[]test11={653,564}; registerRoad("SP7", new Object[] { test4,
		 * LOTRWaypoint.CORUNNA, test10, test11, LOTRWaypoint.LISBON,
		 * LOTRWaypoint.BADAJOZ, LOTRWaypoint.MADRID}); registerRoad("SP5", new Object[]
		 * { LOTRWaypoint.SARAGOSSA,test5,LOTRWaypoint.MADRID}); registerRoad("SP6", new
		 * Object[] { LOTRWaypoint.VALENCIA, test6, test7, LOTRWaypoint.MADRID});
		 * registerRoad("SP7", new Object[] { LOTRWaypoint.BADAJOZ,
		 * LOTRWaypoint.SEVILLE, LOTRWaypoint.CADIZ, LOTRWaypoint.GIBRALTER});
		 * 
		 * int[]cacrossroad= {684,571}; int[]test13 = {678,576}; registerRoad("SP6", new
		 * Object[] { LOTRWaypoint.CARTAGENA, cacrossroad, LOTRWaypoint.MADRID});
		 * registerRoad("SP6", new Object[] { cacrossroad, LOTRWaypoint.VALENCIA});
		 * registerRoad("SP6", new Object[] { test13, LOTRWaypoint.CARTAGENA});
		 * int[]test12={671,575}; registerRoad("SP6", new Object[] { test12,
		 * LOTRWaypoint.GIBRALTER}); registerRoad("SP6", new Object[] { test12,
		 * test13}); registerRoad("SP6", new Object[] { LOTRWaypoint.SEVILLE, test12,
		 * LOTRWaypoint.CORDOVA, LOTRWaypoint.MADRID});
		 */
		long newTime = System.nanoTime(); 
		int roads = allRoads.size(); 
		int points = 0; 
		int dbEntries = 0; 
		int dbPoints = 0; 
		for (LOTRRoads road : allRoads) {
			points += road.roadPoints.length;  
		}
		for (Map.Entry<Pair<Integer, Integer>, List<RoadPoint>> e : roadPointDatabase.pointMap.entrySet()) { 
			dbEntries++; 
			dbPoints += ((List)e.getValue()).size(); 
		}  
		//FMLLog.info("LOTRRoads: Created roads in " + ((newTime - time) / 1.0E9D) + "s", new Object[0]); 
		FMLLog.info("LOTRRoads: roads=" + roads + ", points=" + points + ", dbEntries=" + dbEntries + ", dbPoints=" + dbPoints, new Object[0]); 
	} 



	public static boolean isRoadAt(int x, int z) { 
		return (isRoadNear(x, z, 4) >= 0.0F); 
	} 

	public static void registerRoad(String name, Object... waypoints) { 
		List<RoadPoint> points = new ArrayList<RoadPoint>();
		List<LOTRWaypoint> wps = new ArrayList<LOTRWaypoint>();
		for (Object obj : waypoints) {

			if (obj instanceof LOTRWaypoint) {
				
				LOTRWaypoint wp = (LOTRWaypoint)obj;
				wps.add(wp);
				//System.out.println("Adding waypoint " + wp.getDisplayName() + " to road " + name);
				points.add(new RoadPoint(wp.getXCoord(), wp.getZCoord(), true, wp));
			}
			else if (obj instanceof int[]) {
				int[] coords = (int[])obj;
				if (coords.length == 2){
					RoadPoint p = new RoadPoint(LOTRWaypoint.mapToWorldX(coords[0]), LOTRWaypoint.mapToWorldZ(coords[1]), false);
					//System.out.println("Adding new RP for road " + name + " x" + p.x + " z"+p.z);
					points.add(p);
				}else{
					throw new IllegalArgumentException("Coords length must be 2!");
				}
			} else {
				throw new IllegalArgumentException("Wrong road parameter!");
			} 
		} 
		//System.out.println("Got this far on " + name);
		RoadPoint[] array = (RoadPoint[])points.toArray(new RoadPoint[0]);
		LOTRRoads[] roads = BezierCurves.getSplines(name, array); 
		//System.out.println("allroads size: "  + allRoads.size());
		allRoads.addAll(Arrays.asList(roads)); 
	} 

	public static float isRoadNear(int x, int z, int width) { 
		double widthSq = (width * width); float leastSqRatio = -1.0F; 
		List<RoadPoint> points = roadPointDatabase.getPointsForCoords(x, z);
		for (RoadPoint point : points) { 
			double dx = point.x - x; 
			double dz = point.z - z; 
			double distSq = dx * dx + dz * dz; 
			if (distSq < widthSq) { 
				float f = (float)(distSq / widthSq); 
				if (leastSqRatio == -1.0F) { 
					leastSqRatio = f; 
					continue; 
				}  
				if (f < leastSqRatio) leastSqRatio = f;  
			}  
		}  
		return leastSqRatio; 
	} 
	public static class RoadPoint {
		public final double x; 
		public final double z; 
		public final boolean isWaypoint; 
		public final LOTRWaypoint wp;
		public RoadPoint(double i, double j, boolean flag) { 
			this.x = i; 
			this.z = j; 
			this.isWaypoint = flag; 
			wp = null;
		} 
		
		public RoadPoint(double i, double j, boolean flag, LOTRWaypoint w) { 
			this.x = i; 
			this.z = j; 
			this.isWaypoint = flag; 
			this.wp =w;
		} 
	}
	private static class BezierCurves { 
		private static int roadLengthFactor = 1;


		private static LOTRRoads[] getSplines(String name, RoadPoint[] waypoints) {
			
			if (waypoints.length == 2) {

				LOTRRoads.RoadPoint p1 = waypoints[0];
				LOTRRoads.RoadPoint p2 = waypoints[1];
				LOTRRoads road = new LOTRRoads(name, new LOTRRoads.RoadPoint[] { p1, p2 });

				double dx = p2.x - p1.x;
				double dz = p2.z - p1.z;
				int roadLength = (int)Math.round(Math.sqrt(dx * dx + dz * dz));
				int points = roadLength * roadLengthFactor;
				road.roadPoints = new LOTRRoads.RoadPoint[points];
				//System.out.println("Points for road " + name + ": " + points);
				for (int l = 0; l < points; l++) {

					double t = (double)l / (double)points;
					LOTRRoads.RoadPoint point = bezier(p1, p1, p2, p2, t);
					road.roadPoints[l] = point;
					roadPointDatabase.add(point);
				} 
				//System.out.println("RoadPoints for road " + name + ": " + road.roadPoints.length);
				return new LOTRRoads[] { road };
			} 


			int length = waypoints.length;
			double[] x = new double[length];
			double[] z = new double[length];
			for (int i = 0; i < length; i++) {

				x[i] = (waypoints[i]).x;
				z[i] = (waypoints[i]).z;
			} 

			double[][] controlX = getControlPoints(x);
			double[][] controlZ = getControlPoints(z);
			int controlPoints = controlX[0].length;
			//System.out.println("CP for road " + name + ": " + controlPoints);
			LOTRRoads.RoadPoint[] arrayOfRoadPoint1 = new LOTRRoads.RoadPoint[controlPoints];
			LOTRRoads.RoadPoint[] arrayOfRoadPoint2 = new LOTRRoads.RoadPoint[controlPoints];
			for (int i = 0; i < controlPoints; i++) {

				LOTRRoads.RoadPoint p1 = new LOTRRoads.RoadPoint(controlX[0][i], controlZ[0][i], false);
				LOTRRoads.RoadPoint p2 = new LOTRRoads.RoadPoint(controlX[1][i], controlZ[1][i], false);
				arrayOfRoadPoint1[i] = p1;
				arrayOfRoadPoint2[i] = p2;
			} 

			LOTRRoads[] roads = new LOTRRoads[length - 1];
			for (int i = 0; i < roads.length; i++) {

				LOTRRoads.RoadPoint p1 = waypoints[i];
				LOTRRoads.RoadPoint p2 = waypoints[i + 1];
				LOTRRoads.RoadPoint cp1 = arrayOfRoadPoint1[i];
				LOTRRoads.RoadPoint cp2 = arrayOfRoadPoint2[i];

				LOTRRoads road = new LOTRRoads(name, new LOTRRoads.RoadPoint[] { p1, p2 });
				roads[i] = road;

				double dx = p2.x - p1.x;
				double dz = p2.z - p1.z;
				int roadLength = (int)Math.round(Math.sqrt(dx * dx + dz * dz));
				int points = roadLength * roadLengthFactor;
				road.roadPoints = new LOTRRoads.RoadPoint[points];
				//System.out.println("Points for road " + name + ": " + points);
				for (int l = 0; l < points; l++) {
					double t = (double)l / (double)points;
					LOTRRoads.RoadPoint point = bezier(p1, cp1, cp2, p2, t);
					//LOTRRoads.RoadPoint point = bezier(p1, cp1, p2, cp2, t);
					//System.out.println("point " + l + " t " +t);
					road.roadPoints[l] = point;
					roadPointDatabase.add(point);
				} 
			} 

			return roads;
		}



		private static LOTRRoads.RoadPoint lerp(LOTRRoads.RoadPoint a, LOTRRoads.RoadPoint b, double t) {
			double x = a.x + (b.x - a.x) * t;
			double z = a.z + (b.z - a.z) * t;
			return new LOTRRoads.RoadPoint(x, z, false);
		}


		private static LOTRRoads.RoadPoint bezier(LOTRRoads.RoadPoint a, LOTRRoads.RoadPoint b, LOTRRoads.RoadPoint c, LOTRRoads.RoadPoint d, double t) {
			LOTRRoads.RoadPoint ab = lerp(a, b, t);
			LOTRRoads.RoadPoint bc = lerp(b, c, t);
			LOTRRoads.RoadPoint cd = lerp(c, d, t);
			LOTRRoads.RoadPoint abbc = lerp(ab, bc, t);
			LOTRRoads.RoadPoint bccd = lerp(bc, cd, t);
			return lerp(abbc, bccd, t);
		}


		private static double[][] getControlPoints(double[] src) {
			int length = src.length - 1;
			/* 345 */       double[] p1 = new double[length];
			/* 346 */       double[] p2 = new double[length];

			/* 348 */       double[] a = new double[length];
			/* 349 */       double[] b = new double[length];
			/* 350 */       double[] c = new double[length];
			/* 351 */       double[] r = new double[length];

			/* 353 */       a[0] = 0.0D;
			/* 354 */       b[0] = 2.0D;
			/* 355 */       c[0] = 1.0D;
			/* 356 */       r[0] = src[0] + 2.0D * src[1];

			/* 358 */       for (int i = 1; i < length - 1; i++) {

				/* 360 */         a[i] = 1.0D;
				/* 361 */         b[i] = 4.0D;
				/* 362 */         c[i] = 1.0D;
				/* 363 */         r[i] = 4.0D * src[i] + 2.0D * src[i + 1];
			} 

			/* 366 */       a[length - 1] = 2.0D;
			/* 367 */       b[length - 1] = 7.0D;
			/* 368 */       c[length - 1] = 0.0D;
			/* 369 */       r[length - 1] = 8.0D * src[length - 1] + src[length];

			/* 371 */       for (int i = 1; i < length; i++) {

				/* 373 */         double m = a[i] / b[i - 1];
				/* 374 */         b[i] = b[i] - m * c[i - 1];
				/* 375 */         r[i] = r[i] - m * r[i - 1];
			} 

			/* 378 */       p1[length - 1] = r[length - 1] / b[length - 1];
			/* 379 */       for (int i = length - 2; i >= 0; i--) {

				/* 381 */         double p = (r[i] - c[i] * p1[i + 1]) / b[i];
				/* 382 */         p1[i] = p;
			} 

			/* 385 */       for (int i = 0; i < length - 1; i++)
			{
				/* 387 */         p2[i] = 2.0D * src[i + 1] - p1[i + 1];
			}

			/* 390 */       p2[length - 1] = 0.5D * (src[length] + p1[length - 1]);

			/* 392 */       return new double[][] { p1, p2 };
		} }

}


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\common\world\map\LOTRRoads.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */