package org.haemin.hMDamageLib.api;

public record AppliedModifier(
        String sourceId,
        StatType stat,
        Operation operation,
        double value,
        int priority
) {
}
