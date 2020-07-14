package com.raghu.iot.consumer.serde;

public class CountAndSum {
    private final Long count;
    private final Double sum;

    public CountAndSum(Long count, Double sum) {
        this.count = count;
        this.sum = sum;
    }

    public Long getCount() {
        return count;
    }
    public Double getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "count = " + count + " and sum = " + sum;
    }
}
