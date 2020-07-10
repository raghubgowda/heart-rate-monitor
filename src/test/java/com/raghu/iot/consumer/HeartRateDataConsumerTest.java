package com.raghu.iot.consumer;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;

import static com.raghu.iot.consumer.Constants.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HeartRateDataConsumerTest {

    private TopologyTestDriver topologyTestDriver;
    private TestInputTopic<String, String> inputTopic;

    private final Properties config;

    public HeartRateDataConsumerTest() {
        config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    }

    @Before
    public void setUp(){
        Topology topology = HeartRateDataConsumer.getTopology();
        topologyTestDriver = new TopologyTestDriver(topology, config);
        inputTopic = topologyTestDriver.createInputTopic(TOPIC_NAME, Serdes.String().serializer(), Serdes.String().serializer());
    }

    @After
    public void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    public void shouldProduceValidCountInStateStore() {
        KeyValueStore<String, Long> countStore = topologyTestDriver.getKeyValueStore(MSG_COUNT_STORE);
        inputTopic.pipeInput("Device1", "1");
        assertEquals(1L, (long) countStore.get("Device1"));

        inputTopic.pipeInput("Device1", "2");
        assertEquals(2L, (long) countStore.get("Device1"));

        inputTopic.pipeInput("Device2", "3");
        assertEquals(1L, (long) countStore.get("Device2"));

        inputTopic.pipeInput("Device3", "4");
        assertEquals(1L, (long) countStore.get("Device3"));

        inputTopic.pipeInput("Device2", "5");
        assertEquals(2L, (long) countStore.get("Device2"));

        assertNull(countStore.get("Device0"));
    }

    @Test
    public void shouldProduceValidMinimumValueForStateStore() {
        KeyValueStore<String, Long> minStore = topologyTestDriver.getKeyValueStore(MIN_STORE);
        inputTopic.pipeInput("Device1", "1");
        inputTopic.pipeInput("Device1", "112");
        inputTopic.pipeInput("Device2", "12");
        inputTopic.pipeInput("Device2", "21");
        inputTopic.pipeInput("Device3", "0");
        inputTopic.pipeInput("Device3", "500");
        assertEquals(1L, (long) minStore.get("Device1"));
        assertEquals(12L, (long) minStore.get("Device2"));
        assertEquals(0L, (long) minStore.get("Device3"));
        assertNull(minStore.get("Device4"));
    }

    @Test
    public void shouldProduceValidMaximumValueForStateStore() {
        KeyValueStore<String, Long> maxStore = topologyTestDriver.getKeyValueStore(MAX_STORE);
        inputTopic.pipeInput("Device1", "112");
        inputTopic.pipeInput("Device1", "1");
        inputTopic.pipeInput("Device2", "12");
        inputTopic.pipeInput("Device2", "21");
        inputTopic.pipeInput("Device3", "500");
        assertEquals(112L, (long) maxStore.get("Device1"));
        assertEquals(21L, (long) maxStore.get("Device2"));
        assertEquals(500L, (long) maxStore.get("Device3"));
        assertNull(maxStore.get("Device4"));
    }
}