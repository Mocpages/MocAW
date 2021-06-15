package net.shadowmage.ancientwarfare.npc.event;

import java.util.ArrayList;

import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.ItemShootable;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cuchaz.ships.EntityShip;
import cuchaz.ships.ShipLocator;
import lumien.hardcoredarkness.HardcoreDarkness;
import mcheli.MCH_Camera;
import mcheli.MCH_Lib;
import mcheli.MCH_ViewEntityDummy;
import mcheli.aircraft.MCH_ItemAircraft;
import mcheli.tool.MCH_ItemWrench;
import mcheli.vehicle.MCH_EntityVehicle;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.gamedata.MocData;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemBrainWorm;
import net.shadowmage.ancientwarfare.npc.item.ItemBuildingSettings;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import squeek.applecore.api.plants.PlantGrowthEvent;

public class EventHandler
{
	//file:///C:/Users/witix/Desktop/forgeevents.html
	private EventHandler(){}
	public static final EventHandler INSTANCE = new EventHandler();
	private Gui gui = new Gui();

	@SubscribeEvent
	public void handleRenderLastEvent(RenderWorldLastEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc==null){
			return;
		}

		EntityPlayer player = mc.thePlayer;
		if(player==null){
			return;
		}

		ItemStack stack = player.inventory.getCurrentItem();

