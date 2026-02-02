package org.haemin.hMDamageLib.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamageContext {
    private final Player attacker;
    private final Entity victim;
    private final EntityDamageEvent.DamageCause cause;
    private final double originalDamage;

    public DamageContext(Player attacker, Entity victim, EntityDamageEvent.DamageCause cause, double originalDamage) {
        this.attacker = attacker;
        this.victim = victim;
        this.cause = cause;
        this.originalDamage = originalDamage;
    }

    public Player attacker() {
        return attacker;
    }

    public Entity victim() {
        return victim;
    }

    public EntityDamageEvent.DamageCause cause() {
        return cause;
    }

    public double originalDamage() {
        return originalDamage;
    }
}
