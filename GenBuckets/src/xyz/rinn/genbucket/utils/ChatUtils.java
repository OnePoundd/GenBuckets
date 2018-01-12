package xyz.rinn.genbucket.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.rinn.genbucket.GenBucket;

public class ChatUtils
{
  public static String formatMessage(String message)
  {
    return ChatColor.translateAlternateColorCodes('&', GenBucket.getInstance().getConfig().getString("message-prefix") + message);
  }
}
