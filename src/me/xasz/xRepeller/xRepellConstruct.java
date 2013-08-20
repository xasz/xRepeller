package me.xasz.xRepeller;




import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
/**
 * Represents a Repeller-Structure
 * @author xasz
 * 
 */
public class xRepellConstruct
{
  
  /*
   *        TOP
   *  LEFT  CHEST    RIGHT
   *        BOTTOM
   */
  private Block chestBlock = null;
  private Block leftBlock = null;
  private Block rightBlock = null;
  private Block topBlock = null;
  private Block bottomBlock = null;
  private double repellRadius = 0;
  private boolean isInDatabase = false;
  
  private boolean isRepellingUnnatural = true;
  private boolean isRepellingFriendly = false;

  //this is set to false if the repeller should be calculcated on the next repellrun
  //the calculation for the repeller should just done once u edit a chest -> performance
  private boolean isCalculated = false;
  
  /**
   * see bool isCalculated
   * @return
   */
  public boolean isCalculated()
  {
    return isCalculated;
  }
  /**
   * see bool isCalculated
   * @param isCalculated
   */
  public void setCalculated(boolean isCalculated)
  {
    this.isCalculated = isCalculated;
  }
  /**
   * is repeller in database
   * @return
   */
  public boolean isInDatabase()
  {
    return isInDatabase;
  }
  /**
   * set repeller in database
   * @param isInDatabase
   */
  public void setInDatabase(boolean isInDatabase)
  {
    this.isInDatabase = isInDatabase;
  }

  /*
   * @return true if maxradius of repeller is reached and false if not
   */
  public boolean calcRadius(){
    boolean isMaxRadius = false;
    double maxRadius = 0.f;
    double currentRepellRadius = 0.f;
    double tick = 0.f;
    //blocktypes
    Material[] mat = new Material[3];
    mat[0] = Material.IRON_BLOCK;
    mat[1] = Material.GOLD_BLOCK;
    mat[2] = Material.DIAMOND_BLOCK;
    
    Chest chest = (Chest) chestBlock.getState();
    
    //get the max repell distances depending on repellertype
    if(leftBlock.getType() == Material.IRON_BLOCK){
      maxRadius = xRepeller.ironMaxRepellDistance;
    }else if(leftBlock.getType() == Material.GOLD_BLOCK){
      maxRadius = xRepeller.goldMaxRepellDistance;
    }else if(leftBlock.getType() == Material.DIAMOND_BLOCK){
      maxRadius = xRepeller.diamondMaxRepellDistance;
    }
    
    setRepellingUnnatural(true);
    if(chest.getInventory().contains(Material.LAPIS_BLOCK,1)){
      setRepellingUnnatural(false);
    }
    setRepellingFriendly(false);
    if(chest.getInventory().contains(Material.EMERALD_BLOCK,1)){
    	setRepellingFriendly(true);
    }   
    
    //for each block of material in chest, add distance
    for(Material currentmat : mat){
      int blockCount = 1;
      //choose the current tick
        if(currentmat == Material.IRON_BLOCK){
          tick = xRepeller.ironRepellDistance;             
        }else if(currentmat == Material.GOLD_BLOCK){
          tick = xRepeller.goldRepellDistance;               
        }else if(currentmat == Material.DIAMOND_BLOCK){
          tick = xRepeller.diamondRepellDistance;          
        }
          else{
          tick = 0.f;
        }
        while(chest.getInventory().contains(currentmat, blockCount)){
          blockCount++;
            if(currentRepellRadius >= maxRadius){
              currentRepellRadius = maxRadius;
              isMaxRadius = true; 
              break;
            }
        } 
        currentRepellRadius += tick*(blockCount-1);
      }
      if(currentRepellRadius >= maxRadius){
        currentRepellRadius = maxRadius;
        isMaxRadius = true; 
      }
    
      this.repellRadius = currentRepellRadius;
      isCalculated = true;
      
      return isMaxRadius; 
  }
  
