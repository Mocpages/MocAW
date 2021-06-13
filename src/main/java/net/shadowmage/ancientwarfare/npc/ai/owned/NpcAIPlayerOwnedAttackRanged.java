package net.shadowmage.ancientwarfare.npc.ai.owned;

import com.flansmod.client.FlansModClient;
import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EnumFireMode;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.ItemShootable;
import com.flansmod.common.guns.ShootableType;

import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;

import com.flansmod.common.network.PacketGunRecoil;
import com.flansmod.common.network.PacketPlaySound;
import com.flansmod.common.network.PacketReload;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedAttackRanged extends NpcAI
{

	//private final IRangedAttackMob rangedAttacker;
	private int attackDelay = 35;
	private double attackDistance = 64.d * 64.d;
	private EntityLivingBase target;
	private float reloadTime = 0;
	private int burstRoundsRemaining = 0;
	private boolean reloading = false;
	float shootTime = 0;
	
	public NpcAIPlayerOwnedAttackRanged(NpcBase npc)
	{
		super(npc);
		//this.rangedAttacker = (IRangedAttackMob)npc;//will classcastexception if improperly used..
		this.moveSpeed = 1.d;
	}

	@Override
	public boolean shouldExecute()
	{
		return npc.getIsAIEnabled() && npc.getAttackTarget()!=null && !npc.getAttackTarget().isDead;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting()
	{
		return npc.getIsAIEnabled() && target!=null && !target.isDead && target==npc.getAttackTarget();
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask()
	{
		target = null;
		moveRetryDelay=0;
		attackDelay=0;
		npc.renderMode = 0;
		npc.removeAITask(TASK_ATTACK + TASK_MOVE);
	}

	@Override
	public void startExecuting()
	{
		target = npc.getAttackTarget();
		moveRetryDelay=0;
		attackDelay=0;
		npc.renderMode = 1;
		npc.addAITask(TASK_ATTACK);
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask()
	{  
		npc.renderMode = 1;
		shootTime --;
		reloadTime --;
		double dist = this.npc.getDistanceSq(this.target.posX, this.target.posY, this.target.posZ);
		boolean canSee = this.npc.getEntitySenses().canSee(this.target);  
		
		double dist2d = Math.sqrt(npc.getDistanceSq(target.posX, npc.posY, target.posZ));
		this.npc.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
		
		float yawToTgt = 180 - (float) Math.toDegrees(Math.atan2(npc.posX - target.posX, npc.posZ - target.posZ));
		
		if(yawToTgt > npc.rotationYaw) {
			npc.rotationYaw += 1;
		}else if(yawToTgt < npc.rotationYaw) {
			npc.rotationYaw -= 1;
		}
		
		float pitchToTgt = 90 - (float) Math.toDegrees(Math.atan2(dist2d, npc.posY - target.posY));	
		
		if(pitchToTgt > npc.rotationPitch) {
			npc.rotationPitch += 1;
		}else if(pitchToTgt < npc.rotationPitch) {
			npc.rotationPitch -= 1;
		}
		
		boolean aiming = false;
		if(Math.abs(pitchToTgt-npc.rotationPitch)<=1 && Math.abs(yawToTgt - npc.rotationYaw)<=1) {
			aiming = true;
		}
		//System.out.println("Updating. Aiming " + aiming + " dist " + dist + " cansee " + canSee + " shoot time " + shootTime);
		if(dist>attackDistance || !canSee)
		{
			this.npc.addAITask(TASK_MOVE);
			this.moveToEntity(target, dist);    
		}
		else
		{
			npc.removeAITask(TASK_MOVE);
			this.npc.getNavigator().clearPathEntity();
			ItemStack stack = npc.getEquipmentInSlot(0);
			if(!(stack.getItem() instanceof ItemGun)) {return;}

			if(this.shootTime<=0 && aiming)
			{
			//	System.out.println("Attempting to fire");
				GunType gunType = ((ItemGun) stack.getItem()).type;
				npcShoot(stack, gunType, npc.worldObj);
				
				//npc.setCurrentItemOrArmor(0, npcShoot(stack, gunType, npc.worldObj));
			}
		}  
	}	

	public ItemStack npcShoot(ItemStack gunStack, GunType gunType, World world) {
		//System.out.println("yeet");
		//Go through the bullet stacks in the gun and see if any of them are not null
		int bulletID = 0;
		
		ItemStack bulletStack = null;
		for (; bulletID < gunType.getNumAmmoItemsInGun(gunStack); bulletID++) {
			ItemStack checkingStack = ((ItemGun) gunStack.getItem()).getBulletItemStack(gunStack, bulletID);
			if (checkingStack != null && checkingStack.getItem() != null && checkingStack.getItemDamage() < checkingStack.getMaxDamage()) {
				bulletStack = checkingStack;
				break;
			}
		}

		//If no bullet stack was found, reload
		if (bulletStack == null) {
			
			int maxAmmo = gunType.getNumAmmoItemsInGun(gunStack);
			boolean singlesReload = maxAmmo > 1;
			int reloadCount;

			if (singlesReload) {
				reloadCount = 0;
				for (int i = 0; i < gunType.getNumAmmoItemsInGun(gunStack); i++) {
					ItemStack oldBulletStack = ((ItemGun) gunStack.getItem()).getBulletItemStack(gunStack, i);
					if (oldBulletStack != null && (oldBulletStack.getMaxDamage() - oldBulletStack.getItemDamage()) == 0) {
						reloadCount += 1;
					} else if (oldBulletStack == null) {
						reloadCount += 1;
					}
				}
			} else {
				reloadCount = 1;
			}

			if(false) {//if (((ItemGun) gunStack.getItem()).reload(gunStack, gunType, world, npc, npc.backpackInventory, false, false)) {
				//Set player shoot delay to be the reload delay
				//Set both gun delays to avoid reloading two guns at once
				//data.shootTimeRight = data.shootTimeLeft = (int)gunType.getReloadTime(gunStack);

				float reloadTime = singlesReload ? (gunType.reloadTime / maxAmmo) * reloadCount : gunType.reloadTime;

				reloading = true;
				burstRoundsRemaining = 0;
					
				//Send reload packet to induce reload effects client side
				//ItemGun.setReloadCount(gunStack, reloadCount);
				//FlansMod.getPacketHandler().sendTo(new PacketReload(false, reloadCount, (int) reloadTime), entityplayer);
				//Play reload sound
				String soundToPlay = null;
				AttachmentType grip = gunType.getGrip(gunStack);

				if (gunType.getSecondaryFire(gunStack) && grip != null && grip.secondaryReloadSound != null)
					soundToPlay = grip.secondaryReloadSound;
				else if (gunType.reloadSoundOnEmpty != null)
					soundToPlay = gunType.reloadSoundOnEmpty;
				else if (gunType.reloadSound != null)
					soundToPlay = gunType.reloadSound;

				if (soundToPlay != null && gunType.getNumAmmoItemsInGun(gunStack) == 1)
					PacketPlaySound.sendSoundPacket(npc.posX, npc.posY, npc.posZ, gunType.reloadSoundRange, npc.dimension, soundToPlay, true);
			} else if ((gunType.clickSoundOnEmpty != null)) {
				PacketPlaySound.sendSoundPacket(npc.posX, npc.posY, npc.posZ, gunType.reloadSoundRange, npc.dimension, gunType.clickSoundOnEmpty, true);
			}
		}
		//A bullet stack was found, so try shooting with it
        else if (bulletStack.getItem() instanceof ItemShootable) {
            //Shoot
            shoot(gunStack, gunType, world, bulletStack);
            //canClick = true;

            //Damage the bullet item
            bulletStack.setItemDamage(bulletStack.getItemDamage() + 1);

            //Update the stack in the gun
            setBulletItemStack(gunStack, bulletStack, bulletID, gunType);

            if (gunType.getFireMode(gunStack) == EnumFireMode.BURST) {
                if (burstRoundsRemaining > 0)
                    burstRoundsRemaining--;
            }
            if (gunType.consumeGunUponUse)
                return null;
        }
		return gunStack;
    }
	
	private void shoot(ItemStack stack, GunType gunType, World world, ItemStack bulletStack) {
		//System.out.println("Shooting");
        //flash(entityplayer);
        ShootableType bullet = ((ItemShootable) bulletStack.getItem()).type;
        boolean lastBullet = false;
        ItemStack[] bulletStacks = new ItemStack[gunType.getNumAmmoItemsInGun(stack)];
        for (int i = 0; i < gunType.getNumAmmoItemsInGun(stack); i++) {
            bulletStacks[i] = ((ItemGun) stack.getItem()).getBulletItemStack(stack, i);
            if (bulletStacks[i] != null && bulletStacks[i].getItem() instanceof ItemBullet && (bulletStacks[i].getMaxDamage() - bulletStacks[i].getItemDamage() == 1))
                lastBullet = true;
        }

        // Play a sound if the previous sound has finished
        if (((ItemGun) stack.getItem()).soundDelay <= 0 && gunType.shootSound != null) {
            AttachmentType barrel = gunType.getBarrel(stack);
            AttachmentType grip = gunType.getGrip(stack);

            boolean silenced = barrel != null && barrel.silencer && !gunType.getSecondaryFire(stack);
            //world.playSoundAtEntity(entityplayer, type.shootSound, 10F, type.distortSound ? 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F) : 1.0F);
            String soundToPlay = null;
            if (gunType.getSecondaryFire(stack) && grip != null && grip.secondaryShootSound != null)
                soundToPlay = grip.secondaryShootSound;
            else if (lastBullet && gunType.lastShootSound != null)
                soundToPlay = gunType.lastShootSound;
            else if (silenced && gunType.suppressedShootSound != null)
                soundToPlay = gunType.suppressedShootSound;
            else if (gunType.shootSound != null)
                soundToPlay = gunType.shootSound;

            if (soundToPlay != null)
                PacketPlaySound.sendSoundPacket(npc.posX, npc.posY, npc.posZ, gunType.gunSoundRange, npc.dimension, soundToPlay, gunType.distortSound, silenced);
            ((ItemGun) stack.getItem()).soundDelay = gunType.shootSoundLength;
        }
        if (!world.isRemote && bulletStack.getItem() instanceof ItemShootable) {
        	System.out.println("Beginning bullet spawn process");
            // Spawn the bullet entities
            ItemShootable itemShootable = (ItemShootable) bulletStack.getItem();
            ShootableType shootableType = itemShootable.type;
            int numBullets = -1;
            float spread = -1.0F;


            if (shootableType instanceof BulletType) {
                if (gunType.allowNumBulletsByBulletType) {
                    numBullets = ((BulletType) shootableType).numBullets;
                }
                if (gunType.allowSpreadByBullet) {
                    spread = ((BulletType) shootableType).bulletSpread;
                }
            }


            if (numBullets <= 0) {
                numBullets = gunType.getNumBullets(stack);
            }

            if (spread <= 0) {
                float result = gunType.getSpread(stack);

                //If crouch/sneak, then lower spread by 10%
                if (npc.isSneaking())
                    result = result * 0.9F;
                
                //If running, then increase spread by 75%
                if (npc.isSprinting())
                    result = result * 1.75F;

                spread = result;
            }

            for (int k = 0; k < numBullets; k++) {
            	System.out.println("Spawning bullet");
                world.spawnEntityInWorld(itemShootable.getEntity(
                        world,
                        npc,
                        spread,
                        gunType.getDamage(stack),
                        gunType.getBulletSpeed(stack),
                        numBullets > 1,
                        bulletStack.getItemDamage(),
                        gunType));
            }
        }

        shootTime = gunType.getShootDelay(stack);
    }

	
	
	/**
     * Set the bullet item stack stored in the gun's NBT data (the loaded magazine / bullets)
     */
    public void setBulletItemStack(ItemStack gun, ItemStack bullet, int id, GunType type) {
    	System.out.println("yeet");
        //If the gun has no tags, give it some
        if (!gun.hasTagCompound()) {
            gun.stackTagCompound = new NBTTagCompound();
        }

        String s;
        if (type.getSecondaryFire(gun))
            s = "secondaryAmmo";
        else
            s = "ammo";

        //If the gun has no ammo tags, give it some
        if (!gun.stackTagCompound.hasKey(s)) {
            NBTTagList ammoTagsList = new NBTTagList();
            for (int i = 0; i < type.getNumAmmoItemsInGun(gun); i++) {
                ammoTagsList.appendTag(new NBTTagCompound());
            }
            gun.stackTagCompound.setTag(s, ammoTagsList);
        }
        //Take the list of ammo tags
        NBTTagList ammoTagsList = gun.stackTagCompound.getTagList(s, Constants.NBT.TAG_COMPOUND);
        //Get the specific ammo tags required
        NBTTagCompound ammoTags = ammoTagsList.getCompoundTagAt(id);
        //Represent empty slots by null types
        if (bullet == null) {
            ammoTags = new NBTTagCompound();
        } else {
            //Set the tags to match the bullet stack
            bullet.writeToNBT(ammoTags);
        }
    }
}
