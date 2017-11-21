package org.imas.actors;

import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;

import org.imas.kafka.ContainerIdResolver;
import org.imas.kafka.MessageProducer;
import org.imas.kafka.MessageProducerFactory;
import org.imas.kafka.ProducerType;
import org.imas.registry.ActorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstaractActor implements Actor {

	Logger logger = LoggerFactory.getLogger(AbstaractActor.class);

	public abstract Serializable act(JsonObject payload);
	private JsonObject inboundMessage;
	private long start = System.currentTimeMillis();
	private long end = 0;

	public AbstaractActor(String command) {
		ActorRegistry.REGISTRY.register(command, this);
	}

	public void run(JsonObject inboundMessage) {
		this.inboundMessage = inboundMessage;

		Serializable returnValue = act(inboundMessage.getJsonObject("payload"));
		end(returnValue);
	}

	protected void sendMessage(Action action, JsonObject json) {
		logger.info("sending the message to " + action.getCmd() + ", role: " + action.getRole()); 
		MessageProducer baseProducer = MessageProducerFactory.get(ProducerType.BASE);

		JsonObject imas = inboundMessage.getJsonObject("imas");

		JsonObject newMetaInfo = Json.createObjectBuilder()
				.add("hop", imas.getInt("hop"))
				.add("cmd", action.getCmd())
				.add("uuid", imas.getString("uuid"))
				.add("role", action.getRole())
				.add("containerId", ContainerIdResolver.INSTANCE.getContainerId())
				.build();

		JsonObject toBeSent = Json.createObjectBuilder()
				.add("payload", json)
				.add("imas", newMetaInfo)
				.add("exec_time", end - start)
				.build();

		baseProducer.sendMessage(toBeSent);
	}

	protected Object sendMessageAndGet(Action action, JsonObject json) {
		logger.info("im sending the " + json + "to " + action.getCmd() + " waiting for return");

		return "return value";
	}

	private void end(Serializable obj) {
		logger.info("actor execution ending....");
		this.end = System.currentTimeMillis();
		
		MessageProducer metaProducer = MessageProducerFactory.get(ProducerType.META);
		

		JsonObject imas = inboundMessage.getJsonObject("imas");

		JsonObject newMetaInfo = Json.createObjectBuilder()
				.add("hop", imas.getInt("hop"))
				.add("cmd", "end_ack")
				.add("uuid", imas.getString("uuid"))
				.add("role", "end_ack")
				.add("exec_time", end - start)
				.add("containerId", ContainerIdResolver.INSTANCE.getContainerId())
				.build();
		
//		JsonObject toBeSent = Json.createObjectBuilder()
//				.add("imas", newMetaInfo)
//				
//				.build();
		
		metaProducer.sendMessage(newMetaInfo);
	}

}
