package org.imas.actors;

import java.io.Serializable;

import javax.json.JsonObject;

public interface Actor {
	
	Serializable act(JsonObject payload);
	
}
