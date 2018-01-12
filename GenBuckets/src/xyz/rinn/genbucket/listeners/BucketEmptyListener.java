package xyz.rinn.genbucket.listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.rinn.genbucket.GenBucket;
import xyz.rinn.genbucket.utils.ChatUtils;
import xyz.rinn.genbucket.utils.GenUtils;

public class BucketEmptyListener
  implements Listener
{
  private GenUtils genUtils = new GenUtils();
  Configuration configuration;
  ConfigurationSection messages;
  
  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event)
  {
		if(BoardColl.get().getFactionAt(PS.valueOf(event.getBlockClicked())).getName().equals("WarZone")){
			event.setCancelled(true);
			event.getPlayer().updateInventory();
		}else if(!event.getPlayer().isSneaking() && MPlayer.get(event.getPlayer()).getSkillGenBuckets()){
		    this.configuration = GenBucket.getInstance().getConfig();
		    String bucketType = this.configuration.getString("bucket-type");
		    this.messages = this.configuration.getConfigurationSection("messages");
		    
		    Material type = null;
		    if (bucketType.equalsIgnoreCase("lava")) {
		      type = Material.LAVA_BUCKET;
		    } else if (bucketType.equalsIgnoreCase("water")) {
		      type = Material.WATER_BUCKET;
		    } else {
		      Bukkit.getLogger().log(Level.SEVERE, "[GenBucket] '" + bucketType + "' is an invalid bucket type!");
		    }
		    Player player = event.getPlayer();
		    Material bucketInHand = event.getBucket();
		    if (bucketInHand != type) {
		      return;
		    }
		    String generate = this.configuration.getString("generate");
		    if (generate.equalsIgnoreCase("shifting"))
		    {
		      if (player.isSneaking()) {}
		    }
		    else if (generate.equalsIgnoreCase("not shifting"))
		    {
		      if (!player.isSneaking()) {}
		    }
		    else if (!generate.equalsIgnoreCase("always")) {
		      return;
		    }
		    event.setCancelled(true);
		    if (!player.hasPermission("genbucket.use"))
		    {
		      player.sendMessage(ChatUtils.formatMessage(this.messages.getString("no-permission-place")));
		      return;
		    }
		    if (event.getBlockFace() != BlockFace.UP)
		    {
		      player.sendMessage(ChatUtils.formatMessage(this.messages.getString("invalid-placing")));
		      return;
		    }
		    WorldGuardPlugin worldGuard = GenBucket.getInstance().getWorldGuard();
		    if ((worldGuard != null) && ((worldGuard instanceof WorldGuardPlugin)) && (!worldGuard.canBuild(player, event.getBlockClicked()))) {
		      return;
		    }
		    World world = player.getWorld();
		    if (player.getGameMode() == GameMode.SURVIVAL)
		    {
		      if (this.configuration.getBoolean("Charge Players"))
		      {
		        int amount = this.configuration.getInt("Charge Amount");
		        Economy economy = GenBucket.getInstance().getEconomy();
		        boolean enough = economy.getBalance(player) - amount >= 0.0D;
		        
		        player.sendMessage(ChatUtils.formatMessage(enough ? this.messages.getString("money-taken") : this.messages.getString("not-enough-money")));
		        if (!enough) {
		          return;
		        }
		        economy.withdrawPlayer(player, amount);
		      }
		      ItemStack hand = player.getItemInHand();
		      int amount = hand.getAmount();
		      Material bucketMaterial = Material.BUCKET;
		      if (amount == 1)
		      {
		        hand.setType(bucketMaterial);
		      }
		      else if (amount > 1)
		      {
		        Inventory inventory = player.getInventory();
		        boolean set = false;
		        ItemStack[] arrayOfItemStack;
		        int j = (arrayOfItemStack = inventory.getContents()).length;
		        for (int i = 0; i < j; i++)
		        {
		          ItemStack item = arrayOfItemStack[i];
		          if (item != null) {
		            if (item.getType() == bucketMaterial)
		            {
		              int bucketAmount = item.getAmount();
		              if (bucketAmount < 16)
		              {
		                item.setAmount(bucketAmount + 1);
		                hand.setAmount(amount - 1);
		                set = true;
		                break;
		              }
		            }
		          }
		        }
		        if (!set)
		        {
		          ItemStack bucket = new ItemStack(bucketMaterial);
		          int empty = inventory.firstEmpty();
		          if (empty == -1) {
		            world.dropItem(player.getLocation(), bucket);
		          } else {
		            inventory.setItem(empty, bucket);
		          }
		          hand.setAmount(amount - 1);
		        }
		      }
		    }
		    Location location = event.getBlockClicked().getLocation();
		    int x = location.getBlockX();
		    int y = location.getBlockY() + 1;
		    int z = location.getBlockZ();
		    
		    boolean sponge = this.configuration.getBoolean("allow-sponging");
		    long speed = this.configuration.getLong("genning-speed");
		    int radius = this.configuration.getInt("sponge-radius");
		    for (int i = -1; i <= 1; i += 2)
		    {
		      Location first = new Location(world, x + i, y, z);
		      Location second = new Location(world, x, y, z + i);
		      
		      Block firstBlock = first.getBlock();
		      Block secondBlock = second.getBlock();
		      if (this.genUtils.createBlock(player, firstBlock))
		      {
		        firstBlock.setType(Material.COBBLESTONE);
		        this.genUtils.startGenning(first, sponge, speed, radius);
		        GenBucket.getInstance().getActive().add(first);
		      }
		      if (this.genUtils.createBlock(player, secondBlock))
		      {
		        secondBlock.setType(Material.COBBLESTONE);
		        this.genUtils.startGenning(second, sponge, speed, radius);
		        GenBucket.getInstance().getActive().add(second);
		      }
		    }
		    Block currentBlock = new Location(world, x, y, z).getBlock();
		    if (!this.configuration.getBoolean("keep-liquid")) {
		      return;
		    }
		    if (bucketInHand == Material.LAVA_BUCKET)
		    {
		      currentBlock.setType(Material.STATIONARY_LAVA);
		      return;
		    }
		    currentBlock.setType(Material.STATIONARY_WATER);
		}
  }
}