package org.haemin.hMDamageLib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.haemin.hMDamageLib.api.AppliedModifier;
import org.haemin.hMDamageLib.api.StatService;
import org.haemin.hMDamageLib.api.StatType;
import org.haemin.hMDamageLib.api.StatsSnapshot;

import java.util.ArrayList;
import java.util.List;

public final class HMDamageLibCommand implements CommandExecutor, TabCompleter {

    private final StatService statService;

    public HMDamageLibCommand(StatService statService) {
        this.statService = statService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("stats")) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " stats [player]");
            return true;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
        } else {
            if (sender instanceof Player p) {
                target = p;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " stats <player>");
                return true;
            }
        }

        StatsSnapshot snap = statService.getSnapshot(target);
        sender.sendMessage(ChatColor.AQUA + "[HMDamageLib] Stats of " + target.getName());
        for (StatType t : StatType.values()) {
            sender.sendMessage(ChatColor.GRAY + "- " + t.name() + ": " + ChatColor.WHITE + snap.get(t));
        }

        if (sender.hasPermission("hmdamagelib.admin")) {
            sender.sendMessage(ChatColor.DARK_AQUA + "[HMDamageLib] Breakdown");
            for (StatType t : StatType.values()) {
                List<AppliedModifier> list = snap.breakdown(t);
                if (list.isEmpty()) continue;
                sender.sendMessage(ChatColor.GRAY + "* " + t.name());
                for (AppliedModifier m : list) {
                    sender.sendMessage(ChatColor.DARK_GRAY + "  - " + m.sourceId() + " " + m.operation() + " " + m.value() + " (" + m.priority() + ")");
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("stats");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
            String prefix = args[1].toLowerCase();
            List<String> out = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    out.add(p.getName());
                }
            }
            return out;
        }
        return List.of();
    }
}