		Item item;
		if(stack==null || (item=stack.getItem())==null){
			return;
		}
		if(item==AWNpcItemLoader.scanner){
			renderScannerBoundingBox(player, stack, evt.partialTicks);
		}
	}
	
	
	StructureBB bb = new StructureBB(new BlockPosition(), new BlockPosition()){};
	ItemBuildingSettings settings = new ItemBuildingSettings();
	
	private void renderScannerBoundingBox(EntityPlayer player, ItemStack stack, float delta){
	  ItemBuildingSettings.getSettingsFor(stack, settings);
	  BlockPosition pos1, pos2, min, max;
	  if(settings.hasPos1()){
	    pos1 = settings.pos1();
	    }
	  else{
	    pos1 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
	    }
	  if(settings.hasPos2()){
	    pos2 = settings.pos2();
	    }
	  else{
	    pos2 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
	    }
	  if(pos1!=null && pos2!=null){
		    min = BlockTools.getMin(pos1, pos2);
		    max = BlockTools.getMax(pos1, pos2);
		    if(settings.isContained(player.worldObj)) {
			    renderBoundingBox(player, min, max, delta, 0, 1.f, 0);
		    }else {
			    renderBoundingBox(player, min, max, delta, 1.f, 0, 0);
		    }
		    Minecraft mc = Minecraft.getMinecraft();
		    ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		    int x = sr.getScaledWidth()-10;
		    //String header = "Floor space: " + settings.getLivingSpace(player.worldObj);
		    //gui.drawString(mc.fontRenderer, header, x-mc.fontRenderer.getStringWidth(header), 0, 0xffffffff);

	    }
	  }
	
	@SubscribeEvent
    public void onCropGrowthAppleCore(PlantGrowthEvent.AllowGrowthTick event){
        event.setResult(Event.Result.DENY);
    }

	private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
	  {
	  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
	  RenderTools.adjustBBForPlayerPos(bb, player, delta);
	  RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
	  }

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e){
		EntityPlayer p = e.player;
		if(!p.worldObj.isRemote) {
			MocData data = AWGameData.INSTANCE.getData(MocData.name,p.getEntityWorld(), MocData.class);
			for(MocFaction f : data.factions) {
				if(f.prisoners.contains(p.getCommandSenderName())) {
					if(f.prison != null && !p.getEntityData().getBoolean("isPrisoner")) {
						p.getEntityData().setBoolean("isPrisoner", true);
						p.addChatComponentMessage(new ChatComponentText("You have been imprisoned!"));
						p.setPositionAndUpdate(f.prison.x, f.prison.y, f.prison.z);
					}
				}
			}
		}
	}
	
	private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta, float r, float g, float b) {
	  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
	  RenderTools.adjustBBForPlayerPos(bb, player, delta);
	  RenderTools.drawOutlinedBoundingBox(bb, r, g, b);

	  }


	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		TileTownHall closest = getClosestFriendlyTH(player, Double.MAX_VALUE);
		if(closest == null) {
			//this.print(player, "FINAL DEATH! All items removed.", EnumChatFormatting.DARK_RED);
			//emptyInventory(player);
			return;
		}
		//print(player, "Dist: " + player.getDistance(closest.xCoord, player.posY, closest.zCoord));
		closest.respawn(player);
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			TileTownHall closest = getClosestFriendlyTH(player, 100);
			if(closest == null) {
				this.print(player, "No valid base found! All items removed.", EnumChatFormatting.DARK_RED);
				emptyInventory(player);
			}
			//print(player, "dying");

			return;
		}
	}

	//print(player, "-----------------------------------------------------", EnumChatFormatting.DARK_RED);

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {

	}


	public void emptyInventory(EntityPlayer player) {
		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;


		InventoryTools.dropInventoryInWorld(player.worldObj, player.inventory, x, y, z);
		for(int i = 0; i<player.inventory.getSizeInventory(); i++) {
			player.inventory.setInventorySlotContents(i, null);
		}
	}

	@SubscribeEvent
	public void onPlayerDrop(PlayerDropsEvent event) {

	}

	public TileTownHall getClosestFriendlyTH(EntityPlayer player, double closeDist) {
		TileTownHall closest = null;

		for(Object e : player.worldObj.loadedTileEntityList) { //For every tile entity in the world...
			if(e instanceof TileTownHall) { //if that tile entity is a town hall...
				TileTownHall th = (TileTownHall)e; 
				if(th.canRespawn(player)) {
					if(player.getDistance(th.xCoord, player.posY, th.zCoord) <= closeDist) {
						closeDist = player.getDistance(th.xCoord, player.posX, th.zCoord);
						closest = th;
					}
				}
			}
		}

		return closest;
	}

	public ArrayList<TileTownHall> getTHInRange(EntityPlayer player, int radius) {
		ArrayList<TileTownHall> output = new ArrayList<TileTownHall>();
		for(Object e : player.worldObj.loadedTileEntityList) { //For every tile entity in the world...
			if(e instanceof TileTownHall) { //if that tile entity is a town hall...
				TileTownHall th = (TileTownHall)e; 
				if(Math.sqrt(th.getDistanceFrom(player.posX, th.yCoord, player.posZ))<100) {
					output.add(th);
				}
			}
		}
		return output;
	}


	public boolean canAccess(EntityPlayer player, int x, int z) {
		Team playerTeam = player.getTeam();
		if(playerTeam == null) {
			//print(player, "Cannot edit except in claimed chunks");
			print(player, "You are not in a faction.", EnumChatFormatting.YELLOW);
			return false;
		}

		//If we're within 100 blocks... (or w/e the claim radius should be)
		for(TileTownHall th : getTHInRange(player, 100)) {
			if(!player.isOnSameTeam(player.worldObj.getPlayerEntityByName(th.getOwnerName()))) {//if my faction is NOT the same as the owner of the TH
				print(player, "Cannot edit the base of " + th.name, EnumChatFormatting.DARK_RED); //...Then I can't edit - this is someone else's base!
				return false;
			}else { //My faction IS the same as the TH
				return true; //TODO - contested areas?
			}
		}
		print(player, "Cannot edit in unclaimed chunks", EnumChatFormatting.RED); //If we got this far, there are NO town halls with range, enemy or friendly.
		return false; //We can't edit unclaimed chunks either, so return false.
	}


	public static double[] getRelOffset(double x, double z, double angle, double offX, double offZ) {
		double xPrime = x * Math.cos(angle) + z * Math.sin(angle);
		double zPrime = -x  * Math.sin(angle) + z * Math.cos(angle);

		zPrime += offZ;
		xPrime += offX;

		double x2 = xPrime * Math.cos(angle) - zPrime * Math.sin(angle);
		double z2 = xPrime  * Math.sin(angle) + zPrime * Math.cos(angle);

		return new double[] {x2,z2};
	}

	public static double getAngle(double x1, double x2, double z1, double z2) {
		double cx = (x1 + x2) / 2;
		double cz = (z1 + z2) / 2;
		double angle = Math.atan2(z2 - cz, x2 - cx);
		return angle;
	}


	@SubscribeEvent
	public void clientTick(ClientTickEvent evt) throws Exception {
		try {
			if(Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.nightVision).getDuration() >0 && Minecraft.getMinecraft().gameSettings.hideGUI) {
				Minecraft.getMinecraft().gameSettings.hideGUI = false;
			}
		}catch(Exception e) {}
		if(HardcoreDarkness.INSTANCE.getActiveConfig().getMode() != 2) {
			throw new Exception("HCD must have mode 2.");
		}
		if(HardcoreDarkness.INSTANCE.getActiveConfig().isDimensionBlacklisted(0)) {
			throw new Exception("HCD must not black list overworld.");
		}

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		double range = 5;
		if(player == null){return;}
		if((player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemBrainWorm))){
			if(player.getEntityData().getBoolean("isViewNpc")){
				//System.out.println("agh");
				MCH_Lib.setRenderViewEntity(player);
			}else{

			}
		}
