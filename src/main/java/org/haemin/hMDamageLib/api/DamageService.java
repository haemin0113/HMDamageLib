package org.haemin.hMDamageLib.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageService {
    DamageResult compute(Player attacker, Entity victim, double baseDamage, EntityDamageEvent.DamageCause cause);
}
