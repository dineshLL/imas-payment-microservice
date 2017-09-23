package org.imas.actors;

public class Action {

	private String cmd;
	private String role;
	
	public Action(String cmd, String role) {
		this.cmd = cmd;
		this.role = role;
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
}
