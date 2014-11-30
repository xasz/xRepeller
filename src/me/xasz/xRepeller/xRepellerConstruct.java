package me.xasz.xRepeller;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class xRepellerConstruct {

	private int dbID;
	private int x;
	private int y;
	private int z;
	private String worldUID;
	private String player;

	private xRepeller plugin;

	public xRepellerConstruct(xRepeller plugin, String worldUID, int x, int y,
			int z, String player) {
		this.plugin = plugin;
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldUID = worldUID;
		this.player = player;
		this.dbID = -1;
	}
	public String getPlayer(){
		return this.player;
	}
	public boolean init(){
		if(this.checkConstruction()){
			this.calcRadius();
			return true;
		}
		return false;
	}
	public int getDatabaseID() {
		return dbID;
	}

	public void setDatabaseID(int id) {
		this.dbID = id;
	}

	public Chest getChestblock() {
		return this.chestBlock;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	/*
	 * TOP LEFT CHEST RIGHT BOTTOM
	 */
	private Chest chestBlock = null;
	private Block leftBlock = null;
	private Block rightBlock = null;
	private Block topBlock = null;
	private Block bottomBlock = null;

	private double repellRadius = 0;
	private boolean isMaxRadius = false;
	private boolean isRepellingUnnatural = true;
	private boolean isRepellingFriendly = false;
	private Material material;
	
	public boolean isMaxRadius(){
		return this.isMaxRadius;
	}
	// this is set to false if the repeller should be calculcated on the next
	// repellrun
	// the calculation for the repeller should just done once u edit a chest ->
	// performance

	public boolean isInActiveChunk() {
		return chestBlock.getWorld().isChunkLoaded(chestBlock.getChunk());
	}

	public boolean isRepellingLocation(Location l) {
		boolean is = false;
		if (chestBlock.getWorld().getUID() == l.getWorld().getUID()) {
			if (chestBlock.getLocation().distance(l) <= repellRadius) {
				is = true;
			}
		}
		return is;
	}

	public boolean isPartOf(Chest chestToCheck) {
		boolean isPart = false;
		if (chestToCheck.getBlock().getLocation().toString()
				.equalsIgnoreCase(chestBlock.getLocation().toString())) {
			isPart = true;
		}
		return isPart;
	}

	public Material getMatieral() {
		return this.material;
	}

	public boolean isPartOf(Block blockToCheck) {
		boolean isPart = false;
		if (blockToCheck.getLocation().toString()
				.equalsIgnoreCase(chestBlock.getLocation().toString())
				|| blockToCheck.getLocation().toString()
						.equalsIgnoreCase(leftBlock.getLocation().toString())
				|| blockToCheck.getLocation().toString()
						.equalsIgnoreCase(rightBlock.getLocation().toString())
				|| blockToCheck.getLocation().toString()
						.equalsIgnoreCase(topBlock.getLocation().toString())
				|| blockToCheck.getLocation().toString()
						.equalsIgnoreCase(bottomBlock.getLocation().toString())) {
			isPart = true;
		}
		return isPart;
	}

	public double getMaxRadius() {
		if (leftBlock.getType() == Material.IRON_BLOCK) {
			return xRepeller.ironMaxRepellDistance;
		} else if (leftBlock.getType() == Material.GOLD_BLOCK) {
			return xRepeller.goldMaxRepellDistance;
		} else if (leftBlock.getType() == Material.DIAMOND_BLOCK) {
			return xRepeller.diamondMaxRepellDistance;
		}
		return 0.f;
	}

	public void calcRadius() {
		boolean isMaxRadius = false;
		double maxRadius = 0.f;
		double currentRepellRadius = 0.f;
		double tick = 0.f;
		// blocktypes
		Material[] mat = new Material[3];
		mat[0] = Material.IRON_BLOCK;
		mat[1] = Material.GOLD_BLOCK;
		mat[2] = Material.DIAMOND_BLOCK;

		// get the max repell distances depending on repellertype
		if (leftBlock.getType() == Material.IRON_BLOCK) {
			maxRadius = xRepeller.ironMaxRepellDistance;
		} else if (leftBlock.getType() == Material.GOLD_BLOCK) {
			maxRadius = xRepeller.goldMaxRepellDistance;
		} else if (leftBlock.getType() == Material.DIAMOND_BLOCK) {
			maxRadius = xRepeller.diamondMaxRepellDistance;
		}

		setRepellingUnnatural(true);
		if (this.chestBlock.getInventory().contains(Material.LAPIS_BLOCK, 1)) {
			setRepellingUnnatural(false);
		}
		setRepellingFriendly(false);
		if (this.chestBlock.getInventory().contains(Material.EMERALD_BLOCK, 1)) {
			setRepellingFriendly(true);
		}

		// for each block of material in chest, add distance
		for (Material currentmat : mat) {
			int blockCount = 1;
			// choose the current tick
			if (currentmat == Material.IRON_BLOCK) {
				tick = xRepeller.ironRepellDistance;
			} else if (currentmat == Material.GOLD_BLOCK) {
				tick = xRepeller.goldRepellDistance;
			} else if (currentmat == Material.DIAMOND_BLOCK) {
				tick = xRepeller.diamondRepellDistance;
			} else {
				tick = 0.f;
			}
			while (this.chestBlock.getInventory().contains(currentmat, blockCount)) {
				blockCount++;
				if (currentRepellRadius >= maxRadius) {
					currentRepellRadius = maxRadius;
					isMaxRadius = true;
					break;
				}
			}
			currentRepellRadius += tick * (blockCount - 1);
		}
		if (currentRepellRadius >= maxRadius) {
			currentRepellRadius = maxRadius;
			isMaxRadius = true;
		}

		this.repellRadius = currentRepellRadius;

		this.isMaxRadius = isMaxRadius;
	}

	public double getRepellRadius() {
		return repellRadius;
	}

	public boolean isRepellingUnnatural() {
		return isRepellingUnnatural;
	}

	public void setRepellingUnnatural(boolean isRepellingUnnatural) {
		this.isRepellingUnnatural = isRepellingUnnatural;
	}

	public boolean isRepellingFriendly() {
		return this.isRepellingFriendly;
	}

	public void setRepellingFriendly(boolean isRepellingFriendly) {
		this.isRepellingFriendly = isRepellingFriendly;
	}

	
	public static boolean simpleConstructionCheck(Chest chest){
		Block checkBlock1 = chest.getWorld().getBlockAt(
				chest.getX() + 1, chest.getY(),
				chest.getZ());
		Block checkBlock2 = chest.getWorld().getBlockAt(
				chest.getX() - 1, chest.getY(),
				chest.getZ());
		Block checkBlock3 = chest.getWorld().getBlockAt(
				chest.getX(), chest.getY(),
				chest.getZ() + 1);
		Block checkBlock4 = chest.getWorld().getBlockAt(
				chest.getX(), chest.getY(),
				chest.getZ() - 1);
		if (checkBlock1.getType() == Material.IRON_BLOCK
				&& checkBlock2.getType() == Material.IRON_BLOCK
				&& checkBlock3.getType() == Material.IRON_BLOCK
				&& checkBlock4.getType() == Material.IRON_BLOCK) {
			return true;
		} else if (checkBlock1.getType() == Material.GOLD_BLOCK
				&& checkBlock2.getType() == Material.GOLD_BLOCK
				&& checkBlock3.getType() == Material.GOLD_BLOCK
				&& checkBlock4.getType() == Material.GOLD_BLOCK) {
			return true;
		} else if (checkBlock1.getType() == Material.DIAMOND_BLOCK
				&& checkBlock2.getType() == Material.DIAMOND_BLOCK
				&& checkBlock3.getType() == Material.DIAMOND_BLOCK
				&& checkBlock4.getType() == Material.DIAMOND_BLOCK) {
			return true;
		}  
	    return false;
	}
	private boolean checkConstruction() {
		/*
		 * X = Iron, Gold, Diamond C = Chest
		 * 
		 * Topview X X C X X
		 * 
		 * Sideview
		 * 
		 * X C X
		 */

		//checking world
		Block shouldBechestBlock = null;
	    for( World w :plugin.getServer().getWorlds()){
	        if(w.getUID().toString().equalsIgnoreCase(this.worldUID)){
	          shouldBechestBlock = w.getBlockAt(x, y, z);
	        }
	    }
	    
	    if(shouldBechestBlock == null) return false;
	    

		Block checkBlock1 = shouldBechestBlock.getWorld().getBlockAt(
				shouldBechestBlock.getX() + 1, shouldBechestBlock.getY(),
				shouldBechestBlock.getZ());
		Block checkBlock2 = shouldBechestBlock.getWorld().getBlockAt(
				shouldBechestBlock.getX() - 1, shouldBechestBlock.getY(),
				shouldBechestBlock.getZ());
		Block checkBlock3 = shouldBechestBlock.getWorld().getBlockAt(
				shouldBechestBlock.getX(), shouldBechestBlock.getY(),
				shouldBechestBlock.getZ() + 1);
		Block checkBlock4 = shouldBechestBlock.getWorld().getBlockAt(
				shouldBechestBlock.getX(), shouldBechestBlock.getY(),
				shouldBechestBlock.getZ() - 1);

		this.material = null;
		if (shouldBechestBlock.getType() == Material.CHEST) {
			// check the construct
			if (checkBlock1.getType() == Material.IRON_BLOCK
					&& checkBlock2.getType() == Material.IRON_BLOCK
					&& checkBlock3.getType() == Material.IRON_BLOCK
					&& checkBlock4.getType() == Material.IRON_BLOCK) {
				// build iron xRepeller
				this.material = Material.IRON_BLOCK;
			} else if (checkBlock1.getType() == Material.GOLD_BLOCK
					&& checkBlock2.getType() == Material.GOLD_BLOCK
					&& checkBlock3.getType() == Material.GOLD_BLOCK
					&& checkBlock4.getType() == Material.GOLD_BLOCK) {
				// build gold xRepeller
				this.material = Material.GOLD_BLOCK;
			} else if (checkBlock1.getType() == Material.DIAMOND_BLOCK
					&& checkBlock2.getType() == Material.DIAMOND_BLOCK
					&& checkBlock3.getType() == Material.DIAMOND_BLOCK
					&& checkBlock4.getType() == Material.DIAMOND_BLOCK) {
				// build diamond xRepeller
				this.material = Material.DIAMOND_BLOCK;
			}
			if(this.material == null) return false;
			
		    chestBlock = (Chest) shouldBechestBlock.getState();
		    leftBlock = checkBlock1;
		    rightBlock = checkBlock2;
		    topBlock = checkBlock3;
		    bottomBlock = checkBlock4;   
		    return true;
			
		}
		return false;
	}
}
