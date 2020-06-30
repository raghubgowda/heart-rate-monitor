package com.raghu.iot.consumer.serde;

public class CountAndSum {
    private final Long count;
    private final Long sum;

    public CountAndSum(Long count, Long sum) {
        this.count = count;
        this.sum = sum;
    }

    public Long getCount() {
        return count;
    }
    public Long getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "count = " + count + " and sum = " + sum;
    }
}
