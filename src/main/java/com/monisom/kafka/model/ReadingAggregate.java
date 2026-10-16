package com.monisom.kafka.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReadingAggregate {

    private double energySum = 0.0;
    private long count = 0;
    private double min = Double.MAX_VALUE;
    private double max = -Double.MAX_VALUE;
    private List<Double> samples = new ArrayList<>();

    public void add(double laeq) {
        energySum += Math.pow(10, laeq / 10.0);
        count++;
        if (laeq < min) {
            min = laeq;
        }
        if (laeq > max) {
            max = laeq;
        }
        samples.add(laeq);
    }

    public double leq() {
        return count > 0 ? 10.0 * Math.log10(energySum / count) : 0.0;
    }

    public double l10() {
        return percentile(0.90);
    }

    public double l50() {
        return percentile(0.50);
    }

    public double l90() {
        return percentile(0.10);
    }

    private double percentile(double p) {
        if (samples.isEmpty()) {
            return 0.0;
        }
        List<Double> sorted = new ArrayList<>(samples);
        Collections.sort(sorted);
        double idx = p * (sorted.size() - 1);
        int lo = (int) Math.floor(idx);
        int hi = (int) Math.ceil(idx);
        if (lo == hi) {
            return sorted.get(lo);
        }
        double frac = idx - lo;
        return sorted.get(lo) * (1.0 - frac) + sorted.get(hi) * frac;
    }
}
