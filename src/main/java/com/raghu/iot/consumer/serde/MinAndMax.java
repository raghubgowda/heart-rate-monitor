package com.raghu.iot.consumer.serde;

public class MinAndMax {
    private final Long min;
    private final Long max;

    public MinAndMax(Long min, Long max) {
        this.min = min;
        this.max = max;
    }

    public Long getMin() {
        return min;
    }
    public Long getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "min = " + min + " and max = " + max;
    }
}
