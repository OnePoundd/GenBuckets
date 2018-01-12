package xyz.rinn.genbucket.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import xyz.rinn.genbucket.GenBucket;

public class FileUtils
{
  private GenUtils genUtils = new GenUtils();
  private Configuration configuration;
  private String path = GenBucket.getInstance().getDataFolder().getAbsolutePath();
  private String genning = this.path + File.separator + "genning.gen";
  private String sponged = this.path + File.separator + "sponged.gen";
  
  public void saveGenning(Set<Location> list)
  {
    try
    {
      File file = new File(this.genning);
      if (file.exists()) {
        file.delete();
      }
      FileWriter fstream = new FileWriter(file, true);
      BufferedWriter out = new BufferedWriter(fstream);
      for (Location location : list) {
        out.write(location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + "\n");
      }
      out.flush();
      out.close();
    }
    catch (IOException e)
    {
      if (!(e instanceof FileNotFoundException)) {
        e.printStackTrace();
      }
    }
  }
  
  public void loadGenning()
  {
    try
    {
      File file = new File(this.genning);
      if (!file.exists()) {
        file.mkdirs();
      }
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuilder input = new StringBuilder();
      
      char[] buffer = new char['?'];
      for (int c = reader.read(buffer); c > 0; c = reader.read(buffer)) {
        for (int i = 0; i < c; i++) {
          input.append(buffer[i]);
        }
      }
      String[] lines = input.toString().replace("\r", "").split("\n");
      this.configuration = GenBucket.getInstance().getConfig();
      String[] arrayOfString1;
      int j = (arrayOfString1 = lines).length;
      for (int i = 0; i < j; i++)
      {
        String line = arrayOfString1[i];
        String[] information = line.split(" ");
        if (information.length == 4)
        {
          Location location = new Location(Bukkit.getWorld(information[0]), Double.valueOf(information[1]).doubleValue(), Double.valueOf(information[2]).doubleValue(), Double.valueOf(information[3]).doubleValue());
          this.genUtils.startGenning(location, this.configuration.getBoolean("allow-sponging"), this.configuration.getLong("genning-speed"), this.configuration.getInt("sponge-radius"));
        }
      }
    }
    catch (IOException e)
    {
      if (!(e instanceof FileNotFoundException)) {
        e.printStackTrace();
      }
    }
  }
  
  public void saveSponged(Map<Location, Set<Location>> map)
  {
    try
    {
      File file = new File(this.sponged);
      if (file.exists()) {
        file.delete();
      }
      FileWriter fstream = new FileWriter(file, true);
      BufferedWriter out = new BufferedWriter(fstream);
      
      Iterator<Map.Entry<Location, Set<Location>>> it = GenBucket.getInstance().getSponged().entrySet().iterator();
      Iterator localIterator;
      for (; it.hasNext(); localIterator.hasNext())
      {
        Map.Entry<Location, Set<Location>> sponged = (Map.Entry)it.next();
        
        localIterator = ((Set)sponged.getValue()).iterator(); continue;
      }
      out.flush();
      out.close();
    }
    catch (IOException e)
    {
      if (!(e instanceof FileNotFoundException)) {
        e.printStackTrace();
      }
    }
  }
  
  public void loadSponged()
  {
    try
    {
      File file = new File(this.sponged);
      if (!file.exists()) {
        file.mkdirs();
      }
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuilder input = new StringBuilder();
      
      char[] buffer = new char['?'];
      for (int c = reader.read(buffer); c > 0; c = reader.read(buffer)) {
        for (int i = 0; i < c; i++) {
          input.append(buffer[i]);
        }
      }
      String[] lines = input.toString().replace("\r", "").split("\n");
      this.configuration = GenBucket.getInstance().getConfig();
      String[] arrayOfString1;
      int j = (arrayOfString1 = lines).length;
      for (int i = 0; i < j; i++)
      {
        String line = arrayOfString1[i];
        String[] information = line.split(" ");
        if (information.length == 4)
        {
          Location location = new Location(Bukkit.getWorld(information[0]), Double.valueOf(information[1]).doubleValue(), Double.valueOf(information[2]).doubleValue() + 1.0D, Double.valueOf(information[3]).doubleValue());
          this.genUtils.startGenning(location, this.configuration.getBoolean("allow-sponging"), this.configuration.getLong("genning-speed"), this.configuration.getInt("sponge-radius"));
        }
      }
    }
    catch (IOException e)
    {
      if (!(e instanceof FileNotFoundException)) {
        e.printStackTrace();
      }
    }
  }
}