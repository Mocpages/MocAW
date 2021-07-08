package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcheli.MCH_Camera;
import mcheli.MCH_Lib;
import mcheli.MCH_ViewEntityDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class ItemBrainWorm extends Item{
    //String player = null;
    IIcon open = null;
    IIcon closed = null;

    public ItemBrainWorm(String name){
        super();
        this.setCreativeTab(AWNpcItemLoader.npcTab);

        this.setUnlocalizedName(name);
     //   this.setTextureName("ancientwarfare:npc/papers_closed");


    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg){
        //open = reg.registerIcon("ancientwarfare:npc/papers_open");
     //   closed = reg.registerIcon("ancientwarfare:npc/papers_closed");
    }



    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int i){
        if(i == 1) {
            return open;
        }else {
            return closed;
        }
    }

    public boolean hasNPC(ItemStack d) {
        if(!d.hasTagCompound()) {return false;}
        NBTTagCompound tag = d.getTagCompound();
        if(!tag.hasKey("tgtIdLSB")) {return false;}
        return true;
    }

    public NpcPlayerOwned getTarget(ItemStack d, World w) {
        if(!d.hasTagCompound()) {return null;}
        NBTTagCompound tag = d.getTagCompound();
        if(!tag.hasKey("tgtIdLSB")) {return null;}
        Long lsb = tag.getLong("tgtIdLSB");
        long msb = tag.getLong("tgtIdMSB");
        //UUID id = new UUID(msb, lsb);
        return (NpcPlayerOwned) WorldTools.getEntityByUUID(w, msb, lsb);

    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if(player.worldObj.isRemote) {return stack;}
        NBTTagCompound nbt;
        if (stack.hasTagCompound()){
            nbt = stack.getTagCompound();
        }
        else{
            nbt = new NBTTagCompound();
        }

        if(nbt.hasKey("tgtIdLSB")) {
            return stack;
        }



            MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, 2, 0);//TODO set range from config;
            if(pos!=null && pos.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY && pos.entityHit instanceof NpcPlayerOwned) {
                NpcPlayerOwned target = (NpcPlayerOwned)pos.entityHit;

                nbt.setLong("tgtIdLSB", target.getUniqueID().getLeastSignificantBits());
                nbt.setLong("tgtIdMSB", target.getUniqueID().getMostSignificantBits());

                player.addChatComponentMessage(new ChatComponentText("<" + player.getCommandSenderName()+"> OOGA BOOGA CODE WORDS"));
                player.addChatComponentMessage(new ChatComponentText("<" + target.getCustomNameTag() +"> AOOAOAOAOOAOAOUGUGUGHHH"));

            }

        stack.setTagCompound(nbt);
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean isHeld) {
        if(isHeld) {
            stack.setItemDamage(1);
            if(hasNPC(stack)){
                NpcPlayerOwned npc = getTarget(stack, world);
                if(world.isRemote) {
                    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                    if (npc != null) {
                        MCH_Camera camera = new MCH_Camera(npc.worldObj, npc, npc.posX, npc.posY + 2.1, npc.posZ);
                        camera.rotationPitch = player.rotationPitch;
                        camera.rotationYaw = player.rotationYaw;
                        MCH_ViewEntityDummy.getInstance(player.worldObj).update(camera);
                        MCH_Lib.setRenderViewEntity(MCH_ViewEntityDummy.getInstance(player.worldObj));
                        player.getEntityData().setBoolean("isViewNpc", true);

                        //  player.addChatComponentMessage(new ChatComponentText("e"));
                    } else{
                    }
                }
            }


        }else {

            stack.setItemDamage(0);
        }
    }



}
