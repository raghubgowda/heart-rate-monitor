package com.raghu.iot.consumer.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CountAndSumSerde implements Serde<CountAndSum> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<CountAndSum> serializer() {
        return new Serializer<CountAndSum>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, CountAndSum data) {
                String countAndSum = data.getCount() + ":" + data.getSum();
                return countAndSum.getBytes();
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<CountAndSum> deserializer() {
        return new Deserializer<CountAndSum>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public CountAndSum deserialize(String topic, byte[] countAndSum) {
                String countAndSumStr = new String(countAndSum);
                Long count = Long.valueOf(countAndSumStr.split(":")[0]);
                Double sum = Double.valueOf(countAndSumStr.split(":")[1]);
                return new CountAndSum(count, sum);
            }

            @Override
            public void close() {
            }
        };
    }
}