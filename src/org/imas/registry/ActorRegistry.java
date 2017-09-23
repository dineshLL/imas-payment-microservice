package org.imas.registry;

import java.util.HashMap;
import java.util.Map;

import org.imas.actors.Actor;

public enum ActorRegistry {

	REGISTRY;
	
	private Map<String, Actor> map = new HashMap<String, Actor>();
	
	public void register(String command, Actor actor) {
		this.map.put(command, actor);
	}
	
	public Map<String, Actor> getMap() {
		return this.map;
	}
}
