package org.haemin.hMDamageLib.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class DamageComputeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DamageContext context;
    private final StatsSnapshot attackerStats;
    private final StatsSnapshot victimStats;
    private double damage;
    private boolean critical;

    public DamageComputeEvent(DamageContext context, StatsSnapshot attackerStats, StatsSnapshot victimStats, double damage, boolean critical) {
        this.context = context;
        this.attackerStats = attackerStats;
        this.victimStats = victimStats;
        this.damage = damage;
        this.critical = critical;
    }

    public DamageContext getContext() {
        return context;
    }

    public StatsSnapshot getAttackerStats() {
        return attackerStats;
    }

    public StatsSnapshot getVictimStats() {
        return victimStats;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
