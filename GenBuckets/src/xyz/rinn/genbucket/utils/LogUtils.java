package xyz.rinn.genbucket.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class LogUtils
{
  public static void log(String message)
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + "Gen" + ChatColor.RED + "Bucket" + ChatColor.GREEN + "] " + message);
  }
}
