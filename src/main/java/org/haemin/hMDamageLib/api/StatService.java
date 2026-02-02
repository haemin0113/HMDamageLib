package org.haemin.hMDamageLib.api;

import org.bukkit.entity.Player;

public interface StatService {
    void registerProvider(StatProvider provider);
    void unregisterProvider(String providerId);
    StatsSnapshot getSnapshot(Player player);
    double getValue(Player player, StatType stat);
    void markDirty(Player player);
    void invalidate(Player player);
}