//		MCH_Lib.setRenderViewEntity(player);

	}

	public void updateWeight(MinecraftServer server) {
		for(Object o : server.getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			updatePlayerWeight(player);
		}

	}

	public float getGunWeight(ItemGun g) {
		if(g.type.damage <= 6) {
			return 1.5f;

		}else if(g.type.damage <= 15) {
			if(g.type.moveSpeedModifier != 1F) {
				return 20;
			}else {
				return 8;
			}
		}else if(g.type.damage <= 25) {
			return 12;
		}else {
			return 30;
		}
	}

	public float getWeight(ItemStack s) {
		if(s == null) {return 0.0f;}
		Item i = s.getItem();
		if(i instanceof ItemGun) {
			return getGunWeight((ItemGun)i);
		}

		if(i instanceof ItemBackpack || i.getUnlocalizedName().contains("woodenDevice")) {
			return 100;
		}

		if(i instanceof MCH_ItemAircraft) {
			return 100;
		}
		if(i instanceof MCH_ItemWrench) {
			return 1.5f;
		}


		if(i instanceof ItemFood) {
			ItemFood f = (ItemFood)i;
			return f.func_150905_g(s)/10 * s.stackSize;
		}

		//if(i instanceof ItemTeamArmour) {

		//}

		if(i instanceof ItemShootable) {
			ItemShootable magazine = ((ItemShootable)i);
			if(magazine.type.explosionPower > 0) {
				return 1.0f * s.stackSize;
			}else {

				return magazine.type.roundsPerItem * 0.04f * s.stackSize;
			}
		}


		return 0;
	}

	public void updatePlayerWeight(EntityPlayer player) {
		float weight = 0.0f;
		if(player == null) {return;}
		if(player.inventory == null) {return;}
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			weight += getWeight(player.inventory.getStackInSlot(i));
		}

		//if(player.worldObj.getWorldTime() % 100 == 0) {
		//this.print(player, "Weight: "  +weight);
		//}
		float movementSpeed = 0.0f; //MathUtils.clampf((float) (0.1 - (0.04f + (0.010f * (gunCount)))), 0.00f, 0.1f);
		float jumpspeed = 0.0f;
		if(weight <= 45) {

		}else if(weight <= 60) {
			movementSpeed = 0.025f;
			jumpspeed -= 0.05f;
		}else if(weight <= 80) {
			movementSpeed = 0.03f;
			jumpspeed = 0.06f;
		}else if(weight <= 100) {
			movementSpeed = 0.05f;
			jumpspeed = 0.1f;
		}else if(weight <= 120) {
			movementSpeed = 0.075f;
			jumpspeed = 0.15f;
		}else {
			movementSpeed = 0.99f;
			jumpspeed = 0.19f;
		}

		try {
			player.capabilities.setPlayerWalkSpeed(0.1f - movementSpeed);
		}catch(Exception e) {}
		//player.jumpMovementFactor = 0.05f - jumpspeed ;//MathUtils.clampf((float) (0.2 - (0.04f + (0.010f * (gunCount)))), 0.00f, 0.1f);;
	}

	public void updateStamina(World worldObj) {
		for(Object o : worldObj.playerEntities) {
			if(o instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)o;
				int countHeavy = 0; //MGs, rocket launchers, crates, etc
				int countMedium = 0; //Rifles
				int countLight = 0; //Pistols, SMGs
				for(int i = 0; i< player.inventory.getSizeInventory(); i++) {
					ItemStack item = player.inventory.getStackInSlot(i);
					if(item != null) {
						
					}
				}
			}
		}
	}
	
	


	public int getItemWeight(ItemStack item) {
		if(item.getItem() instanceof MCH_ItemAircraft) {
			return 3;
		}
		//if(item.getItem() instanceof WoodenDevice) {
			
		//}
		
		return 0;
	}
	
	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent evt) {
		//evt.toolTip.add("�cMoc, �fplease fucking �1die");
	}
	
	@SubscribeEvent
	public void serverTick(ServerTickEvent evt){
		if(evt.phase==Phase.END){
			MinecraftServer server = MinecraftServer.getServer();
			//updateWeight(server);
			if(server!=null && server.getEntityWorld()!=null){
				//world dimension 0 = overworld
				//dim 100 = lotr ME
				MocData data = ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,server.worldServerForDimension(100), MocData.class));
				data.onTick(server.getEntityWorld());

				updateStamina(server.getEntityWorld());

				ArrayList<World> worlds = new ArrayList<World>();
				for(Object o : server.getConfigurationManager().playerEntityList){
					if(o instanceof EntityPlayer){
						EntityPlayer player = (EntityPlayer)o;
						if(!worlds.contains(player.getEntityWorld())){
							worlds.add(player.getEntityWorld());
						}
					}
				}

				for(World w : worlds){
					MocData data2 = ((MocData)AWGameData.INSTANCE.getPerWorldData(MocData.name,w, MocData.class));
				//	data2.onTick(w);


				}
			}
		}
	}

	private void handleRiding(EntityPlayerMP player) {
		EntityHorse horse = (EntityHorse) player.ridingEntity;
		
	}

	//@SubscribeEvent
	//public void onClientTick(ClientTickEvent event) {
	//if(event.side == Side.)
	//updatePlayerWeight(Minecraft.getMinecraft().thePlayer);

	//}

	@SubscribeEvent
	public void onBlockBreakEvent(BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player.worldObj.isRemote) {return;}
		if(player.capabilities.isCreativeMode) {return;}
		if (player.getEntityData().getBoolean("isPrisoner")){
			player.addChatComponentMessage(new ChatComponentText("Can't break blocks in prison"));
			event.setCanceled(true);
		}
		for(TileTownHall th : getTHInRange(player, 100)) {
			if(th.inBattle()) {
				if(event.block.getUnlocalizedName().contains("sandbag")){return;}//Can break sandbags in battle to avoid fuckery
				event.setCanceled(true);
				print(player, "[" + th.name + "]: Cannot break blocks in battle, except sandbags.", EnumChatFormatting.RED);
			}else if(!th.isOnSameTeam(player)) {
				event.setCanceled(true);
				print(player, "[" + th.name + "]: Cannot break blocks in another faction's base.", EnumChatFormatting.RED);
			}
		}
	}

	public static void print(EntityPlayer player, String message) {
		print(player, message, EnumChatFormatting.WHITE);
	}

	public static void print(EntityPlayer player, String message, EnumChatFormatting color) {
		ChatComponentText text = new ChatComponentText(message);
		text.getChatStyle().setColor(color);
		player.addChatComponentMessage(text);
	}

	@SubscribeEvent
	public void onBlockPlaceEvent(PlaceEvent event) {
		EntityPlayer player = event.player;
		if(player.worldObj.isRemote) {return;}
		if(player.capabilities.isCreativeMode) {return;}
		if (player.getEntityData().getBoolean("isPrisoner")){
			player.addChatComponentMessage(new ChatComponentText("Can't place blocks in prison"));
			event.setCanceled(true);
		}
		for(TileTownHall th : getTHInRange(player, 100)) {
			if(th.inBattle()) {
				if(event.block.getUnlocalizedName().contains("sandbag")) {
					Block b = player.worldObj.getBlock(event.x, event.y-1, event.z); //Sandbags cannot be placed on top of other sandbags during battle, so get the block under the one that is being placede
					if(b==null) {return;} //If it's air then we're good. Avoid null except.
					if(b.getUnlocalizedName().contains("sandbag")) {
						event.setCanceled(true);
						print(player, "[" + th.name + "]: Cannot place a sandbag on top of another sandbag.", EnumChatFormatting.RED);
					}else {
						return; //This is a sandbag being placed in battle, not on top of another sandbag. A-OK!
					}
				}else {
					event.setCanceled(true);
					print(player, "[" + th.name + "]: Only sandbags may be placed during battle.", EnumChatFormatting.RED);
				}

			}else if(!th.isOnSameTeam(player)) {
				event.setCanceled(true);
				print(player, "[" + th.name + "]: Cannot place blocks in another faction's base.", EnumChatFormatting.RED);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		if(player.worldObj.isRemote) {return;}
		if(player.capabilities.isCreativeMode) {return;}
		if (player.getEntityData().getBoolean("isPrisoner")){
			player.addChatComponentMessage(new ChatComponentText("Can't interact with blocks in prison"));
			event.setCanceled(true);
		}
		for(TileTownHall th : getTHInRange(player, 100)) {

			if(!th.isOnSameTeam(player) && !th.inBattle()) {
				event.setCanceled(true);
				print(player, "[" + th.name + "]: Cannot interact with blocks in another faction's base except during battle.", EnumChatFormatting.RED);
			}
		}


	}


	private FontRenderer fontRenderer;
	private int color = 0xFFFFFF;
	private static final int red = 0xEB1717;
	private static final int green = 0x22EB17;

	private void verifyRenderer() {
		if (fontRenderer != null) return;
		Minecraft minecraft = Minecraft.getMinecraft();
		fontRenderer = minecraft.fontRenderer;
	}

	@SubscribeEvent
	public void render(RenderGameOverlayEvent.Post event) {

		//EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		//Mw.instance.markerManager.addMarker("TEST", "group", (int)player.posX, 100, (int)player.posZ, 0, red);
		//Mw.instance.markerManager.setVisibleGroupName("group");
		//Mw.instance.markerManager.update();
		//Mw.instance.markerManager.selectNextMarker();
	}

	/*
	 * @SubscribeEvent public void onPlayerOpenContainer(PlayerOpenContainerEvent
	 * event) { EntityPlayer player = event.entityPlayer;
	 * if(player.worldObj.isRemote || player.getTeam() == null) {return;}
	 * if(player.capabilities.isCreativeMode) {return;} if(!canAccess(player,
	 * (int)player.posX, (int)player.posZ)) { player.addChatComponentMessage(new
	 * ChatComponentText("Cannot open containers except in a claimed chunk!"));
	 * event.setCanceled(true); }
	 * 
	 * }
	 */
	//@SubscribeEvent


	@SubscribeEvent
	public void entitySpawnEvent(EntityJoinWorldEvent evt)
	{
		if(evt.entity instanceof NpcBase){return;}
		String s = EntityList.getEntityString(evt.entity);
		if(AncientWarfareNPC.statics.shouldEntityTargetNpcs(s))
		{    
			if(evt.entity instanceof EntityCreature)
			{
				EntityCreature e = (EntityCreature)evt.entity;
				if(evt.entity instanceof EntitySkeleton)
				{
					EntitySkeleton skel = (EntitySkeleton)evt.entity;
					if(skel.getSkeletonType()==0)//normal
					{
						e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));          
					}
					else//wither
					{
						e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));      
						e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));          
					}
				}
				else if(evt.entity instanceof IRangedAttackMob)
				{
					e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
				}
				else
				{        
					e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));      
					e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));           
				}
			}
		}
	}

}
