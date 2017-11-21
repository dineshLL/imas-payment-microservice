package org.imas.kafka;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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
			System.out.println("from send message " + imas.toString());
			int hop = 0;
			String cmd;
			String role;
			
			if(imas.getString("cmd").equals("ack")) {
				hop = imas.getInt("hop");
				hop++;
				cmd = "ack";
				role = "ack";
			
			} else {
				hop = imas.getInt("hop");
				hop++;
				cmd = "end_ack";
				role = "end_ack";
			}

			String uuid = imas.getString("uuid");

			JsonObjectBuilder builder = Json.createObjectBuilder()
					.add("imas", Json.createObjectBuilder()
							.add("hop", hop)
							.add("cmd", cmd)
							.add("uuid", uuid)
							.add("role", role)
							.add("containerId", ContainerIdResolver.INSTANCE.getContainerId()));
			
			if(imas.getString("cmd").equals("end_ack")) {
				builder.add("exec_time", String.valueOf(imas.get("exec_time")));
			}
			
			JsonObject json = builder.build();
			
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
