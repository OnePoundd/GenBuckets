package xyz.rinn.genbucket.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.rinn.genbucket.GenBucket;
import xyz.rinn.genbucket.utils.ChatUtils;

public class GenBucketCommand
  implements CommandExecutor
{
  ConfigurationSection messages;
  
  public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
  {
    this.messages = GenBucket.getInstance().getConfig().getConfigurationSection("messages");
    String noPermission = ChatUtils.formatMessage(this.messages.getString("no-permission-command"));
    if (args.length < 1)
    {
      if (sender.hasPermission("genbucket.command"))
      {
        sender.sendMessage(ChatUtils.formatMessage("&cMissing arguements! GenBucket commands are:\n&a- /genbucket reload"));
        return true;
      }
      sender.sendMessage(noPermission);
      return false;
    }
    String arg = args[0];
    if (!arg.equalsIgnoreCase("reload"))
    {
      if (sender.hasPermission("genbucket.command"))
      {
        sender.sendMessage(ChatUtils.formatMessage("&c'" + arg + "' is not a recognized command! GenBucket commands are: \n&a- /genbucket reload"));
        return true;
      }
      sender.sendMessage(noPermission);
      return false;
    }
    if (!sender.hasPermission("genbucket.commands.reload"))
    {
      sender.sendMessage(noPermission);
      return false;
    }
    GenBucket.getInstance().reloadConfig();
    sender.sendMessage(ChatUtils.formatMessage(this.messages.getString("configuration-reloaded")));
    return true;
  }
}
