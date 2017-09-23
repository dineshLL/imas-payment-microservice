package org.imas.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public enum KafkaProvider {

	INSTANCE;

	private KafkaProducer<String, String> producer;
	private KafkaConsumer<String, String> consumer;

	private KafkaProvider() {
		Properties props = new Properties();

		props.put("bootstrap.servers", "192.168.100.199:9092");
		props.put("group.id", "auth-grp");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", 
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", 
				"org.apache.kafka.common.serialization.StringDeserializer");

		consumer = new KafkaConsumer<String, String>(props);
		
		
		Properties pProps = new Properties();
		pProps.put("bootstrap.servers", "192.168.100.199:9092");  
		pProps.put("acks", "all");
		pProps.put("retries", 0);
		pProps.put("batch.size", 16384);
		pProps.put("linger.ms", 1);   
		pProps.put("buffer.memory", 33554432);

		pProps.put("key.serializer", 
				"org.apache.kafka.common.serialization.StringSerializer");

		pProps.put("value.serializer", 
				"org.apache.kafka.common.serialization.StringSerializer");

		producer = new KafkaProducer<String, String>(pProps);

	}

	public KafkaProducer<String, String> getProducer() {
		return this.producer;
	}

	public KafkaConsumer<String, String> getConsumer() {
		return this.consumer;
	}

}

