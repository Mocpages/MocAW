package net.shadowmage.ancientwarfare.npc.economy;

public class Check {
	double value = 0;
	BankAccount recipient;
	
	private Check(BankAccount r, double v) {
		value = v;
		recipient = r;
	}
	
	public static Check write(BankAccount r, double v) {
		return new Check(r, v);
	}
	
	public void resolve() {
		recipient.deposit(value);
	}
}
