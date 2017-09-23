package org.imas.kafka;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.imas.actors.Commander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;

public class Consumer {
	
	private static Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	public static void main(String[] args) {
		initActors();
		
		String topicName = "auth";

		KafkaConsumer<String, String> consumer = KafkaProvider.INSTANCE.getConsumer();
		
		consumer.subscribe(Arrays.asList(topicName));

		//print the topic name
		System.out.println("Subscribed to topic " + topicName);

		Thread loop = new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("loop");
				try {
					while (true) {
						ConsumerRecords<String, String> records = consumer.poll(100);
						for (ConsumerRecord<String, String> record : records) {

							String value = record.value();

							try (JsonReader reader = Json.createReader(new StringReader(value))) {
								JsonObject inbound = reader.readObject();
								JsonObject imas = inbound.getJsonObject("imas");
								logger.info("starting to process a message " + imas);
								String role = imas.getString("role");
								
								MessageProducer metaProducer = MessageProducerFactory.get(ProducerType.META);
								metaProducer.sendMessage(imas);
								
								new Commander().execute(role, inbound);
								
								logger.info("message processing completed");
								
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} catch (WakeupException e) {
					// ignore for shutdown
				} finally {
					System.out.println("consumer closed");
					consumer.close();
				}
			}
		});

		loop.start();


		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			ThreadGroup group = Thread.currentThread().getThreadGroup();
			Thread[] threads = new Thread[group.activeCount()];
			group.enumerate(threads);

			for(Thread t : threads) {
				if(t.getName().equals("loop")) {

					t.interrupt();
				}
			}
		}));
	}

	private static void initActors() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try {
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			  if (info.getName().startsWith("org.imas.client")) {
			    final Class<?> clazz = info.load();
			    clazz.newInstance();
			  }
			}
		} catch (IOException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println("init method called");
	}
	
	/*public static void main(String[] args) {
		Map<String, Actor<?>> map = new HashMap<>();
		
		map.put("one", new ActorImpl());
		map.put("two", new ActorTwo());
		
		AbstaractActor<?> a = (AbstaractActor<?>) map.get("one");
		
		a.run("Dinesh");
	}*/
}
