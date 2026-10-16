package com.monisom.kafka.model;

public enum LeqLevel {
    COMFORTABLE,
    ADEQUATE,
    ATTENTION,
    CRITICAL;

    public static LeqLevel from(double leq) {
        if (leq <= 35.0) {
            return COMFORTABLE;
        }
        if (leq <= 65.0) {
            return ADEQUATE;
        }
        if (leq <= 85.0) {
            return ATTENTION;
        }
        return CRITICAL;
    }
}
