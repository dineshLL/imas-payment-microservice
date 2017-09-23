package org.imas.kafka;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMessageProducer implements MessageProducer {

	private static BaseMessageProducer instance = null;

	private Logger logger = LoggerFactory.getLogger(BaseMessageProducer.class);
	private final String topicName = "base-q";

	public static BaseMessageProducer getInstance() {
		if(instance == null) instance = new BaseMessageProducer();

		return instance;
	}

	@Override
	public void sendMessage(JsonObject message) {
		logger.info("starting to send the message to the base-q");
		JsonObject imas = message.getJsonObject("imas");

		int hop = imas.getInt("hop");
		hop++;
		String cmd = imas.getString("cmd");
		String uuid = imas.getString("uuid");
		String role = imas.getString("role");

		JsonObject newMetaInfo = Json.createObjectBuilder()
				.add("hop", hop)
				.add("cmd", cmd)
				.add("uuid", uuid)
				.add("role", role)
				.add("containerId", ContainerIdResolver.INSTANCE.getContainerId())
				.build();
		JsonObject toBeSent = Json.createObjectBuilder()
				.add("payload", message.get("payload"))
				.add("imas", newMetaInfo)
				.build();

		KafkaProvider.INSTANCE.getProducer().send(new ProducerRecord<String, String>(
				topicName, 
				Integer.toString(1),
				toBeSent.toString()
				));

		logger.info("message sent to the base-q");
	}


}
