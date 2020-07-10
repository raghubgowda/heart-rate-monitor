package com.raghu.iot.consumer;

import com.raghu.iot.consumer.restapi.controller.HeartRateMetricsController;
import com.raghu.iot.consumer.serde.MinAndMax;
import com.raghu.iot.consumer.serde.MinAndMaxSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Properties;

import static com.raghu.iot.consumer.Constants.*;

public class HeartRateDataConsumer {

  private static final Logger log = LoggerFactory.getLogger(HeartRateDataConsumer.class);

  public static void main(final String[] args) throws Exception {
    if (args.length == 0 || args.length > 2) {
      throw new IllegalArgumentException("usage: ... <portForRestEndPoint> [<bootstrap.servers> (optional)]");
    }
    final int port = Integer.parseInt(args[0]);
    final String bootstrapServers = args.length > 1 ? args[1] : "localhost:9092";

    final Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, CLIENT_ID);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, DEFAULT_HOST + ":" + port);


    final File stateDirectory = Files.createTempDirectory(new File("/tmp").toPath(), "iot-data-processor").toFile();
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateDirectory.getPath());

    final KafkaStreams countStreams = createStreams(streamsConfiguration);
    countStreams.cleanUp();
    countStreams.start();

    // Start the Restful proxy for servicing remote access to state stores
    final HeartRateMetricsController heartRateMetricsController = startRestProxy(countStreams, DEFAULT_HOST, port);

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        countStreams.close();
        heartRateMetricsController.stop();
      } catch (final Exception e) {
        log.error(e.getMessage());
      }
    }));
  }

  static HeartRateMetricsController startRestProxy(final KafkaStreams streams,
                                                   final String host,
                                                   final int port) throws Exception {
    final HostInfo hostInfo = new HostInfo(host, port);
    final HeartRateMetricsController heartRateHeartRateMetricsController = new HeartRateMetricsController(streams, hostInfo);
    heartRateHeartRateMetricsController.start(port);
    return heartRateHeartRateMetricsController;
  }

  static KafkaStreams createStreams(final Properties streamsConfiguration) {
    return new KafkaStreams(getTopology(), streamsConfiguration);
  }

  static Topology getTopology(){
    final StreamsBuilder builder = new StreamsBuilder();
    MinAndMaxSerde minAndMaxSerde = new MinAndMaxSerde();
    final KStream<String, String>
            dataStream = builder.stream(TOPIC_NAME, Consumed.with(Serdes.String(), Serdes.String()));
    final KGroupedStream<String, String> groupedByDevice = dataStream.groupByKey();

    KTable<String, MinAndMax> minAndMaxKTable = groupedByDevice
            .aggregate(() -> new MinAndMax((long) Integer.MAX_VALUE, (long) Integer.MIN_VALUE), (key, value, current) -> {
                      Long newMin = Math.min(current.getMin(), Long.parseLong(value));
                      Long newMax = Math.max(current.getMax(), Long.parseLong(value));
                      return new MinAndMax(newMin, newMax);
                    },
                    Materialized.with(Serdes.String(), minAndMaxSerde));

    // Create a State Store for with the all time min
    minAndMaxKTable
            .mapValues((key, minAndMax) -> minAndMax.getMin(),
                    Materialized.<String, Long,
                            KeyValueStore<Bytes, byte[]>>as(MIN_STORE)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(Serdes.Long()));

    // Create a State Store for with the all time max
    minAndMaxKTable
            .mapValues((key, minAndMax) -> minAndMax.getMax(),
                    Materialized.<String, Long,
                            KeyValueStore<Bytes, byte[]>>as(MAX_STORE)
                            .withKeySerde(Serdes.String())
                            .withValueSerde(Serdes.Long()));


    // Create a State Store for with the all time message count per device
    groupedByDevice.count(Materialized.<String, Long,
            KeyValueStore<Bytes, byte[]>>as(MSG_COUNT_STORE)
            .withValueSerde(Serdes.Long()));

    // Create a Windowed State Store that contains the message count for every 1 minute per device
    groupedByDevice.windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
            .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as(RANGE_MSG_CNT_STORE)
                    .withValueSerde(Serdes.Long()));

    return builder.build();
  }

}
