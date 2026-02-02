package org.haemin.hMDamageLib.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.haemin.hMDamageLib.api.AppliedModifier;
import org.haemin.hMDamageLib.api.Operation;
import org.haemin.hMDamageLib.api.StatModifier;
import org.haemin.hMDamageLib.api.StatProvider;
import org.haemin.hMDamageLib.api.StatRecalculateEvent;
import org.haemin.hMDamageLib.api.StatService;
import org.haemin.hMDamageLib.api.StatType;
import org.haemin.hMDamageLib.api.StatsSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StatServiceImpl implements StatService {

    private final Plugin plugin;
    private final Map<String, StatProvider> providers = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry> cache = new ConcurrentHashMap<>();

    public StatServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerProvider(StatProvider provider) {
        if (provider == null || provider.id() == null) return;
        providers.put(provider.id(), provider);
    }

    @Override
    public void unregisterProvider(String providerId) {
        if (providerId == null) return;
        providers.remove(providerId);
    }

    @Override
    public StatsSnapshot getSnapshot(Player player) {
        if (player == null) {
            return new StatsSnapshot(new EnumMap<>(StatType.class), new EnumMap<>(StatType.class));
        }

        CacheEntry entry = cache.computeIfAbsent(player.getUniqueId(), k -> new CacheEntry());
        if (entry.snapshot == null || entry.dirty) {
            entry.snapshot = recalc(player);
            entry.dirty = false;
        }
        return entry.snapshot;
    }

    @Override
    public double getValue(Player player, StatType stat) {
        return getSnapshot(player).get(stat);
    }

    @Override
    public void markDirty(Player player) {
        if (player == null) return;
        CacheEntry entry = cache.computeIfAbsent(player.getUniqueId(), k -> new CacheEntry());
        entry.dirty = true;
    }

    @Override
    public void invalidate(Player player) {
        if (player == null) return;
        cache.remove(player.getUniqueId());
    }

    private StatsSnapshot recalc(Player player) {
        EnumMap<StatType, List<AppliedModifier>> breakdown = new EnumMap<>(StatType.class);
        for (StatType t : StatType.values()) {
            breakdown.put(t, new ArrayList<>());
        }

        List<StatModifier> all = new ArrayList<>();
        for (StatProvider provider : providers.values()) {
            Collection<StatModifier> mods;
            try {
                mods = provider.provide(player);
            } catch (Throwable t) {
                continue;
            }
            if (mods == null || mods.isEmpty()) continue;
            for (StatModifier m : mods) {
                if (m == null || m.stat() == null || m.operation() == null || m.sourceId() == null) continue;
                all.add(m);
            }
        }

        all.sort(Comparator.comparing(StatModifier::stat).thenComparingInt(StatModifier::priority));

        EnumMap<StatType, Double> values = new EnumMap<>(StatType.class);
        for (StatType t : StatType.values()) {
            values.put(t, 0.0);
        }

        for (StatType stat : StatType.values()) {
            double add = 0.0;
            double mul = 1.0;
            double finalAdd = 0.0;
            double finalMul = 1.0;

            for (StatModifier m : all) {
                if (m.stat() != stat) continue;
                breakdown.get(stat).add(new AppliedModifier(m.sourceId(), m.stat(), m.operation(), m.value(), m.priority()));
                if (m.operation() == Operation.ADD) {
                    add += m.value();
                } else if (m.operation() == Operation.MUL) {
                    mul *= (1.0 + m.value());
                } else if (m.operation() == Operation.FINAL_ADD) {
                    finalAdd += m.value();
                } else if (m.operation() == Operation.FINAL_MUL) {
                    finalMul *= (1.0 + m.value());
                }
            }

            double v = (add * mul + finalAdd) * finalMul;
            values.put(stat, v);
        }

        EnumMap<StatType, List<AppliedModifier>> breakdownFinal = new EnumMap<>(StatType.class);
        for (Map.Entry<StatType, List<AppliedModifier>> e : breakdown.entrySet()) {
            breakdownFinal.put(e.getKey(), List.copyOf(e.getValue()));
        }

        StatsSnapshot snapshot = new StatsSnapshot(values, breakdownFinal);

        if (Bukkit.isPrimaryThread()) {
            StatRecalculateEvent event = new StatRecalculateEvent(player, snapshot);
            Bukkit.getPluginManager().callEvent(event);
            StatsSnapshot result = event.getSnapshot();
            return result == null ? snapshot : result;
        }

        return snapshot;
    }

    private static final class CacheEntry {
        private volatile StatsSnapshot snapshot;
        private volatile boolean dirty = true;
    }
}
