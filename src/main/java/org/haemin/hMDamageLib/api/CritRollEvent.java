package org.haemin.hMDamageLib.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CritRollEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final DamageContext context;
    private double chance;
    private Boolean forcedCritical;

    public CritRollEvent(DamageContext context, double chance) {
        this.context = context;
        this.chance = chance;
    }

    public DamageContext getContext() {
        return context;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public Boolean getForcedCritical() {
        return forcedCritical;
    }

    public void forceCritical(Boolean critical) {
        this.forcedCritical = critical;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
