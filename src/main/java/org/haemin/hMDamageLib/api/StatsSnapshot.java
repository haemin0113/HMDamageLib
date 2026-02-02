package org.haemin.hMDamageLib.api;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class StatsSnapshot {
    private final EnumMap<StatType, Double> values;
    private final EnumMap<StatType, List<AppliedModifier>> breakdown;

    public StatsSnapshot(EnumMap<StatType, Double> values, EnumMap<StatType, List<AppliedModifier>> breakdown) {
        this.values = new EnumMap<>(StatType.class);
        if (values != null) {
            this.values.putAll(values);
        }
        this.breakdown = new EnumMap<>(StatType.class);
        if (breakdown != null) {
            for (Map.Entry<StatType, List<AppliedModifier>> e : breakdown.entrySet()) {
                this.breakdown.put(e.getKey(), e.getValue() == null ? List.of() : List.copyOf(e.getValue()));
            }
        }
    }

    public double get(StatType stat) {
        return values.getOrDefault(stat, 0.0);
    }

    public Map<StatType, Double> values() {
        return Collections.unmodifiableMap(values);
    }

    public List<AppliedModifier> breakdown(StatType stat) {
        List<AppliedModifier> list = breakdown.get(stat);
        return list == null ? List.of() : list;
    }

    public Map<StatType, List<AppliedModifier>> breakdownAll() {
        EnumMap<StatType, List<AppliedModifier>> copy = new EnumMap<>(StatType.class);
        copy.putAll(breakdown);
        return Collections.unmodifiableMap(copy);
    }
}
