package net.shadowmage.ancientwarfare.npc.economy;

import java.util.ArrayList;
import java.util.Comparator;

import net.minecraft.item.Item;

public class AskList {
	public Item item;
	public ArrayList<Ask> asks = new ArrayList<Ask>();

	public AskList(Item i) {
		item = i;
	}

	public void clear() {
		asks.clear();
	}

	public void sort() {
		asks.sort(askSorter);
	}
	
	public Ask get(int i) {
		return asks.get(0);
	}
	
	public void remove(Ask a) {
		asks.remove(a);
	}
	
	public static Comparator<Ask> askSorter = new Comparator<Ask>(){
		@Override
		public int compare(Ask ask1, Ask ask2) {
			return (int)(ask2.pricePer-ask1.pricePer);
		}
	};


}
