package lbn.spread.sheet;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lbn.spread.sheet.command.TestSheetCommand;

public class SheetPlugin extends JavaPlugin {
  public static String dataFolder;

  @Override
  public void onEnable() {
    dataFolder = getDataFolder().getAbsolutePath();

    getCommand("sheet").setExecutor(new TestSheetCommand());

    String string = getConfig().getString("keyFileFullPath");
    SpreadSheet.jsonKeyUrl = string;

    Logger logger = Bukkit.getLogger();
    logger.info("Spread SheetのKeyとして次のファイルを読み込みました：" + SpreadSheet.jsonKeyUrl);

    getConfig().options().copyDefaults(true);
    saveDefaultConfig();
  }
}
