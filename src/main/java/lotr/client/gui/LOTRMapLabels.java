/*     */ package lotr.client.gui;
/*     */ 
/*     */ import lotr.common.world.biome.LOTRBiome;
/*     */ import net.minecraft.util.StatCollector;
/*     */ 
/*     */ public enum LOTRMapLabels
/*     */ {
//	;
/*   8 */   //BELEGAER("belegaer", 250, 1100, 16.0F, 0, -6.0F, -1.0F),
			//AMERICA("america", )
/* 125 */   FOREST_TROLLS(LOTRBiome.halfTrollForest, 1740, 2530, 2.0F, 5, -2.0F, 1.5F);
/*     */   
/*     */   public final int posX;
/*     */   
/*     */   public final int posY;
/*     */   public final float scale;
/*     */   public final int angle;
/*     */   public final float minZoom;
/*     */   public final float maxZoom;
/*     */   private LOTRBiome biome;
/*     */   private String labelName;
/*     */   
/*     */   LOTRMapLabels(Object label, int x, int y, float f, int r, float z1, float z2) {
/* 138 */     this.posX = x;
/* 139 */     this.posY = y;
/* 140 */     this.scale = f;
/* 141 */     this.angle = r;
/* 142 */     this.minZoom = z1;
/* 143 */     this.maxZoom = z2;
/* 144 */     if (label instanceof LOTRBiome) {
/*     */       
/* 146 */       this.biome = (LOTRBiome)label;
/*     */     }
/*     */     else {
/*     */       
/* 150 */       this.labelName = (String)label;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayName() {
/* 156 */     if (this.labelName != null)
/*     */     {
/* 158 */       return StatCollector.translateToLocal("lotr.map." + this.labelName);
/*     */     }
/* 160 */     return this.biome.getBiomeDisplayName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 165 */   public static LOTRMapLabels[] allMapLabels() { return values(); }
/*     */ }


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\client\gui\LOTRMapLabels.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */