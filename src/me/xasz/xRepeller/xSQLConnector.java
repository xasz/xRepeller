package me.xasz.xRepeller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class xSQLConnector {

	private xRepeller plugin;

	private Connection con;

	private String server;
	private int port;
	private String database;
	private String user;
	private String password;

	public xSQLConnector(final xRepeller plugin, String server, int port,
			String database, String user, String password) {
		this.plugin = plugin;
		this.server = server;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
	}

	public boolean connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + server + ":"
					+ port + "/" + database, user, password);
			con.setAutoCommit(false);

			Statement stmt = this.con.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS repeller" + "("
					+ "id INTEGER auto_increment PRIMARY KEY,"
					+ "worlduid char(200)," + "X INTEGER, " + "Y INTEGER, "
					+ "Z INTEGER, " + "player char(30)" + ");");
			con.commit();
			stmt.close();

		} catch (SQLException e) {
			this.plugin
					.getLogger()
					.severe("Could not connect to the Database - Plugin will will shut down");
			if (con != null)
				try {
					con.close();
				} catch (SQLException e1) {
				}
		}
		try {
			if (con == null || con.isClosed())
				return false;
		} catch (SQLException e) {
		}
		return true;
	}

	public void disconnect() {
		try {
			con.commit();
		} catch (SQLException e) {
		}
		try {
			con.setAutoCommit(true);
		} catch (SQLException e) {
		}
		try {
			this.con.close();
		} catch (SQLException e) {
		}
	}

	public void removeRepellerFromDataBase(int id) {
		this.connect();
		Statement stmt = null;
		try {
			stmt = this.con.createStatement();
			stmt.executeUpdate("DELETE FROM repeller WHERE id = " + id + ";");
			stmt.close();
		} catch (SQLException e) {
  			this.plugin.getLogger().severe("Could not delete Repeller");
			e.printStackTrace();
		}
		this.disconnect();
	}
    public void saveRepeller(xRepellerConstruct repeller) {
        this.connect();
              Statement stmt = null;
                if(repeller.getDatabaseID() < 0){
                        try {
                          stmt = this.con.createStatement();
                          String query = "INSERT INTO repeller (worlduid, x, y, z, player) "+
                                          "VALUES ('"+
                                          repeller.getChestblock().getWorld().getUID()+"', "+
                                          repeller.getX()+", "+
                                          repeller.getY()+", "+
                                          repeller.getZ()+", "+
                                          "'" + repeller.getPlayer()+"'"+
                                          ")";
                          stmt.execute(query, Statement.RETURN_GENERATED_KEYS);
                          ResultSet autoKeys = stmt.getGeneratedKeys();
                          autoKeys.next();
                          int id = autoKeys.getInt(1);
                          repeller.setDatabaseID(id);
                          con.commit();
                          stmt.close();
                          
                  }catch(SQLException ex){
          			this.plugin.getLogger().severe("Could not save Repeller");
          			ex.printStackTrace();
                  }
                }
        this.disconnect();
      }
    public List<xRepellerConstruct> loadList(){
        this.connect();
              Statement stmt = null;
              List<xRepellerConstruct> list = new ArrayList<xRepellerConstruct>();
              
              try {
                      stmt = this.con.createStatement();
                      ResultSet result = stmt.executeQuery("SELECT * FROM repeller");
                      while(result.next())
                      { 
                        int repellerid = result.getInt("id");
                            try
                            {
                              xRepellerConstruct tmpRepllerConstruct = new xRepellerConstruct(this.plugin, result.getString("worlduid"),result.getInt("X"),result.getInt("Y"),result.getInt("Z"),result.getString("player"));
                              tmpRepllerConstruct.setDatabaseID(repellerid);
                              list.add(tmpRepllerConstruct);
                            }
                            catch (Exception e)
                            {
                            }
                      }
                      result.close();
                      stmt.close();
                      
              } catch (SQLException ex) {
            	  this.plugin.getLogger().severe(ex.getMessage());
                  this.disconnect();
                  try{
                	  list.remove(null);
                  }catch(NullPointerException ex1){}
                  return list;
              }
              this.disconnect();
              return list;
      }
}
