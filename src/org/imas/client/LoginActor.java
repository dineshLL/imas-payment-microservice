package org.imas.client;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

import org.imas.actors.AbstaractActor;
import org.imas.actors.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginActor extends AbstaractActor {

	Logger logger = LoggerFactory.getLogger(LoginActor.class);
	
	public LoginActor() {
		super("login");
	}

	@Override
	public Serializable act(JsonObject payload) {
		logger.info("login service executing...");
		
		sendMessage(new Action("log", "info"), JsonProvider.provider().createObjectBuilder().add("message", "please log me").build());
		return null;
	}

}
