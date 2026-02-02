package org.haemin.hMDamageLib.api;

public interface StatModifier {
    String sourceId();
    StatType stat();
    Operation operation();
    double value();
    int priority();
}
