/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.streamer.restproxy.controller;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@SuppressWarnings("rawtypes")
@Service
@PropertySource("classpath:application.properties")
public class PublisherSubscriberServiceImpl implements PublisherSubscriberService {

	private static final String EARLIEST = "earliest";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final int MAX_NO_MESSAGE_FOUND_COUNT = 100;

	@Value("${bootstrap.servers}")
	private String bootStrapServer;

	@Value("${security.protocol}")
	private String securityProtocol;

	@Value("${sasl.mechanism}")
	private String saslMechanism;

	@Value("${max.block.ms}")
	private int maxBlockMs;

	@Value("${acks}")
	private String acks;

	@Value("${retries}")
	private int retries;

	@Value("${batch.size}")
	private int batchSize;

	@Value("${linger.ms}")
	private int lingerMs;

	@Value("${buffer.memory}")
	private int bufferMemory;

	@Value("${key.serializer}")
	private String keySerializer;

	@Value("${value.serializer}")
	private String valueSerializer;

	@Value("${key.deserializer}")
	private String keyDeserializer;

	@Value("${value.deserializer}")
	private String valueDeSerializer;

	@Value("${enable.auto.commit}")
	private String enableAutoCommit;

	@Value("${auto.commit.interval.ms}")
	private int autoCommitIntervalMs;

	@Value("${request.timeout.ms}")
	private int requestTimeoutMs;

	@SuppressWarnings("resource")
	@Override
	public ResponseEntity postMsgsToKafka(String payload, String topic) {
		Producer<String, String> producer = new KafkaProducer<>(createPublisherConnectionProperties());
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, payload);

		try {

			Future<RecordMetadata> future = producer.send(record);
			RecordMetadata recordMetadata = future.get();
			logger.debug("offset:" + recordMetadata.offset());
			logger.debug("partition:" + recordMetadata.partition());
			logger.debug("topic:" + recordMetadata.topic());
			logger.debug("value size:" + recordMetadata.serializedValueSize());
		} catch (Exception e) {
			logger.error("Error in postMsgsToKafka :", e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity getMsgsFromKafka(String authorization, String topic, String groupName, String groupId) {
		Consumer<String, String> consumer = new KafkaConsumer<>(createConsumerConnectionProperties(groupId, groupName));
		consumer.subscribe(Collections.singletonList(topic));
		StringBuilder sb = new StringBuilder();
		int noMessageToFetech = 0;
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				logger.debug("Record Count:" + records.count());
				logger.debug("Number of tries:" + noMessageToFetech);
				if (records.count() == 0) {
					noMessageToFetech++;
					if (noMessageToFetech > MAX_NO_MESSAGE_FOUND_COUNT) {
						break;
					} else {
						continue;
					}
				}
				records.forEach(record -> {
					sb.append(record.value());
					sb.append("\n");
				});
				logger.debug("Records:" + sb.toString());
				consumer.commitSync();

			}
		} catch (Exception e) {
			logger.error("Error in getting MsgsToKafka :", e);
			consumer.close();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		consumer.close();
		return new ResponseEntity(sb.toString(), HttpStatus.OK);
	}

	private Properties createPublisherConnectionProperties() {
		Properties props = new Properties();

		props.put("bootstrap.servers", bootStrapServer);
		props.put("sasl.mechanism", saslMechanism);
		props.put("max.block.ms", maxBlockMs);
		props.put("acks", acks);
		props.put("retries", retries);
		props.put("batch.size", batchSize);
		props.put("linger.ms", lingerMs);
		props.put("buffer.memory", bufferMemory);
		props.put("key.serializer", keySerializer);
		props.put("value.serializer", valueSerializer);
		props.put("request.timeout.ms", requestTimeoutMs);

		return props;
	}

	private Properties createConsumerConnectionProperties(String groupId, String groupName) {
		String offset = null;

		Properties props = new Properties();
		props.put("bootstrap.servers", bootStrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put("sasl.mechanism", saslMechanism);
		props.put("enable.auto.commit", enableAutoCommit);
		props.put("auto.commit.interval.ms", autoCommitIntervalMs);
		props.put("request.timeout.ms", requestTimeoutMs);
		props.put("key.deserializer", keyDeserializer);
		props.put("value.deserializer", valueDeSerializer);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
		return props;
	}
}
