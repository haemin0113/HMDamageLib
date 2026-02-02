package org.haemin.hMDamageLib.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class StatRecalculateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private StatsSnapshot snapshot;

    public StatRecalculateEvent(Player player, StatsSnapshot snapshot) {
        this.player = player;
        this.snapshot = snapshot;
    }

    public Player getPlayer() {
        return player;
    }

    public StatsSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(StatsSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
