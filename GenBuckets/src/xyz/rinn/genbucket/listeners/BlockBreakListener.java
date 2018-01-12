package xyz.rinn.genbucket.listeners;

import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.rinn.genbucket.GenBucket;
import xyz.rinn.genbucket.utils.GenUtils;

public class BlockBreakListener
  implements Listener
{
  private Configuration configuration;
  
  @EventHandler
  public void onBreak(BlockBreakEvent event)
  {
    this.configuration = GenBucket.getInstance().getConfig();
    if (!this.configuration.getBoolean("continue-after-sponge-broken")) {
      return;
    }
    Block broken = event.getBlock();
    if (broken.getType() != Material.SPONGE) {
      return;
    }
    Location sponge = broken.getLocation();
    if (GenBucket.getInstance().getSponged().get(sponge) == null) {
      return;
    }
    for (Location block : GenBucket.getInstance().getSponged().get(sponge)) {
      new GenUtils().startGenning(new Location(block.getWorld(), block.getX(), block.getY() + 1.0D, block.getZ()), this.configuration.getBoolean("allow-sponging"), this.configuration.getLong("genning-speed"), this.configuration.getInt("sponge-radius"));
    }
    GenBucket.getInstance().getSponged().remove(sponge);
  }
}