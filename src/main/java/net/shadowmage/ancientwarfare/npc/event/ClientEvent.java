package net.shadowmage.ancientwarfare.npc.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lumien.hardcoredarkness.HardcoreDarkness;
import mcheli.MCH_Lib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemBrainWorm;
import net.shadowmage.ancientwarfare.npc.item.ItemBuildingSettings;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

@SideOnly(Side.CLIENT)
public class ClientEvent {
    private ClientEvent(){}
    public static final ClientEvent INSTANCE = new ClientEvent();
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
        if(item== AWNpcItemLoader.scanner){
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
    private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta, float r, float g, float b) {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
        RenderTools.adjustBBForPlayerPos(bb, player, delta);
        RenderTools.drawOutlinedBoundingBox(bb, r, g, b);

    }
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) throws Exception {
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

                player.getEntityData().setBoolean("isViewNpc", false);
                MCH_Lib.setRenderViewEntity(player);

                //player.addChatComponentMessage(new ChatComponentText("b"));

            }else{
                //player.addChatComponentMessage(new ChatComponentText("test: " + player.getEntityData().getBoolean("isViewNpc")));

            }
        }
//		MCH_Lib.setRenderViewEntity(player);

    }
}
