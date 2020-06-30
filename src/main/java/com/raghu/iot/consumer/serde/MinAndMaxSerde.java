package com.raghu.iot.consumer.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class MinAndMaxSerde implements Serde<MinAndMax> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<MinAndMax> serializer() {
        return new Serializer<MinAndMax>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, MinAndMax data) {
                String minAndMax = data.getMin() + ":" + data.getMax();
                return minAndMax.getBytes();
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<MinAndMax> deserializer() {
        return new Deserializer<MinAndMax>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public MinAndMax deserialize(String topic, byte[] countAndSum) {
                String minAndMaxStr = new String(countAndSum);
                Long min = Long.valueOf(minAndMaxStr.split(":")[0]);
                Long max = Long.valueOf(minAndMaxStr.split(":")[1]);
                return new MinAndMax(min, max);
            }

            @Override
            public void close() {
            }
        };
    }
}
