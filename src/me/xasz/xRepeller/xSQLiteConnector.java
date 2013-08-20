package me.xasz.xRepeller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;





/** 
 * functions for SQLite database
 * @author xasz
 */
public class xSQLiteConnector{
        Logger log = Logger.getLogger("Minecraft");
        Connection con = null;
        ResultSet rs = null;
        protected final xRepeller x;
        boolean status = false;
        /** 
         * create database
         * @param player
         * @param itemid
         */
        public xSQLiteConnector(final xRepeller instance){
                x = instance;
        }
        
        
        public boolean connect(){
                try {
                        con = DriverManager.getConnection("jdbc:mysql://"+x.mysqlServer+":"+x.mysqlPort+"/"+x.mysqlDatabase,x.mysqlUser,x.mysqlPassword);
                        con.setAutoCommit(false);
                        status = true;
                        Statement stmt = this.con.createStatement();
                        stmt.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS repeller"+
                                        "(" +
                                        "id INTEGER auto_increment PRIMARY KEY," +
                                        "worlduid char(100),"+
                                        "X INTEGER, "+
                                        "Y INTEGER, "+
                                        "Z INTEGER "+
                                        ");"
                                );
                        con.commit();
                        stmt.close();
                       
                        
                        
                } catch (SQLException e) {
                  status = false;
                        System.out.println("[xRepeller] Could not connect to the Database");
                        //writeError(e.getMessage(), true);
                }
                return status;
        }
        
        
        public void disconnect(){
                try {
                        con.commit();
                        con.setAutoCommit(true);
                        this.con.close();
                        status = false;
                } catch (SQLException e) {                      
          //              writeError(e.getMessage(), true);
                }
        }
        /** 
         * error output
         * @param toWrite
         * @param severe
         */
        public void writeError(String toWrite, boolean severe) {
                if (toWrite != null) {
                        if (severe) {
                                this.log.severe("[xRepeller]" + "[SQLite] " + toWrite);
                        } else {
                                this.log.warning("[xRepeller]" + "[SQLite] " + toWrite);
                        }
                }
        }
        /**
         * 
         * @return true = connected || false = not connected
         */
        public boolean checkConnection(){
                return status;
        }
        
        public void deleteRepeller(xRepellConstruct repeller)
        {
          this.connect();
          Statement stmt = null;
          try
          {
            System.out.println("DELETE FROM repeller WHERE x = "+repeller.getChestblock().getX()+", y = "+repeller.getChestblock().getY()+", z = "+repeller.getChestblock().getZ()+", worlduid = '"+repeller.getChestblock().getWorld().getUID()+"');");
             stmt = this.con.createStatement();
             stmt.executeUpdate("DELETE FROM repeller WHERE x = "+repeller.getChestblock().getX()+", y = "+repeller.getChestblock().getY()+", z = "+repeller.getChestblock().getZ()+", worlduid = '"+repeller.getChestblock().getWorld().getUID()+"');");  
             stmt.close();   
           }
           catch (SQLException e)
           {
             // TODO Auto-generated catch block
             e.printStackTrace();     
           }
          this.disconnect();
        }
        public void deleteRepeller(String world, int x, int y, int z)
        {
          this.connect();
          Statement stmt = null;
          try
          {
             stmt = this.con.createStatement();
              stmt.executeUpdate("DELETE FROM repeller WHERE x = "+x+" and y = "+y+" and z = "+z+" and worlduid = '"+world+"';");  
             stmt.close();   
           }
           catch (SQLException e)
           {
             // TODO Auto-generated catch block
             e.printStackTrace();     
           }
          this.disconnect();
        }
        public boolean saveRepeller(List<xRepellConstruct> repeller) {
          this.connect();
                boolean success = false;
                Statement stmt = null;
                for(xRepellConstruct currenRepeller : repeller){
                  if(!currenRepeller.isInDatabase()){
                          try {
                            stmt = this.con.createStatement();
                            String query = "INSERT INTO repeller (worlduid, x, y, z) "+
                                            "VALUES ('"+
                                            currenRepeller.getChestblock().getWorld().getUID()+"', "+
                                            currenRepeller.getChestblock().getX()+", "+
                                            currenRepeller.getChestblock().getY()+", "+
                                            currenRepeller.getChestblock().getZ()+
                                            ")";
                            stmt.executeUpdate(query);
                            currenRepeller.setInDatabase(true);
                            con.commit();
                            success = true;
                            stmt.close();
                            
                    }catch(SQLException ex){
                            writeError(ex.getMessage(), true); 
                    }
                  }
                }
                this.disconnect();
                return success;
        }

        
        public List<xRepellConstruct> getRepellerListFromDB(){
          this.connect();
                Statement stmt = null;
                List<xRepellConstruct> list = null;
                List<Integer> idToRemove = new ArrayList<Integer>();
                try {
                        list = new ArrayList<xRepellConstruct>();
                        stmt = this.con.createStatement();
                        ResultSet result = stmt.executeQuery("SELECT * FROM repeller");
                        while(result.next())
                        { 
                          int repellerid = result.getInt("id");
                              try
                              {
                                xRepellConstruct tempReller = new xRepellConstruct(x,result.getString("worlduid"),result.getInt("X"),result.getInt("Y"),result.getInt("Z"));
                                tempReller.setInDatabase(true);
                                list.add(tempReller);
                              }
                              catch (Exception e)
                              {
                                idToRemove.add(repellerid);
                              }
                        }
                        con.commit();
                        result.close();
                        stmt.close();
                        
                } catch (SQLException ex) {

                        writeError(ex.getMessage(), true);
                        this.disconnect();
                        return list;
                }
                
                
                //removeing bad ids 
                 for(Integer toRemove : idToRemove){                 
                   try
                   {
                      stmt = this.con.createStatement();
                      stmt.executeUpdate("DELETE FROM repeller WHERE id = " + toRemove);  
                      stmt.close();   
                    }
                    catch (SQLException e)
                    {
                      // TODO Auto-generated catch block
                      e.printStackTrace();     
                    }
                 }

                this.disconnect();
                return list;
        }
        /*
        public List<xRepellConstruct> getRepellerListFromDB(){
          this.connect();
          List<xRepellConstruct> rList = new ArrayList<xRepellConstruct>();
          Statement stmt = null;
          try
          {
             stmt = this.con.createStatement();
             ResultSet result = stmt.executeQuery("Select * from repeller");  
             while(result.next()){
               System.out.println("result");
             }
             stmt.close();   
           }
           catch (SQLException e)
           {
             System.out.println("resultfail");
             // TODO Auto-generated catch block
             e.printStackTrace();     
           }
          
          
          this.disconnect();
          return rList;
        }*/
        
}
