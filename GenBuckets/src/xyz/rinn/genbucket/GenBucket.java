package xyz.rinn.genbucket;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.rinn.genbucket.commands.GenBucketCommand;
import xyz.rinn.genbucket.listeners.BlockBreakListener;
import xyz.rinn.genbucket.listeners.BucketEmptyListener;
import xyz.rinn.genbucket.utils.FileUtils;
import xyz.rinn.genbucket.utils.LogUtils;

public class GenBucket
  extends JavaPlugin
{
  private WorldGuardPlugin worldGuard;
  private Economy economy;
  private static GenBucket instance;
  private FileUtils fileUtils;
  private Set<Location> currentlyActive = new HashSet();
  private Map<Location, Set<Location>> currentlySponged = new HashMap();
  private String brackets = ChatColor.YELLOW + "========== ";
  private String smallBrackets = ChatColor.YELLOW + "==== ";
  private String error = ChatColor.RED + "error" + ChatColor.YELLOW + "!";
  
  public void onEnable()
  {
    LogUtils.log(this.brackets + ChatColor.GREEN + "Enabling GenBucket " + this.brackets);
    getLogger().setLevel(Level.OFF);
    instance = this;
    this.fileUtils = new FileUtils();
    
    String config = ChatColor.YELLOW + "Loading the configuration... ";
    try
    {
      saveDefaultConfig();
      reloadConfig();
      
      config = config + ChatColor.GREEN + "loaded" + ChatColor.YELLOW + "!";
    }
    catch (Exception e)
    {
      config = config + this.error;
    }
    LogUtils.log(config);
    String events = ChatColor.YELLOW + "Registering events... ";
    try
    {
      getServer().getPluginManager().registerEvents(new BucketEmptyListener(), this);
      getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
      
      events = events + ChatColor.GREEN + "registered" + ChatColor.YELLOW + "!";
    }
    catch (Exception e)
    {
      events = events + this.error;
    }
    LogUtils.log(events);
    String commands = ChatColor.YELLOW + "Registering commands... ";
    try
    {
      getCommand("genbucket").setExecutor(new GenBucketCommand());
      
      commands = commands + ChatColor.GREEN + "registered" + ChatColor.YELLOW + "!";
    }
    catch (Exception e)
    {
      commands = commands + this.error;
    }
    LogUtils.log(commands);
    
    LogUtils.log(ChatColor.YELLOW + "Setting up dependencies... ");
    setupDependencies();
    LogUtils.log(ChatColor.YELLOW + "Set up dependencies!");
    
    LogUtils.log(ChatColor.YELLOW + "Loading files... ");
    this.fileUtils.loadGenning();
    this.fileUtils.loadSponged();
    LogUtils.log(ChatColor.YELLOW + "Loaded files!");
    
    LogUtils.log(this.smallBrackets + ChatColor.GREEN + "Successfully enabled GenBucket " + this.smallBrackets);
  }
  
  public void onDisable()
  {
    LogUtils.log(this.brackets + ChatColor.GREEN + "Disabling GenBucket" + this.brackets);
    
    LogUtils.log(ChatColor.YELLOW + "Saving files...");
    this.fileUtils.saveGenning(this.currentlyActive);
    this.fileUtils.saveSponged(this.currentlySponged);
    LogUtils.log(ChatColor.YELLOW + "Saved files!");
    
    LogUtils.log(this.smallBrackets + ChatColor.GREEN + "Successfully disabled GenBucket" + this.smallBrackets);
  }
  
  private void setupDependencies()
  {
    String found = ChatColor.GREEN + "found" + ChatColor.YELLOW + "!";
    String notFound = ChatColor.RED + "not found" + ChatColor.YELLOW + "!";
    
    LogUtils.log(ChatColor.YELLOW + "Searching for WorldGuard... " + (setupWorldGuard() ? found : notFound));
    LogUtils.log(ChatColor.YELLOW + "Searching for Vault... " + (setupEconomy() ? found : notFound));
  }
  
  private boolean setupEconomy()
  {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    this.economy = ((Economy)rsp.getProvider());
    return this.economy != null;
  }
  
  private boolean setupWorldGuard()
  {
    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
    this.worldGuard = ((WorldGuardPlugin)plugin);
    
    return (this.worldGuard != null) && ((this.worldGuard instanceof WorldGuardPlugin));
  }
  
  public WorldGuardPlugin getWorldGuard()
  {
    return this.worldGuard;
  }
  
  public boolean isWorldGuardNull()
  {
    return (this.worldGuard == null) || (!(this.worldGuard instanceof WorldGuardPlugin));
  }
  
  public Economy getEconomy()
  {
    return this.economy;
  }
  
  public Set<Location> getActive()
  {
    return this.currentlyActive;
  }
  
  public Map<Location, Set<Location>> getSponged()
  {
    return this.currentlySponged;
  }
  
  public static GenBucket getInstance()
  {
    return instance;
  }
}