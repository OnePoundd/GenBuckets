package xyz.rinn.genbucket.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.rinn.genbucket.GenBucket;

public class GenUtils
{
  public boolean createBlock(Player player, Block block)
  {
    if ((!GenBucket.getInstance().isWorldGuardNull()) && 
      (!GenBucket.getInstance().getWorldGuard().canBuild(player, block))) {
      return false;
    }
    World world = block.getWorld();
    
    int x = block.getX();
    int y = block.getY() - 1;
    int z = block.getZ();
    if (GenBucket.getInstance().getConfig().getBoolean("check-for-border"))
    {
      double size = world.getWorldBorder().getSize() / 2.0D;
      if (x > 0)
      {
        if (x >= size) {
          return false;
        }
        if (x <= -size) {
          return false;
        }
      }
      if (z > 0)
      {
        if (z >= size) {
          return false;
        }
        if (z <= -size) {
          return false;
        }
      }
    }
    Block blockUnder = new Location(world, x, y, z).getBlock();
    if ((block.getType().isSolid()) || (block.isLiquid()) || (blockUnder.getType().isSolid()) || (blockUnder.isLiquid())) {
      return false;
    }
    return true;
  }
  
  public boolean shouldGenerate(Block newBlock, boolean sponge, int radius)
  {
    Location above = new Location(newBlock.getWorld(), newBlock.getX(), newBlock.getY() + 1, newBlock.getZ());
    if (above.getBlock().getType() != Material.COBBLESTONE) {
      return false;
    }
    if ((newBlock.isLiquid()) || (newBlock.getType().isSolid())) {
      return false;
    }
    if (!sponge) {
      return true;
    }
    for (int x = -radius; x <= radius; x++) {
      for (int y = -radius; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++)
        {
          Block block = newBlock.getRelative(x, y, z);
          if (block.getType() == Material.SPONGE)
          {
            Location location = block.getLocation();
            Set<Location> blocks = new HashSet();
            if (GenBucket.getInstance().getSponged().get(location) != null) {
              blocks.addAll((Collection)GenBucket.getInstance().getSponged().get(location));
            }
            blocks.add(newBlock.getLocation());
            
            GenBucket.getInstance().getSponged().put(location, blocks);
            return false;
          }
        }
      }
    }
    return true;
  }
  
  public void startGenning(final Location location, final boolean sponge, final long speed, final int radius)
  {
    new BukkitRunnable()
    {
      public void run()
      {
        Location newLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        Block currentBlock = newLocation.getBlock();
        if (GenUtils.this.shouldGenerate(currentBlock, sponge, radius))
        {
          currentBlock.setType(Material.COBBLESTONE);
          GenBucket.getInstance().getActive().remove(location);
          GenUtils.this.startGenning(newLocation, sponge, speed, radius);
          GenBucket.getInstance().getActive().add(newLocation);
          return;
        }
        GenBucket.getInstance().getActive().remove(location);
        cancel();
      }
    }.runTaskTimer(GenBucket.getInstance(), speed, speed);
  }
}