/*     */ package lotr.common;
/*     */ 
/*     */ import com.google.common.math.IntMath;
/*     */ import cpw.mods.fml.common.FMLLog;
/*     */ import java.awt.Color;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import lotr.common.network.LOTRPacketDate;
/*     */ import lotr.common.network.LOTRPacketHandler;
/*     */ import net.minecraft.entity.player.EntityPlayerMP;
/*     */ import net.minecraft.nbt.NBTTagCompound;
/*     */ import net.minecraft.server.MinecraftServer;
/*     */ import net.minecraft.util.StatCollector;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LOTRDate
/*     */ {
/*     */   public static void saveDates(NBTTagCompound levelData) {
/*  24 */     NBTTagCompound nbt = new NBTTagCompound();
/*  25 */     nbt.setInteger("ShireDate", ShireReckoning.currentDay);
/*  26 */     levelData.setTag("Dates", nbt);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void loadDates(NBTTagCompound levelData) {
/*  31 */     if (levelData.hasKey("Dates")) {
/*     */       
/*  33 */       NBTTagCompound nbt = levelData.getCompoundTag("Dates");
/*  34 */       ShireReckoning.currentDay = nbt.getInteger("ShireDate");
/*     */     }
/*     */     else {
/*     */       
/*  38 */       ShireReckoning.currentDay = 0;
/*     */     } 
/*     */   }
/*     */   
/*  42 */   private static int ticksInDay = LOTRTime.DAY_LENGTH;
/*  43 */   private static long prevWorldTime = -1L;
/*     */ 
/*     */ 
/*     */   
/*  47 */   public static void resetWorldTimeInMenu() { prevWorldTime = -1L; }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void update(World world) {
/*  52 */     if (!(world.getWorldInfo() instanceof lotr.common.world.LOTRWorldInfo)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  57 */     long worldTime = world.getWorldTime();
/*     */     
/*  59 */     if (prevWorldTime == -1L)
/*     */     {
/*  61 */       prevWorldTime = worldTime;
/*     */     }
/*     */     
/*  64 */     long prevDay = prevWorldTime / ticksInDay;
/*  65 */     long day = worldTime / ticksInDay;
/*  66 */     if (day != prevDay)
/*     */     {
/*  68 */       setDate(ShireReckoning.currentDay + 1);
/*     */     }
/*     */     
/*  71 */     prevWorldTime = worldTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setDate(int date) {
/*  76 */     ShireReckoning.currentDay = date;
/*  77 */     LOTRLevelData.markDirty();
/*     */     
/*  79 */     FMLLog.info("Updating LOTR day: " + ShireReckoning.getShireDate().getDateName(false), new Object[0]);
/*  80 */     for (Object obj : (MinecraftServer.getServer().getConfigurationManager()).playerEntityList) {
/*     */       
/*  82 */       EntityPlayerMP entityplayer = (EntityPlayerMP)obj;
/*  83 */       sendUpdatePacket(entityplayer, true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void sendUpdatePacket(EntityPlayerMP entityplayer, boolean update) {
/*  89 */     NBTTagCompound nbt = new NBTTagCompound();
/*  90 */     saveDates(nbt);
/*  91 */     LOTRPacketDate packet = new LOTRPacketDate(nbt, update);
/*  92 */     LOTRPacketHandler.networkWrapper.sendTo(packet, entityplayer);
/*     */   }
/*     */   
/*     */   public enum Season
/*     */   {
/*  97 */     SPRING("spring", 0, new float[] { 1.0F, 1.0F, 1.0F }),
/*  98 */     SUMMER("summer", 1, new float[] { 1.15F, 1.15F, 0.9F }),
/*  99 */     AUTUMN("autumn", 2, new float[] { 1.2F, 1.0F, 0.7F }),
/* 100 */     WINTER("winter", 3, new float[] { 1.0F, 0.8F, 0.8F }); public static Season[] allSeasons; private final String name; public final int seasonID; private final float[] grassRGB;
/*     */     static  {
/* 102 */       allSeasons = new Season[] { SPRING, SUMMER, AUTUMN, WINTER };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Season(String s, int i, float[] f) {
/* 110 */       this.name = s;
/* 111 */       this.seasonID = i;
/* 112 */       this.grassRGB = f;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 117 */     public String codeName() { return this.name; }
/*     */ 
/*     */ 
/*     */     
/*     */     public int transformColor(int color) {
/* 122 */       float[] rgb = (new Color(color)).getRGBColorComponents(null);
/* 123 */       float r = rgb[0];
/* 124 */       float g = rgb[1];
/* 125 */       float b = rgb[2];
/* 126 */       r = Math.min(r * this.grassRGB[0], 1.0F);
/* 127 */       g = Math.min(g * this.grassRGB[1], 1.0F);
/* 128 */       b = Math.min(b * this.grassRGB[2], 1.0F);
/* 129 */       return (new Color(r, g, b)).getRGB();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ShireReckoning
/*     */   {
/*     */     public enum Day
/*     */     {
/* 137 */       STERDAY("sterday"),
/* 138 */       SUNDAY("sunday"),
/* 139 */       MONDAY("monday"),
/* 140 */       TREWSDAY("trewsday"),
/* 141 */       HEVENSDAY("hevensday"),
/* 142 */       MERSDAY("mersday"),
/* 143 */       HIGHDAY("highday");
/*     */ 
/*     */       
/*     */       private String name;
/*     */ 
/*     */       
/* 149 */       Day(String s) { this.name = s; }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 154 */       public String getDayName() { return StatCollector.translateToLocal("lotr.date.shire.day." + this.name); }
/*     */     }
/*     */ 
/*     */     
/*     */     public enum Month
/*     */     {
/* 160 */       JAN("jan", 31, LOTRDate.Season.WINTER, false, false),
/* 161 */       FEB("feb", 28, LOTRDate.Season.WINTER, false, false),
/* 162 */       MAR("mar", 31, LOTRDate.Season.SPRING, false, false),
/* 163 */       APR("apr", 30, LOTRDate.Season.SPRING, false, false),
/* 164 */       MAY("may", 31, LOTRDate.Season.SPRING, false, false),
/* 165 */       JUN("jun", 30, LOTRDate.Season.SUMMER, false, false),
/* 166 */       JUL("jul", 31, LOTRDate.Season.SUMMER, false, false),
/* 167 */       AUG("aug", 31, LOTRDate.Season.SUMMER, false, false),
/* 168 */       SEP("sep", 30, LOTRDate.Season.AUTUMN, false, false),
/* 169 */       OCT("oct", 31, LOTRDate.Season.AUTUMN, false, true),
/* 170 */       NOV("nov", 30, LOTRDate.Season.AUTUMN, false, false),
/* 171 */       DEC("dec", 31, LOTRDate.Season.WINTER, false, false);
/*     */ 
/*     */       
/*     */       private String name;
/*     */       
/*     */       public int days;
/*     */       
/*     */       public boolean hasWeekdayName;
/*     */       
/*     */       public boolean isLeapYear;
/*     */       
/*     */       public LOTRDate.Season season;
/*     */ 
/*     */       
/*     */       Month(String s, int i, LOTRDate.Season se, boolean flag, boolean flag1) {
/* 192 */         this.name = s;
/* 193 */         this.days = i;
/* 194 */         this.hasWeekdayName = flag;
/* 195 */         this.isLeapYear = flag1;
/* 196 */         this.season = se;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 201 */       public String getMonthName() { return StatCollector.translateToLocal("lotr.date.shire.month." + this.name); }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 206 */       public boolean isSingleDay() { return (this.days == 1); }
/*     */     }
/*     */ 
/*     */     
/*     */     public static class Date
/*     */     {
/*     */       public final int year;
/*     */       
/*     */       public final LOTRDate.ShireReckoning.Month month;
/*     */       public final int monthDate;
/*     */       private LOTRDate.ShireReckoning.Day day;
/*     */       
/*     */       public Date(int y, LOTRDate.ShireReckoning.Month m, int d) {
/* 219 */         this.year = y;
/* 220 */         this.month = m;
/* 221 */         this.monthDate = d;
/*     */       }
/*     */ 
/*     */       
/*     */       public String getDateName(boolean longName) {
/* 226 */         String[] dayYear = getDayAndYearNames(longName);
/* 227 */         return dayYear[0] + ", " + dayYear[1];
/*     */       }
/*     */ 
/*     */       
/*     */       public String[] getDayAndYearNames(boolean longName) {
/* 232 */         StringBuilder builder = new StringBuilder();
/* 233 */         if (this.month.hasWeekdayName)
/*     */         {
/* 235 */           builder.append(getDay().getDayName());
/*     */         }
/* 237 */         builder.append(" ");
/* 238 */         if (!this.month.isSingleDay())
/*     */         {
/* 240 */           builder.append(this.monthDate);
/*     */         }
/* 242 */         builder.append(" ");
/* 243 */         builder.append(this.month.getMonthName());
/* 244 */         String dateName = builder.toString();
/*     */         
/* 246 */         builder = new StringBuilder();
/* 247 */         if (longName) {
/*     */           
/* 249 */           builder.append(StatCollector.translateToLocal("lotr.date.shire.long"));
/*     */         }
/*     */         else {
/*     */           
/* 253 */           builder.append(StatCollector.translateToLocal("lotr.date.shire"));
/*     */         } 
/* 255 */         builder.append(" ");
/* 256 */         builder.append(this.year);
/* 257 */         String yearName = builder.toString();
/*     */         
/* 259 */         return new String[] { dateName, yearName };
/*     */       }
/*     */ 
/*     */       
/*     */       public LOTRDate.ShireReckoning.Day getDay() {
/* 264 */         if (!this.month.hasWeekdayName)
/*     */         {
/* 266 */           return null;
/*     */         }
/*     */ 
/*     */         
/* 270 */         if (this.day == null) {
/*     */           
/* 272 */           int yearDay = 0;
/*     */           
/* 274 */           int monthID = this.month.ordinal();
/* 275 */           for (int i = 0; i < monthID; i++) {
/*     */             
/* 277 */             LOTRDate.ShireReckoning.Month m = LOTRDate.ShireReckoning.Month.values()[i];
/* 278 */             if (m.hasWeekdayName)
/*     */             {
/* 280 */               yearDay += m.days;
/*     */             }
/*     */           } 
/*     */           
/* 284 */           yearDay += this.monthDate;
/*     */           
/* 286 */           int dayID = IntMath.mod(yearDay - 1, LOTRDate.ShireReckoning.Day.values().length);
/* 287 */           this.day = LOTRDate.ShireReckoning.Day.values()[dayID];
/*     */         } 
/* 289 */         return this.day;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 295 */       public Date copy() { return new Date(this.year, this.month, this.monthDate); }
/*     */ 
/*     */ 
/*     */       
/*     */       public Date increment() {
/* 300 */         int newYear = this.year;
/* 301 */         LOTRDate.ShireReckoning.Month newMonth = this.month;
/* 302 */         int newDate = this.monthDate;
/*     */         
/* 304 */         newDate++;
/* 305 */         if (newDate > newMonth.days) {
/*     */           
/* 307 */           newDate = 1;
/*     */           
/* 309 */           int monthID = newMonth.ordinal();
/* 310 */           monthID++;
/*     */           
/* 312 */           if (monthID >= LOTRDate.ShireReckoning.Month.values().length) {
/*     */             
/* 314 */             monthID = 0;
/* 315 */             newYear++;
/*     */           } 
/* 317 */           newMonth = LOTRDate.ShireReckoning.Month.values()[monthID];
/*     */           
/* 319 */           if (newMonth.isLeapYear && !LOTRDate.ShireReckoning.isLeapYear(newYear)) {
/*     */             
/* 321 */             monthID++;
/* 322 */             newMonth = LOTRDate.ShireReckoning.Month.values()[monthID];
/*     */           } 
/*     */         } 
/*     */         
/* 326 */         return new Date(newYear, newMonth, newDate);
/*     */       }
/*     */ 
/*     */       
/*     */       public Date decrement() {
/* 331 */         int newYear = this.year;
/* 332 */         LOTRDate.ShireReckoning.Month newMonth = this.month;
/* 333 */         int newDate = this.monthDate;
/*     */         
/* 335 */         newDate--;
/* 336 */         if (newDate < 0) {
/*     */           
/* 338 */           int monthID = newMonth.ordinal();
/* 339 */           monthID--;
/*     */           
/* 341 */           if (monthID < 0) {
/*     */             
/* 343 */             monthID = LOTRDate.ShireReckoning.Month.values().length - 1;
/* 344 */             newYear--;
/*     */           } 
/* 346 */           newMonth = LOTRDate.ShireReckoning.Month.values()[monthID];
/*     */           
/* 348 */           if (newMonth.isLeapYear && !LOTRDate.ShireReckoning.isLeapYear(newYear)) {
/*     */             
/* 350 */             monthID--;
/* 351 */             newMonth = LOTRDate.ShireReckoning.Month.values()[monthID];
/*     */           } 
/*     */           
/* 354 */           newDate = newMonth.days;
/*     */         } 
/*     */         
/* 357 */         return new Date(newYear, newMonth, newDate);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 363 */     public static boolean isLeapYear(int year) { return (year % 4 == 0 && year % 100 != 0); }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 368 */     public static Date getShireDate() { return getShireDate(currentDay); }
/*     */ 
/*     */ 
/*     */     
/*     */     public static Date getShireDate(int day) {
/* 373 */       Date date = (Date)cachedDates.get(Integer.valueOf(day));
/* 374 */       if (date == null) {
/*     */         
/* 376 */         date = startDate.copy();
/* 377 */         if (day < 0) {
/*     */           
/* 379 */           for (int i = 0; i < -day; i++)
/*     */           {
/* 381 */             date = date.decrement();
/*     */           
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 387 */           for (int i = 0; i < day; i++)
/*     */           {
/* 389 */             date = date.increment();
/*     */           }
/*     */         } 
/* 392 */         cachedDates.put(Integer.valueOf(day), date);
/*     */       } 
/* 394 */       return date;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 399 */     public static LOTRDate.Season getSeason() { return (getShireDate()).month.season; }
/*     */ 
/*     */     
/* 402 */     public static Date startDate = new Date(1871, Month.JUL, 4);
/* 403 */     public static int currentDay = 0;
/* 404 */     private static Map<Integer, Date> cachedDates = new HashMap();
/*     */   }
/*     */   
/* 407 */   public static int SECOND_AGE_LENGTH = 3441; 
public static class Date {
/* 408 */     public final int year; 
public final LOTRDate.ShireReckoning.Month month; 
public final int monthDate; 
private LOTRDate.ShireReckoning.Day day; 
public Date(int y, LOTRDate.ShireReckoning.Month m, int d) { 
	this.year = y; 
	this.month = m; 
	this.monthDate = d; 
	} 
public String getDateName(boolean longName) { 
	String[] dayYear = getDayAndYearNames(longName);
	return dayYear[0] + ", " + dayYear[1]; 
	} 
public String[] getDayAndYearNames(boolean longName) { 
	StringBuilder builder = new StringBuilder(); 
	if (this.month.hasWeekdayName) builder.append(getDay().getDayName());  
	builder.append(" "); 
	if (!this.month.isSingleDay()) builder.append(this.monthDate);  
	builder.append(" "); 
	builder.append(this.month.getMonthName()); 
	String dateName = builder.toString(); 
	builder = new StringBuilder(); 
	if (longName) { 
		builder.append(StatCollector.translateToLocal("lotr.date.shire.long")); 
		} else { 
			builder.append(StatCollector.translateToLocal("lotr.date.shire")); } 
	builder.append(" "); 
	builder.append(this.year); String yearName = builder.toString(); return new String[] { dateName, yearName }; } 
public LOTRDate.ShireReckoning.Day getDay() { if (!this.month.hasWeekdayName) return null;  if (this.day == null) { 
	int yearDay = 0; int monthID = this.month.ordinal(); for (int i = 0; i < monthID; i++) { LOTRDate.ShireReckoning.Month m = LOTRDate.ShireReckoning.Month.values()[i]; if (m.hasWeekdayName) yearDay += m.days;  }  yearDay += this.monthDate; int dayID = IntMath.mod(yearDay - 1, LOTRDate.ShireReckoning.Day.values().length); this.day = LOTRDate.ShireReckoning.Day.values()[dayID]; }  return this.day; } public Date copy() { return new Date(this.year, this.month, this.monthDate); } public Date increment() { int newYear = this.year; LOTRDate.ShireReckoning.Month newMonth = this.month; int newDate = this.monthDate; newDate++; if (newDate > newMonth.days) { newDate = 1; int monthID = newMonth.ordinal(); monthID++; if (monthID >= LOTRDate.ShireReckoning.Month.values().length) { monthID = 0; newYear++; }  newMonth = LOTRDate.ShireReckoning.Month.values()[monthID]; if (newMonth.isLeapYear && !LOTRDate.ShireReckoning.isLeapYear(newYear)) { monthID++; newMonth = LOTRDate.ShireReckoning.Month.values()[monthID]; }  }  return new Date(newYear, newMonth, newDate); } public Date decrement() { int newYear = this.year; LOTRDate.ShireReckoning.Month newMonth = this.month; int newDate = this.monthDate; newDate--; if (newDate < 0) { int monthID = newMonth.ordinal(); monthID--; if (monthID < 0) { monthID = LOTRDate.ShireReckoning.Month.values().length - 1; newYear--; }  newMonth = LOTRDate.ShireReckoning.Month.values()[monthID]; if (newMonth.isLeapYear && !LOTRDate.ShireReckoning.isLeapYear(newYear)) { monthID--; newMonth = LOTRDate.ShireReckoning.Month.values()[monthID]; }  newDate = newMonth.days; }  return new Date(newYear, newMonth, newDate); } } 
			public static int THIRD_AGE_LENGTH = 3021;
/* 409 */   public static int SR_TO_TA = 1871; //1600
/* 410 */   public static int THIRD_AGE_CURRENT = ShireReckoning.startDate.year + SR_TO_TA;
/*     */ }


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\common\LOTRDate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */