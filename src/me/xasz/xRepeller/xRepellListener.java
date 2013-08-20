package me.xasz.xRepeller;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens of Blocks and CHest
 * 
 * @author xasz
 * 
 */
public class xRepellListener implements Listener {

	private xRepeller plugin = null;

	// list for saving all the repeller on runtime
	List<xRepellConstruct> repeller = new ArrayList<xRepellConstruct>();

	/**
	 * returns all repeller as list
	 * 
	 * @return
	 */
	public List<xRepellConstruct> getRepeller() {
		return repeller;
	}

	/**
	 * set a new repellerlist, old data will be deleted
	 * 
	 * @param repeller
	 */
	public void setRepeller(List<xRepellConstruct> repeller) {
		this.repeller = repeller;
	}

	/**
	 * creates a new instance of the Listener
	 * 
	 * @param instance
	 */
	public xRepellListener(xRepeller instance) {
		this.plugin = instance;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * listens to blocks, if there is a new repeller created
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material mat = block.getType();
		if (mat == Material.IRON_BLOCK || mat == Material.GOLD_BLOCK
				|| mat == Material.DIAMOND_BLOCK || mat == Material.CHEST) {
			// have to be checked
			for (int i = 0; i < repeller.size(); i++) {
				if (repeller.get(i).isPartOf(block)) {
					if (!event.getPlayer().hasPermission("xRepeller.create")) {
						player.sendMessage(ChatColor.YELLOW
								+ "[xRepeller]"
								+ ChatColor.WHITE
								+ "You have no Permission to destroy a xRepeller");
						event.setCancelled(true);
						return;
					}
					player.sendMessage(ChatColor.YELLOW + "[xRepeller]"
							+ ChatColor.WHITE
							+ "You destroyed a Mobrepeller. Type:"
							+ repeller.get(i).getMatieral().toString());

					repeller.remove(i);
					plugin.getConnector().deleteRepeller(
							event.getBlock().getWorld().getUID().toString(),
							event.getBlock().getX(), event.getBlock().getY(),
							event.getBlock().getZ());

					// this.plugin.sql.deleteRepeller(repeller.get(i));
					break;
				}
			}
		}

	}

