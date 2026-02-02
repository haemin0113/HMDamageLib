package org.haemin.hMDamageLib.impl;

import org.haemin.hMDamageLib.api.Operation;
import org.haemin.hMDamageLib.api.StatModifier;
import org.haemin.hMDamageLib.api.StatType;

public record SimpleStatModifier(
        String sourceId,
        StatType stat,
        Operation operation,
        double value,
        int priority
) implements StatModifier {
}
