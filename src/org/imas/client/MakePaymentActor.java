package org.imas.client;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

import org.imas.actors.AbstaractActor;
import org.imas.actors.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakePaymentActor extends AbstaractActor {

	Logger logger = LoggerFactory.getLogger(MakePaymentActor.class);
	
	public MakePaymentActor() {
		super("make");
	}

	@Override
	public Serializable act(JsonObject payload) {
		logger.info("make paymant service executing...");
		
		sendMessage(new Action("log", "info"), JsonProvider.provider().createObjectBuilder().add("message", "from make payment service").build());
		return null;
	}

}
