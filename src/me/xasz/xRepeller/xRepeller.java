package me.xasz.xRepeller;

// Our base class


import org.bukkit.plugin.java.JavaPlugin;

// some other bukkit stuff
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * 
 * @author xasz
 * xRepeller - build a repeller to prevent creatures from spawning in a custom range
 */
public class xRepeller extends JavaPlugin
{
  
  public static int ironRepellDistance = 1;
  public static int goldRepellDistance = 2;
  public static int diamondRepellDistance = 10;
  public static double ironMaxRepellDistance = 15.0f;
  public static double goldMaxRepellDistance = 30.0f;
  public static double diamondMaxRepellDistance = 450.0f;
  
  public String mysqlUser = "";
  public String mysqlPassword = "";
  public String mysqlServer = "";
  public int mysqlPort = 3604;
  public String mysqlDatabase = "";
  
  
  
  
  
  public PluginLogger logger = null;
  
  private PluginDescriptionFile pdf = null;
  private xRepellListener repellerListener = null;
  xSQLiteConnector sql = null;
  @Override
  public void onEnable()
  {
    ironRepellDistance = this.getConfig().getInt("ironRepellDistance");
    goldRepellDistance = this.getConfig().getInt("goldRepellDistance");
    diamondRepellDistance = this.getConfig().getInt("diamondRepellDistance");
    ironMaxRepellDistance = this.getConfig().getInt("ironMaxRepellDistance");
    goldMaxRepellDistance = this.getConfig().getInt("goldMaxRepellDistance");
    diamondMaxRepellDistance = this.getConfig().getInt("diamondMaxRepellDistance");
    mysqlDatabase = this.getConfig().getString("mysql.db");               
    mysqlUser = this.getConfig().getString("mysql.username");             
    mysqlServer = this.getConfig().getString("mysql.server");           
    mysqlPort = this.getConfig().getInt("mysql.port");            
    mysqlPassword = this.getConfig().getString("mysql.password");
    this.getConfig().options().copyDefaults(true);
    saveConfig();
    
    // Load the PluginDescriptionFile (plugin.yml)
    pdf = this.getDescription();
    
    // Get the instance of PluginLogger
    logger = (PluginLogger) this.getLogger();
    //Ok now we create some Listeners
    
    
    //load database
    sql = new xSQLiteConnector(this);
    repellerListener = new xRepellListener(this);
    repellerListener.setRepeller(sql.getRepellerListFromDB()); 

    logger.info("Your plugin has been enabled. (Version " + pdf.getVersion() + ")");
  }
 
  public xSQLiteConnector getConnector(){
    return sql;
  }
  @Override
  public void onDisable()
  {
    if(sql.checkConnection()){
      sql.saveRepeller(repellerListener.getRepeller());  
    }   
    //sql.disconnect();
    sql = null;
    repellerListener = null;
    logger.info("Your plugin has been disabled. (Version " + pdf.getVersion() + ")");
  }

  						
  
  public xRepellListener getRepellerListener()
  {
    return repellerListener;
  }

  public void setRepellerListener(xRepellListener repellerListener)
  {
    this.repellerListener = repellerListener;
  }
  public void saveRepeller()
  {
    sql.saveRepeller(repellerListener.getRepeller());    
  }
}
