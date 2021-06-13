package net.shadowmage.ancientwarfare.npc.economy;

import java.util.ArrayList;

import net.minecraft.item.Item;

public class Market {
	public ArrayList<AskList> asks = new ArrayList<AskList>();
	public Bank bank;
	
	public Market(Bank b) {
		bank=b;
	}
	
	public AskList getAskList(Item i) {
		for(AskList list : asks) {
			if(list.item == i) {
				return list;
			}
		}
		AskList list = new AskList(i);
		asks.add(list);
		return list;
	}
	
	public void cleanLists() {
		for(AskList list : asks) {
			list.clear();
		}
	}
	
	public double getPrice(Item i) {
		try{
			return getFirst(i).pricePer;
		}catch(Exception e) {
			return -1;
		}
	}
	
	public Ask getFirst(Item i) {
		AskList list = getAskList(i);
		list.sort(); //TODO Assume list is sorted at beginning of round
		return list.get(0);
	}
	
	public void removeAsk(Ask a) {
		getAskList(a.item).remove(a);
	}
	
	public boolean buy(Item i, double p) {
		Ask ask = getFirst(i);
		if(p != ask.pricePer) {return false;} //Somehow the NPC expected price is wrong - which is bad.
		bank.addCheck(ask.writeCheck());
		ask.qty--;
		if(ask.qty <= 0) {
			removeAsk(ask);
		}
		return true;
	}
}