	/**
	 * listens to blocks, if there is a repeller destroyed
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		xRepellConstruct rc = xRepellConstruct.isRepeller(block);
		if (rc != null) {
			if (event.getPlayer().hasPermission("xRepeller.create")) {
				player.sendMessage(ChatColor.YELLOW + "[xRepeller]"
						+ ChatColor.WHITE
						+ "You build a new Mobrepeller. Type:"
						+ rc.getMatieral().toString());
				player.sendMessage(ChatColor.YELLOW
						+ "[xRepeller]"
						+ ChatColor.WHITE
						+ "Hit on the Chest after you changed its content to check the radius!");
				repeller.add(rc);
				plugin.getConnector().saveRepeller(repeller);
			} else {
				player.sendMessage(ChatColor.YELLOW + "[xRepeller]"
						+ ChatColor.WHITE
						+ "You have no Permission to build a xRepeller.");
			}

		}
	}

	/**
	 * checks if spawnevents are repelled
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(CreatureSpawnEvent event) {
		EntityType type = null;
		
		List<EntityType> badMobs = new ArrayList<EntityType>();
		badMobs.add(EntityType.ZOMBIE );
		badMobs.add(EntityType.WITHER);
		badMobs.add(EntityType.WITCH);
		badMobs.add(EntityType.SPIDER);
		badMobs.add(EntityType.SLIME);
		badMobs.add(EntityType.SKELETON);
		badMobs.add(EntityType.PIG_ZOMBIE);
		badMobs.add(EntityType.MAGMA_CUBE);
		badMobs.add(EntityType.GIANT); badMobs.add(EntityType.GHAST);
		badMobs.add(EntityType.ENDERMAN);
		badMobs.add(EntityType.CREEPER);
		badMobs.add(EntityType.CAVE_SPIDER);
		badMobs.add(EntityType.BLAZE);
		badMobs.add(EntityType.SILVERFISH);
		
		List<EntityType> friendlyMobs = new ArrayList<EntityType>();
		friendlyMobs.add(EntityType.BAT);
		friendlyMobs.add(EntityType.CHICKEN);
		friendlyMobs.add(EntityType.COW);
		friendlyMobs.add(EntityType.OCELOT);
		friendlyMobs.add(EntityType.PIG);
		friendlyMobs.add(EntityType.SHEEP);
		friendlyMobs.add(EntityType.SQUID);
		friendlyMobs.add(EntityType.WOLF);
		friendlyMobs.add(EntityType.MUSHROOM_COW);
		
		try {
			type = event.getEntityType();
			boolean isFriendly = false;
			boolean isBad = false;
			isBad = badMobs.contains(type);
			isFriendly = friendlyMobs.contains(type);
			
			if (isBad || isFriendly) {
				for (xRepellConstruct x : repeller) {
					if (x.isInActiveChunk()) {
						if (x.isRepellingLocation(event.getLocation())) {
							// the location would be repelled
							if(isFriendly){
								if(!x.isRepellingFriendly()){
									break;
								}
							}
							if (x.isRepellingUnnatural()) {
								event.setCancelled(true);
								break;
							} else {
								if ( event.getSpawnReason() == SpawnReason.SPAWNER_EGG
									|| event.getSpawnReason() == SpawnReason.SPAWNER
									|| event.getSpawnReason() == SpawnReason.CUSTOM
									|| event.getSpawnReason() == SpawnReason.BREEDING) {
									break;
									// do not break the event
								}else{
					                event.setCancelled(true);
					                break;
					              }
							}	
							
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[xRepeller]: No Valid Spawnevent");
		}
	}

	/**
	 * checks if a inventory of a repeller chest is closed if yes the repeller
	 * will be recalculated
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event) {
		try {
			if (event.getInventory().getHolder() instanceof Chest) {
				for (xRepellConstruct x : repeller) {
					if (((Chest) event.getInventory().getHolder()).getBlock()
							.equals(x.getChestblock())) {
						x.calcRadius();
						System.out.println("Repeller ist recalculated");
					}
				}
			}
		} catch (Exception ex) {
			// nothing to do
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * Shows the player the effective radius of the repeller
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerLeftClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CHEST) {
				Chest thisChest = (Chest) event.getClickedBlock().getState();
				for (xRepellConstruct x : repeller) {
					if (x.isPartOf(thisChest)) {
						if (x.calcRadius()) {
							event.getPlayer().sendMessage(
									ChatColor.YELLOW + "[xRepeller]"
											+ ChatColor.WHITE
											+ "Maximum radius "
											+ x.getMaxRadius()
											+ " reached! Current radius: "
											+ x.getRepellRadius());
						} else {
							event.getPlayer().sendMessage(
									ChatColor.YELLOW + "[xRepeller]"
											+ ChatColor.WHITE
											+ "Current radius: "
											+ x.getRepellRadius());
						}
						if (!x.isRepellingUnnatural()) {
							event.getPlayer()
									.sendMessage(
											ChatColor.YELLOW
													+ "[xRepeller]"
													+ ChatColor.WHITE
													+ "This xRepeller does not repell Spawner, Egg oder Custom spawned Mobs.");
						}

						if (!x.isRepellingFriendly()) {
							event.getPlayer()
									.sendMessage(
											ChatColor.YELLOW
													+ "[xRepeller]"
													+ ChatColor.WHITE
													+ "This xRepeller does not repell friendly Mobs.");
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		List<Block> iBlocks = event.getBlocks();
		for (Block block : iBlocks) {
			// oldposition
			Location triggeredBlockloc = block.getRelative(
					event.getDirection(), 0).getLocation();
			triggeredBlockloc.setY(triggeredBlockloc.getY());
			Block triggeredBlock = triggeredBlockloc.getBlock();
			Material mat = triggeredBlock.getType();
			if (mat == Material.IRON_BLOCK || mat == Material.GOLD_BLOCK
					|| mat == Material.DIAMOND_BLOCK || mat == Material.CHEST) {
				// have to be checked
				for (int i = 0; i < repeller.size(); i++) {
					if (repeller.get(i).isPartOf(block)) {
						event.setCancelled(true);
						break;
					}
				}
			}
		}
	}
}