  /**
   * creates a new repeller 
   * @param tchestBlock
   * @param tleftBlock
   * @param trightBlock
   * @param ttopBlock
   * @param tbottomBlock
   * @throws Exception
   */
  public xRepellConstruct(Block tchestBlock, Block tleftBlock, Block trightBlock, Block ttopBlock, Block tbottomBlock) throws Exception{
    Material first = tleftBlock.getType();
    if(!(first == trightBlock.getType() && first == ttopBlock.getType() && first == tbottomBlock.getType())){
      throw new Exception("Not Same Material");
    }
    
    chestBlock = tchestBlock;
    leftBlock = tleftBlock;
    rightBlock = trightBlock;
    topBlock = ttopBlock;
    bottomBlock = tbottomBlock;   
  }
  /**
   * checks if there is a repeller on the given coords in the given world and creates it, otherwise throws Exception
   * @param plugin
   * @param worlduid
   * @param x
   * @param y
   * @param z
   * @throws Exception
   */
  public xRepellConstruct(xRepeller plugin, String worlduid, int x, int y, int z) throws Exception {
    for( World w :plugin.getServer().getWorlds()){
      if(w.getUID().toString().equalsIgnoreCase(worlduid)){
        Block perhabsChest = w.getBlockAt(x, y, z);
       if(null != isRepeller(perhabsChest)){
         chestBlock = perhabsChest;
         leftBlock = chestBlock.getWorld().getBlockAt(chestBlock.getX()+1,chestBlock.getY(),chestBlock.getZ());
         rightBlock = chestBlock.getWorld().getBlockAt(chestBlock.getX()-1,chestBlock.getY(),chestBlock.getZ());
         topBlock = chestBlock.getWorld().getBlockAt(chestBlock.getX(),chestBlock.getY(),chestBlock.getZ()+1);
         bottomBlock = chestBlock.getWorld().getBlockAt(chestBlock.getX(),chestBlock.getY(),chestBlock.getZ()-1);

       }else{
         throw new Exception("Not in this World");
       }
      }
    }
  }
  /**
   * checks if a block is the chestblock of a repeller
   * if yes u get the repellconstruct
   * if no u get null
   * @param shouldBechestBlock
   * @return
   */
  public static xRepellConstruct isRepeller(Block shouldBechestBlock){
    /*
     *  X = Iron, Gold, Diamond
     *  C = Chest
     *  
     *  Topview
     *     X
     *   X C X
     *     X
     *     
     *  Sideview
     *  
     *  X C X
     * 
     */
    
    //check if chest
    Material newRepellerMaterial = null;
    
    Block checkBlock1 = shouldBechestBlock.getWorld().getBlockAt(shouldBechestBlock.getX()+1,shouldBechestBlock.getY(),shouldBechestBlock.getZ());
    Block checkBlock2 = shouldBechestBlock.getWorld().getBlockAt(shouldBechestBlock.getX()-1,shouldBechestBlock.getY(),shouldBechestBlock.getZ());
    Block checkBlock3 = shouldBechestBlock.getWorld().getBlockAt(shouldBechestBlock.getX(),shouldBechestBlock.getY(),shouldBechestBlock.getZ()+1);
    Block checkBlock4 = shouldBechestBlock.getWorld().getBlockAt(shouldBechestBlock.getX(),shouldBechestBlock.getY(),shouldBechestBlock.getZ()-1);
    
    if(shouldBechestBlock.getType() == Material.CHEST){
      //check the construct
      if(checkBlock1.getType() == Material.IRON_BLOCK && 
          checkBlock2.getType() == Material.IRON_BLOCK && 
              checkBlock3.getType() == Material.IRON_BLOCK && 
                  checkBlock4.getType() == Material.IRON_BLOCK )   {
         //build iron xRepeller
         newRepellerMaterial = Material.IRON_BLOCK;
      } else
      if(checkBlock1.getType() == Material.GOLD_BLOCK && 
          checkBlock2.getType() == Material.GOLD_BLOCK && 
              checkBlock3.getType() == Material.GOLD_BLOCK && 
                  checkBlock4.getType() == Material.GOLD_BLOCK )   {
          //build gold xRepeller
        newRepellerMaterial = Material.GOLD_BLOCK;         
       } else
         if(checkBlock1.getType() == Material.DIAMOND_BLOCK && 
             checkBlock2.getType() == Material.DIAMOND_BLOCK && 
                 checkBlock3.getType() == Material.DIAMOND_BLOCK && 
                     checkBlock4.getType() == Material.DIAMOND_BLOCK )   {
         //build diamond xRepeller
           newRepellerMaterial = Material.DIAMOND_BLOCK;        
      }
     if(newRepellerMaterial != null){
       try
        {

         return new xRepellConstruct(shouldBechestBlock,checkBlock1,checkBlock2,checkBlock3,checkBlock4);

        }
        catch (Exception e)
        {
          //should not get here
          e.printStackTrace();
          return null;
        }
     }
    }
    return null;
  }
  
