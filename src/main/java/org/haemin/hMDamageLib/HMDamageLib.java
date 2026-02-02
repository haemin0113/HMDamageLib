package org.haemin.hMDamageLib;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.haemin.hMDamageLib.api.DamageService;
import org.haemin.hMDamageLib.api.StatService;
import org.haemin.hMDamageLib.impl.DamageServiceImpl;
import org.haemin.hMDamageLib.impl.StatServiceImpl;
import org.haemin.hMDamageLib.listener.DamageListener;

public final class HMDamageLib extends JavaPlugin {

    private static HMDamageLib instance;

    private StatService statService;
    private DamageService damageService;

    public static HMDamageLib getInstance() {
        return instance;
    }

    public StatService getStatService() {
        return statService;
    }

    public DamageService getDamageService() {
        return damageService;
    }

    @Override
    public void onEnable() {
        instance = this;

        statService = new StatServiceImpl(this);
        damageService = new DamageServiceImpl(this, statService);

        Bukkit.getServicesManager().register(StatService.class, statService, this, ServicePriority.Normal);
        Bukkit.getServicesManager().register(DamageService.class, damageService, this, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(new DamageListener(damageService), this);

        PluginCommand cmd = getCommand("hmdamagelib");
        if (cmd != null) {
            HMDamageLibCommand executor = new HMDamageLibCommand(statService);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
        instance = null;
    }
}
