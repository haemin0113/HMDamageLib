package org.haemin.hMDamageLib.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface StatProvider {
    String id();
    Collection<StatModifier> provide(Player player);
}
