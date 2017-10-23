package me.xasz.xRepeller;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class xRepellListener implements Listener {
	private xRepeller plugin;
	private xRepellerContainer container;

	public xRepellListener(xRepeller plugin, xRepellerContainer container) {
		this.plugin = plugin;
		this.container = container;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material mat = block.getType();
		
		if (mat == Material.IRON_BLOCK || mat == Material.GOLD_BLOCK
				|| mat == Material.DIAMOND_BLOCK || mat == Material.CHEST) {
			// have to be checked
			for (xRepellerConstruct repeller : this.container
					.getThreadSafeArray())
				if (repeller.isPartOf(block)) {
					if (!event.getPlayer().hasPermission("xRepeller.destroy")) {
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
							+ repeller.getMatieral().toString());

					this.container.remove(repeller);
					break;
				}
		}

	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if(block.getType() == Material.CHEST){
			if(xRepellerConstruct.simpleConstructionCheck((Chest)block.getState())){
				if (event.getPlayer().hasPermission("xRepeller.create")) {
					xRepellerConstruct repeller = new xRepellerConstruct(this.plugin,block.getWorld().getUID().toString(),block.getX(),block.getY(),block.getZ(),player.getName());
					repeller.init();
					player.sendMessage(ChatColor.YELLOW + "[xRepeller]"
							+ ChatColor.WHITE
							+ "You build a new Mobrepeller. Type:"
							+ repeller.getMatieral().toString());
					player.sendMessage(ChatColor.YELLOW
							+ "[xRepeller]"
							+ ChatColor.WHITE
							+ "Hit on the Chest after you changed its content to check the radius!");
					this.container.add(repeller);
				} else {
					player.sendMessage(ChatColor.YELLOW + "[xRepeller]"
							+ ChatColor.WHITE
							+ "You have no Permission to build a xRepeller.");
				}
			}
		}
		
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event) {
		try {
			if (event.getInventory().getHolder() instanceof Chest) {
				for (xRepellerConstruct repeller : this.container
						.getThreadSafeArray()) {
					if (((Chest) event.getInventory().getHolder()).getBlock()
							.equals(repeller.getChestblock())) {
						repeller.calcRadius();
						 this.plugin.logger.info("Repeller is recalculated at position " + repeller.getChestblock().getX()+ repeller.getChestblock().getY()+ repeller.getChestblock().getZ());
					}
				}
			}
		} catch (Exception ex) {
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerLeftClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CHEST) {
				Chest thisChest = (Chest) event.getClickedBlock().getState();
				for (xRepellerConstruct repeller : this.container
						.getThreadSafeArray())  {
					if (repeller.isPartOf(thisChest)) {
						repeller.calcRadius();
						if (repeller.isMaxRadius()) {
							event.getPlayer().sendMessage(
									ChatColor.YELLOW + "[xRepeller]"
											+ ChatColor.WHITE
											+ "Maximum radius "
											+ repeller.getMaxRadius()
											+ " reached! Current radius: "
											+ repeller.getRepellRadius());
						} else {
							event.getPlayer().sendMessage(
									ChatColor.YELLOW + "[xRepeller]"
											+ ChatColor.WHITE
											+ "Current radius: "
											+ repeller.getRepellRadius());
						}
						if (!repeller.isRepellingUnnatural()) {
							event.getPlayer()
									.sendMessage(
											ChatColor.YELLOW
													+ "[xRepeller]"
													+ ChatColor.WHITE
													+ "This xRepeller does not repell Spawner, Egg oder Custom spawned Mobs.");
						}

						if (!repeller.isRepellingFriendly()) {
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
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(CreatureSpawnEvent event) {
		EntityType type = null;
		
		List<EntityType> badMobs = new ArrayList<EntityType>();
		List<EntityType> friendlyMobs = new ArrayList<EntityType>();
		friendlyMobs.add(EntityType.BAT);
		badMobs.add(EntityType.BLAZE);
		badMobs.add(EntityType.CAVE_SPIDER);
		friendlyMobs.add(EntityType.CHICKEN);
		friendlyMobs.add(EntityType.COW);
		badMobs.add(EntityType.CREEPER);
		friendlyMobs.add(EntityType.DONKEY);
		badMobs.add(EntityType.ELDER_GUARDIAN);
		badMobs.add(EntityType.ENDER_DRAGON);
		badMobs.add(EntityType.ENDERMAN);
		badMobs.add(EntityType.ENDERMITE);
		badMobs.add(EntityType.EVOKER);
		badMobs.add(EntityType.EVOKER_FANGS);
		badMobs.add(EntityType.GHAST);
		badMobs.add(EntityType.GIANT);
		badMobs.add(EntityType.GUARDIAN);
		badMobs.add(EntityType.HUSK);
		friendlyMobs.add(EntityType.IRON_GOLEM);
		friendlyMobs.add(EntityType.LLAMA);
		badMobs.add(EntityType.MAGMA_CUBE);
		friendlyMobs.add(EntityType.MULE);
		friendlyMobs.add(EntityType.MUSHROOM_COW);
		friendlyMobs.add(EntityType.OCELOT);
		friendlyMobs.add(EntityType.PARROT);
		friendlyMobs.add(EntityType.PIG);
		badMobs.add(EntityType.PIG_ZOMBIE);
		badMobs.add(EntityType.POLAR_BEAR);
		friendlyMobs.add(EntityType.RABBIT);
		friendlyMobs.add(EntityType.SHEEP);
		badMobs.add(EntityType.SHULKER);
		badMobs.add(EntityType.SILVERFISH);
		badMobs.add(EntityType.SKELETON);
		badMobs.add(EntityType.SKELETON_HORSE);
		badMobs.add(EntityType.SLIME);
		friendlyMobs.add(EntityType.SNOWMAN);
		badMobs.add(EntityType.SPIDER);
		friendlyMobs.add(EntityType.SQUID);
		badMobs.add(EntityType.STRAY);
		badMobs.add(EntityType.VEX);
		badMobs.add(EntityType.VINDICATOR);
		badMobs.add(EntityType.WITCH);
		badMobs.add(EntityType.WITHER);
		badMobs.add(EntityType.WITHER_SKELETON);
		friendlyMobs.add(EntityType.WOLF);
		badMobs.add(EntityType.ZOMBIE);
		badMobs.add(EntityType.ZOMBIE_HORSE);
		badMobs.add(EntityType.ZOMBIE_VILLAGER);
		
		
		try {
			type = event.getEntityType();
			boolean isFriendly = false;
			boolean isBad = false;
			isBad = badMobs.contains(type);
			isFriendly = friendlyMobs.contains(type);
			
			if (isBad || isFriendly) {
				for (xRepellerConstruct repeller : this.container
						.getThreadSafeArray()) {
					try{
						if (repeller.isInActiveChunk()){
							if (repeller.isRepellingLocation(event.getLocation())) {
								// the location would be repelled
								if(isFriendly){
									if(!repeller.isRepellingFriendly()){
										break;
									}
								}
								if (repeller.isRepellingUnnatural()) {
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
					}catch(Exception ex){
						
					}
				}
			}
		} catch (Exception e) {
			this.plugin.logger.info("No Valid Spawnevent");
			e.printStackTrace();
		}
	}
}
