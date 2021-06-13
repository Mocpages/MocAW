package net.shadowmage.ancientwarfare.npc.economy;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.gamedata.MocFaction;

public class Firm {
	public ArrayList<ItemStack> capital = new ArrayList<ItemStack>();
	public ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
	public ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
	public FirmType type;
	public MocFaction fac;
	
}
