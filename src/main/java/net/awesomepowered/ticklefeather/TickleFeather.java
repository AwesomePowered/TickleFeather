package net.awesomepowered.ticklefeather;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TickleFeather extends JavaPlugin implements Listener {

    String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "TickleFeather" + ChatColor.GOLD + "] ";

    boolean clearArmor;
    boolean gameEnabled;
    String theTickler;
    int minPlayers;

    ItemStack theFeather = new ItemStack(Material.FEATHER, 1);

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        gameEnabled = getConfig().getBoolean("game.EnableOnStart");
        minPlayers = getConfig().getInt("game.MinimumPlayers");
        clearArmor = getConfig().getBoolean("game.ClearTicklerArmor");
    }

    public void onDisable() {
        gameEnabled = false;
        theTickler = null;
    }

    public void setTickler(String name, int ver, String oldTickler) {
        if (playersActive() && gameEnabled) {
            Player tickler = Bukkit.getPlayer(name);
            theFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 16);
            theTickler = name;
            tickler.getInventory().setItemInHand(theFeather);
            tickler.updateInventory();
            if (clearArmor) {
                tickler.getInventory().setArmorContents(new ItemStack[4]);
                tickler.updateInventory();
            }
            if (ver == 1) {
                 bmsg(prefix + ChatColor.WHITE + name + ChatColor.GREEN + " got the tickle feather from " + ChatColor.WHITE + oldTickler);
            }
            if (ver == 2) {
                bmsg(prefix + ChatColor.WHITE + name + ChatColor.GREEN + " is now the tickler!");
            }
        }
    }

    public boolean playersActive() {
        if (minPlayers < Bukkit.getOnlinePlayers().length) {
            return true;
        }
        return false;
    }

    public void bmsg(String msg) {
        Bukkit.broadcastMessage(prefix + msg);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent ev) {
        if (theTickler == null) {
            setTickler(ev.getPlayer().getName(), 2, null);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent ev) {
        String deadPlayer = ev.getEntity().getName();
        if (deadPlayer.equalsIgnoreCase(theTickler)) {
             setTickler(ev.getEntity().getKiller().getName(), 1, deadPlayer);
        }
    }


    //YOU CAN'T CHANGE ME!
    //DEAL WITH IT
    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        String m = ev.getMessage();
        Player p = ev.getPlayer();
        if (m.equalsIgnoreCase("@tickler true") && p.hasPermission("ticklefeather.admin")) {
             if (gameEnabled) {
                 p.sendMessage(prefix + "Game already enabled!");
             } else {
                 gameEnabled = true;
             }
            ev.setCancelled(true);
        }
        if (m.equalsIgnoreCase("@tickler false") && p.hasPermission("ticklefeather.admin")) {
             if (!gameEnabled) {
                 p.sendMessage(prefix + "Game already disabled!");
             } else {
                 gameEnabled = false;
                 theTickler = null;
             }
            ev.setCancelled(true);
        }
        if (m.equalsIgnoreCase("@tickler override") && p.hasPermission("ticklefeather.admin")) {
            if (gameEnabled) {
                setTickler(p.getName(), 2, null);
            } else {
                p.sendMessage(prefix + "The game is disabled.");
            }
            ev.setCancelled(true);
        }
        if (m.equalsIgnoreCase("?tickler")) {
              if (gameEnabled) {
                 p.sendMessage(prefix + "The tickler is " + theTickler);
                  if (theTickler != null) {
                      Player tickler = Bukkit.getPlayer(theTickler);
                      Location tl = tickler.getLocation();
                      p.sendMessage(prefix + "The tickler is at " + "x: " + tl.getBlockX() + " y: " + tl.getBlockY() + " z: " + tl.getBlockZ());
                  }
              } else {
                  p.sendMessage(prefix + "Game is not enabled!");
              }
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        if (ev.getPlayer().getName().equalsIgnoreCase(theTickler)) {
            theTickler = null;
        }
    }
}