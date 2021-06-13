package net.shadowmage.ancientwarfare.npc.gamedata;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RoleOfficer extends Role{
	public static Item revolver_1860 = GameData.getItemRegistry().get("flansmod:1860");
	public static Item ammo_1860 = GameData.getItemRegistry().get("flansmod:44calball");
	public static Item aua_1860_uniform = GameData.getItemRegistry().get("flansmod:usa1800");
	public static Item sword = GameData.getItemRegistry().get("flansmod:infantry");
	public static RoleOfficer role = new RoleOfficer();
	
	public RoleOfficer() {
		super("officer");
		super.addItem(new ItemStack(revolver_1860), 0);
		super.addItem(new ItemStack(sword), 1);
		super.addItem(new ItemStack(aua_1860_uniform), 38);
		super.addItem(new ItemStack(ammo_1860, 12), 2);
		super.addItem(new ItemStack(ammo_1860, 12), 2);
	}
	
}
