package org.imas.actors;

import java.util.Map;

import javax.json.JsonObject;

import org.imas.registry.ActorRegistry;

public class Commander {

	public void execute(String command, JsonObject inboundMessage) {
		Map<String, Actor> map = ActorRegistry.REGISTRY.getMap();
		AbstaractActor actor = (AbstaractActor) map.get(command);

		actor.run(inboundMessage);
	}

}