 /**
  * get the chestblock of this repeller
  * @return
  */
  public Block getChestblock(){
    return chestBlock;
  }
  /**
   * get the material of this repeller
   * @return
   */
  public Material getMatieral(){
    return leftBlock.getType();
  }
  public boolean isPartOf(Block blockToCheck){
    boolean isPart = false;
    if(blockToCheck.getLocation().toString().equalsIgnoreCase(chestBlock.getLocation().toString()) ||
        blockToCheck.getLocation().toString().equalsIgnoreCase(leftBlock.getLocation().toString() )||
        blockToCheck.getLocation().toString().equalsIgnoreCase(rightBlock.getLocation().toString()) || 
        blockToCheck.getLocation().toString().equalsIgnoreCase(topBlock.getLocation().toString()) || 
        blockToCheck.getLocation().toString().equalsIgnoreCase(bottomBlock.getLocation().toString() )){
      isPart = true;
    }
    return isPart;
  }
  /**
   * checks if the given chest is part of this repeller
   * @param chestToCheck
   * @return
   */
  public boolean isPartOf(Chest chestToCheck){
    boolean isPart = false;
    if(chestToCheck.getBlock().getLocation().toString().equalsIgnoreCase(chestBlock.getLocation().toString())){
      isPart = true;
    }
    return isPart;
  }
  /**
   * checks if this repeller ist repelling the given location
   * true if yes
   * false if no
   * @param l
   * @return
   */
  public boolean isRepellingLocation(Location l){
    if(!isCalculated){
      this.calcRadius();
    }
    boolean is = false;
    if(chestBlock.getWorld().getUID() == l.getWorld().getUID()){
        if(chestBlock.getLocation().distance(l) <= repellRadius){
          is = true;
        }
    }
    return is;
  }
  /**
   * is repeller in active chunk
   * @return
   */
  public boolean isInActiveChunk(){
     return chestBlock.getWorld().isChunkLoaded(chestBlock.getChunk());
  }
  /**
   * returns the maxradios of this repeller, depends by material
   * @return
   */
  public double getMaxRadius(){
    if(leftBlock.getType() == Material.IRON_BLOCK){
      return xRepeller.ironMaxRepellDistance;
    }else if(leftBlock.getType() == Material.GOLD_BLOCK){
      return xRepeller.goldMaxRepellDistance;
    }else if(leftBlock.getType() == Material.DIAMOND_BLOCK){
      return xRepeller.diamondMaxRepellDistance;
    }
    return 0.f;    
  }
  /**
   * gets the repellradius of this repeller
   * @return
   */
  public double getRepellRadius(){
    return repellRadius;
  }
  public boolean isRepellingUnnatural()
  {
    return isRepellingUnnatural;
  }
  public void setRepellingUnnatural(boolean isRepellingUnnatural)
  {
    this.isRepellingUnnatural = isRepellingUnnatural;
  }
  public boolean isRepellingFriendly(){
	  return this.isRepellingFriendly;
  }
  public void setRepellingFriendly(boolean isRepellingFriendly)
  {
    this.isRepellingFriendly = isRepellingFriendly;
  }
}
