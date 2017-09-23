package org.imas.kafka;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaMessageProducer implements MessageProducer {

	private static MetaMessageProducer instance = null;

	private Logger logger = LoggerFactory.getLogger(MetaMessageProducer.class);
	private final String topicName = "base-q";


	@Override
	public void sendMessage(JsonObject imas) {
		logger.info("meta message sent started " + new Date().toString());

		try {
			int hop = imas.getInt("hop");
			hop++;
			String cmd = "ack";
			String uuid = imas.getString("uuid");
			String role = "ack";

			JsonObject json = Json.createObjectBuilder()
					.add("imas", Json.createObjectBuilder()
							.add("hop", hop)
							.add("cmd", cmd)
							.add("uuid", uuid)
							.add("role", role)
							.add("containerId", ContainerIdResolver.INSTANCE.getContainerId()))
					.build();

			KafkaProvider.INSTANCE.getProducer().send(new ProducerRecord<String, String>(
					topicName, 
					Integer.toString(1),
					json.toString()
					));
			
			logger.info("sending message to " + topicName + " json message " + json.toString());
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		logger.info("acknowledgement sent to the server");

	}

	public static MessageProducer getInstance() {
		if(instance == null) instance = new MetaMessageProducer();

		return instance;
	}

}
