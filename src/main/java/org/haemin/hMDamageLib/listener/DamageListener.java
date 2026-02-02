package org.haemin.hMDamageLib.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.haemin.hMDamageLib.api.DamageResult;
import org.haemin.hMDamageLib.api.DamageService;

public final class DamageListener implements Listener {

    private final DamageService damageService;

    public DamageListener(DamageService damageService) {
        this.damageService = damageService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player attacker = resolveAttacker(event.getDamager());
        if (attacker == null) return;

        Entity victim = event.getEntity();
        DamageResult result = damageService.compute(attacker, victim, event.getDamage(), event.getCause());
        event.setDamage(result.damage());
    }

    private Player resolveAttacker(Entity damager) {
        if (damager instanceof Player p) return p;
        if (damager instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        return null;
    }
}
