package me.xasz.xRepeller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class xRepellerContainer {
	
	private xSQLConnector connector;
	private Collection<xRepellerConstruct> repellers;
	private xRepellerConstruct[] threadSafeArray;
	
	public xRepellerContainer(){
		this.connector = null;
		this.repellers = new ArrayList<xRepellerConstruct>();
		this.threadSafeArray = this.repellers.toArray(new xRepellerConstruct[this.repellers.size()]);
	}
	
	public xRepellerContainer(xSQLConnector connector){
		this.connector = connector;
		this.repellers = this.connector.loadList();
		
		List<xRepellerConstruct> invalds = new ArrayList<xRepellerConstruct>();
		for(xRepellerConstruct repeller: this.repellers){
			if(!repeller.init()){
				invalds.add(repeller);
			}
		}
		//remove invalids
		for(xRepellerConstruct repeller: invalds){
			this.repellers.remove(repeller);
			if(connector != null)
				this.connector.removeRepellerFromDataBase(repeller.getDatabaseID());
		}
		this.threadSafeArray = this.repellers.toArray(new xRepellerConstruct[this.repellers.size()]);
	}
	public boolean add(xRepellerConstruct repeller){
		if(repeller.init()){
			this.repellers.add(repeller);
			if(connector != null)
				this.connector.saveRepeller(repeller);
			this.threadSafeArray = this.repellers.toArray(new xRepellerConstruct[this.repellers.size()]);
		}
		return false;
	}
	public void remove(xRepellerConstruct repeller){
		this.repellers.remove(repeller);
		if(repeller.getDatabaseID() >= 0) this.connector.removeRepellerFromDataBase(repeller.getDatabaseID());
		this.threadSafeArray = this.repellers.toArray(new xRepellerConstruct[this.repellers.size()]);
	}
	public xRepellerConstruct[] getThreadSafeArray(){
		return this.threadSafeArray;
	}
}
