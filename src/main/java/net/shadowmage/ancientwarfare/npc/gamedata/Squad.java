package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

public class Squad {
	public ArrayList<PlayerRole> players = new ArrayList<PlayerRole>();
	public ArrayList<String> invitations = new ArrayList<String>();
	public String name;
	public String type;
	
	public Squad(EntityPlayer sl, String n, String t) {
		players.add(new PlayerRole(sl, null));
		name = n;
		type = t;
	}
	
	public boolean isPlayerInSquad(EntityPlayer p) {
		for(PlayerRole pr : players) {
			if(pr.player == p){
				return true;
			}
		}
		return false;
	}
	
	public PlayerRole getPRForPlayer(EntityPlayer p) {
		for(PlayerRole pr : players) {
			if(pr.player == p){
				return pr;
			}
		}
		return null;
	}
	
	public class PlayerRole{
		EntityPlayer player;
		private Role role;
		
		PlayerRole(EntityPlayer p, Role r){
			player = p;
			setRole(r);
		}
		
		public String getPlayerName() {
			return player.getDisplayName();
		}
		
		public String getRoleName() {
			if(getRole() == null) {
				return "None";
			}else if(getRole() instanceof RoleOfficer) {
				return "Officer";
			}
			
			return "None";
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}
	}
}
