/*
 * Copyright Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raghu.iot.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.raghu.iot.consumer.Constants.*;

public class HeartRateDataProducer {

  private static final Logger log = LoggerFactory.getLogger(HeartRateDataProducer.class);

  public static void main(final String [] args) throws Exception {
    final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";

    final Properties producerConfig = new Properties();
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    producerConfig.put(ProducerConfig.ACKS_CONFIG, ALL);
    producerConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
    producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    ProducerRecord<String, String> record;
    Random random = new Random();
    String KEY_PREFIX = DEVICE_PREFIX;

    while(true){
      try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerConfig)) {
        for (int i = 1; i <= 5; i++) {
          String key = KEY_PREFIX + i;
          String value = String.valueOf(random.nextInt(20));
          System.out.println("Key:"+key+" Value: "+value);
          log.info(String.format("Key: {} value: {value}", key, value));
          record = new ProducerRecord<>(TOPIC_NAME, key, value);
          RecordMetadata rm = kafkaProducer.send(record).get();
          log.info("Partition for key-value {0}::{1} is {2}", key, value, rm.partition());
        }
        TimeUnit.SECONDS.sleep(10);
      } catch (Exception e) {
        log.error("Producer thread was interrupted");
        e.printStackTrace();
        throw e;
      } finally {
        log.info("Producer closed");
      }
    }
  }
}
