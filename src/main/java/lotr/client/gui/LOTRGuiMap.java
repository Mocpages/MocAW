/*      */ package lotr.client.gui;
import java.awt.Color;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
import java.util.Set;
/*      */ import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
/*      */ import org.lwjgl.input.Keyboard;
/*      */ import org.lwjgl.input.Mouse;
/*      */ import org.lwjgl.opengl.GL11;

import com.google.common.math.IntMath;
/*      */ 
/*      */ import com.mojang.authlib.GameProfile;
/*      */ import com.mojang.authlib.minecraft.MinecraftProfileTexture;

/*      */ import lotr.client.LOTRClientProxy;
/*      */ import lotr.client.LOTRKeyHandler;
/*      */ import lotr.client.LOTRTextures;
import lotr.client.LOTRTickHandlerClient;
/*      */ import lotr.common.LOTRConfig;
/*      */ import lotr.common.LOTRDimension;
/*      */ import lotr.common.LOTRLevelData;
import lotr.common.LOTRMod;
/*      */ import lotr.common.LOTRPlayerData;
import lotr.common.fac.LOTRAlignmentValues;
/*      */ import lotr.common.fac.LOTRControlZone;
/*      */ import lotr.common.fac.LOTRFaction;
/*      */ import lotr.common.fac.LOTRFactionRank;
/*      */ import lotr.common.fellowship.LOTRFellowshipClient;
/*      */ import lotr.common.network.LOTRPacketCWPSharedHide;
/*      */ import lotr.common.network.LOTRPacketClientMQEvent;
/*      */ import lotr.common.network.LOTRPacketConquestGridRequest;
/*      */ import lotr.common.network.LOTRPacketCreateCWP;
import lotr.common.network.LOTRPacketDeleteCWP;
/*      */ import lotr.common.network.LOTRPacketFastTravel;
/*      */ import lotr.common.network.LOTRPacketHandler;
/*      */ import lotr.common.network.LOTRPacketIsOpRequest;
import lotr.common.network.LOTRPacketMapTp;
/*      */ import lotr.common.network.LOTRPacketRenameCWP;
/*      */ import lotr.common.network.LOTRPacketShareCWP;
/*      */ import lotr.common.quest.LOTRMiniQuest;
import lotr.common.world.biome.LOTRBiome;
/*      */ import lotr.common.world.genlayer.LOTRGenLayerWorld;
/*      */ import lotr.common.world.map.LOTRAbstractWaypoint;
/*      */ import lotr.common.world.map.LOTRConquestGrid;
/*      */ import lotr.common.world.map.LOTRConquestZone;
/*      */ import lotr.common.world.map.LOTRCustomWaypoint;
import lotr.common.world.map.LOTRFixedStructures;
/*      */ import lotr.common.world.map.LOTRRoads;
import lotr.common.world.map.LOTRWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
/*      */ import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
/*      */ import net.minecraft.client.gui.ScaledResolution;
/*      */ import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
/*      */ import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
/*      */ import net.minecraft.util.MathHelper;
/*      */ import net.minecraft.util.ResourceLocation;
/*      */ import net.minecraft.util.StatCollector;
/*      */ 
/*      */ public class LOTRGuiMap extends LOTRGuiMenuBase {
/*      */   private static class PlayerLocationInfo { public GameProfile profile;
/*      */     
/*      */     public PlayerLocationInfo(GameProfile p, double x, double z) {
/*   54 */       this.profile = p;
/*   55 */       this.posX = x;
/*   56 */       this.posZ = z;
/*      */     }
/*      */     public double posX;
/*      */     public double posZ; }
/*   60 */   private static Map<UUID, PlayerLocationInfo> playerLocations = new HashMap();
/*      */ 
/*      */   
/*      */   public static void addPlayerLocationInfo(GameProfile player, double x, double z) {
/*   64 */     if (player.isComplete())
/*      */     {
/*   66 */       playerLocations.put(player.getId(), new PlayerLocationInfo(player, x, z));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*   72 */   public static void clearPlayerLocations() { playerLocations.clear(); }
/*      */ 
/*      */   
/*   75 */   public static final ResourceLocation mapIconsTexture = new ResourceLocation("lotr:map/mapScreen.png");
/*   76 */   public static final ResourceLocation conquestTexture = new ResourceLocation("lotr:map/conquest.png");
/*   77 */   private static final ItemStack questBookIcon = new ItemStack(LOTRMod.redBook);
/*      */   
/*      */   public static final int BLACK = -16777216;
/*      */   
/*      */   public static final int BORDER_COLOR = -6156032;
/*      */   
/*      */   private static final int MIN_ZOOM = -3;
/*      */   private static final int MAX_ZOOM = 4;
/*      */   private static final int mapBorder = 30;
/*      */   private static boolean fullscreen = true;
/*      */   private static int mapWidth;
/*      */   private static int mapHeight;
/*      */   private static int mapXMin;
/*      */   private static int mapXMax;
/*      */   private static int mapYMin;
/*      */   private static int mapYMax;
/*      */   private static int mapXMin_W;
/*      */   private static int mapXMax_W;
/*      */   private static int mapYMin_W;
/*      */   private static int mapYMax_W;
/*   97 */   private static List<LOTRGuiMapWidget> mapWidgets = new ArrayList();
/*      */   
/*      */   private LOTRGuiMapWidget widgetAddCWP;
/*      */   
/*      */   private LOTRGuiMapWidget widgetDelCWP;
/*      */   private LOTRGuiMapWidget widgetRenameCWP;
/*      */   private LOTRGuiMapWidget widgetToggleWPs;
/*      */   private LOTRGuiMapWidget widgetToggleCWPs;
/*      */   private LOTRGuiMapWidget widgetToggleHiddenSWPs;
/*      */   private LOTRGuiMapWidget widgetZoomIn;
/*      */   private LOTRGuiMapWidget widgetZoomOut;
/*      */   private LOTRGuiMapWidget widgetFullScreen;
/*      */   private LOTRGuiMapWidget widgetSepia;
/*      */   private LOTRGuiMapWidget widgetLabels;
/*      */   private LOTRGuiMapWidget widgetShareCWP;
/*      */   private LOTRGuiMapWidget widgetHideSWP;
/*      */   private LOTRGuiMapWidget widgetUnhideSWP;
/*      */   private float posX;
/*      */   private float posY;
/*      */   private int isMouseButtonDown;
/*      */   private int prevMouseX;
/*      */   private int prevMouseY;
/*      */   private boolean isMouseWithinMap;
/*      */   private int mouseXCoord;
/*      */   private int mouseZCoord;
/*      */   private float posXMove;
/*      */   private float posYMove;
/*      */   private float prevPosX;
/*      */   private float prevPosY;
/*  126 */   private static int zoomPower = 0;
/*  127 */   private int prevZoomPower = zoomPower;
/*      */   private float zoomScale;
/*      */   private float zoomScaleStable;
/*      */   private float zoomExp;
/*  131 */   private static int zoomTicksMax = 6;
/*      */   
/*      */   private int zoomTicks;
/*      */   
/*      */   public boolean enableZoomOutWPFading = true;
/*      */   private LOTRAbstractWaypoint selectedWaypoint;
/*      */   private static final int waypointSelectRange = 5;
/*      */   public static boolean showWP = true;
/*      */   public static boolean showCWP = true;
/*      */   public static boolean showHiddenSWP = false;
/*      */   private boolean hasOverlay;
/*      */   private String[] overlayLines;
/*      */   private GuiButton buttonOverlayFunction;
/*      */   private GuiTextField nameWPTextField;
/*      */   private boolean creatingWaypoint;
/*      */   private boolean creatingWaypointNew;
/*      */   private boolean deletingWaypoint;
/*      */   private boolean renamingWaypoint;
/*      */   private boolean sharingWaypoint;
/*      */   private boolean sharingWaypointNew;
/*      */   private LOTRGuiFellowships fellowshipDrawGUI;
/*      */   private LOTRFellowshipClient mouseOverRemoveSharedFellowship;
/*  153 */   private LOTRGuiScrollPane scrollPaneWPShares = new LOTRGuiScrollPane(9, 8);
/*      */   
/*      */   private List<UUID> displayedWPShareList;
/*      */   
/*      */   private static int maxDisplayedWPShares;
/*      */   
/*      */   private int displayedWPShares;
/*      */   
/*      */   public boolean isPlayerOp = false;
/*      */   private int tickCounter;
/*      */   private boolean hasControlZones = false;
/*      */   private LOTRFaction controlZoneFaction;
/*      */   private boolean mouseControlZone;
/*      */   private boolean mouseControlZoneReduced;
/*      */   private boolean isConquestGrid = false;
/*      */   private static final int conqBorderW = 8;
/*      */   private static final int conqBorderUp = 22;
/*      */   private static final int conqBorderDown = 54;
/*      */   private boolean loadingConquestGrid = false;
/*  172 */   private Map<LOTRFaction, List<LOTRConquestZone>> facConquestGrids = new HashMap();
/*  173 */   private Set<LOTRFaction> requestedFacGrids = new HashSet();
/*  174 */   private Set<LOTRFaction> receivedFacGrids = new HashSet();
/*  175 */   private int ticksUntilRequestFac = 40;
/*      */   
/*      */   private static final int REQUEST_FAC_WAIT = 40;
/*      */   private float highestViewedConqStr;
/*      */   private static final int conqKeyGrades = 10;
/*      */   public static final int CONQUEST_COLOR = 12255232;
/*      */   private static final int CONQUEST_COLOR_OPQ = -4521984;
/*      */   private static final int CONQUEST_COLOR_NO_EFFECT = 1973790;
/*      */   private static LOTRDimension.DimensionRegion currentRegion;
/*      */   private static LOTRDimension.DimensionRegion prevRegion;
/*      */   private static List<LOTRFaction> currentFactionList;
/*  186 */   private int currentFactionIndex = 0;
/*  187 */   private int prevFactionIndex = 0;
/*      */   private LOTRFaction conquestViewingFaction;
/*  189 */   private static Map<LOTRDimension.DimensionRegion, LOTRFaction> lastViewedRegions = new HashMap();
/*      */   
/*  191 */   private float currentFacScroll = 0.0F;
/*      */   private boolean isFacScrolling = false;
/*      */   private boolean wasMouseDown;
/*      */   private boolean mouseInFacScroll;
/*  195 */   private int facScrollWidth = 240;
/*  196 */   private int facScrollHeight = 14;
/*      */   private int facScrollX;
/*      */   private int facScrollY;
/*  199 */   private int facScrollBorder = 1;
/*  200 */   private int facScrollWidgetWidth = 17;
/*  201 */   private int facScrollWidgetHeight = 12;
/*      */   
/*      */   private GuiButton buttonConquestRegions;
/*      */   
/*      */   public LOTRGuiMap() {
/*  206 */     if (!LOTRGenLayerWorld.loadedBiomeImage())
/*      */     {
/*  208 */       new LOTRGenLayerWorld();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public LOTRGuiMap setConquestGrid() {
/*  214 */     this.isConquestGrid = true;
/*  215 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setControlZone(LOTRFaction f) {
/*  220 */     this.hasControlZones = true;
/*  221 */     this.controlZoneFaction = f;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void initGui() {
/*  227 */     this.xSize = 256;
/*  228 */     this.ySize = 256;
/*  229 */     super.initGui();
/*  230 */     if (fullscreen) {
/*      */       
/*  232 */       int midX = this.width / 2;
/*  233 */       int d = 100;
/*  234 */       this.buttonMenuReturn.xPosition = midX - d - this.buttonMenuReturn.width;
/*  235 */       this.buttonMenuReturn.yPosition = 4;
/*      */     } 
/*  237 */     if (this.isConquestGrid || this.hasControlZones)
/*      */     {
/*  239 */       this.buttonList.remove(this.buttonMenuReturn);
/*      */     }
/*  241 */     setupMapWidgets();
/*      */     
/*  243 */     if (this.isConquestGrid) {
/*      */       
/*  245 */       this.loadingConquestGrid = true;
/*      */       
/*  247 */       setupMapDimensions();
/*      */       
/*  249 */       this.conquestViewingFaction = LOTRLevelData.getData(this.mc.thePlayer).getPledgeFaction();
/*  250 */       if (this.conquestViewingFaction == null)
/*      */       {
/*  252 */         this.conquestViewingFaction = LOTRLevelData.getData(this.mc.thePlayer).getViewingFaction();
/*      */       }
/*  254 */       prevRegion = currentRegion = this.conquestViewingFaction.factionRegion;
/*  255 */       currentFactionList = currentRegion.factionList;
/*  256 */       this.prevFactionIndex = this.currentFactionIndex = currentFactionList.indexOf(this.conquestViewingFaction);
/*  257 */       lastViewedRegions.put(currentRegion, this.conquestViewingFaction);
/*      */       
/*  259 */       this.facScrollX = mapXMin;
/*  260 */       this.facScrollY = mapYMax + 8;
/*  261 */       setCurrentScrollFromFaction();
/*      */       
/*  263 */       this.buttonList.add(this.buttonConquestRegions = new LOTRGuiButtonRedBook(200, mapXMax - 120, mapYMax + 5, 120, 20, ""));
/*      */     } 
/*      */     
/*  266 */     if (this.hasControlZones) {
/*      */       
/*  268 */       setupMapDimensions();
/*      */       
/*  270 */       int[] zoneBorders = this.controlZoneFaction.calculateFullControlZoneWorldBorders();
/*  271 */       int xMin = zoneBorders[0];
/*  272 */       int xMax = zoneBorders[1];
/*  273 */       int zMin = zoneBorders[2];
/*  274 */       int zMax = zoneBorders[3];
/*  275 */       float x = (xMin + xMax) / 2.0F;
/*  276 */       float z = (zMin + zMax) / 2.0F;
/*  277 */       this.posX = x / LOTRGenLayerWorld.scale + 810.0F;
/*  278 */       this.posY = z / LOTRGenLayerWorld.scale + 730.0F;
/*      */       
/*  280 */       int zoneWidth = xMax - xMin;
/*  281 */       int zoneHeight = zMax - zMin;
/*  282 */       double mapZoneWidth = zoneWidth / LOTRGenLayerWorld.scale;
/*  283 */       double mapZoneHeight = zoneHeight / LOTRGenLayerWorld.scale;
/*  284 */       int zoomPowerWidth = MathHelper.floor_double(Math.log(mapWidth / mapZoneWidth) / Math.log(2.0D));
/*  285 */       int zoomPowerHeight = MathHelper.floor_double(Math.log(mapHeight / mapZoneHeight) / Math.log(2.0D));
/*  286 */       zoomPower = Math.min(zoomPowerWidth, zoomPowerHeight);
/*  287 */       this.prevZoomPower = zoomPower;
/*      */     }
/*  289 */     else if (this.mc.thePlayer != null) {
/*      */       
/*  291 */       this.posX = (float)(this.mc.thePlayer.posX / LOTRGenLayerWorld.scale) + 810.0F;
/*  292 */       this.posY = (float)(this.mc.thePlayer.posZ / LOTRGenLayerWorld.scale) + 730.0F;
/*      */     } 
/*  294 */     this.prevPosX = this.posX;
/*  295 */     this.prevPosY = this.posY;
/*      */     
/*  297 */     this.buttonOverlayFunction = new GuiButton(0, 0, 0, 160, 20, "");
/*  298 */     this.buttonOverlayFunction.enabled = this.buttonOverlayFunction.visible = false;
/*  299 */     this.buttonList.add(this.buttonOverlayFunction);
/*      */     
/*  301 */     this.nameWPTextField = new GuiTextField(this.fontRendererObj, mapXMin + mapWidth / 2 - 80, mapYMin + 50, 160, 20);
/*      */     
/*  303 */     this.fellowshipDrawGUI = new LOTRGuiFellowships();
/*  304 */     this.fellowshipDrawGUI.setWorldAndResolution(this.mc, this.width, this.height);
/*      */     
/*  306 */     if (this.mc.currentScreen == this) {
/*      */       
/*  308 */       LOTRPacketClientMQEvent packet = new LOTRPacketClientMQEvent(LOTRPacketClientMQEvent.ClientMQEvent.MAP);
/*  309 */       LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void setupMapWidgets() {
/*  315 */     mapWidgets.clear();
/*      */     
/*  317 */     mapWidgets.add(this.widgetAddCWP = new LOTRGuiMapWidget(-16, 6, 10, "addCWP", 0, 0));
/*  318 */     mapWidgets.add(this.widgetDelCWP = new LOTRGuiMapWidget(-16, 20, 10, "delCWP", 10, 0));
/*  319 */     mapWidgets.add(this.widgetRenameCWP = new LOTRGuiMapWidget(-16, 34, 10, "renameCWP", 0, 10));
/*  320 */     mapWidgets.add(this.widgetToggleWPs = new LOTRGuiMapWidget(-58, 6, 10, "toggleWPs", 80, 0));
/*  321 */     mapWidgets.add(this.widgetToggleCWPs = new LOTRGuiMapWidget(-44, 6, 10, "toggleCWPs", 90, 0));
/*  322 */     mapWidgets.add(this.widgetToggleHiddenSWPs = new LOTRGuiMapWidget(-30, 6, 10, "toggleHiddenSWPs", 100, 0));
/*  323 */     mapWidgets.add(this.widgetZoomIn = new LOTRGuiMapWidget(6, 6, 10, "zoomIn", 30, 0));
/*  324 */     mapWidgets.add(this.widgetZoomOut = new LOTRGuiMapWidget(6, 20, 10, "zoomOut", 40, 0));
/*  325 */     mapWidgets.add(this.widgetFullScreen = new LOTRGuiMapWidget(20, 6, 10, "fullScreen", 50, 0));
/*  326 */     mapWidgets.add(this.widgetSepia = new LOTRGuiMapWidget(34, 6, 10, "sepia", 60, 0));
/*  327 */     mapWidgets.add(this.widgetLabels = new LOTRGuiMapWidget(-72, 6, 10, "labels", 70, 0));
/*  328 */     mapWidgets.add(this.widgetShareCWP = new LOTRGuiMapWidget(-16, 48, 10, "shareCWP", 10, 10));
/*  329 */     mapWidgets.add(this.widgetHideSWP = new LOTRGuiMapWidget(-16, 20, 10, "hideSWP", 20, 0));
/*  330 */     mapWidgets.add(this.widgetUnhideSWP = new LOTRGuiMapWidget(-16, 20, 10, "unhideSWP", 20, 10));
/*      */     
/*  332 */     if (this.isConquestGrid) {
/*      */       
/*  334 */       mapWidgets.clear();
/*      */       
/*  336 */       mapWidgets.add(this.widgetToggleWPs);
/*  337 */       mapWidgets.add(this.widgetToggleCWPs);
/*  338 */       mapWidgets.add(this.widgetToggleHiddenSWPs);
/*  339 */       mapWidgets.add(this.widgetZoomIn);
/*  340 */       mapWidgets.add(this.widgetZoomOut);
/*  341 */       mapWidgets.add(this.widgetLabels);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void setupMapDimensions() {
/*  347 */     if (this.isConquestGrid) {
/*      */       
/*  349 */       int windowWidth = 400;
/*  350 */       int windowHeight = 240;
/*  351 */       mapXMin = this.width / 2 - windowWidth / 2;
/*  352 */       mapXMax = this.width / 2 + windowWidth / 2;
/*  353 */       mapYMin = this.height / 2 - 16 - windowHeight / 2;
/*  354 */       mapYMax = mapYMin + windowHeight;
/*      */ 
/*      */     
/*      */     }
/*  358 */     else if (fullscreen) {
/*      */       
/*  360 */       mapXMin = 30;
/*  361 */       mapXMax = this.width - 30;
/*  362 */       mapYMin = 30;
/*  363 */       mapYMax = this.height - 30;
/*      */     }
/*      */     else {
/*      */       
/*  367 */       int windowWidth = 312;
/*  368 */       mapXMin = this.width / 2 - windowWidth / 2;
/*  369 */       mapXMax = this.width / 2 + windowWidth / 2;
/*  370 */       mapYMin = this.guiTop;
/*  371 */       mapYMax = this.guiTop + 200;
/*      */     } 
/*      */ 
/*      */     
/*  375 */     mapWidth = mapXMax - mapXMin;
/*  376 */     mapHeight = mapYMax - mapYMin;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateScreen() {
/*  382 */     super.updateScreen();
/*  383 */     this.tickCounter++;
/*      */     
/*  385 */     if (this.zoomTicks > 0)
/*      */     {
/*  387 */       this.zoomTicks--;
/*      */     }
/*      */     
/*  390 */     if (this.creatingWaypointNew || this.renamingWaypoint || this.sharingWaypointNew)
/*      */     {
/*  392 */       this.nameWPTextField.updateCursorCounter();
/*      */     }
/*      */     
/*  395 */     handleMapKeyboardMovement();
/*      */     
/*  397 */     if (this.isConquestGrid) {
/*      */       
/*  399 */       updateCurrentDimensionAndFaction();
/*      */       
/*  401 */       if (this.ticksUntilRequestFac > 0)
/*      */       {
/*  403 */         this.ticksUntilRequestFac--;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void updateCurrentDimensionAndFaction() {
/*  410 */     if (this.currentFactionIndex != this.prevFactionIndex) {
/*      */       
/*  412 */       this.conquestViewingFaction = (LOTRFaction)currentFactionList.get(this.currentFactionIndex);
/*  413 */       this.ticksUntilRequestFac = 40;
/*      */     } 
/*  415 */     this.prevFactionIndex = this.currentFactionIndex;
/*      */     
/*  417 */     if (currentRegion != prevRegion) {
/*      */       
/*  419 */       lastViewedRegions.put(prevRegion, this.conquestViewingFaction);
/*      */       
/*  421 */       currentFactionList = currentRegion.factionList;
/*  422 */       this.conquestViewingFaction = lastViewedRegions.containsKey(currentRegion) ? (LOTRFaction)lastViewedRegions.get(currentRegion) : (LOTRFaction)currentRegion.factionList.get(0);
/*  423 */       this.prevFactionIndex = this.currentFactionIndex = currentFactionList.indexOf(this.conquestViewingFaction);
/*  424 */       this.ticksUntilRequestFac = 40;
/*      */     } 
/*  426 */     prevRegion = currentRegion;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setupZoomVariables(float f) {
/*  431 */     this.zoomExp = zoomPower;
/*  432 */     if (this.zoomTicks > 0) {
/*      */       
/*  434 */       float progress = (zoomTicksMax - this.zoomTicks - f) / zoomTicksMax;
/*  435 */       this.zoomExp = this.prevZoomPower + (zoomPower - this.prevZoomPower) * progress;
/*      */     } 
/*  437 */     this.zoomScale = (float)Math.pow(2.0D, this.zoomExp);
/*  438 */     this.zoomScaleStable = (float)Math.pow(2.0D, ((this.zoomTicks == 0) ? zoomPower : Math.min(zoomPower, this.prevZoomPower)));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void drawScreen(int i, int j, float f) {
/*  444 */     Tessellator tess = Tessellator.instance;
/*  445 */     this.zLevel = 0.0F;
/*      */     
/*  447 */     setupMapDimensions();
/*  448 */     setupZoomVariables(f);
/*      */     
/*  450 */     if (this.isConquestGrid) {
/*      */       
/*  452 */       this.loadingConquestGrid = !this.receivedFacGrids.contains(this.conquestViewingFaction);
/*      */       
/*  454 */       this.highestViewedConqStr = 0.0F;
/*  455 */       setupConquestScrollBar(i, j);
/*      */       
/*  457 */       this.buttonConquestRegions.displayString = currentRegion.getRegionName();
/*  458 */       this.buttonConquestRegions.visible = this.buttonConquestRegions.enabled = true;
/*      */     } 
/*      */     
/*  461 */     this.posX = this.prevPosX;
/*  462 */     this.posY = this.prevPosY;
/*  463 */     this.isMouseWithinMap = (i >= mapXMin && i < mapXMax && j >= mapYMin && j < mapYMax);
/*  464 */     if (!this.hasOverlay && !this.isFacScrolling && this.zoomTicks == 0 && Mouse.isButtonDown(0)) {
/*      */       
/*  466 */       if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && this.isMouseWithinMap)
/*      */       {
/*  468 */         if (this.isMouseButtonDown == 0) {
/*      */           
/*  470 */           this.isMouseButtonDown = 1;
/*      */         }
/*      */         else {
/*      */           
/*  474 */           float x = (i - this.prevMouseX) / this.zoomScale;
/*  475 */           float y = (j - this.prevMouseY) / this.zoomScale;
/*  476 */           this.posX -= x;
/*  477 */           this.posY -= y;
/*  478 */           if (x != 0.0F || y != 0.0F)
/*      */           {
/*  480 */             this.selectedWaypoint = null;
/*      */           }
/*      */         } 
/*      */         
/*  484 */         this.prevMouseX = i;
/*  485 */         this.prevMouseY = j;
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  490 */       this.isMouseButtonDown = 0;
/*      */     } 
/*  492 */     this.prevPosX = this.posX;
/*  493 */     this.prevPosY = this.posY;
/*  494 */     this.posX += this.posXMove * f;
/*  495 */     this.posY += this.posYMove * f;
/*      */     
/*  497 */     boolean isSepia = (this.isConquestGrid || LOTRConfig.enableSepiaMap);
/*      */     
/*  499 */     if (this.isConquestGrid)
/*      */     {
/*  501 */       drawDefaultBackground();
/*      */     }
/*      */     
/*  504 */     if (fullscreen || this.isConquestGrid) {
/*      */       
/*  506 */       this.mc.getTextureManager().bindTexture(LOTRTextures.overlayTexture);
/*  507 */       if (this.conquestViewingFaction != null) {
/*      */         
/*  509 */         float[] cqColors = this.conquestViewingFaction.getFactionColorComponents();
/*  510 */         GL11.glColor4f(cqColors[0], cqColors[1], cqColors[2], 1.0F);
/*      */       }
/*      */       else {
/*      */         
/*  514 */         GL11.glColor4f(0.65F, 0.5F, 0.35F, 1.0F);
/*      */       } 
/*      */       
/*  517 */       tess.startDrawingQuads();
/*  518 */       if (this.isConquestGrid) {
/*      */         
/*  520 */         int w = 8;
/*  521 */         int up = 22;
/*  522 */         int down = 54;
/*      */         
/*  524 */         tess.addVertexWithUV((mapXMin - w), (mapYMax + down), this.zLevel, 0.0D, 1.0D);
/*  525 */         tess.addVertexWithUV((mapXMax + w), (mapYMax + down), this.zLevel, 1.0D, 1.0D);
/*  526 */         tess.addVertexWithUV((mapXMax + w), (mapYMin - up), this.zLevel, 1.0D, 0.0D);
/*  527 */         tess.addVertexWithUV((mapXMin - w), (mapYMin - up), this.zLevel, 0.0D, 0.0D);
/*      */       }
/*      */       else {
/*      */         
/*  531 */         tess.addVertexWithUV(0.0D, this.height, this.zLevel, 0.0D, 1.0D);
/*  532 */         tess.addVertexWithUV(this.width, this.height, this.zLevel, 1.0D, 1.0D);
/*  533 */         tess.addVertexWithUV(this.width, 0.0D, this.zLevel, 1.0D, 0.0D);
/*  534 */         tess.addVertexWithUV(0.0D, 0.0D, this.zLevel, 0.0D, 0.0D);
/*      */       } 
/*  536 */       tess.draw();
/*      */       
/*  538 */       int redW = this.isConquestGrid ? 2 : 4;
/*  539 */       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  540 */       renderGraduatedRects(mapXMin - 1, mapYMin - 1, mapXMax + 1, mapYMax + 1, -6156032, -16777216, redW);
/*      */     }
/*      */     else {
/*      */       
/*  544 */       drawDefaultBackground();
/*  545 */       renderGraduatedRects(mapXMin - 1, mapYMin - 1, mapXMax + 1, mapYMax + 1, -6156032, -16777216, 4);
/*      */     } 
/*      */     
/*  548 */     setupScrollBars(i, j);
/*      */     
/*  550 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  551 */     int oceanColor = LOTRTextures.getMapOceanColor(isSepia);
/*  552 */     drawRect(mapXMin, mapYMin, mapXMax, mapYMax, oceanColor);
/*      */     
/*  554 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  555 */     if (!this.isConquestGrid) {
/*      */       
/*  557 */       String title = StatCollector.translateToLocal("lotr.gui.map.title");
/*  558 */       if (fullscreen) {
/*      */         
/*  560 */         drawCenteredString(title, this.width / 2, 10, 16777215);
/*      */       }
/*      */       else {
/*      */         
/*  564 */         drawCenteredString(title, this.width / 2, this.guiTop - 30, 16777215);
/*      */       } 
/*      */     } 
/*      */     
/*  568 */     if (this.hasControlZones && LOTRFaction.controlZonesEnabled(this.mc.theWorld)) {
/*      */       
/*  570 */       renderMapAndOverlay(isSepia, 1.0F, false);
/*  571 */       renderControlZone(i, j);
/*  572 */       GL11.glEnable(3042);
/*  573 */       renderMapAndOverlay(isSepia, 0.5F, true);
/*  574 */       GL11.glDisable(3042);
/*      */       
/*  576 */       String tooltip = null;
/*  577 */       if (this.mouseControlZone) {
/*      */         
/*  579 */         tooltip = StatCollector.translateToLocal("lotr.gui.map.controlZoneFull");
/*      */       }
/*  581 */       else if (this.mouseControlZoneReduced) {
/*      */         
/*  583 */         tooltip = StatCollector.translateToLocal("lotr.gui.map.controlZoneReduced");
/*      */       } 
/*      */       
/*  586 */       if (tooltip != null)
/*      */       {
/*  588 */         int strWidth = this.mc.fontRenderer.getStringWidth(tooltip);
/*  589 */         int strHeight = this.mc.fontRenderer.FONT_HEIGHT;
/*      */         
/*  591 */         int rectX = i + 12;
/*  592 */         int rectY = j - 12;
/*      */         
/*  594 */         int border = 3;
/*  595 */         int rectWidth = strWidth + border * 2;
/*  596 */         int rectHeight = strHeight + border * 2;
/*      */         
/*  598 */         int mapBorder2 = 2;
/*  599 */         rectX = Math.max(rectX, mapXMin + mapBorder2);
/*  600 */         rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
/*  601 */         rectY = Math.max(rectY, mapYMin + mapBorder2);
/*  602 */         rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
/*      */         
/*  604 */         GL11.glTranslatef(0.0F, 0.0F, 300.0F);
/*  605 */         drawFancyRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
/*  606 */         this.mc.fontRenderer.drawString(tooltip, rectX + border, rectY + border, 16777215);
/*  607 */         GL11.glTranslatef(0.0F, 0.0F, -300.0F);
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  612 */       renderMapAndOverlay(isSepia, 1.0F, true);
/*      */       
/*  614 */       if (this.isConquestGrid && this.conquestViewingFaction != null) {
/*      */         
/*  616 */         requestConquestGridIfNeed(this.conquestViewingFaction);
/*      */         
/*  618 */         if (!this.loadingConquestGrid) {
/*      */           
/*  620 */           GL11.glEnable(3042);
/*  621 */           GL11.glBlendFunc(770, 771);
/*  622 */           setupMapClipping();
/*      */           
/*  624 */           float alphaFunc = GL11.glGetFloat(3010);
/*  625 */           GL11.glAlphaFunc(516, 0.005F);
/*  626 */           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*      */           
/*  628 */           Collection<LOTRConquestZone> allZones = (Collection)this.facConquestGrids.get(this.conquestViewingFaction);
/*  629 */           if (allZones == null)
/*      */           {
/*  631 */             allZones = new ArrayList<LOTRConquestZone>();
/*      */           }
/*  633 */           Collection<LOTRConquestZone> zonesInView = new ArrayList<LOTRConquestZone>();
/*      */           
/*  635 */           this.highestViewedConqStr = 0.0F;
/*  636 */           float mouseOverStr = 0.0F;
/*  637 */           LOTRConquestZone mouseOverZone = null;
/*  638 */           LOTRConquestGrid.ConquestEffective mouseOverEffect = null;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  643 */           for (int pass = 0; pass <= 1; pass++) {
/*      */             
/*  645 */             if (pass == 1)
/*      */             {
/*  647 */               if (this.highestViewedConqStr <= 0.0F) {
/*      */                 continue;
/*      */               }
/*      */             }
/*      */ 
/*      */             
/*  653 */             Collection<LOTRConquestZone> zoneList = (pass == 0) ? allZones : zonesInView;
/*  654 */             for (LOTRConquestZone zone : zoneList) {
/*      */               
/*  656 */               float strength = zone.getConquestStrength(this.conquestViewingFaction, this.mc.theWorld);
/*      */               
/*  658 */               int[] min = LOTRConquestGrid.getMinCoordsOnMap(zone);
/*  659 */               int[] max = LOTRConquestGrid.getMaxCoordsOnMap(zone);
/*  660 */               float[] minF = transformMapCoords(min[0], min[1]);
/*  661 */               float[] maxF = transformMapCoords(max[0], max[1]);
/*  662 */               float minX = minF[0];
/*  663 */               float maxX = maxF[0];
/*  664 */               float minY = minF[1];
/*  665 */               float maxY = maxF[1];
/*      */               
/*  667 */               if (maxX >= mapXMin && minX <= mapXMax && maxY >= mapYMin && minY <= mapYMax) {
/*      */                 
/*  669 */                 if (pass == 0) {
/*      */                   
/*  671 */                   if (strength > this.highestViewedConqStr)
/*      */                   {
/*  673 */                     this.highestViewedConqStr = strength;
/*      */                   }
/*      */                   
/*  676 */                   zonesInView.add(zone);
/*      */                   continue;
/*      */                 } 
/*  679 */                 if (pass == 1)
/*      */                 {
/*  681 */                   if (strength > 0.0F) {
/*      */                     
/*  683 */                     float strFrac = strength / this.highestViewedConqStr;
/*  684 */                     strFrac = MathHelper.clamp_float(strFrac, 0.0F, 1.0F);
/*  685 */                     float strAlpha = strFrac;
/*  686 */                     if (strength > 0.0F)
/*      */                     {
/*  688 */                       strAlpha = Math.max(strAlpha, 0.1F);
/*      */                     }
/*      */                     
/*  691 */                     LOTRConquestGrid.ConquestEffective effect = LOTRConquestGrid.getConquestEffectIn(this.mc.theWorld, zone, this.conquestViewingFaction);
/*  692 */                     int zoneColor = 0xBB0000 | Math.round(strAlpha * 255.0F) << 24;
/*  693 */                     if (effect == LOTRConquestGrid.ConquestEffective.EFFECTIVE) {
/*      */                       
/*  695 */                       drawFloatRect(minX, minY, maxX, maxY, zoneColor);
/*      */                     }
/*      */                     else {
/*      */                       
/*  699 */                       int zoneColor2 = 0x1E1E1E | Math.round(strAlpha * 255.0F) << 24;
/*  700 */                       if (effect == LOTRConquestGrid.ConquestEffective.ALLY_BOOST) {
/*      */                         
/*  702 */                         float zoneYSize = maxY - minY;
/*  703 */                         int strips = 8;
/*  704 */                         for (int l = 0; l < strips; l++)
/*      */                         {
/*  706 */                           float stripYSize = zoneYSize / strips;
/*  707 */                           drawFloatRect(minX, minY + stripYSize * l, maxX, minY + stripYSize * (l + 1), (l % 2 == 0) ? zoneColor : zoneColor2);
/*      */                         }
/*      */                       
/*  710 */                       } else if (effect == LOTRConquestGrid.ConquestEffective.NO_EFFECT) {
/*      */                         
/*  712 */                         drawFloatRect(minX, minY, maxX, maxY, zoneColor2);
/*      */                       } 
/*      */                     } 
/*      */                     
/*  716 */                     if (i >= minX && i < maxX && j >= minY && j < maxY) {
/*      */                       
/*  718 */                       mouseOverStr = strength;
/*  719 */                       mouseOverZone = zone;
/*  720 */                       mouseOverEffect = effect;
/*      */                     } 
/*      */                   } 
/*      */                 }
/*      */               } 
/*      */             } 
/*      */             continue;
/*      */           } 
/*  728 */           GL11.glAlphaFunc(516, alphaFunc);
/*      */           
/*  730 */           if (mouseOverZone != null)
/*      */           {
/*  732 */             if (i >= mapXMin && i < mapXMax && j >= mapYMin && j < mapYMax) {
/*      */               
/*  734 */               int[] min = LOTRConquestGrid.getMinCoordsOnMap(mouseOverZone);
/*  735 */               int[] max = LOTRConquestGrid.getMaxCoordsOnMap(mouseOverZone);
/*  736 */               float[] minF = transformMapCoords(min[0], min[1]);
/*  737 */               float[] maxF = transformMapCoords(max[0], max[1]);
/*  738 */               float minX = minF[0];
/*  739 */               float maxX = maxF[0];
/*  740 */               float minY = minF[1];
/*  741 */               float maxY = maxF[1];
/*      */               
/*  743 */               drawFloatRect(minX - 1.0F, minY - 1.0F, maxX + 1.0F, minY, -16777216);
/*  744 */               drawFloatRect(minX - 1.0F, maxY, maxX + 1.0F, maxY + 1.0F, -16777216);
/*  745 */               drawFloatRect(minX - 1.0F, minY, minX, maxY, -16777216);
/*  746 */               drawFloatRect(maxX, minY, maxX + 1.0F, maxY, -16777216);
/*  747 */               drawFloatRect(minX - 2.0F, minY - 2.0F, maxX + 2.0F, minY - 1.0F, -4521984);
/*  748 */               drawFloatRect(minX - 2.0F, maxY + 1.0F, maxX + 2.0F, maxY + 2.0F, -4521984);
/*  749 */               drawFloatRect(minX - 2.0F, minY - 1.0F, minX - 1.0F, maxY + 1.0F, -4521984);
/*  750 */               drawFloatRect(maxX + 1.0F, minY - 1.0F, maxX + 2.0F, maxY + 1.0F, -4521984);
/*      */               
/*  752 */               String tooltip = LOTRAlignmentValues.formatConqForDisplay(mouseOverStr, false);
/*  753 */               String subtip = null;
/*  754 */               if (mouseOverEffect == LOTRConquestGrid.ConquestEffective.ALLY_BOOST) {
/*      */                 
/*  756 */                 subtip = StatCollector.translateToLocalFormatted("lotr.gui.map.conquest.allyBoost", new Object[] { this.conquestViewingFaction.factionName() });
/*      */               }
/*  758 */               else if (mouseOverEffect == LOTRConquestGrid.ConquestEffective.NO_EFFECT) {
/*      */                 
/*  760 */                 subtip = StatCollector.translateToLocal("lotr.gui.map.conquest.noEffect");
/*      */               } 
/*  762 */               int strWidth = this.mc.fontRenderer.getStringWidth(tooltip);
/*  763 */               int subWidth = (subtip == null) ? 0 : this.mc.fontRenderer.getStringWidth(subtip);
/*  764 */               int strHeight = this.mc.fontRenderer.FONT_HEIGHT;
/*      */               
/*  766 */               float guiScale = (new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight)).getScaleFactor();
/*  767 */               float subScale = (guiScale <= 2.0F) ? guiScale : (guiScale - 1.0F);
/*  768 */               float subScaleRel = subScale / guiScale;
/*      */               
/*  770 */               int rectX = i + 12;
/*  771 */               int rectY = j - 12;
/*      */               
/*  773 */               int border = 3;
/*  774 */               int iconSize = 16;
/*  775 */               int rectWidth = border * 2 + Math.max(strWidth + iconSize + border, (int)(subWidth * subScaleRel));
/*  776 */               int rectHeight = Math.max(strHeight, iconSize) + border * 2;
/*  777 */               if (subtip != null)
/*      */               {
/*  779 */                 rectHeight += border + (int)(strHeight * subScaleRel);
/*      */               }
/*      */               
/*  782 */               int mapBorder2 = 2;
/*  783 */               rectX = Math.max(rectX, mapXMin + mapBorder2);
/*  784 */               rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
/*  785 */               rectY = Math.max(rectY, mapYMin + mapBorder2);
/*  786 */               rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
/*      */               
/*  788 */               GL11.glTranslatef(0.0F, 0.0F, 300.0F);
/*  789 */               drawFancyRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
/*  790 */               this.mc.getTextureManager().bindTexture(LOTRClientProxy.alignmentTexture);
/*  791 */               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  792 */               drawTexturedModalRect(rectX + border, rectY + border, 0, 228, iconSize, iconSize);
/*  793 */               this.mc.fontRenderer.drawString(tooltip, rectX + iconSize + border * 2, rectY + border + (iconSize - strHeight) / 2, 16777215);
/*  794 */               if (subtip != null) {
/*      */                 
/*  796 */                 GL11.glPushMatrix();
/*  797 */                 GL11.glScalef(subScaleRel, subScaleRel, 1.0F);
/*  798 */                 int subX = rectX + border;
/*  799 */                 int subY = rectY + border * 2 + iconSize;
/*  800 */                 this.mc.fontRenderer.drawString(subtip, Math.round(subX / subScaleRel), Math.round(subY / subScaleRel), 16777215);
/*  801 */                 GL11.glPopMatrix();
/*      */               } 
/*  803 */               GL11.glTranslatef(0.0F, 0.0F, -300.0F);
/*      */             } 
/*      */           }
/*      */           
/*  807 */           endMapClipping();
/*  808 */           GL11.glDisable(3042);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  813 */     this.zLevel += 50.0F;
/*  814 */     LOTRTextures.drawMapCompassBottomLeft((mapXMin + 12), (mapYMax - 12), this.zLevel, 1.0D);
/*  815 */     this.zLevel -= 50.0F;
/*      */     
/*  817 */     renderRoads();
/*  818 */     renderPlayers(i, j);
/*  819 */     if (!this.loadingConquestGrid)
/*      */     {
/*  821 */       renderMiniQuests(this.mc.thePlayer, i, j);
/*      */     }
/*      */     
/*  824 */     renderWaypoints(0, i, j);
/*  825 */     renderLabels();
/*  826 */     renderWaypoints(1, i, j);
/*      */     
/*  828 */     if (!this.loadingConquestGrid && this.selectedWaypoint != null && isWaypointVisible(this.selectedWaypoint)) {
/*      */       
/*  830 */       if (!this.hasOverlay)
/*      */       {
/*  832 */         renderWaypointTooltip(this.selectedWaypoint, true, i, j);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/*  837 */       this.selectedWaypoint = null;
/*      */     } 
/*      */     
/*  840 */     if (this.isConquestGrid) {
/*      */       
/*  842 */       if (this.loadingConquestGrid) {
/*      */         
/*  844 */         drawRect(mapXMin, mapYMin, mapXMax, mapYMax, -1429949539);
/*      */         
/*  846 */         GL11.glEnable(3042);
/*  847 */         GL11.glBlendFunc(770, 771);
/*      */         
/*  849 */         this.mc.getTextureManager().bindTexture(LOTRTextures.overlayTexture);
/*  850 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
/*  851 */         tess.startDrawingQuads();
/*  852 */         tess.addVertexWithUV(mapXMin, mapYMax, 0.0D, 0.0D, 1.0D);
/*  853 */         tess.addVertexWithUV(mapXMax, mapYMax, 0.0D, 1.0D, 1.0D);
/*  854 */         tess.addVertexWithUV(mapXMax, mapYMin, 0.0D, 1.0D, 0.0D);
/*  855 */         tess.addVertexWithUV(mapXMin, mapYMin, 0.0D, 0.0D, 0.0D);
/*  856 */         tess.draw();
/*      */         
/*  858 */         String loadText = "";
/*  859 */         LOTRConquestGrid.ConquestViewableQuery query = LOTRConquestGrid.canPlayerViewConquest(this.mc.thePlayer, this.conquestViewingFaction);
/*  860 */         if (query.result == LOTRConquestGrid.ConquestViewable.CAN_VIEW) {
/*      */           
/*  862 */           loadText = StatCollector.translateToLocal("lotr.gui.map.conquest.wait");
/*  863 */           int ellipsis = 1 + this.tickCounter / 10 % 3;
/*  864 */           for (int l = 0; l < ellipsis; l++)
/*      */           {
/*  866 */             loadText = loadText + ".";
/*      */           
/*      */           }
/*      */         
/*      */         }
/*  871 */         else if (query.result == LOTRConquestGrid.ConquestViewable.UNPLEDGED) {
/*      */           
/*  873 */           loadText = StatCollector.translateToLocal("lotr.gui.map.conquest.noPledge");
/*      */         }
/*      */         else {
/*      */           
/*  877 */           LOTRPlayerData pd = LOTRLevelData.getData(this.mc.thePlayer);
/*  878 */           LOTRFaction pledgeFac = pd.getPledgeFaction();
/*  879 */           LOTRFactionRank needRank = query.needRank;
/*  880 */           String needAlign = LOTRAlignmentValues.formatAlignForDisplay(needRank.alignment);
/*      */           
/*  882 */           String format = "";
/*  883 */           if (query.result == LOTRConquestGrid.ConquestViewable.NEED_RANK)
/*      */           {
/*  885 */             format = "lotr.gui.map.conquest.needRank";
/*      */           }
/*      */           
/*  888 */           loadText = StatCollector.translateToLocalFormatted(format, new Object[] { pledgeFac.factionName(), needRank.getFullNameWithGender(pd), needAlign });
/*      */         } 
/*      */ 
/*      */         
/*  892 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  893 */         int stringWidth = 250;
/*      */         
					//n"));
/*  896 */         List<String> loadLines = new ArrayList<String>();
/*  897 */         for (String line : loadLines)
/*      */         {
/*  899 */           loadLines.addAll(this.fontRendererObj.listFormattedStringToWidth(line, stringWidth));
/*      */         }
/*  901 */         int stringX = mapXMin + mapWidth / 2;
/*  902 */         int stringY = (mapYMin + mapYMax) / 2 - loadLines.size() * this.fontRendererObj.FONT_HEIGHT / 2;
/*  903 */         for (String s : loadLines) {
/*      */           
/*  905 */           drawCenteredString(s, stringX, stringY, 3748142);
/*  906 */           stringY += this.fontRendererObj.FONT_HEIGHT;
/*      */         } 
/*      */         
/*  909 */         GL11.glDisable(3042);
/*      */       } 
/*      */       
/*  912 */       this.mc.getTextureManager().bindTexture(conquestTexture);
/*  913 */       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  914 */       drawTexturedModalRect(mapXMin - 8, mapYMin - 22, 0, 0, mapWidth + 16, mapHeight + 22 + 54, 512);
/*      */     } 
/*      */     
/*  917 */     this.zLevel = 100.0F;
/*      */     
/*  919 */     if (!this.hasOverlay)
/*      */     {
/*  921 */       if (isMiddleEarth() && this.selectedWaypoint != null) {
/*      */         
/*  923 */         this.zLevel += 500.0F;
/*      */         
/*  925 */         boolean hasUnlocked = this.selectedWaypoint.hasPlayerUnlocked(this.mc.thePlayer);
/*  926 */         int fastTravel = LOTRLevelData.getData(this.mc.thePlayer).getFTTimer();
/*  927 */         boolean canFastTravel = (fastTravel <= 0 && hasUnlocked);
/*      */         
/*  929 */         String notUnlocked = "If you can read this, something has gone hideously wrong";
/*  930 */         if (this.selectedWaypoint instanceof LOTRCustomWaypoint) {
/*      */           
/*  932 */           if (((LOTRCustomWaypoint)this.selectedWaypoint).isShared())
/*      */           {
/*  934 */             notUnlocked = StatCollector.translateToLocal("lotr.gui.map.waypointUnavailableShared");
/*      */           
/*      */           }
/*      */         
/*      */         }
/*  939 */         else if (!this.selectedWaypoint.isUnlockable(this.mc.thePlayer)) {
/*      */           
/*  941 */           notUnlocked = StatCollector.translateToLocal("lotr.gui.map.waypointUnavailableEnemy");
/*      */         }
/*      */         else {
/*      */           
/*  945 */           notUnlocked = StatCollector.translateToLocal("lotr.gui.map.waypointUnavailableTravel");
/*      */         } 
/*      */ 
/*      */         
/*  949 */         int waypointCooldown = LOTRLevelData.getData(this.mc.thePlayer).getFTCooldown(this.selectedWaypoint);
/*  950 */         String ftLine1 = StatCollector.translateToLocalFormatted("lotr.gui.map.fastTravel.1", new Object[] { GameSettings.getKeyDisplayString(LOTRKeyHandler.keyBindingFastTravel.getKeyCode()) });
/*  951 */         String ftLine2 = StatCollector.translateToLocalFormatted("lotr.gui.map.fastTravel.2", new Object[] { LOTRLevelData.getHMSTime(waypointCooldown) });
/*      */         
/*  953 */         String cooldownTime = StatCollector.translateToLocalFormatted("lotr.gui.map.fastTravelTimeRemaining", new Object[] { LOTRLevelData.getHMSTime(fastTravel) });
/*      */         
/*  955 */         if (fullscreen || this.isConquestGrid) {
/*      */           
/*  957 */           if (!hasUnlocked)
/*      */           {
/*  959 */             renderFullscreenSubtitles(new String[] { notUnlocked });
/*      */           }
/*  961 */           else if (canFastTravel)
/*      */           {
/*  963 */             renderFullscreenSubtitles(new String[] { ftLine1, ftLine2 });
/*      */           }
/*      */           else
/*      */           {
/*  967 */             renderFullscreenSubtitles(new String[] { cooldownTime });
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/*  972 */           int stringHeight = this.fontRendererObj.FONT_HEIGHT;
/*  973 */           int rectWidth = 256;
/*  974 */           int border = 3;
/*  975 */           int rectHeight = border * 2 + stringHeight;
/*  976 */           if (canFastTravel)
/*      */           {
/*  978 */             rectHeight += stringHeight + border;
/*      */           }
/*  980 */           int x = mapXMin + mapWidth / 2 - rectWidth / 2;
/*  981 */           int y = mapYMax + 10;
/*      */           
/*  983 */           int strX = mapXMin + mapWidth / 2;
/*  984 */           int strY = y + border;
/*      */           
/*  986 */           if (!hasUnlocked) {
/*      */             
/*  988 */             drawFancyRect(x, y, x + rectWidth, y + rectHeight);
/*  989 */             drawCenteredString(notUnlocked, strX, strY, 16777215);
/*      */           }
/*  991 */           else if (canFastTravel) {
/*      */             
/*  993 */             drawFancyRect(x, y, x + rectWidth, y + rectHeight);
/*  994 */             drawCenteredString(ftLine1, strX, strY, 16777215);
/*  995 */             drawCenteredString(ftLine2, strX, strY + stringHeight + border, 16777215);
/*      */           }
/*      */           else {
/*      */             
/*  999 */             drawFancyRect(x, y, x + rectWidth, y + rectHeight);
/* 1000 */             drawCenteredString(cooldownTime, strX, strY, 16777215);
/*      */           } 
/*      */         } 
/*      */         
/* 1004 */         this.zLevel -= 500.0F;
/*      */       }
/* 1006 */       else if (this.isMouseWithinMap) {
/*      */         
/* 1008 */         this.zLevel += 500.0F;
/*      */         
/* 1010 */         float biomePosX = this.posX + (i - mapXMin - mapWidth / 2) / this.zoomScale;
/* 1011 */         float biomePosZ = this.posY + (j - mapYMin - mapHeight / 2) / this.zoomScale;
/* 1012 */         int biomePosX_int = MathHelper.floor_double(biomePosX);
/* 1013 */         int biomePosZ_int = MathHelper.floor_double(biomePosZ);
/*      */         
/* 1015 */         LOTRBiome biome = LOTRGenLayerWorld.getBiomeOrOcean(biomePosX_int, biomePosZ_int);
/* 1016 */         if (biome.isHiddenBiome() && !LOTRLevelData.getData(this.mc.thePlayer).hasAchievement(biome.getBiomeAchievement()))
/*      */         {
/* 1018 */           biome = LOTRBiome.ocean;
/*      */         }
/*      */         
/* 1021 */         this.mouseXCoord = Math.round((biomePosX - 810.0F) * LOTRGenLayerWorld.scale);
/* 1022 */         this.mouseZCoord = Math.round((biomePosZ - 730.0F) * LOTRGenLayerWorld.scale);
/*      */         
/* 1024 */         String biomeName = biome.getBiomeDisplayName();
/* 1025 */         String coords = StatCollector.translateToLocalFormatted("lotr.gui.map.coords", new Object[] { Integer.valueOf(this.mouseXCoord), Integer.valueOf(this.mouseZCoord) });
/* 1026 */         String teleport = StatCollector.translateToLocalFormatted("lotr.gui.map.tp", new Object[] { GameSettings.getKeyDisplayString(LOTRKeyHandler.keyBindingMapTeleport.getKeyCode()) });
/* 1027 */         int stringHeight = this.fontRendererObj.FONT_HEIGHT;
/*      */         
/* 1029 */         if (fullscreen || this.isConquestGrid) {
/*      */           
/* 1031 */           renderFullscreenSubtitles(new String[] { biomeName, coords });
/*      */           
/* 1033 */           if (canTeleport())
/*      */           {
/* 1035 */             GL11.glPushMatrix();
/* 1036 */             if (this.isConquestGrid) {
/*      */               
/* 1038 */               GL11.glTranslatef(((mapXMax - mapXMin) / 2 - 8 - this.fontRendererObj.getStringWidth(teleport) / 2), 0.0F, 0.0F);
/*      */             }
/*      */             else {
/*      */               
/* 1042 */               GL11.glTranslatef((this.width / 2 - 30 - this.fontRendererObj.getStringWidth(teleport) / 2), 0.0F, 0.0F);
/*      */             } 
/* 1044 */             renderFullscreenSubtitles(new String[] { teleport });
/* 1045 */             GL11.glPopMatrix();
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/* 1050 */           int rectWidth = 256;
/* 1051 */           int border = 3;
/* 1052 */           int rectHeight = border * 3 + stringHeight * 2;
/* 1053 */           if (canTeleport())
/*      */           {
/* 1055 */             rectHeight += (stringHeight + border) * 2;
/*      */           }
/* 1057 */           int x = mapXMin + mapWidth / 2 - rectWidth / 2;
/* 1058 */           int y = mapYMax + 10;
/*      */           
/* 1060 */           drawFancyRect(x, y, x + rectWidth, y + rectHeight);
/*      */           
/* 1062 */           int strX = mapXMin + mapWidth / 2;
/* 1063 */           int strY = y + border;
/* 1064 */           drawCenteredString(biomeName, strX, strY, 16777215);
/* 1065 */           strY += stringHeight + border;
/* 1066 */           drawCenteredString(coords, strX, strY, 16777215);
/*      */           
/* 1068 */           if (canTeleport())
/*      */           {
/* 1070 */             drawCenteredString(teleport, strX, strY + (stringHeight + border) * 2, 16777215);
/*      */           }
/*      */         } 
/*      */         
/* 1074 */         this.zLevel -= 500.0F;
/*      */       } 
/*      */     }
/*      */     
/* 1078 */     if (this.isConquestGrid) {
/*      */       
/* 1080 */       String s = StatCollector.translateToLocalFormatted("lotr.gui.map.conquest.title", new Object[] { this.conquestViewingFaction.factionName() });
/* 1081 */       int x = mapXMin + mapWidth / 2;
/* 1082 */       int y = mapYMin - 14;
/* 1083 */       LOTRTickHandlerClient.drawAlignmentText(this.fontRendererObj, x - this.fontRendererObj.getStringWidth(s) / 2, y, s, 1.0F);
/*      */       
/* 1085 */       if (!this.loadingConquestGrid) {
/*      */         
/* 1087 */         int keyBorder = 8;
/* 1088 */         int keyWidth = 24;
/* 1089 */         int keyHeight = 12;
/* 1090 */         int iconSize = 16;
/* 1091 */         int iconGap = keyBorder / 2;
/*      */         
/* 1093 */         float guiScale = (new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight)).getScaleFactor();
/* 1094 */         float labelScale = (guiScale <= 2.0F) ? guiScale : (guiScale - 1.0F);
/* 1095 */         float labelScaleRel = labelScale / guiScale;
/*      */         
/* 1097 */         this.mc.getTextureManager().bindTexture(LOTRClientProxy.alignmentTexture);
/* 1098 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1099 */         drawTexturedModalRect(mapXMax - keyBorder - iconSize, mapYMax - keyBorder - iconSize, 0, 228, iconSize, iconSize);
/*      */         
/* 1101 */         for (int pass = 0; pass <= 1; pass++) {
/*      */           
/* 1103 */           for (int l = 0; l <= 10; l++) {
/*      */             
/* 1105 */             float frac = (10 - l) / 10.0F;
/* 1106 */             float strFrac = frac * this.highestViewedConqStr;
/*      */             
/* 1108 */             int x1 = mapXMax - keyBorder - iconSize - iconGap - l * keyWidth;
/* 1109 */             int x0 = x1 - keyWidth;
/* 1110 */             int y1 = mapYMax - keyBorder - (iconSize - keyHeight) / 2;
/* 1111 */             int y0 = y1 - keyHeight;
/*      */             
/* 1113 */             if (pass == 0) {
/*      */               
/* 1115 */               int color = 0xBB0000 | Math.round(frac * 255.0F) << 24;
/* 1116 */               drawRect(x0, y0, x1, y1, color);
/*      */             }
/* 1118 */             else if (pass == 1) {
/*      */               
/* 1120 */               if (l % 2 == 0) {
/*      */                 
/* 1122 */                 String keyLabel = LOTRAlignmentValues.formatConqForDisplay(strFrac, false);
/* 1123 */                 int strX = (int)((x0 + keyWidth / 2) / labelScaleRel);
/* 1124 */                 int strY = (int)((y0 + keyHeight / 2) / labelScaleRel) - this.fontRendererObj.FONT_HEIGHT / 2;
/*      */                 
/* 1126 */                 GL11.glPushMatrix();
/* 1127 */                 GL11.glScalef(labelScaleRel, labelScaleRel, 1.0F);
/* 1128 */                 drawCenteredString(keyLabel, strX, strY, 16777215);
/* 1129 */                 GL11.glPopMatrix();
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1136 */       if (hasConquestScrollBar()) {
/*      */         
/* 1138 */         this.mc.getTextureManager().bindTexture(LOTRGuiFactions.factionsTexture);
/* 1139 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1140 */         drawTexturedModalRect(this.facScrollX, this.facScrollY, 0, 128, this.facScrollWidth, this.facScrollHeight);
/*      */         
/* 1142 */         int factions = currentFactionList.size();
/* 1143 */         for (int index = 0; index < factions; index++) {
/*      */           
/* 1145 */           LOTRFaction faction = (LOTRFaction)currentFactionList.get(index);
/*      */           
/* 1147 */           float[] factionColors = faction.getFactionColorComponents();
/* 1148 */           float shade = 0.6F;
/* 1149 */           GL11.glColor4f(factionColors[0] * shade, factionColors[1] * shade, factionColors[2] * shade, 1.0F);
/*      */           
/* 1151 */           float xMin = index / factions;
/* 1152 */           float xMax = (index + 1) / factions;
/* 1153 */           xMin = (this.facScrollX + this.facScrollBorder) + xMin * (this.facScrollWidth - this.facScrollBorder * 2);
/* 1154 */           xMax = (this.facScrollX + this.facScrollBorder) + xMax * (this.facScrollWidth - this.facScrollBorder * 2);
/* 1155 */           float yMin = (this.facScrollY + this.facScrollBorder);
/* 1156 */           float yMax = (this.facScrollY + this.facScrollHeight - this.facScrollBorder);
/*      */           
/* 1158 */           float minU = (0 + this.facScrollBorder) / 256.0F;
/* 1159 */           float maxU = (0 + this.facScrollWidth - this.facScrollBorder) / 256.0F;
/* 1160 */           float minV = (128 + this.facScrollBorder) / 256.0F;
/* 1161 */           float maxV = (128 + this.facScrollHeight - this.facScrollBorder) / 256.0F;
/*      */           
/* 1163 */           tess.startDrawingQuads();
/* 1164 */           tess.addVertexWithUV(xMin, yMax, this.zLevel, minU, maxV);
/* 1165 */           tess.addVertexWithUV(xMax, yMax, this.zLevel, maxU, maxV);
/* 1166 */           tess.addVertexWithUV(xMax, yMin, this.zLevel, maxU, minV);
/* 1167 */           tess.addVertexWithUV(xMin, yMin, this.zLevel, minU, minV);
/* 1168 */           tess.draw();
/*      */         } 
/*      */         
/* 1171 */         this.mc.getTextureManager().bindTexture(LOTRGuiFactions.factionsTexture);
/* 1172 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1173 */         int scroll = (int)(this.currentFacScroll * (this.facScrollWidth - this.facScrollBorder * 2 - this.facScrollWidgetWidth));
/* 1174 */         drawTexturedModalRect(this.facScrollX + this.facScrollBorder + scroll, this.facScrollY + this.facScrollBorder, 0, 142, this.facScrollWidgetWidth, this.facScrollWidgetHeight);
/*      */       } 
/*      */     } 
/*      */     
/* 1178 */     if (!this.hasOverlay && this.hasControlZones) {
/*      */       
/* 1180 */       String s = StatCollector.translateToLocalFormatted("lotr.gui.map.controlZones", new Object[] { this.controlZoneFaction.factionName() });
/* 1181 */       int x = mapXMin + mapWidth / 2;
/* 1182 */       int y = mapYMin + 20;
/* 1183 */       LOTRTickHandlerClient.drawAlignmentText(this.fontRendererObj, x - this.fontRendererObj.getStringWidth(s) / 2, y, s, 1.0F);
/*      */       
/* 1185 */       if (!LOTRFaction.controlZonesEnabled(this.mc.theWorld)) {
/*      */         
/* 1187 */         s = StatCollector.translateToLocal("lotr.gui.map.controlZonesDisabled");
/* 1188 */         LOTRTickHandlerClient.drawAlignmentText(this.fontRendererObj, x - this.fontRendererObj.getStringWidth(s) / 2, mapYMin + mapHeight / 2, s, 1.0F);
/*      */       } 
/*      */     } 
/*      */     
/* 1192 */     boolean buttonVisible = this.buttonOverlayFunction.visible;
/* 1193 */     this.buttonOverlayFunction.visible = false;
/* 1194 */     super.drawScreen(i, j, f);
/* 1195 */     this.buttonOverlayFunction.visible = buttonVisible;
/*      */     
/* 1197 */     renderMapWidgets(i, j);
/*      */     
/* 1199 */     if (this.hasOverlay) {
/*      */       
/* 1201 */       GL11.glTranslatef(0.0F, 0.0F, 500.0F);
/*      */       
/* 1203 */       int overlayBorder = 10;
/* 1204 */       if (fullscreen)
/*      */       {
/* 1206 */         overlayBorder = 40;
/*      */       }
/* 1208 */       int rectX0 = mapXMin + overlayBorder;
/* 1209 */       int rectY0 = mapYMin + overlayBorder;
/* 1210 */       int rectX1 = mapXMax - overlayBorder;
/* 1211 */       int rectY1 = mapYMax - overlayBorder;
/* 1212 */       drawFancyRect(rectX0, rectY0, rectX1, rectY1);
/*      */       
/* 1214 */       if (this.overlayLines != null) {
/*      */         
/* 1216 */         int stringX = mapXMin + mapWidth / 2;
/* 1217 */         int stringY = rectY0 + 3 + this.mc.fontRenderer.FONT_HEIGHT;
/* 1218 */         int stringWidth = (int)((mapWidth - overlayBorder * 2) * 0.75F);
/*      */         
/* 1220 */         List<String> displayLines = new ArrayList<String>();
/* 1221 */         for (String s : this.overlayLines)
/*      */         {
/* 1223 */           displayLines.addAll(this.fontRendererObj.listFormattedStringToWidth(s, stringWidth));
/*      */         }
/*      */         
/* 1226 */         for (String s : displayLines) {
/*      */           
/* 1228 */           drawCenteredString(s, stringX, stringY, 16777215);
/* 1229 */           stringY += this.mc.fontRenderer.FONT_HEIGHT;
/*      */         } 
/* 1231 */         stringY += this.mc.fontRenderer.FONT_HEIGHT;
/*      */         
/* 1233 */         if (this.sharingWaypoint) {
/*      */           
/* 1235 */           this.fellowshipDrawGUI.clearMouseOverFellowship();
/* 1236 */           this.mouseOverRemoveSharedFellowship = null;
/*      */           
/* 1238 */           int iconWidth = 8;
/* 1239 */           int iconGap = 8;
/* 1240 */           int fullWidth = this.fellowshipDrawGUI.xSize + iconGap + iconWidth;
/* 1241 */           int fsX = mapXMin + mapWidth / 2 - fullWidth / 2;
/* 1242 */           int fsY = stringY;
/*      */           
/* 1244 */           this.scrollPaneWPShares.paneX0 = fsX;
/* 1245 */           this.scrollPaneWPShares.scrollBarX0 = fsX + fullWidth + 2 + 2;
/* 1246 */           this.scrollPaneWPShares.paneY0 = fsY;
/* 1247 */           this.scrollPaneWPShares.paneY1 = fsY + (this.mc.fontRenderer.FONT_HEIGHT + 5) * this.displayedWPShares;
/* 1248 */           this.scrollPaneWPShares.mouseDragScroll(i, j);
/*      */           
/* 1250 */           int[] sharesMinMax = this.scrollPaneWPShares.getMinMaxIndices(this.displayedWPShareList, this.displayedWPShares);
/* 1251 */           for (int index = sharesMinMax[0]; index <= sharesMinMax[1]; index++) {
/*      */             
/* 1253 */             UUID fsID = (UUID)this.displayedWPShareList.get(index);
/* 1254 */             LOTRFellowshipClient fs = LOTRLevelData.getData(this.mc.thePlayer).getClientFellowshipByID(fsID);
/* 1255 */             if (fs != null) {
/*      */               
/* 1257 */               this.fellowshipDrawGUI.drawFellowshipEntry(fs, fsX, fsY, i, j, false, fullWidth);
/*      */               
/* 1259 */               int iconRemoveX = fsX + this.fellowshipDrawGUI.xSize + iconGap;
/* 1260 */               int iconRemoveY = fsY;
/* 1261 */               boolean mouseOverRemove = false;
/* 1262 */               if (fs == this.fellowshipDrawGUI.getMouseOverFellowship()) {
/*      */                 
/* 1264 */                 mouseOverRemove = (i >= iconRemoveX && i <= iconRemoveX + iconWidth && j >= iconRemoveY && j <= iconRemoveY + iconWidth);
/* 1265 */                 if (mouseOverRemove)
/*      */                 {
/* 1267 */                   this.mouseOverRemoveSharedFellowship = fs;
/*      */                 }
/*      */               } 
/*      */               
/* 1271 */               this.mc.getTextureManager().bindTexture(LOTRGuiFellowships.iconsTextures);
/* 1272 */               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1273 */               drawTexturedModalRect(iconRemoveX, iconRemoveY, 8, 16 + (mouseOverRemove ? 0 : iconWidth), iconWidth, iconWidth);
/*      */               
/* 1275 */               stringY += this.mc.fontRenderer.FONT_HEIGHT + 5;
/* 1276 */               fsY = stringY;
/*      */             } 
/*      */           } 
/*      */           
/* 1280 */           if (this.scrollPaneWPShares.hasScrollBar)
/*      */           {
/* 1282 */             this.scrollPaneWPShares.drawScrollBar();
/*      */           }
/*      */         } 
/*      */         
/* 1286 */         if (this.creatingWaypointNew || this.renamingWaypoint || this.sharingWaypointNew) {
/*      */           
/* 1288 */           this.nameWPTextField.xPosition = (rectX0 + rectX1) / 2 - this.nameWPTextField.width / 2;
/* 1289 */           this.nameWPTextField.yPosition = stringY;
/*      */           
/* 1291 */           GL11.glPushMatrix();
/* 1292 */           GL11.glTranslatef(0.0F, 0.0F, this.zLevel);
/* 1293 */           this.nameWPTextField.drawTextBox();
/* 1294 */           GL11.glPopMatrix();
/*      */           
/* 1296 */           if (this.sharingWaypointNew && isFellowshipAlreadyShared(this.nameWPTextField.getText(), (LOTRCustomWaypoint)this.selectedWaypoint)) {
/*      */             
/* 1298 */             String alreadyShared = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.shareNew.already");
/* 1299 */             int sx = this.nameWPTextField.xPosition + this.nameWPTextField.width + 6;
/* 1300 */             int sy = this.nameWPTextField.yPosition + this.nameWPTextField.height / 2 - this.fontRendererObj.FONT_HEIGHT / 2;
/* 1301 */             this.fontRendererObj.drawString(alreadyShared, sx, sy, 16711680);
/*      */           } 
/*      */           
/* 1304 */           stringY += this.nameWPTextField.height + this.mc.fontRenderer.FONT_HEIGHT;
/*      */         } 
/*      */         
/* 1307 */         stringY += this.mc.fontRenderer.FONT_HEIGHT;
/* 1308 */         if (this.buttonOverlayFunction.visible) {
/*      */           
/* 1310 */           this.buttonOverlayFunction.enabled = true;
/* 1311 */           if (this.creatingWaypointNew || this.renamingWaypoint)
/*      */           {
/* 1313 */             this.buttonOverlayFunction.enabled = isValidWaypointName(this.nameWPTextField.getText());
/*      */           }
/* 1315 */           if (this.sharingWaypointNew)
/*      */           {
/* 1317 */             this.buttonOverlayFunction.enabled = isExistingUnsharedFellowshipName(this.nameWPTextField.getText(), (LOTRCustomWaypoint)this.selectedWaypoint);
/*      */           }
/*      */           
/* 1320 */           this.buttonOverlayFunction.xPosition = stringX - this.buttonOverlayFunction.width / 2;
/* 1321 */           this.buttonOverlayFunction.yPosition = stringY;
/* 1322 */           if (this.sharingWaypoint)
/*      */           {
/* 1324 */             this.buttonOverlayFunction.yPosition = rectY1 - 3 - this.mc.fontRenderer.FONT_HEIGHT - this.buttonOverlayFunction.height;
/*      */           }
/*      */           
/* 1327 */           this.buttonOverlayFunction.drawButton(this.mc, i, j);
/*      */         } 
/*      */       } 
/*      */       
/* 1331 */       GL11.glTranslatef(0.0F, 0.0F, -500.0F);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void setupScrollBars(int i, int j) {
/* 1337 */     maxDisplayedWPShares = fullscreen ? 8 : 5;
/*      */     
/* 1339 */     if (this.selectedWaypoint != null && this.hasOverlay && this.sharingWaypoint) {
/*      */       
/* 1341 */       this.displayedWPShareList = ((LOTRCustomWaypoint)this.selectedWaypoint).getSharedFellowshipIDs();
/* 1342 */       this.displayedWPShares = this.displayedWPShareList.size();
/*      */       
/* 1344 */       this.scrollPaneWPShares.hasScrollBar = false;
/* 1345 */       if (this.displayedWPShares > maxDisplayedWPShares)
/*      */       {
/* 1347 */         this.displayedWPShares = maxDisplayedWPShares;
/* 1348 */         this.scrollPaneWPShares.hasScrollBar = true;
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/* 1353 */       this.displayedWPShareList = null;
/* 1354 */       this.displayedWPShares = 0;
/* 1355 */       this.scrollPaneWPShares.hasScrollBar = false;
/* 1356 */       this.scrollPaneWPShares.mouseDragScroll(i, j);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void renderMapAndOverlay(boolean sepia, float alpha, boolean overlay) {
/* 1362 */     mapXMin_W = mapXMin;
/* 1363 */     mapXMax_W = mapXMax;
/* 1364 */     mapYMin_W = mapYMin;
/* 1365 */     mapYMax_W = mapYMax;
/*      */     
/* 1367 */     float mapScaleX = mapWidth / this.zoomScale;
/* 1368 */     float mapScaleY = mapHeight / this.zoomScale;
/* 1369 */     double minU = (this.posX - mapScaleX / 2.0F) / LOTRGenLayerWorld.imageWidth;
/* 1370 */     double maxU = (this.posX + mapScaleX / 2.0F) / LOTRGenLayerWorld.imageWidth;
/* 1371 */     double minV = (this.posY - mapScaleY / 2.0F) / LOTRGenLayerWorld.imageHeight;
/* 1372 */     double maxV = (this.posY + mapScaleY / 2.0F) / LOTRGenLayerWorld.imageHeight;
/*      */     
/* 1374 */     if (minU < 0.0D) {
/*      */       
/* 1376 */       mapXMin_W = mapXMin + (int)Math.round((0.0D - minU) * LOTRGenLayerWorld.imageWidth * this.zoomScale);
/* 1377 */       minU = 0.0D;
/* 1378 */       if (maxU < 0.0D) {
/*      */         
/* 1380 */         maxU = 0.0D;
/* 1381 */         mapXMax_W = mapXMin_W;
/*      */       } 
/*      */     } 
/* 1384 */     if (maxU > 1.0D) {
/*      */       
/* 1386 */       mapXMax_W = mapXMax - (int)Math.round((maxU - 1.0D) * LOTRGenLayerWorld.imageWidth * this.zoomScale);
/* 1387 */       maxU = 1.0D;
/* 1388 */       if (minU > 1.0D) {
/*      */         
/* 1390 */         minU = 1.0D;
/* 1391 */         mapXMin_W = mapXMax_W;
/*      */       } 
/*      */     } 
/* 1394 */     if (minV < 0.0D) {
/*      */       
/* 1396 */       mapYMin_W = mapYMin + (int)Math.round((0.0D - minV) * LOTRGenLayerWorld.imageHeight * this.zoomScale);
/* 1397 */       minV = 0.0D;
/* 1398 */       if (maxV < 0.0D) {
/*      */         
/* 1400 */         maxV = 0.0D;
/* 1401 */         mapYMax_W = mapYMin_W;
/*      */       } 
/*      */     } 
/* 1404 */     if (maxV > 1.0D) {
/*      */       
/* 1406 */       mapYMax_W = mapYMax - (int)Math.round((maxV - 1.0D) * LOTRGenLayerWorld.imageHeight * this.zoomScale);
/* 1407 */       maxV = 1.0D;
/* 1408 */       if (minV > 1.0D) {
/*      */         
/* 1410 */         minV = 1.0D;
/* 1411 */         mapYMin_W = mapYMax_W;
/*      */       } 
/*      */     } 
/*      */     
/* 1415 */     LOTRTextures.drawMap(this.mc.thePlayer, sepia, mapXMin_W, mapXMax_W, mapYMin_W, mapYMax_W, this.zLevel, minU, maxU, minV, maxV, alpha);
/* 1416 */     if (overlay && !isOSRS())
/*      */     {
/* 1418 */       LOTRTextures.drawMapOverlay(this.mc.thePlayer, mapXMin, mapXMax, mapYMin, mapYMax, this.zLevel, minU, maxU, minV, maxV);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderGraduatedRects(int x1, int y1, int x2, int y2, int c1, int c2, int w) {
/* 1424 */     float[] rgb1 = (new Color(c1)).getColorComponents(null);
/* 1425 */     float[] rgb2 = (new Color(c2)).getColorComponents(null);
/*      */     
/* 1427 */     for (int l = w - 1; l >= 0; l--) {
/*      */       
/* 1429 */       float f = l / (w - 1);
/* 1430 */       float r = rgb1[0] + (rgb2[0] - rgb1[0]) * f;
/* 1431 */       float g = rgb1[1] + (rgb2[1] - rgb1[1]) * f;
/* 1432 */       float b = rgb1[2] + (rgb2[2] - rgb1[2]) * f;
/* 1433 */       int color = (new Color(r, g, b)).getRGB() + -16777216;
/* 1434 */       drawRect(x1 - l, y1 - l, x2 + l, y2 + l, color);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderMapWidgets(int mouseX, int mouseY) {
/* 1440 */     this.widgetAddCWP.visible = (!this.hasOverlay && isMiddleEarth());
/* 1441 */     this.widgetDelCWP.visible = (!this.hasOverlay && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint && !((LOTRCustomWaypoint)this.selectedWaypoint).isShared());
/* 1442 */     this.widgetRenameCWP.visible = (!this.hasOverlay && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint && !((LOTRCustomWaypoint)this.selectedWaypoint).isShared());
/* 1443 */     this.widgetToggleWPs.visible = !this.hasOverlay;
/* 1444 */     this.widgetToggleWPs.setTexVIndex(showWP ? 0 : 1);
/* 1445 */     this.widgetToggleCWPs.visible = !this.hasOverlay;
/* 1446 */     this.widgetToggleCWPs.setTexVIndex(showCWP ? 0 : 1);
/* 1447 */     this.widgetToggleHiddenSWPs.visible = !this.hasOverlay;
/* 1448 */     this.widgetToggleHiddenSWPs.setTexVIndex(showHiddenSWP ? 1 : 0);
/* 1449 */     this.widgetZoomIn.visible = !this.hasOverlay;
/* 1450 */     this.widgetZoomIn.setTexVIndex((zoomPower < 4) ? 0 : 1);
/* 1451 */     this.widgetZoomOut.visible = !this.hasOverlay;
/* 1452 */     this.widgetZoomOut.setTexVIndex((zoomPower > -3) ? 0 : 1);
/* 1453 */     this.widgetFullScreen.visible = !this.hasOverlay;
/* 1454 */     this.widgetSepia.visible = !this.hasOverlay;
/* 1455 */     this.widgetLabels.visible = !this.hasOverlay;
/* 1456 */     this.widgetShareCWP.visible = (!this.hasOverlay && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint && !((LOTRCustomWaypoint)this.selectedWaypoint).isShared());
/* 1457 */     this.widgetHideSWP.visible = (!this.hasOverlay && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint && ((LOTRCustomWaypoint)this.selectedWaypoint).isShared() && !((LOTRCustomWaypoint)this.selectedWaypoint).isSharedHidden());
/* 1458 */     this.widgetUnhideSWP.visible = (!this.hasOverlay && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint && ((LOTRCustomWaypoint)this.selectedWaypoint).isShared() && ((LOTRCustomWaypoint)this.selectedWaypoint).isSharedHidden());
/*      */     
/* 1460 */     LOTRGuiMapWidget mouseOverWidget = null;
/* 1461 */     for (LOTRGuiMapWidget widget : mapWidgets) {
/*      */       
/* 1463 */       if (widget.visible) {
/*      */         
/* 1465 */         this.mc.getTextureManager().bindTexture(mapIconsTexture);
/* 1466 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1467 */         drawTexturedModalRect(mapXMin + widget.getMapXPos(mapWidth), mapYMin + widget.getMapYPos(mapHeight), widget.getTexU(), widget.getTexV(), widget.width, widget.width);
/*      */         
/* 1469 */         if (widget.isMouseOver(mouseX - mapXMin, mouseY - mapYMin, mapWidth, mapHeight))
/*      */         {
/* 1471 */           mouseOverWidget = widget;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1476 */     if (mouseOverWidget != null) {
/*      */       
/* 1478 */       float z = this.zLevel;
/*      */       
/* 1480 */       int stringWidth = 200;
/* 1481 */       List desc = this.fontRendererObj.listFormattedStringToWidth(mouseOverWidget.getTranslatedName(), stringWidth);
/* 1482 */       func_146283_a(desc, mouseX, mouseY);
/* 1483 */       GL11.glDisable(2896);
/* 1484 */       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*      */       
/* 1486 */       this.zLevel = z;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderFullscreenSubtitles(String... lines) {
/* 1492 */     int strX = mapXMin + mapWidth / 2;
/* 1493 */     int strY = mapYMax + 7;
/* 1494 */     if (this.isConquestGrid)
/*      */     {
/* 1496 */       strY = mapYMax + 26;
/*      */     }
/* 1498 */     int border = this.fontRendererObj.FONT_HEIGHT + 3;
/*      */     
/* 1500 */     if (lines.length == 1)
/*      */     {
/* 1502 */       strY += border / 2;
/*      */     }
/*      */     
/* 1505 */     for (String s : lines) {
/*      */       
/* 1507 */       drawCenteredString(s, strX, strY, 16777215);
/* 1508 */       strY += border;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderPlayers(int mouseX, int mouseY) {
/* 1514 */     String mouseOverPlayerName = null;
/* 1515 */     double mouseOverPlayerX = 0.0D;
/* 1516 */     double mouseOverPlayerY = 0.0D;
/* 1517 */     double distanceMouseOverPlayer = Double.MAX_VALUE;
/* 1518 */     int iconWidthHalf = 4;
/*      */     
/* 1520 */     Map<UUID, PlayerLocationInfo> playersToRender = new HashMap<UUID, PlayerLocationInfo>();
/* 1521 */     playersToRender.putAll(playerLocations);
/* 1522 */     if (isMiddleEarth())
/*      */     {
/* 1524 */       playersToRender.put(this.mc.thePlayer.getUniqueID(), new PlayerLocationInfo(this.mc.thePlayer.getGameProfile(), this.mc.thePlayer.posX, this.mc.thePlayer.posZ));
/*      */     }
/*      */     
/* 1527 */     for (Map.Entry<UUID, PlayerLocationInfo> entry : playersToRender.entrySet()) {
/*      */       
/* 1529 */       UUID player = (UUID)entry.getKey();
/* 1530 */       PlayerLocationInfo info = (PlayerLocationInfo)entry.getValue();
/* 1531 */       GameProfile profile = info.profile;
/* 1532 */       String playerName = profile.getName();
/* 1533 */       double playerPosX = info.posX;
/* 1534 */       double playerPosZ = info.posZ;
/* 1535 */       float[] pos = transformCoords(playerPosX, playerPosZ);
/* 1536 */       int playerX = Math.round(pos[0]);
/* 1537 */       int playerY = Math.round(pos[1]);
/*      */       
/* 1539 */       double distToPlayer = renderPlayerIcon(profile, playerName, playerX, playerY, mouseX, mouseY);
/* 1540 */       if (distToPlayer <= (iconWidthHalf + 3))
/*      */       {
/* 1542 */         if (distToPlayer <= distanceMouseOverPlayer) {
/*      */           
/* 1544 */           mouseOverPlayerName = playerName;
/* 1545 */           mouseOverPlayerX = playerX;
/* 1546 */           mouseOverPlayerY = playerY;
/* 1547 */           distanceMouseOverPlayer = distToPlayer;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1552 */     if (mouseOverPlayerName != null && !this.hasOverlay && !this.loadingConquestGrid) {
/*      */       
/* 1554 */       int strWidth = this.mc.fontRenderer.getStringWidth(mouseOverPlayerName);
/* 1555 */       int strHeight = this.mc.fontRenderer.FONT_HEIGHT;
/*      */       
/* 1557 */       int rectX = (int)Math.round(mouseOverPlayerX);
/* 1558 */       int rectY = (int)Math.round(mouseOverPlayerY);
/* 1559 */       rectY += iconWidthHalf + 3;
/*      */       
/* 1561 */       int border = 3;
/* 1562 */       int rectWidth = strWidth + border * 2;
/* 1563 */       rectX -= rectWidth / 2;
/* 1564 */       int rectHeight = strHeight + border * 2;
/*      */       
/* 1566 */       int mapBorder2 = 2;
/* 1567 */       rectX = Math.max(rectX, mapXMin + mapBorder2);
/* 1568 */       rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
/* 1569 */       rectY = Math.max(rectY, mapYMin + mapBorder2);
/* 1570 */       rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
/*      */       
/* 1572 */       GL11.glTranslatef(0.0F, 0.0F, 300.0F);
/* 1573 */       drawFancyRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
/* 1574 */       this.mc.fontRenderer.drawString(mouseOverPlayerName, rectX + border, rectY + border, 16777215);
/* 1575 */       GL11.glTranslatef(0.0F, 0.0F, -300.0F);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private double renderPlayerIcon(GameProfile profile, String playerName, double playerX, double playerY, int mouseX, int mouseY) {
/* 1581 */     Tessellator tessellator = Tessellator.instance;
/*      */     
/* 1583 */     int iconWidthHalf = 4;
/* 1584 */     int iconBorder = iconWidthHalf + 1;
/*      */     
/* 1586 */     playerX = Math.max((mapXMin + iconBorder), playerX);
/* 1587 */     playerX = Math.min((mapXMax - iconBorder - 1), playerX);
/* 1588 */     playerY = Math.max((mapYMin + iconBorder), playerY);
/* 1589 */     playerY = Math.min((mapYMax - iconBorder - 1), playerY);
/*      */     
/* 1591 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*      */     
/* 1593 */     ResourceLocation skin = AbstractClientPlayer.locationStevePng;
/* 1594 */     if (profile.getId().equals(this.mc.thePlayer.getUniqueID())) {
/*      */       
/* 1596 */       skin = this.mc.thePlayer.getLocationSkin();
/*      */     }
/*      */     else {
/*      */       
/* 1600 */       Map map = this.mc.func_152342_ad().func_152788_a(profile);
/* 1601 */       MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.SKIN;
/* 1602 */       if (map.containsKey(type))
/*      */       {
/* 1604 */         skin = this.mc.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(type), type);
/*      */       }
/*      */     } 
/* 1607 */     this.mc.getTextureManager().bindTexture(skin);
/*      */     
/* 1609 */     double iconMinU = 0.125D;
/* 1610 */     double iconMaxU = 0.25D;
/* 1611 */     double iconMinV = 0.25D;
/* 1612 */     double iconMaxV = 0.5D;
/*      */     
/* 1614 */     double playerX_d = playerX + 0.5D;
/* 1615 */     double playerY_d = playerY + 0.5D;
/*      */     
/* 1617 */     tessellator.startDrawingQuads();
/* 1618 */     tessellator.addVertexWithUV(playerX_d - iconWidthHalf, playerY_d + iconWidthHalf, this.zLevel, iconMinU, iconMaxV);
/* 1619 */     tessellator.addVertexWithUV(playerX_d + iconWidthHalf, playerY_d + iconWidthHalf, this.zLevel, iconMaxU, iconMaxV);
/* 1620 */     tessellator.addVertexWithUV(playerX_d + iconWidthHalf, playerY_d - iconWidthHalf, this.zLevel, iconMaxU, iconMinV);
/* 1621 */     tessellator.addVertexWithUV(playerX_d - iconWidthHalf, playerY_d - iconWidthHalf, this.zLevel, iconMinU, iconMinV);
/* 1622 */     tessellator.draw();
/*      */     
/* 1624 */     iconMinU = 0.625D;
/* 1625 */     iconMaxU = 0.75D;
/* 1626 */     iconMinV = 0.25D;
/* 1627 */     iconMaxV = 0.5D;
/*      */     
/* 1629 */     tessellator.startDrawingQuads();
/* 1630 */     tessellator.addVertexWithUV(playerX_d - iconWidthHalf - 0.5D, playerY_d + iconWidthHalf + 0.5D, this.zLevel, iconMinU, iconMaxV);
/* 1631 */     tessellator.addVertexWithUV(playerX_d + iconWidthHalf + 0.5D, playerY_d + iconWidthHalf + 0.5D, this.zLevel, iconMaxU, iconMaxV);
/* 1632 */     tessellator.addVertexWithUV(playerX_d + iconWidthHalf + 0.5D, playerY_d - iconWidthHalf - 0.5D, this.zLevel, iconMaxU, iconMinV);
/* 1633 */     tessellator.addVertexWithUV(playerX_d - iconWidthHalf - 0.5D, playerY_d - iconWidthHalf - 0.5D, this.zLevel, iconMinU, iconMinV);
/* 1634 */     tessellator.draw();
/*      */     
/* 1636 */     double dx = playerX - mouseX;
/* 1637 */     double dy = playerY - mouseY;
/* 1638 */     return Math.sqrt(dx * dx + dy * dy);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void renderMiniQuests(EntityPlayer entityplayer, int mouseX, int mouseY) {
/* 1644 */     if (this.hasOverlay) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1649 */     LOTRMiniQuest mouseOverQuest = null;
/* 1650 */     int mouseOverQuestX = 0;
/* 1651 */     int mouseOverQuestY = 0;
/* 1652 */     double distanceMouseOverQuest = Double.MAX_VALUE;
/*      */     
/* 1654 */     List<LOTRMiniQuest> quests = LOTRLevelData.getData(entityplayer).getActiveMiniQuests();
/* 1655 */     for (LOTRMiniQuest quest : quests) {
/*      */       
/* 1657 */       ChunkCoordinates location = quest.getLastLocation();
/* 1658 */       if (location != null) {
/*      */         
/* 1660 */         float[] pos = transformCoords(location.posX, location.posZ);
/* 1661 */         int questX = Math.round(pos[0]);
/* 1662 */         int questY = Math.round(pos[1]);
/*      */         
/* 1664 */         float scale = 0.5F;
/* 1665 */         float invScale = 1.0F / scale;
/*      */         
/* 1667 */         IIcon icon = questBookIcon.getIconIndex();
/* 1668 */         int iconWidthHalf = icon.getIconWidth() / 2;
/* 1669 */         iconWidthHalf = (int)(iconWidthHalf * scale);
/* 1670 */         int iconBorder = iconWidthHalf + 1;
/*      */         
/* 1672 */         questX = Math.max(mapXMin + iconBorder, questX);
/* 1673 */         questX = Math.min(mapXMax - iconBorder - 1, questX);
/* 1674 */         questY = Math.max(mapYMin + iconBorder, questY);
/* 1675 */         questY = Math.min(mapYMax - iconBorder - 1, questY);
/*      */         
/* 1677 */         int iconX = Math.round(questX * invScale);
/* 1678 */         int iconY = Math.round(questY * invScale);
/* 1679 */         iconX -= iconWidthHalf;
/* 1680 */         iconY -= iconWidthHalf;
/*      */         
/* 1682 */         GL11.glScalef(scale, scale, scale);
/* 1683 */         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1684 */         GL11.glEnable(2896);
/* 1685 */         GL11.glEnable(2884);
/* 1686 */         renderItem.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), questBookIcon, iconX, iconY);
/* 1687 */         GL11.glDisable(2896);
/* 1688 */         GL11.glEnable(3008);
/* 1689 */         GL11.glScalef(invScale, invScale, invScale);
/*      */         
/* 1691 */         double dx = (questX - mouseX);
/* 1692 */         double dy = (questY - mouseY);
/* 1693 */         double distToQuest = Math.sqrt(dx * dx + dy * dy);
/* 1694 */         if (distToQuest <= (iconWidthHalf + 3))
/*      */         {
/* 1696 */           if (distToQuest <= distanceMouseOverQuest) {
/*      */             
/* 1698 */             mouseOverQuest = quest;
/* 1699 */             mouseOverQuestX = questX;
/* 1700 */             mouseOverQuestY = questY;
/* 1701 */             distanceMouseOverQuest = distToQuest;
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1707 */     if (mouseOverQuest != null && !this.hasOverlay) {
/*      */       
/* 1709 */       String name = mouseOverQuest.entityName;
/* 1710 */       int stringWidth = this.mc.fontRenderer.getStringWidth(name);
/* 1711 */       int stringHeight = this.mc.fontRenderer.FONT_HEIGHT;
/*      */       
/* 1713 */       int x = mouseOverQuestX;
/* 1714 */       int y = mouseOverQuestY;
/* 1715 */       y += 7;
/*      */       
/* 1717 */       int border = 3;
/* 1718 */       int rectWidth = stringWidth + border * 2;
/* 1719 */       x -= rectWidth / 2;
/* 1720 */       int rectHeight = stringHeight + border * 2;
/*      */       
/* 1722 */       int mapBorder2 = 2;
/* 1723 */       x = Math.max(x, mapXMin + mapBorder2);
/* 1724 */       x = Math.min(x, mapXMax - mapBorder2 - rectWidth);
/* 1725 */       y = Math.max(y, mapYMin + mapBorder2);
/* 1726 */       y = Math.min(y, mapYMax - mapBorder2 - rectHeight);
/*      */       
/* 1728 */       GL11.glTranslatef(0.0F, 0.0F, 300.0F);
/* 1729 */       drawFancyRect(x, y, x + rectWidth, y + rectHeight);
/* 1730 */       this.mc.fontRenderer.drawString(name, x + border, y + border, 16777215);
/* 1731 */       GL11.glTranslatef(0.0F, 0.0F, -300.0F);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderControlZone(int mouseX, int mouseY) {
/* 1737 */     this.mouseControlZone = false;
/* 1738 */     this.mouseControlZoneReduced = false;
/*      */     
/* 1740 */     LOTRFaction faction = this.controlZoneFaction;
/* 1741 */     if (faction.factionDimension == LOTRDimension.MIDDLE_EARTH) {
/*      */       
/* 1743 */       List<LOTRControlZone> controlZones = faction.getControlZones();
/* 1744 */       if (!controlZones.isEmpty()) {
/*      */         
/* 1746 */         Tessellator tessellator = Tessellator.instance;
/* 1747 */         setupMapClipping();
/* 1748 */         GL11.glDisable(3553);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1755 */         for (int pass = 0; pass <= 2; pass++) {
/*      */           
/* 1757 */           int color = 16711680;
/* 1758 */           if (pass == 1)
/*      */           {
/* 1760 */             color = 5570560;
/*      */           }
/* 1762 */           if (pass == 0)
/*      */           {
/* 1764 */             color = 16733525;
/*      */           }
/*      */           
/* 1767 */           for (LOTRControlZone zone : controlZones) {
/*      */             
/* 1769 */             float radius = zone.radius;
/* 1770 */             if (pass == 2)
/*      */             {
/* 1772 */               radius--;
/*      */             }
/* 1774 */             if (pass == 0)
/*      */             {
/* 1776 */               radius = (zone.radius + this.controlZoneFaction.getControlZoneReducedRange());
/*      */             }
/* 1778 */             float radiusWorld = LOTRWaypoint.mapToWorldR(radius);
/*      */             
/* 1780 */             tessellator.startDrawing(9);
/* 1781 */             tessellator.setColorOpaque_I(color);
/* 1782 */             int sides = 100;
/* 1783 */             for (int l = sides - 1; l >= 0; l--) {
/*      */               
/* 1785 */               float angle = l / sides * 2.0F * 3.1415927F;
/*      */               
/* 1787 */               double x = zone.xCoord;
/* 1788 */               double z = zone.zCoord;
/* 1789 */               x += (MathHelper.cos(angle) * radiusWorld);
/* 1790 */               z += (MathHelper.sin(angle) * radiusWorld);
/* 1791 */               float[] trans = transformCoords(x, z);
/* 1792 */               tessellator.addVertex(trans[0], trans[1], this.zLevel);
/*      */             } 
/* 1794 */             tessellator.draw();
/*      */             
/* 1796 */             if (!this.mouseControlZone || !this.mouseControlZoneReduced) {
/*      */               
/* 1798 */               float[] trans = transformCoords(zone.xCoord, zone.zCoord);
/* 1799 */               float dx = mouseX - trans[0];
/* 1800 */               float dy = mouseY - trans[1];
/* 1801 */               float rScaled = radius * this.zoomScale;
/* 1802 */               if (dx * dx + dy * dy <= rScaled * rScaled) {
/*      */                 
/* 1804 */                 if (pass >= 1) {
/*      */                   
/* 1806 */                   this.mouseControlZone = true; continue;
/*      */                 } 
/* 1808 */                 if (pass == 0)
/*      */                 {
/* 1810 */                   this.mouseControlZoneReduced = true;
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/* 1817 */         GL11.glEnable(3553);
/* 1818 */         endMapClipping();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderRoads() {
/* 1825 */     if ((!showWP && !showCWP) || !LOTRFixedStructures.hasMapFeatures(this.mc.theWorld)) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1830 */     renderRoads(hasMapLabels());
/*      */   }
/*      */ 
/*      */   
/*      */   public void renderRoads(boolean labels) {
/* 1835 */     float roadZoomlerp = (this.zoomExp - -3.3F) / 2.2F;
/* 1836 */     roadZoomlerp = Math.min(roadZoomlerp, 1.0F);
/* 1837 */     if (!this.enableZoomOutWPFading)
/*      */     {
/* 1839 */       roadZoomlerp = 1.0F;
/*      */     }
/*      */     
/* 1842 */     if (roadZoomlerp > 0.0F)
/*      */     {
/* 1844 */       for (LOTRRoads road : LOTRRoads.allRoads) {
/*      */         
/* 1846 */         int interval = Math.round(400.0F / this.zoomScaleStable);
/* 1847 */         interval = Math.max(interval, 1); int i;
/* 1848 */         for (i = 0; i < road.roadPoints.length; i += interval) {
/*      */           
/* 1850 */           LOTRRoads.RoadPoint point = road.roadPoints[i];
/* 1851 */           float[] pos = transformCoords(point.x, point.z);
/* 1852 */           float x = pos[0];
/* 1853 */           float y = pos[1];
/*      */           
/* 1855 */           if (x >= mapXMin && x < mapXMax && y >= mapYMin && y < mapYMax) {
/*      */             
/* 1857 */             double roadWidth = 1.0D;
/* 1858 */             int roadColor = 0;
/* 1859 */             float roadAlpha = roadZoomlerp;
/*      */             
/* 1861 */             if (isOSRS()) {
/*      */               
/* 1863 */               roadWidth = 3.0D * this.zoomScale;
/* 1864 */               roadColor = 6575407;
/* 1865 */               roadAlpha = 1.0F;
/*      */             } 
/*      */             
/* 1868 */             double roadWidthLess1 = roadWidth - 1.0D;
/*      */             
/* 1870 */             GL11.glDisable(3553);
/* 1871 */             GL11.glEnable(3042);
/* 1872 */             GL11.glBlendFunc(770, 771);
/*      */             
/* 1874 */             Tessellator tessellator = Tessellator.instance;
/* 1875 */             tessellator.startDrawingQuads();
/* 1876 */             tessellator.setColorRGBA_I(roadColor, (int)(roadAlpha * 255.0F));
/* 1877 */             tessellator.addVertex(x - roadWidthLess1, y + roadWidth, this.zLevel);
/* 1878 */             tessellator.addVertex(x + roadWidth, y + roadWidth, this.zLevel);
/* 1879 */             tessellator.addVertex(x + roadWidth, y - roadWidthLess1, this.zLevel);
/* 1880 */             tessellator.addVertex(x - roadWidthLess1, y - roadWidthLess1, this.zLevel);
/* 1881 */             tessellator.draw();
/*      */             
/* 1883 */             GL11.glDisable(3042);
/* 1884 */             GL11.glEnable(3553);
/*      */           } 
/*      */           
/* 1887 */           if (labels) {
/*      */             
/* 1889 */             int clip = 200;
/* 1890 */             if (x >= (mapXMin - clip) && x <= (mapXMax + clip) && y >= (mapYMin - clip) && y <= (mapYMax + clip)) {
/*      */               
/* 1892 */               float zoomlerp = (this.zoomExp - -1.0F) / 4.0F;
/* 1893 */               zoomlerp = Math.min(zoomlerp, 1.0F);
/* 1894 */               float scale = zoomlerp;
/*      */               
/* 1896 */               String name = road.getDisplayName();
/* 1897 */               int nameWidth = this.fontRendererObj.getStringWidth(name);
/* 1898 */               int nameInterval = (int)((nameWidth * 2 + 100) * 200.0F / this.zoomScaleStable);
/* 1899 */               if (i % nameInterval < interval) {
/*      */                 
/* 1901 */                 boolean endNear = false;
/* 1902 */                 double dMax = (nameWidth / 2.0D + 25.0D) * scale;
/* 1903 */                 double dMaxSq = dMax * dMax;
/* 1904 */                 for (LOTRRoads.RoadPoint end : road.endpoints) {
/*      */                   
/* 1906 */                   float[] endPos = transformCoords(end.x, end.z);
/* 1907 */                   float endX = endPos[0];
/* 1908 */                   float endY = endPos[1];
/* 1909 */                   float dx = x - endX;
/* 1910 */                   float dy = y - endY;
/* 1911 */                   double dSq = (dx * dx + dy * dy);
/* 1912 */                   if (dSq < dMaxSq)
/*      */                   {
/* 1914 */                     endNear = true;
/*      */                   }
/*      */                 } 
/*      */                 
/* 1918 */                 if (!endNear && zoomlerp > 0.0F) {
/*      */                   
/* 1920 */                   setupMapClipping();
/*      */                   
/* 1922 */                   GL11.glPushMatrix();
/* 1923 */                   GL11.glTranslatef(x, y, 0.0F);
/*      */                   
/* 1925 */                   GL11.glScalef(scale, scale, scale);
/* 1926 */                   LOTRRoads.RoadPoint nextPoint = road.roadPoints[Math.min(i + 1, road.roadPoints.length - 1)];
/* 1927 */                   LOTRRoads.RoadPoint prevPoint = road.roadPoints[Math.max(i - 1, 0)];
/* 1928 */                   double grad = (nextPoint.z - prevPoint.z) / (nextPoint.x - prevPoint.x);
/* 1929 */                   float angle = (float)Math.atan(grad);
/* 1930 */                   angle = (float)Math.toDegrees(angle);
/* 1931 */                   if (Math.abs(angle) > 90.0F)
/*      */                   {
/* 1933 */                     angle += 180.0F;
/*      */                   }
/* 1935 */                   GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
/*      */                   
/* 1937 */                   float alpha = zoomlerp;
/* 1938 */                   alpha *= 0.8F;
/* 1939 */                   int alphaI = LOTRClientProxy.getAlphaInt(alpha);
/* 1940 */                   GL11.glEnable(3042);
/* 1941 */                   GL11.glBlendFunc(770, 771);
/*      */                   
/* 1943 */                   int strX = -nameWidth / 2;
/* 1944 */                   int strY = -15;
/* 1945 */                   this.fontRendererObj.drawString(name, strX + 1, strY + 1, 0 + (alphaI << 24));
/* 1946 */                   this.fontRendererObj.drawString(name, strX, strY, 16777215 + (alphaI << 24));
/*      */                   
/* 1948 */                   GL11.glDisable(3042);
/* 1949 */                   GL11.glPopMatrix();
/*      */                   
/* 1951 */                   endMapClipping();
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isWaypointVisible(LOTRAbstractWaypoint wp) {
/* 1963 */     if (wp instanceof LOTRCustomWaypoint) {
/*      */       
/* 1965 */       LOTRCustomWaypoint cwp = (LOTRCustomWaypoint)wp;
/* 1966 */       if (cwp.isShared())
/*      */       {
/* 1968 */         if (cwp.isSharedHidden() && !showHiddenSWP)
/*      */         {
/* 1970 */           return false;
/*      */         }
/*      */       }
/* 1973 */       return showCWP;
/*      */     } 
/* 1975 */     return showWP;
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderWaypoints(int pass, int mouseX, int mouseY) {
/* 1980 */     if ((!showWP && !showCWP && !showHiddenSWP) || !LOTRFixedStructures.hasMapFeatures(this.mc.theWorld)) {
/*      */       return;
/*      */     }
/*      */     
/* 1984 */     renderWaypoints(LOTRLevelData.getData(this.mc.thePlayer).getAllAvailableWaypoints(), pass, mouseX, mouseY, hasMapLabels(), false);
/*      */   }
/*      */ 
/*      */   
/*      */   public void renderWaypoints(List<LOTRAbstractWaypoint> waypoints, int pass, int mouseX, int mouseY, boolean labels, boolean overrideToggles) {
/* 1989 */     setupMapClipping();
/*      */     
/* 1991 */     LOTRAbstractWaypoint mouseOverWP = null;
/* 1992 */     double distanceMouseOverWP = Double.MAX_VALUE;
/*      */     
/* 1994 */     float wpZoomlerp = (this.zoomExp - -3.3F) / 2.2F;
/* 1995 */     wpZoomlerp = Math.min(wpZoomlerp, 1.0F);
/* 1996 */     if (!this.enableZoomOutWPFading)
/*      */     {
/* 1998 */       wpZoomlerp = 1.0F;
/*      */     }
/*      */     
/* 2001 */     if (wpZoomlerp > 0.0F)
/*      */     {
/* 2003 */       for (LOTRAbstractWaypoint waypoint : waypoints) {
/*      */         
/* 2005 */         boolean unlocked = (this.mc.thePlayer != null && waypoint.hasPlayerUnlocked(this.mc.thePlayer));
/* 2006 */         boolean hidden = waypoint.isHidden();
/* 2007 */         boolean custom = waypoint instanceof LOTRCustomWaypoint;
/* 2008 */         boolean shared = (waypoint instanceof LOTRCustomWaypoint && ((LOTRCustomWaypoint)waypoint).isShared());
/*      */         
/* 2010 */         if ((isWaypointVisible(waypoint) || overrideToggles) && (!hidden || unlocked)) {
/*      */           
/* 2012 */           float[] pos = transformCoords(waypoint.getXCoord(), waypoint.getZCoord());
/* 2013 */           float x = pos[0];
/* 2014 */           float y = pos[1];
/*      */           
/* 2016 */           int clip = 200;
/* 2017 */           if (x >= (mapXMin - clip) && x <= (mapXMax + clip) && y >= (mapYMin - clip) && y <= (mapYMax + clip)) {
/*      */             
/* 2019 */             if (pass == 0) {
/*      */               
/* 2021 */               float wpAlpha = wpZoomlerp;
/*      */               
/* 2023 */               GL11.glEnable(3042);
/* 2024 */               GL11.glBlendFunc(770, 771);
/*      */               
/* 2026 */               if (isOSRS()) {
/*      */                 
/* 2028 */                 float osScale = 0.33F;
/* 2029 */                 GL11.glPushMatrix();
/* 2030 */                 GL11.glScalef(0.33F, 0.33F, 1.0F);
/*      */                 
/* 2032 */                 this.mc.getTextureManager().bindTexture(LOTRTextures.osrsTexture);
/* 2033 */                 GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 2034 */                 drawTexturedModalRectFloat(x / 0.33F - 8.0F, y / 0.33F - 8.0F, 0, 0, 15.0F, 15.0F);
/*      */                 
/* 2036 */                 GL11.glPopMatrix();
/*      */               }
/*      */               else {
/*      */                 
/* 2040 */                 this.mc.getTextureManager().bindTexture(mapIconsTexture);
/* 2041 */                 GL11.glColor4f(1.0F, 1.0F, 1.0F, wpAlpha);
/* 2042 */                 drawTexturedModalRectFloat(x - 2.0F, y - 2.0F, 0 + (unlocked ? 4 : 0),  (shared ? 8 : (custom ? 4 : Character.MIN_VALUE)), 4.0F, 4.0F);
/*      */               } 
/*      */               
/* 2045 */               GL11.glDisable(3042);
/*      */               
/* 2047 */               if (labels) {
/*      */                 
/* 2049 */                 float zoomlerp = (this.zoomExp - -1.0F) / 4.0F;
/* 2050 */                 zoomlerp = Math.min(zoomlerp, 1.0F);
/* 2051 */                 if (zoomlerp > 0.0F) {
/*      */                   
/* 2053 */                   GL11.glPushMatrix();
/* 2054 */                   GL11.glTranslatef(x, y, 0.0F);
/*      */                   
/* 2056 */                   float scale = zoomlerp;
/* 2057 */                   GL11.glScalef(scale, scale, scale);
/*      */                   
/* 2059 */                   float alpha = zoomlerp;
/* 2060 */                   alpha *= 0.8F;
/* 2061 */                   int alphaI = (int)(alpha * 255.0F);
/* 2062 */                   alphaI = MathHelper.clamp_int(alphaI, 4, 255);
/* 2063 */                   GL11.glEnable(3042);
/* 2064 */                   GL11.glBlendFunc(770, 771);
/*      */                   
/* 2066 */                   String s = waypoint.getDisplayName();
/* 2067 */                   int strX = -this.fontRendererObj.getStringWidth(s) / 2;
/* 2068 */                   int strY = -15;
/* 2069 */                   this.fontRendererObj.drawString(s, strX + 1, strY + 1, 0 + (alphaI << 24));
/* 2070 */                   this.fontRendererObj.drawString(s, strX, strY, 16777215 + (alphaI << 24));
/*      */                   
/* 2072 */                   GL11.glDisable(3042);
/* 2073 */                   GL11.glPopMatrix();
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */             
/* 2078 */             if (pass == 1)
/*      */             {
/* 2080 */               if (waypoint != this.selectedWaypoint)
/*      */               {
/* 2082 */                 if (x >= (mapXMin - 2) && x <= (mapXMax + 2) && y >= (mapYMin - 2) && y <= (mapYMax + 2)) {
/*      */                   
/* 2084 */                   double dx = (x - mouseX);
/* 2085 */                   double dy = (y - mouseY);
/* 2086 */                   double distToWP = Math.sqrt(dx * dx + dy * dy);
/* 2087 */                   if (distToWP <= 5.0D)
/*      */                   {
/* 2089 */                     if (distToWP <= distanceMouseOverWP) {
/*      */                       
/* 2091 */                       mouseOverWP = waypoint;
/* 2092 */                       distanceMouseOverWP = distToWP;
/*      */                     } 
/*      */                   }
/*      */                 } 
/*      */               }
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2103 */     if (pass == 1)
/*      */     {
/* 2105 */       if (mouseOverWP != null && !this.hasOverlay && !this.loadingConquestGrid)
/*      */       {
/* 2107 */         renderWaypointTooltip(mouseOverWP, false, mouseX, mouseY);
/*      */       }
/*      */     }
/*      */     
/* 2111 */     endMapClipping();
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderWaypointTooltip(LOTRAbstractWaypoint waypoint, boolean selected, int mouseX, int mouseY) {
/* 2116 */     String coords, name = waypoint.getDisplayName();
/* 2117 */     int wpX = waypoint.getXCoord();
/* 2118 */     int wpZ = waypoint.getZCoord();
/* 2119 */     int wpY = waypoint.getYCoordDisplay();
/*      */     
/* 2121 */     if (wpY >= 0) {
/*      */       
/* 2123 */       coords = StatCollector.translateToLocalFormatted("lotr.gui.map.coordsY", new Object[] { Integer.valueOf(wpX), Integer.valueOf(wpY), Integer.valueOf(wpZ) });
/*      */     }
/*      */     else {
/*      */       
/* 2127 */       coords = StatCollector.translateToLocalFormatted("lotr.gui.map.coords", new Object[] { Integer.valueOf(wpX), Integer.valueOf(wpZ) });
/*      */     } 
/*      */     
/* 2130 */     String loreText = waypoint.getLoreText(this.mc.thePlayer);
/* 2131 */     float guiScale = (new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight)).getScaleFactor();
/* 2132 */     float loreScale = guiScale - 1.0F;
/* 2133 */     if (guiScale <= 2.0F)
/*      */     {
/* 2135 */       loreScale = guiScale;
/*      */     }
/* 2137 */     float loreScaleRel = loreScale / guiScale;
/* 2138 */     float loreScaleRelInv = 1.0F / loreScaleRel;
/* 2139 */     int loreFontHeight = MathHelper.ceiling_double_int((this.fontRendererObj.FONT_HEIGHT * loreScaleRel));
/*      */     
/* 2141 */     float[] pos = transformCoords(wpX, wpZ);
/* 2142 */     int rectX = Math.round(pos[0]);
/* 2143 */     int rectY = Math.round(pos[1]);
/*      */     
/* 2145 */     rectY += 5;
/*      */     
/* 2147 */     int border = 3;
/* 2148 */     int stringHeight = this.fontRendererObj.FONT_HEIGHT;
/* 2149 */     int innerRectWidth = this.fontRendererObj.getStringWidth(name);
/* 2150 */     if (selected) {
/*      */       
/* 2152 */       innerRectWidth = Math.max(innerRectWidth, this.fontRendererObj.getStringWidth(coords));
/* 2153 */       if (loreText != null) {
/*      */         
/* 2155 */         innerRectWidth += 50;
/* 2156 */         innerRectWidth = Math.round(innerRectWidth * loreScaleRel / 0.66667F);
/*      */       } 
/*      */     } 
/*      */     
/* 2160 */     int rectWidth = innerRectWidth + border * 2;
/* 2161 */     rectX -= rectWidth / 2;
/* 2162 */     int rectHeight = border * 2 + stringHeight;
/* 2163 */     if (selected) {
/*      */       
/* 2165 */       rectHeight += border + stringHeight;
/* 2166 */       if (loreText != null) {
/*      */         
/* 2168 */         rectHeight += border;
/* 2169 */         rectHeight += this.fontRendererObj.listFormattedStringToWidth(loreText, (int)(innerRectWidth * loreScaleRelInv)).size() * loreFontHeight;
/* 2170 */         rectHeight += border;
/*      */       } 
/*      */     } 
/*      */     
/* 2174 */     int mapBorder2 = 2;
/* 2175 */     rectX = Math.max(rectX, mapXMin + mapBorder2);
/* 2176 */     rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
/* 2177 */     rectY = Math.max(rectY, mapYMin + mapBorder2);
/* 2178 */     rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
/*      */     
/* 2180 */     int stringX = rectX + rectWidth / 2;
/* 2181 */     int stringY = rectY + border;
/*      */     
/* 2183 */     GL11.glTranslatef(0.0F, 0.0F, 300.0F);
/*      */     
/* 2185 */     drawFancyRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
/* 2186 */     drawCenteredString(name, stringX, stringY, 16777215);
/* 2187 */     stringY += this.fontRendererObj.FONT_HEIGHT + border;
/* 2188 */     if (selected) {
/*      */       
/* 2190 */       drawCenteredString(coords, stringX, stringY, 16777215);
/* 2191 */       stringY += this.fontRendererObj.FONT_HEIGHT + border * 2;
/*      */       
/* 2193 */       if (loreText != null) {
/*      */         
/* 2195 */         GL11.glPushMatrix();
/* 2196 */         GL11.glScalef(loreScaleRel, loreScaleRel, 1.0F);
/* 2197 */         List loreLines = this.fontRendererObj.listFormattedStringToWidth(loreText, (int)(innerRectWidth * loreScaleRelInv));
/* 2198 */         for (int l = 0; l < loreLines.size(); l++) {
/*      */           
/* 2200 */           String line = (String)loreLines.get(l);
/* 2201 */           drawCenteredString(line, (int)(stringX * loreScaleRelInv), (int)(stringY * loreScaleRelInv), 16777215);
/* 2202 */           stringY += loreFontHeight;
/*      */         } 
/* 2204 */         GL11.glPopMatrix();
/*      */       } 
/*      */     } 
/*      */     
/* 2208 */     GL11.glTranslatef(0.0F, 0.0F, -300.0F);
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderLabels() {
/* 2213 */     if (!hasMapLabels()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 2218 */     setupMapClipping();
/*      */     
/* 2220 */     LOTRMapLabels[] allLabels = LOTRMapLabels.allMapLabels();
/*      */     
/* 2222 */     for (LOTRMapLabels label : allLabels) {
/*      */       
/* 2224 */       float[] pos = transformMapCoords(label.posX, label.posY);
/* 2225 */       float x = pos[0];
/* 2226 */       float y = pos[1];
/*      */       
/* 2228 */       float zoomlerp = (this.zoomExp - label.minZoom) / (label.maxZoom - label.minZoom);
/* 2229 */       if (zoomlerp > 0.0F && zoomlerp < 1.0F) {
/*      */         
/* 2231 */         float alpha = (0.5F - Math.abs(zoomlerp - 0.5F)) / 0.5F;
/* 2232 */         alpha *= 0.7F;
/* 2233 */         if (isOSRS()) {
/*      */           
/* 2235 */           if (alpha < 0.3F) {
/*      */             continue;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 2241 */           alpha = 1.0F;
/*      */         } 
/*      */ 
/*      */         
/* 2245 */         GL11.glPushMatrix();
/* 2246 */         GL11.glTranslatef(x, y, 0.0F);
/*      */         
/* 2248 */         float scale = this.zoomScale * label.scale;
/* 2249 */         GL11.glScalef(scale, scale, scale);
/* 2250 */         if (!isOSRS())
/*      */         {
/* 2252 */           GL11.glRotatef(label.angle, 0.0F, 0.0F, 1.0F);
/*      */         }
/*      */         
/* 2255 */         int alphaI = (int)(alpha * 255.0F);
/* 2256 */         alphaI = MathHelper.clamp_int(alphaI, 4, 255);
/* 2257 */         GL11.glEnable(3042);
/* 2258 */         GL11.glBlendFunc(770, 771);
/* 2259 */         float alphaFunc = GL11.glGetFloat(3010);
/* 2260 */         GL11.glAlphaFunc(516, 0.01F);
/*      */         
/* 2262 */         String s = label.getDisplayName();
/* 2263 */         int strX = -this.fontRendererObj.getStringWidth(s) / 2;
/* 2264 */         int strY = -this.fontRendererObj.FONT_HEIGHT / 2;
/*      */         
/* 2266 */         if (isOSRS()) {
/*      */           
/* 2268 */           if (label.scale > 2.5F)
/*      */           {
/* 2270 */             this.fontRendererObj.drawString(s, strX + 1, strY + 1, 0 + (alphaI << 24));
/* 2271 */             this.fontRendererObj.drawString(s, strX, strY, 16755200 + (alphaI << 24));
/*      */           }
/*      */           else
/*      */           {
/* 2275 */             this.fontRendererObj.drawString(s, strX + 1, strY + 1, 0 + (alphaI << 24));
/* 2276 */             this.fontRendererObj.drawString(s, strX, strY, 16777215 + (alphaI << 24));
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 2282 */           this.fontRendererObj.drawString(s, strX, strY, 16777215 + (alphaI << 24));
/*      */         } 
/*      */         
/* 2285 */         GL11.glAlphaFunc(516, alphaFunc);
/* 2286 */         GL11.glDisable(3042);
/* 2287 */         GL11.glPopMatrix();
/*      */       } 
/*      */       continue;
/*      */     } 
/* 2291 */     endMapClipping();
/*      */   }
/*      */ 
/*      */   
/*      */   private void setupMapClipping() {
/* 2296 */     ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
/* 2297 */     int scale = sr.getScaleFactor();
/* 2298 */     GL11.glEnable(3089);
/* 2299 */     GL11.glScissor(mapXMin * scale, (this.height - mapYMax) * scale, mapWidth * scale, mapHeight * scale);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2304 */   private void endMapClipping() { GL11.glDisable(3089); }
/*      */ 
/*      */ 
/*      */   
/*      */   private float[] transformCoords(float x, float z) {
/* 2309 */     x = x / LOTRGenLayerWorld.scale + 810.0F;
/* 2310 */     z = z / LOTRGenLayerWorld.scale + 730.0F;
/* 2311 */     return transformMapCoords(x, z);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2316 */   private float[] transformCoords(double x, double z) { return transformCoords((float)x, (float)z); }
/*      */ 
/*      */ 
/*      */   
private float[] transformMapCoords(float x, float z) {
	x -= this.posX;
	z -= this.posY;
	x *= this.zoomScale;
	z *= this.zoomScale;
	x += (mapXMin + mapWidth / 2);
	z += (mapYMin + mapHeight / 2);
	return new float[] { x, z };
}
/*      */ 
/*      */   
/*      */   private void drawFancyRect(int x1, int y1, int x2, int y2) {
/* 2332 */     drawRect(x1, y1, x2, y2, -1073741824);
/* 2333 */     drawHorizontalLine(x1 - 1, x2, y1 - 1, -6156032);
/* 2334 */     drawHorizontalLine(x1 - 1, x2, y2, -6156032);
/* 2335 */     drawVerticalLine(x1 - 1, y1 - 1, y2, -6156032);
/* 2336 */     drawVerticalLine(x2, y1 - 1, y2, -6156032);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2341 */   private boolean isValidWaypointName(String name) { return !StringUtils.isBlank(name); }
/*      */ 
/*      */ 
/*      */   
/*      */   private LOTRFellowshipClient getFellowshipByName(String name) {
/* 2346 */     String fsName = StringUtils.strip(name);
/* 2347 */     return LOTRLevelData.getData(this.mc.thePlayer).getClientFellowshipByName(fsName);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isExistingFellowshipName(String name) {
/* 2352 */     LOTRFellowshipClient fs = getFellowshipByName(name);
/* 2353 */     return (fs != null);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isExistingUnsharedFellowshipName(String name, LOTRCustomWaypoint waypoint) {
/* 2358 */     LOTRFellowshipClient fs = getFellowshipByName(name);
/* 2359 */     return (fs != null && !waypoint.hasSharedFellowship(fs.getFellowshipID()));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2364 */   private boolean isFellowshipAlreadyShared(String name, LOTRCustomWaypoint waypoint) { return (isExistingFellowshipName(name) && !isExistingUnsharedFellowshipName(name, waypoint)); }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void keyTyped(char c, int i) {
/* 2370 */     if (this.hasOverlay) {
/*      */       
/* 2372 */       if (this.creatingWaypointNew && this.nameWPTextField.textboxKeyTyped(c, i)) {
/*      */         return;
/*      */       }
/*      */       
/* 2376 */       if (this.renamingWaypoint && this.nameWPTextField.textboxKeyTyped(c, i)) {
/*      */         return;
/*      */       }
/*      */       
/* 2380 */       if (this.sharingWaypointNew && this.nameWPTextField.textboxKeyTyped(c, i)) {
/*      */         return;
/*      */       }
/*      */       
/* 2384 */       if (i == 1 || i == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
/*      */         
/* 2386 */         if (this.creatingWaypointNew) {
/*      */           
/* 2388 */           openOverlayCreate();
/*      */         }
/* 2390 */         else if (this.sharingWaypointNew) {
/*      */           
/* 2392 */           openOverlayShare();
/*      */         }
/*      */         else {
/*      */           
/* 2396 */           closeOverlay();
/*      */         } 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } else {
/* 2403 */       if (!this.loadingConquestGrid) {
/*      */         
/* 2405 */         if (i == LOTRKeyHandler.keyBindingFastTravel.getKeyCode() && isMiddleEarth() && this.selectedWaypoint != null && this.selectedWaypoint.hasPlayerUnlocked(this.mc.thePlayer) && LOTRLevelData.getData(this.mc.thePlayer).getFTTimer() <= 0) {
/*      */           
/* 2407 */           LOTRPacketFastTravel packet = new LOTRPacketFastTravel(this.selectedWaypoint);
/* 2408 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2410 */           this.mc.thePlayer.closeScreen();
/*      */           return;
/*      */         } 
/* 2413 */         if (this.selectedWaypoint == null && i == LOTRKeyHandler.keyBindingMapTeleport.getKeyCode() && this.isMouseWithinMap && canTeleport()) {
/*      */           
/* 2415 */           LOTRPacketMapTp packet = new LOTRPacketMapTp(this.mouseXCoord, this.mouseZCoord);
/* 2416 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2418 */           this.mc.thePlayer.closeScreen();
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/* 2423 */       if (this.hasControlZones)
/*      */       {
/* 2425 */         if (i == 1 || i == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
/*      */           
/* 2427 */           Minecraft.getMinecraft().displayGuiScreen(new LOTRGuiFactions());
/*      */           
/*      */           return;
/*      */         } 
/*      */       }
/* 2432 */       super.keyTyped(c, i);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void closeOverlay() {
/* 2438 */     this.hasOverlay = false;
/* 2439 */     this.overlayLines = null;
/* 2440 */     this.creatingWaypoint = false;
/* 2441 */     this.creatingWaypointNew = false;
/* 2442 */     this.deletingWaypoint = false;
/* 2443 */     this.renamingWaypoint = false;
/* 2444 */     this.sharingWaypoint = false;
/* 2445 */     this.sharingWaypointNew = false;
/* 2446 */     this.buttonOverlayFunction.enabled = this.buttonOverlayFunction.visible = false;
/* 2447 */     this.nameWPTextField.setText("");
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayCreate() {
/* 2452 */     closeOverlay();
/* 2453 */     this.hasOverlay = true;
/* 2454 */     this.creatingWaypoint = true;
/*      */     
/* 2456 */     int numWaypoints = LOTRLevelData.getData(this.mc.thePlayer).getCustomWaypoints().size();
/* 2457 */     int maxWaypoints = LOTRLevelData.getData(this.mc.thePlayer).getMaxCustomWaypoints();
/* 2458 */     int remaining = maxWaypoints - numWaypoints;
/* 2459 */     if (remaining <= 0) {
/*      */       
/* 2461 */       this
/*      */ 
/*      */ 
/*      */         
/* 2465 */         .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.allUsed.1", new Object[] { Integer.valueOf(maxWaypoints) }), "", StatCollector.translateToLocal("lotr.gui.map.customWaypoint.allUsed.2") };
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 2470 */       this
/*      */ 
/*      */ 
/*      */         
/* 2474 */         .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.create.1", new Object[] { Integer.valueOf(numWaypoints), Integer.valueOf(maxWaypoints) }), "", StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.create.2", new Object[0]) };
/*      */       
/* 2476 */       this.buttonOverlayFunction.visible = true;
/* 2477 */       this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.create.button");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayCreateNew() {
/* 2483 */     closeOverlay();
/* 2484 */     this.hasOverlay = true;
/* 2485 */     this.creatingWaypointNew = true;
/*      */     
/* 2487 */     this
/*      */       
/* 2489 */       .overlayLines = new String[] { StatCollector.translateToLocal("lotr.gui.map.customWaypoint.createNew.1") };
/*      */     
/* 2491 */     this.buttonOverlayFunction.visible = true;
/* 2492 */     this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.createNew.button");
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayDelete() {
/* 2497 */     closeOverlay();
/* 2498 */     this.hasOverlay = true;
/* 2499 */     this.deletingWaypoint = true;
/*      */     
/* 2501 */     this
/*      */       
/* 2503 */       .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.delete.1", new Object[] { this.selectedWaypoint.getDisplayName() }) };
/*      */     
/* 2505 */     this.buttonOverlayFunction.visible = true;
/* 2506 */     this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.delete.button");
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayRename() {
/* 2511 */     closeOverlay();
/* 2512 */     this.hasOverlay = true;
/* 2513 */     this.renamingWaypoint = true;
/*      */     
/* 2515 */     this
/*      */       
/* 2517 */       .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.rename.1", new Object[] { this.selectedWaypoint.getDisplayName() }) };
/*      */     
/* 2519 */     this.buttonOverlayFunction.visible = true;
/* 2520 */     this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.rename.button");
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayShare() {
/* 2525 */     closeOverlay();
/* 2526 */     this.hasOverlay = true;
/* 2527 */     this.sharingWaypoint = true;
/*      */     
/* 2529 */     this
/*      */       
/* 2531 */       .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.share.1", new Object[] { this.selectedWaypoint.getDisplayName() }) };
/*      */     
/* 2533 */     this.buttonOverlayFunction.visible = true;
/* 2534 */     this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.share.button");
/*      */   }
/*      */ 
/*      */   
/*      */   private void openOverlayShareNew() {
/* 2539 */     closeOverlay();
/* 2540 */     this.hasOverlay = true;
/* 2541 */     this.sharingWaypointNew = true;
/*      */     
/* 2543 */     this
/*      */       
/* 2545 */       .overlayLines = new String[] { StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.shareNew.1", new Object[] { this.selectedWaypoint.getDisplayName() }) };
/*      */     
/* 2547 */     this.buttonOverlayFunction.visible = true;
/* 2548 */     this.buttonOverlayFunction.displayString = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.shareNew.button");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mouseClicked(int i, int j, int k) {
/* 2554 */     LOTRGuiMapWidget mouseWidget = null;
/* 2555 */     for (LOTRGuiMapWidget widget : mapWidgets) {
/*      */       
/* 2557 */       if (widget.isMouseOver(i - mapXMin, j - mapYMin, mapWidth, mapHeight)) {
/*      */         
/* 2559 */         mouseWidget = widget;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 2564 */     if (this.hasOverlay && (this.creatingWaypointNew || this.renamingWaypoint || this.sharingWaypointNew))
/*      */     {
/* 2566 */       this.nameWPTextField.mouseClicked(i, j, k);
/*      */     }
/*      */     
/* 2569 */     if (this.hasOverlay && k == 0 && this.sharingWaypoint && this.mouseOverRemoveSharedFellowship != null) {
/*      */       
/* 2571 */       String fsName = this.mouseOverRemoveSharedFellowship.getName();
/* 2572 */       LOTRPacketShareCWP packet = new LOTRPacketShareCWP((LOTRCustomWaypoint)this.selectedWaypoint, fsName, false);
/* 2573 */       LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */       
/*      */       return;
/*      */     } 
/* 2577 */     if (!this.hasOverlay && k == 0 && isMiddleEarth() && this.selectedWaypoint instanceof LOTRCustomWaypoint) {
/*      */       
/* 2579 */       LOTRCustomWaypoint cwp = (LOTRCustomWaypoint)this.selectedWaypoint;
/* 2580 */       if (!cwp.isShared()) {
/*      */         
/* 2582 */         if (mouseWidget == this.widgetDelCWP) {
/*      */           
/* 2584 */           openOverlayDelete();
/*      */           
/*      */           return;
/*      */         } 
/* 2588 */         if (mouseWidget == this.widgetRenameCWP) {
/*      */           
/* 2590 */           openOverlayRename();
/*      */           
/*      */           return;
/*      */         } 
/* 2594 */         if (mouseWidget == this.widgetShareCWP) {
/*      */           
/* 2596 */           openOverlayShare();
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */       } else {
/* 2602 */         if (mouseWidget == this.widgetHideSWP) {
/*      */           
/* 2604 */           LOTRPacketCWPSharedHide packet = new LOTRPacketCWPSharedHide(cwp, true);
/* 2605 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/* 2606 */           this.selectedWaypoint = null;
/*      */           
/*      */           return;
/*      */         } 
/* 2610 */         if (mouseWidget == this.widgetUnhideSWP) {
/*      */           
/* 2612 */           LOTRPacketCWPSharedHide packet = new LOTRPacketCWPSharedHide(cwp, false);
/* 2613 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2619 */     if (!this.hasOverlay && k == 0 && isMiddleEarth())
/*      */     {
/* 2621 */       if (mouseWidget == this.widgetAddCWP) {
/*      */         
/* 2623 */         openOverlayCreate();
/*      */         
/*      */         return;
/*      */       } 
/*      */     }
/* 2628 */     if (!this.hasOverlay && k == 0) {
/*      */       
/* 2630 */       if (mouseWidget == this.widgetToggleWPs) {
/*      */         
/* 2632 */         showWP = !showWP;
/* 2633 */         LOTRClientProxy.sendClientInfoPacket(null, null);
/*      */         
/*      */         return;
/*      */       } 
/* 2637 */       if (mouseWidget == this.widgetToggleCWPs) {
/*      */         
/* 2639 */         showCWP = !showCWP;
/* 2640 */         LOTRClientProxy.sendClientInfoPacket(null, null);
/*      */         
/*      */         return;
/*      */       } 
/* 2644 */       if (mouseWidget == this.widgetToggleHiddenSWPs) {
/*      */         
/* 2646 */         showHiddenSWP = !showHiddenSWP;
/* 2647 */         LOTRClientProxy.sendClientInfoPacket(null, null);
/*      */         
/*      */         return;
/*      */       } 
/* 2651 */       if (this.zoomTicks == 0) {
/*      */         
/* 2653 */         if (mouseWidget == this.widgetZoomIn) {
/*      */           
/* 2655 */           zoomIn();
/*      */           
/*      */           return;
/*      */         } 
/* 2659 */         if (mouseWidget == this.widgetZoomOut) {
/*      */           
/* 2661 */           zoomOut();
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/* 2666 */       if (mouseWidget == this.widgetFullScreen) {
/*      */         
/* 2668 */         fullscreen = !fullscreen;
/* 2669 */         ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
/* 2670 */         setWorldAndResolution(this.mc, sr.getScaledWidth(), sr.getScaledHeight());
/*      */         
/*      */         return;
/*      */       } 
/* 2674 */       if (mouseWidget == this.widgetSepia) {
/*      */         
/* 2676 */         LOTRConfig.toggleSepia();
/*      */         
/*      */         return;
/*      */       } 
/* 2680 */       if (mouseWidget == this.widgetLabels) {
/*      */         
/* 2682 */         toggleMapLabels();
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/* 2687 */     if (!this.hasOverlay && !this.loadingConquestGrid)
/*      */     {
/* 2689 */       if (k == 0 && this.isMouseWithinMap) {
/*      */         
/* 2691 */         this.selectedWaypoint = null;
/* 2692 */         double distanceSelectedWP = Double.MAX_VALUE;
/*      */         
/* 2694 */         List<LOTRAbstractWaypoint> waypoints = LOTRLevelData.getData(this.mc.thePlayer).getAllAvailableWaypoints();
/* 2695 */         for (LOTRAbstractWaypoint waypoint : waypoints) {
/*      */           
/* 2697 */           boolean unlocked = waypoint.hasPlayerUnlocked(this.mc.thePlayer);
/* 2698 */           boolean hidden = waypoint.isHidden();
/*      */           
/* 2700 */           if (isWaypointVisible(waypoint) && (!hidden || unlocked)) {
/*      */             
/* 2702 */             float[] pos = transformCoords(waypoint.getXCoord(), waypoint.getZCoord());
/* 2703 */             float x = pos[0];
/* 2704 */             float y = pos[1];
/*      */             
/* 2706 */             float dx = x - i;
/* 2707 */             float dy = y - j;
/* 2708 */             double distToWP = Math.sqrt((dx * dx + dy * dy));
/* 2709 */             if (distToWP <= 5.0D)
/*      */             {
/* 2711 */               if (distToWP <= distanceSelectedWP) {
/*      */                 
/* 2713 */                 this.selectedWaypoint = waypoint;
/* 2714 */                 distanceSelectedWP = distToWP;
/*      */               } 
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2722 */     super.mouseClicked(i, j, k);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void actionPerformed(GuiButton button) {
/* 2728 */     if (button.enabled)
/*      */     {
/* 2730 */       if (button == this.buttonOverlayFunction) {
/*      */         
/* 2732 */         if (this.creatingWaypoint)
/*      */         {
/* 2734 */           openOverlayCreateNew();
/*      */         }
/* 2736 */         else if (this.creatingWaypointNew && isValidWaypointName(this.nameWPTextField.getText()))
/*      */         {
/* 2738 */           String waypointName = this.nameWPTextField.getText();
/*      */           
/* 2740 */           LOTRPacketCreateCWP packet = new LOTRPacketCreateCWP(waypointName);
/* 2741 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2743 */           closeOverlay();
/*      */         }
/* 2745 */         else if (this.deletingWaypoint)
/*      */         {
/* 2747 */           LOTRPacketDeleteCWP packet = new LOTRPacketDeleteCWP((LOTRCustomWaypoint)this.selectedWaypoint);
/* 2748 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2750 */           closeOverlay();
/* 2751 */           this.selectedWaypoint = null;
/*      */         }
/* 2753 */         else if (this.renamingWaypoint && isValidWaypointName(this.nameWPTextField.getText()))
/*      */         {
/* 2755 */           String newName = this.nameWPTextField.getText();
/*      */           
/* 2757 */           LOTRPacketRenameCWP packet = new LOTRPacketRenameCWP((LOTRCustomWaypoint)this.selectedWaypoint, newName);
/* 2758 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2760 */           closeOverlay();
/*      */         }
/* 2762 */         else if (this.sharingWaypoint)
/*      */         {
/* 2764 */           openOverlayShareNew();
/*      */         }
/* 2766 */         else if (this.sharingWaypointNew && isExistingUnsharedFellowshipName(this.nameWPTextField.getText(), (LOTRCustomWaypoint)this.selectedWaypoint))
/*      */         {
/* 2768 */           String fsName = this.nameWPTextField.getText();
/*      */           
/* 2770 */           LOTRPacketShareCWP packet = new LOTRPacketShareCWP((LOTRCustomWaypoint)this.selectedWaypoint, fsName, true);
/* 2771 */           LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */           
/* 2773 */           openOverlayShare();
/*      */         }
/*      */       
/* 2776 */       } else if (button == this.buttonConquestRegions) {
/*      */         
/* 2778 */         List<LOTRDimension.DimensionRegion> regionList = LOTRDimension.MIDDLE_EARTH.dimensionRegions;
/* 2779 */         if (!regionList.isEmpty())
/*      */         {
/* 2781 */           int i = regionList.indexOf(currentRegion);
/* 2782 */           i++;
/* 2783 */           i = IntMath.mod(i, regionList.size());
/* 2784 */           currentRegion = (LOTRDimension.DimensionRegion)regionList.get(i);
/* 2785 */           updateCurrentDimensionAndFaction();
/* 2786 */           setCurrentScrollFromFaction();
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 2791 */         super.actionPerformed(button);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void handleMapKeyboardMovement() {
/* 2798 */     this.prevPosX += this.posXMove;
/* 2799 */     this.prevPosY += this.posYMove;
/* 2800 */     this.posXMove = 0.0F;
/* 2801 */     this.posYMove = 0.0F;
/*      */     
/* 2803 */     if (!this.hasOverlay) {
/*      */       
/* 2805 */       float move = 12.0F / (float)Math.pow(this.zoomScale, 0.800000011920929D);
/*      */       
/* 2807 */       if (isKeyDownAndNotMouse(this.mc.gameSettings.keyBindLeft) || Keyboard.isKeyDown(203))
/*      */       {
/* 2809 */         this.posXMove -= move;
/*      */       }
/*      */       
/* 2812 */       if (isKeyDownAndNotMouse(this.mc.gameSettings.keyBindRight) || Keyboard.isKeyDown(205))
/*      */       {
/* 2814 */         this.posXMove += move;
/*      */       }
/*      */       
/* 2817 */       if (isKeyDownAndNotMouse(this.mc.gameSettings.keyBindForward) || Keyboard.isKeyDown(200))
/*      */       {
/* 2819 */         this.posYMove -= move;
/*      */       }
/*      */       
/* 2822 */       if (isKeyDownAndNotMouse(this.mc.gameSettings.keyBindBack) || Keyboard.isKeyDown(208))
/*      */       {
/* 2824 */         this.posYMove += move;
/*      */       }
/*      */       
/* 2827 */       if (this.posXMove != 0.0F || this.posYMove != 0.0F)
/*      */       {
/* 2829 */         this.selectedWaypoint = null;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2836 */   private boolean isKeyDownAndNotMouse(KeyBinding keybinding) { return (keybinding.getKeyCode() >= 0 && GameSettings.isKeyDown(keybinding)); }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleMouseInput() {
/* 2842 */     super.handleMouseInput();
/* 2843 */     int k = Mouse.getEventDWheel();
/*      */     
/* 2845 */     if (this.isConquestGrid && hasConquestScrollBar() && this.mouseInFacScroll)
/*      */     {
/* 2847 */       if (k != 0) {
/*      */         
/* 2849 */         if (k < 0) {
/*      */           
/* 2851 */           this.currentFactionIndex = Math.min(this.currentFactionIndex + 1, Math.max(0, currentFactionList.size() - 1));
/*      */         }
/* 2853 */         else if (k > 0) {
/*      */           
/* 2855 */           this.currentFactionIndex = Math.max(this.currentFactionIndex - 1, 0);
/*      */         } 
/* 2857 */         setCurrentScrollFromFaction();
/*      */         
/*      */         return;
/*      */       } 
/*      */     }
/* 2862 */     if (!this.hasOverlay)
/*      */     {
/* 2864 */       if (this.zoomTicks == 0) {
/*      */         
/* 2866 */         if (k < 0 && zoomPower > -3) {
/*      */           
/* 2868 */           zoomOut();
/*      */           
/*      */           return;
/*      */         } 
/* 2872 */         if (k > 0 && zoomPower < 4) {
/*      */           
/* 2874 */           zoomIn();
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     }
/* 2880 */     if (this.hasOverlay && k != 0) {
/*      */       
/* 2882 */       k = Integer.signum(k);
/* 2883 */       if (this.scrollPaneWPShares.hasScrollBar && this.scrollPaneWPShares.mouseOver) {
/*      */         
/* 2885 */         int l = this.displayedWPShareList.size() - this.displayedWPShares;
/* 2886 */         this.scrollPaneWPShares.mouseWheelScroll(k, l);
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2894 */   private void zoomOut() { zoom(-1); }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2899 */   private void zoomIn() { zoom(1); }
/*      */ 
/*      */ 
/*      */   
/*      */   private void zoom(int boost) {
/* 2904 */     this.prevZoomPower = zoomPower;
/* 2905 */     zoomPower += boost;
/* 2906 */     this.zoomTicks = zoomTicksMax;
/* 2907 */     this.selectedWaypoint = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCWPProtectionMessage(IChatComponent message) {
/* 2912 */     closeOverlay();
/* 2913 */     this.hasOverlay = true;
/* 2914 */     this.creatingWaypoint = false;
/* 2915 */     this.creatingWaypointNew = false;
/*      */     
/* 2917 */     String protection = StatCollector.translateToLocalFormatted("lotr.gui.map.customWaypoint.protected.1", new Object[] { message.getFormattedText() });
/* 2918 */     String warning = StatCollector.translateToLocal("lotr.gui.map.customWaypoint.protected.2");
/* 2919 */     this.overlayLines = new String[] { protection, "", warning };
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean canTeleport() {
/* 2924 */     if (!isMiddleEarth())
/*      */     {
/* 2926 */       return false;
/*      */     }
/*      */     
/* 2929 */     if (this.loadingConquestGrid)
/*      */     {
/* 2931 */       return false;
/*      */     }
/*      */     
/* 2934 */     int chunkX = MathHelper.floor_double(this.mc.thePlayer.posX) >> 4;
/* 2935 */     int chunkZ = MathHelper.floor_double(this.mc.thePlayer.posZ) >> 4;
/* 2936 */     if (this.mc.theWorld.getChunkProvider().provideChunk(chunkX, chunkZ) instanceof net.minecraft.world.chunk.EmptyChunk)
/*      */     {
/* 2938 */       return false;
/*      */     }
/*      */     
/* 2941 */     requestIsOp();
/* 2942 */     return this.isPlayerOp;
/*      */   }
/*      */ 
/*      */   
/*      */   private void requestIsOp() {
/* 2947 */     if (this.mc.isSingleplayer()) {
/*      */       
/* 2949 */       IntegratedServer integratedServer = this.mc.getIntegratedServer();
/* 2950 */       this.isPlayerOp = (integratedServer.worldServers[0].getWorldInfo().areCommandsAllowed() && integratedServer.getServerOwner().equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName()));
/*      */     }
/*      */     else {
/*      */       
/* 2954 */       LOTRPacketIsOpRequest packet = new LOTRPacketIsOpRequest();
/* 2955 */       LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2961 */   private boolean isMiddleEarth() { return (this.mc.thePlayer.dimension == LOTRDimension.MIDDLE_EARTH.dimensionID); }
/*      */ 
/*      */ 
/*      */   
/*      */   private void requestConquestGridIfNeed(LOTRFaction conqFac) {
/* 2966 */     if (!this.requestedFacGrids.contains(conqFac))
/*      */     {
/* 2968 */       if (this.ticksUntilRequestFac <= 0) {
/*      */         
/* 2970 */         this.requestedFacGrids.add(conqFac);
/* 2971 */         LOTRPacketConquestGridRequest packet = new LOTRPacketConquestGridRequest(conqFac);
/* 2972 */         LOTRPacketHandler.networkWrapper.sendToServer(packet);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void receiveConquestGrid(LOTRFaction conqFac, List<LOTRConquestZone> allZones) {
/* 2979 */     if (this.isConquestGrid) {
/*      */       
/* 2981 */       this.receivedFacGrids.add(conqFac);
/* 2982 */       this.facConquestGrids.put(conqFac, allZones);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 2988 */   private boolean hasConquestScrollBar() { return (this.isConquestGrid && currentFactionList.size() > 1); }
/*      */ 
/*      */ 
/*      */   
/*      */   private void setupConquestScrollBar(int i, int j) {
/* 2993 */     boolean isMouseDown = Mouse.isButtonDown(0);
/*      */     
/* 2995 */     int i1 = this.facScrollX;
/* 2996 */     int j1 = this.facScrollY;
/* 2997 */     int i2 = i1 + this.facScrollWidth;
/* 2998 */     int j2 = j1 + this.facScrollHeight;
/* 2999 */     this.mouseInFacScroll = (i >= i1 && j >= j1 && i < i2 && j < j2);
/*      */     
/* 3001 */     if (!this.wasMouseDown && isMouseDown && this.mouseInFacScroll)
/*      */     {
/* 3003 */       this.isFacScrolling = true;
/*      */     }
/*      */     
/* 3006 */     if (!isMouseDown)
/*      */     {
/* 3008 */       this.isFacScrolling = false;
/*      */     }
/*      */     
/* 3011 */     this.wasMouseDown = isMouseDown;
/*      */     
/* 3013 */     if (this.isFacScrolling) {
/*      */       
/* 3015 */       this.currentFacScroll = ((i - i1) - this.facScrollWidgetWidth / 2.0F) / ((i2 - i1) - this.facScrollWidgetWidth);
/* 3016 */       this.currentFacScroll = MathHelper.clamp_float(this.currentFacScroll, 0.0F, 1.0F);
/* 3017 */       this.currentFactionIndex = Math.round(this.currentFacScroll * (currentFactionList.size() - 1));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 3023 */   private void setCurrentScrollFromFaction() { this.currentFacScroll = this.currentFactionIndex / (currentFactionList.size() - 1); }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasMapLabels() {
/* 3028 */     if (this.isConquestGrid)
/*      */     {
/* 3030 */       return LOTRConfig.mapLabels;
/*      */     }
/*      */ 
/*      */     
/* 3034 */     return LOTRConfig.mapLabels;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void toggleMapLabels() {
/* 3040 */     if (this.isConquestGrid) {
/*      */       
/* 3042 */       LOTRConfig.toggleMapLabels();
/*      */     }
/*      */     else {
/*      */       
/* 3046 */       LOTRConfig.toggleMapLabels();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setFakeMapProperties(float x, float y, float scale, float scaleExp, float scaleStable) {
/* 3052 */     this.posX = x;
/* 3053 */     this.posY = y;
/* 3054 */     this.zoomScale = scale;
/* 3055 */     this.zoomExp = scaleExp;
/* 3056 */     this.zoomScaleStable = scaleStable;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int[] setFakeStaticProperties(int w, int h, int xmin, int xmax, int ymin, int ymax) {
/* 3061 */     int[] ret = { mapWidth, mapHeight, mapXMin, mapXMax, mapYMin, mapYMax };
/* 3062 */     mapWidth = w;
/* 3063 */     mapHeight = h;
/* 3064 */     mapXMin = xmin;
/* 3065 */     mapXMax = xmax;
/* 3066 */     mapYMin = ymin;
/* 3067 */     mapYMax = ymax;
/* 3068 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 3073 */   private static boolean isOSRS() { return LOTRConfig.osrsMap; }
/*      */ }


/* Location:              C:\Users\tani\Desktop\minecraft-modding\LOTRMod v35.3\!\lotr\client\gui\LOTRGuiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */