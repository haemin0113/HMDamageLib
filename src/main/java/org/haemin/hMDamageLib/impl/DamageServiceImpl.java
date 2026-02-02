package org.haemin.hMDamageLib.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.haemin.hMDamageLib.api.CritRollEvent;
import org.haemin.hMDamageLib.api.DamageComputeEvent;
import org.haemin.hMDamageLib.api.DamageContext;
import org.haemin.hMDamageLib.api.DamageResult;
import org.haemin.hMDamageLib.api.DamageService;
import org.haemin.hMDamageLib.api.StatService;
import org.haemin.hMDamageLib.api.StatType;
import org.haemin.hMDamageLib.api.StatsSnapshot;

import java.util.concurrent.ThreadLocalRandom;

public final class DamageServiceImpl implements DamageService {

    private final Plugin plugin;
    private final StatService statService;

    public DamageServiceImpl(Plugin plugin, StatService statService) {
        this.plugin = plugin;
        this.statService = statService;
    }

    @Override
    public DamageResult compute(Player attacker, Entity victim, double baseDamage, EntityDamageEvent.DamageCause cause) {
        if (attacker == null || victim == null) {
            return new DamageResult(baseDamage, false);
        }

        StatsSnapshot atk = statService.getSnapshot(attacker);
        StatsSnapshot def = victim instanceof Player p ? statService.getSnapshot(p) : null;

        double damage = baseDamage;
        damage += atk.get(StatType.ATTACK_DAMAGE);
        damage *= (1.0 + atk.get(StatType.DAMAGE_MULTIPLIER));

        DamageContext ctx = new DamageContext(attacker, victim, cause, baseDamage);

        double chance = clamp(atk.get(StatType.CRIT_CHANCE), 0.0, 1.0);
        boolean critical;

        if (Bukkit.isPrimaryThread()) {
            CritRollEvent critEvent = new CritRollEvent(ctx, chance);
            Bukkit.getPluginManager().callEvent(critEvent);
            chance = clamp(critEvent.getChance(), 0.0, 1.0);
            Boolean forced = critEvent.getForcedCritical();
            if (forced != null) {
                critical = forced;
            } else {
                critical = ThreadLocalRandom.current().nextDouble() < chance;
            }
        } else {
            critical = ThreadLocalRandom.current().nextDouble() < chance;
        }

        if (critical) {
            double critBonus = clamp(atk.get(StatType.CRIT_DAMAGE), 0.0, 10.0);
            damage *= (1.0 + critBonus);
        }

        if (def != null) {
            double defense = Math.max(0.0, def.get(StatType.DEFENSE));
            if (defense > 0.0) {
                damage *= 100.0 / (100.0 + defense);
            }
            double reduction = clamp(def.get(StatType.DAMAGE_REDUCTION), 0.0, 0.95);
            damage *= (1.0 - reduction);
        }

        if (Bukkit.isPrimaryThread()) {
            DamageComputeEvent computeEvent = new DamageComputeEvent(ctx, atk, def, damage, critical);
            Bukkit.getPluginManager().callEvent(computeEvent);
            damage = computeEvent.getDamage();
            critical = computeEvent.isCritical();
        }

        if (damage < 0.0) {
            damage = 0.0;
        }

        return new DamageResult(damage, critical);
    }

    private static double clamp(double v, double min, double max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
