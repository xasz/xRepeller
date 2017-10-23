package me.xasz.xRepeller;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class xRepeller extends JavaPlugin{
	
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
	  
	  public static boolean databaseEnabled = true;  

	  private PluginDescriptionFile pdf = null;
	  public PluginLogger logger = null;
	  
	  private xSQLConnector connector;
	  private xRepellerContainer repellerContainer;
	  private xRepellListener listener;
	  
	  
	  public xRepellListener getListener() {
		  return listener;
	  }
	  
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
	    databaseEnabled = this.getConfig().getBoolean("databaseEnabled");
	    this.getConfig().options().copyDefaults(true);
	    saveConfig();
	    pdf = this.getDescription();
	    logger = (PluginLogger) this.getLogger();
	    
	    if(databaseEnabled){
	    	this.connector = new xSQLConnector(this,mysqlServer,mysqlPort,mysqlDatabase,mysqlUser,mysqlPassword);
	    	if(this.connector.connect()){
	    		this.connector.disconnect();
	    		this.repellerContainer = new xRepellerContainer(this.connector);
	    	}else{
	    		logger.severe("Plugin is started in database mode but could not connect. Repeller will not be saved or loaded from database");
	    		this.repellerContainer = new xRepellerContainer();
	    	}
	    }else{
	    	this.repellerContainer = new xRepellerContainer();
	    }
	    
	    this.listener = new xRepellListener(this,this.repellerContainer);
	    
	    logger.info("Started and loaded xRepeller (Version " + pdf.getVersion() + ")");
	  }
	  @Override
	  public void onDisable()
	  {
		logger.info("Disabled xRepeller (Version " + pdf.getVersion() + ")");  
	  }
}
