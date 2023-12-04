package unsw.loopmania;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.GameMode.Mode;
import unsw.loopmania.Item.Slot;
import unsw.loopmania.ItemFactory.ItemType;

/**
 * A backend world.
 *
 * A world can contain many entities, each occupy a square. More than one
 * entity can occupy the same square.
 */
public class LoopManiaWorld {
    // TODO = add additional backend functionality

    public static final int unequippedInventoryWidth = 4;
    public static final int unequippedInventoryHeight = 4;
    public static final int equippedInventoryWidth = 4;
    public static final int equippedInventoryHeight = 2;

    /**
     * width of the world in GridPane cells
     */
    private int width;

    /**
     * height of the world in GridPane cells
     */
    private int height;

    /**
     * generic entitites - i.e. those which don't have dedicated fields
     */
    private List<Entity> nonSpecifiedEntities;

    private Character character;

    private HerosCastle castle;

    private int cycle;

    private int exp;

    private int gold;

    private boolean doggieDefeated;

    private boolean elanDefeated;

    private Goal goal;

    private Mode gameMode;

    // TODO = add more lists for other entities, for equipped inventory items, etc...

    // TODO = expand the range of enemies
    private List<BasicEnemy> enemies;

    // TODO = expand the range of cards
    private List<Card> cardEntities;

    // TODO = expand the range of items
    private List<Item> unequippedInventoryItems;

    // TODO = expand the range of buildings
    private List<Building> buildingEntities;

    private ItemFactory itemFactory;

    private List<GoldSpawn> goldOnMap;

    private List<HealthPotion> healthPotionOnMap;

    /**
     * list of x,y coordinate pairs in the order by which moving entities traverse them
     */
    private List<Pair<Integer, Integer>> orderedPath;

    /**
     * create the world (constructor)
     * 
     * @param width width of world in number of cells
     * @param height height of world in number of cells
     * @param orderedPath ordered list of x, y coordinate pairs representing position of path cells in world
     */
    public LoopManiaWorld(int width, int height, List<Pair<Integer, Integer>> orderedPath) {
        this.width = width;
        this.height = height;
        nonSpecifiedEntities = new ArrayList<>();
        character = null;
        castle = null;
        cycle = 0;
        exp = 0;
        gold = 0;
        this.gameMode = null;
        enemies = new ArrayList<>();
        cardEntities = new ArrayList<>();
        unequippedInventoryItems = new ArrayList<>();
        goldOnMap = new ArrayList<>();
        healthPotionOnMap = new ArrayList<>();
        //equippedInventoryItems = new ArrayList<>();
        this.orderedPath = orderedPath;
        buildingEntities = new ArrayList<>();
        this.itemFactory = new ItemFactory();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * set the character. This is necessary because it is loaded as a special entity out of the file
     * @param character the character
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setCastle(HerosCastle castle) {
        this.castle = castle;
        buildingEntities.add(castle);
    }

    public void setGameMode(Mode mode) {
        this.gameMode = mode;
    }

    public Mode getGameMode() {
        return gameMode;
    }

    public Character getCharacter() {
        return character;
    }

    /**
     * add a generic entity (without it's own dedicated method for adding to the world)
     * @param entity
     */
    public void addEntity(Entity entity) {
        // for adding non-specific entities (ones without another dedicated list)
        // TODO = if more specialised types being added from main menu, add more methods like this with specific input types...
        nonSpecifiedEntities.add(entity);
    }

    /**
     * spawns slugs if the conditions warrant it, adds to world
     * @return list of the slugs to be displayed on screen
     */
    public List<BasicEnemy> possiblySpawnEnemies(){
        // TODO = expand this very basic version
        Pair<Integer, Integer> pos = possiblyGetBasicEnemySpawnPosition();
        List<BasicEnemy> spawningEnemies = new ArrayList<>();
        if (pos != null){
            int indexInPath = orderedPath.indexOf(pos);
            BasicEnemy enemy = new Slug(new PathPosition(indexInPath, orderedPath));
            enemies.add(enemy);
            spawningEnemies.add(enemy);
        }
        return spawningEnemies;
    }

    /**
     * Adds the specified enemy to the enemies list. For ease of testing
     */
    public void addEnemy(BasicEnemy enemy) {
        enemies.add(enemy);
    }

    /**
     * kill an enemy
     * @param enemy enemy to be killed
     */
    private void killEnemy(BasicEnemy enemy){
        enemy.destroy();
        enemies.remove(enemy);
    }

    /**
     * run the expected battles in the world, based on current world state
     * @return list of enemies which have been killed
     */
    public List<BasicEnemy> runBattles() {
        List<MovingEntity> battleEnemies = gatherEnemies();
        //If there is at least one enemy to fight, start a battle
        if (battleEnemies.size() > 0){
            List<MovingEntity> battleAllies = gatherAllies();
            Battle battle = new Battle(character, battleAllies,  battleEnemies);
            battle.Fight();

            List<BasicEnemy> defeatedEnemies = battle.getDefeatedEnemies();
            for (BasicEnemy e: defeatedEnemies){
                // IMPORTANT = we kill enemies here, because killEnemy removes the enemy from the enemies list
                // if we killEnemy in prior loop, we get java.util.ConcurrentModificationException
                // due to mutating list we're iterating over
                killEnemy(e);
            }
            return defeatedEnemies;
        } 
        //If theres nothing to fight, return an empty list
        else {
            return new ArrayList<BasicEnemy>();
        }

    }

    /**
     * determines which allies take part in a specific fight, and adds them to a list alongside hero to pass to Battle()
     * @return list of MovingEnititys on the side of good
     */
    public List<MovingEntity> gatherAllies() {
        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        
        //Add character
        allies.add(character);

        //Add allies
        for (Ally ally : character.getAllies()) {
            allies.add(ally);
        }
        
        //Add towers
        for (Building building : buildingEntities) {
            //Check building type
            if (building instanceof TowerBuilding){
                //Check distance
                //Radius of tower support is 8 tiles 8^2 = 64
                if (Math.pow((character.getX()-building.getX()), 2) + Math.pow((character.getY()-building.getY()), 2) <= 
                    Math.pow(((TowerBuilding) building).getSupportRadius(), 2)){
                    TowerAlly tempTower = new TowerAlly(null);
                    allies.add(tempTower);
                }
            }
        }

        return allies;
    }

    /**
     * determines which enemies take part in a specific fight, and adds them to a list to pass to Battle()
     * if its count is 0, no battle occurs this iteration
     * @return list of MovingEnititys on the side of good
     */
    public List<MovingEntity> gatherEnemies() {
        ArrayList<MovingEntity> battleEnemies = new ArrayList<MovingEntity>();

        //Check for those in battle range
        for (BasicEnemy e: enemies){
            if (Math.pow((character.getX()-e.getX()), 2) + Math.pow((character.getY()-e.getY()), 2) <= (e.getBattleRadius() * e.getBattleRadius())){
                battleEnemies.add(e);
            }
        }

        //if there is at least one in battle range, look for additional enemies in support range
        if (battleEnemies.size() > 0){
            //Check for those in support range
            for (BasicEnemy e: enemies){
                if (Math.pow((character.getX()-e.getX()), 2) +  Math.pow((character.getY()-e.getY()), 2) <= (e.getSupportRadius() * e.getSupportRadius())){
                    //Check it wasnt already added due to being in battle range
                    if (!battleEnemies.contains(e)){
                        battleEnemies.add(e);
                    }
                }
            }   
        }

        return battleEnemies;
    }

    /** Given a list of enemies defeated in battle, calculates rewards and
     *  updates trackers in world state accordingly
     */
    public void GainBattleRewards(List<BasicEnemy> enemies){
        for (BasicEnemy basicEnemy : enemies) {
            setExp(getExp() + basicEnemy.getXp());
            setGold(getGold() + basicEnemy.getGold());
        }

    }

    
    public Pair<Card, Item> loadCard(Class<?> type) {
        Pair<Card, Item> card = null;
        Item item = null;
        if (cardEntities.size() >= getWidth()){
            removeCard(0);
            item = cardRemovalLoot();
        }
        if(type == VampireCastleCard.class) {
            VampireCastleCard vampireCastleCard = new VampireCastleCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(vampireCastleCard);
            card = new Pair<>(vampireCastleCard, item);
        } else if(type == ZombiePitCard.class) {
            ZombiePitCard zombiePitCard = new ZombiePitCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(zombiePitCard);
            card = new Pair<>(zombiePitCard, item);
        } else if(type == BarracksCard.class) {
            BarracksCard barracksCard = new BarracksCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(barracksCard);
            card = new Pair<>(barracksCard, item);
        } else if(type == VillageCard.class) {
            VillageCard villageCard = new VillageCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(villageCard);
            card = new Pair<>(villageCard, item);
        } else if(type == CampfireCard.class) {
            CampfireCard campfireCard = new CampfireCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(campfireCard);
            card = new Pair<>(campfireCard, item);
        } else if(type == TrapCard.class) {
            TrapCard trapCard = new TrapCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(trapCard);
            card = new Pair<>(trapCard, item);
        } else {
            TowerCard towerCard = new TowerCard(new SimpleIntegerProperty(cardEntities.size()), new SimpleIntegerProperty(0));
            cardEntities.add(towerCard);
            card = new Pair<>(towerCard, item);
        }

        return card;
    }

    /**
     * Give gold/experience/item rewards for the discarding of the oldest card
     */
    private Item cardRemovalLoot() {
        Item item = null;
        setGold(getGold() + 10);
        setExp(getExp() + 100);
        Random rand = new Random();
        int seed = rand.nextInt(20);
        if(seed == 19) {
            item = createRandomWeapon();
        }
        return item;
    }

    /**
     * remove card at a particular index of cards (position in gridpane of unplayed cards)
     * @param index the index of the card, from 0 to length-1
     */
    private void removeCard(int index){
        Card c = cardEntities.get(index);
        int x = c.getX();
        c.destroy();
        cardEntities.remove(index);
        shiftCardsDownFromXCoordinate(x);
    }

    public Boolean usingPotion() {
        HealthPotion healthPotion = (HealthPotion)character.getEquipment(Slot.POTION);
        if (healthPotion != null && character.getHealth() < character.getMaxHealth()) {
            healthPotion.useItem(character);
            character.DeequipItem(healthPotion);
            healthPotion.destroy();
            return true;
        } 
        return false;
    }

    /**
     * spawn an item in the world and return the sword entity
     * @return an item to be spawned in the controller as a JavaFX node
     */
    public Item addUnequippedItem(ItemType itemType){
        // TODO = expand this - we would like to be able to add multiple types of items, apart from swords
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        if (firstAvailableSlot == null){
            // eject the oldest unequipped item and replace it... oldest item is that at beginning of items
            // TODO = give some cash/experience rewards for the discarding of the oldest item
            removeItemByPositionInUnequippedInventoryItems(0);
            setExp(getExp() + 50);
            setGold(getGold() + 50);
            firstAvailableSlot = getFirstAvailableSlotForItem();
        }
        
        // now we insert the new sword, as we know we have at least made a slot available...
        Item item = itemFactory.createItem(itemType, new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        unequippedInventoryItems.add(item);
        return item;
    }

    public Item addUnequippedItemByCoordinate(ItemType itemType, int x, int y){
        Item item = itemFactory.createItem(itemType, new SimpleIntegerProperty(x), new SimpleIntegerProperty(y));
        unequippedInventoryItems.add(item);
        return item;
    }

    public Item addEquippedItemByCoordinate(ItemType itemType, int x, int y){
        Item item = itemFactory.createItem(itemType, new SimpleIntegerProperty(x), new SimpleIntegerProperty(y));
        character.equipItem(item);
        return item;
    }

    /**
     * remove an item by x,y coordinates
     * @param x x coordinate from 0 to width-1
     * @param y y coordinate from 0 to height-1
     */
    public void removeUnequippedInventoryItemByCoordinates(int x, int y){
        Item item = getUnequippedInventoryItemEntityByCoordinates(x, y);
        if(item != null) {
            removeUnequippedInventoryItem(item);
        }
    }

    public Item moveFromUnequippedToEquipped(int x, int y, int x2, int y2) {
        Item item = getUnequippedInventoryItemEntityByCoordinates(x, y);
        if (gameMode.equals(Mode.CONFUSING)) addExtendedProperties(item);
        equipItem(item);
        item.setX(x2);
        item.setY(y2);
        unequippedInventoryItems.remove(item);
        return item;
    }
    
    public Item moveFromEquippedToUnequipped(int x, int y, int x2, int y2) {
        Item item = character.DeequipItemByCoordinate(x, y);
        item.setX(x2);
        item.setY(y2);
        unequippedInventoryItems.add(item);
        return item;
    }

    /**
     * equip an item (move from unequipped inventory to character equipment)
     */
    private void equipItem(Item item){
        character.equipItem(item);
        unequippedInventoryItems.remove(item);
    }

    /**
     * run moves which occur with every tick without needing to spawn anything immediately
     */
    public void runTickMoves(){
        character.moveDownPath();
        moveBasicEnemies();
        if(character.getX() == castle.getX() && character.getY() == castle.getY()) {
            cycle++;
        }
    }

    /**
     * remove an item from the unequipped inventory
     * @param item item to be removed
     */
    private void removeUnequippedInventoryItem(Item item){
        item.destroy();
        unequippedInventoryItems.remove(item);
    }

    /**
     * return an unequipped inventory item by x and y coordinates
     * assumes that no 2 unequipped inventory items share x and y coordinates
     * @param x x index from 0 to width-1
     * @param y y index from 0 to height-1
     * @return unequipped inventory item at the input position
     */
    public Item getUnequippedInventoryItemEntityByCoordinates(int x, int y) {
        for (Item e: unequippedInventoryItems){
            if ((e.getX() == x) && (e.getY() == y)){
                return e;
            }
        }
        return null;
    }

    /**
     * remove item at a particular index in the unequipped inventory items list (this is ordered based on age in the starter code)
     * @param index index from 0 to length-1
     */
    private void removeItemByPositionInUnequippedInventoryItems(int index){
        Item item = unequippedInventoryItems.get(index);
        item.destroy();
        unequippedInventoryItems.remove(index);
    }

    /**
     * get the first pair of x,y coordinates which don't have any items in it in the unequipped inventory
     * @return x,y coordinate pair
     */
    private Pair<Integer, Integer> getFirstAvailableSlotForItem(){
        // first available slot for an item...
        // IMPORTANT - have to check by y then x, since trying to find first available slot defined by looking row by row
        for (int y=0; y<unequippedInventoryHeight; y++){
            for (int x=0; x<unequippedInventoryWidth; x++){
                if (getUnequippedInventoryItemEntityByCoordinates(x, y) == null){
                    return new Pair<Integer, Integer>(x, y);
                }
            }
        }
        return null;
    }

    /**
     * shift card coordinates down starting from x coordinate
     * @param x x coordinate which can range from 0 to width-1
     */
    private void shiftCardsDownFromXCoordinate(int x){
        for (Card c: cardEntities){
            if (c.getX() >= x){
                c.x().set(c.getX()-1);
            }
        }
    }

    /**
     * move all enemies
     */
    private void moveBasicEnemies() {
        for (BasicEnemy e: enemies){
            // Changes the direction of movement of the vampires first
            if(e instanceof Vampire) {
                for(Building b : buildingEntities) {
                    if(b instanceof CampfireBuilding) {
                        if(Math.pow((e.getX()-b.getX()), 2) + Math.pow((e.getY()-b.getY()), 2) <= Math.pow(((CampfireBuilding) b).getBuffRadius(), 2)) {
                            int downPos = (e.getPositionInPath() + 1)%orderedPath.size();
                            int upPos = (e.getPositionInPath() - 1 + orderedPath.size())%orderedPath.size();

                            int xDown = orderedPath.get(downPos).getValue0();
                            int yDown = orderedPath.get(downPos).getValue1();
                            int xUp = orderedPath.get(upPos).getValue0();
                            int yUp = orderedPath.get(upPos).getValue1();

                            if(Math.pow(xDown - b.getX(), 2) + Math.pow(yDown - b.getY(), 2) > Math.pow(((CampfireBuilding) b).getBuffRadius(), 2) && 
                                ((Vampire) e).getDirection() == false) {
                                    ((Vampire) e).changeDirection();
                            } else if (Math.pow(xUp - b.getX(), 2) + Math.pow(yUp - b.getY(), 2) > Math.pow(((CampfireBuilding) b).getBuffRadius(), 2) && 
                                        ((Vampire) e).getDirection() == true) {
                                            ((Vampire) e).changeDirection();
                            }
                            break;
                        }
                    }
                }
            }
            e.move();
        }
    }

    /**
     * get a randomly generated position which could be used to spawn an enemy
     * @return null if random choice is that wont be spawning an enemy or it isn't possible, or random coordinate pair if should go ahead
     */
    private Pair<Integer, Integer> possiblyGetBasicEnemySpawnPosition(){
        // has a chance spawning a basic enemy on a tile the character isn't on or immediately before or after (currently space required = 2)...
        Random rand = new Random();
        int choice = rand.nextInt(11);
        if ((choice == 10) && (enemies.size() < 2)){
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
            // inclusive start and exclusive end of range of positions not allowed
            int startNotAllowed = (indexPosition - 2 + orderedPath.size())%orderedPath.size();
            int endNotAllowed = (indexPosition + 3)%orderedPath.size();
            // note terminating condition has to be != rather than < since wrap around...
            for (int i=endNotAllowed; i!=startNotAllowed; i=(i+1)%orderedPath.size()){
                orderedPathSpawnCandidates.add(orderedPath.get(i));
            }

            // Can't spawn on buildings
            for(Building b: buildingEntities) {
                Pair<Integer, Integer> buildingPos = new Pair<Integer, Integer>(b.getX(), b.getY());
                orderedPathSpawnCandidates.remove(buildingPos);
            }

            // choose random choice
            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates.get(rand.nextInt(orderedPathSpawnCandidates.size()));

            return spawnPosition;
        }
        return null;
    }

    /**
     * get a randomly generated position which can be used to spawn a Boss
     * @return random position on the path
     */
    private PathPosition getBossSpawnPosition() {
        var rand = new Random();
        int pos = rand.nextInt(orderedPath.size());

        return new PathPosition(pos, orderedPath);
    }

    /**
     * Grabs potential spawning coordinates for the gold
     * @return list of spawn positions for the gold to be spawned
     */
    private Pair<Integer, Integer> possibleGetItemSpawnPosition() {
        Random rand = new Random();
        int choice = rand.nextInt(50);
        if(choice == 19) {
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
            int startNotAllowed = (indexPosition - 2 + orderedPath.size())%orderedPath.size();
            int endNotAllowed = (indexPosition + 3)%orderedPath.size();
            for (int i=endNotAllowed; i!=startNotAllowed; i=(i+1)%orderedPath.size()){
                orderedPathSpawnCandidates.add(orderedPath.get(i));
            }

            // Can't spawn on buildings
            for(Building b: buildingEntities) {
                Pair<Integer, Integer> buildingPos = new Pair<Integer, Integer>(b.getX(), b.getY());
                orderedPathSpawnCandidates.remove(buildingPos);
            }

            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates.get(rand.nextInt(orderedPathSpawnCandidates.size()));

            return spawnPosition;
        }
        return null;
    }
    
    /**
     * spawns gold if the conditions warrant it, adds to world
     * @return list of the gold to be displayed on screen
     */
    public List<GoldSpawn> possiblySpawnGold(){
        Pair<Integer, Integer> pos = possibleGetItemSpawnPosition();
        List<GoldSpawn> spawningGold = new ArrayList<>();
        if (pos != null){
            GoldSpawn gold = new GoldSpawn(new SimpleIntegerProperty(pos.getValue0()), new SimpleIntegerProperty(pos.getValue1()));
            goldOnMap.add(gold);
            spawningGold.add(gold);
        }
        return spawningGold;
    }

    /**
     * Character picks up gold and remove it from map
     */
    public void pickUpGold() {
        List<GoldSpawn> removeGold = new ArrayList<>();
        for(GoldSpawn gold : goldOnMap) {
            if(gold.getX() == character.getX() && gold.getY() == character.getY()) {
                setGold(getGold() + 10);
                removeGold.add(gold);
            }
        }
        for(GoldSpawn gold : removeGold) {
            goldOnMap.remove(gold);
            gold.destroy();
        }
    }

    public List<GoldSpawn> getGoldOnMap() {
        return goldOnMap;
    }

    /**
     * Spawns potion on the world map
     * @return the list of spawned potions
     */
    public List<HealthPotion> possiblySpawnPotion() {
        Pair<Integer, Integer> pos = possibleGetItemSpawnPosition();
        List<HealthPotion> spawningPotion = new ArrayList<>();
        if (pos != null){
            HealthPotion healthPotion = new HealthPotion(new SimpleIntegerProperty(pos.getValue0()), new SimpleIntegerProperty(pos.getValue1()));
            healthPotionOnMap.add(healthPotion);
            spawningPotion.add(healthPotion);
        }
        return spawningPotion;
    }

    /**
     * Picks up the spawned potion on the map when character walks over it
     */
    public Item pickUpPotion() {
        List<HealthPotion> removePotion = new ArrayList<>();
        Item item = null;
        for(HealthPotion p : healthPotionOnMap) {
            if(p.getX() == character.getX() && p.getY() == character.getY()) {
                item = addUnequippedItem(ItemType.HEALTH_POTION);
                removePotion.add(p);
            }
        }
        for(HealthPotion p : removePotion) {
            healthPotionOnMap.remove(p);
            p.destroy();
        }
        return item;
    }
    
    public List<HealthPotion> getPotionOnMap() {
        return healthPotionOnMap;
    }

    /**
     * Spawn either a gold or potion with 50% chance
     * @return a pair of a list of potions and gold spawned
     */
    public Pair<List<HealthPotion>, List<GoldSpawn>> possiblySpawnItem() {
        Pair<List<HealthPotion>, List<GoldSpawn>> goldOrPotion = new Pair<>(new ArrayList<>(), new ArrayList<>());
        Random rand = new Random();
        int choice = rand.nextInt(2);
        if(choice == 0) {
            List<HealthPotion> potion = possiblySpawnPotion();
            goldOrPotion.getValue0().addAll(potion);
        } else {
            List<GoldSpawn> gold = possiblySpawnGold();
            goldOrPotion.getValue1().addAll(gold);
        }
        return goldOrPotion;
    }

    
    /**
     * For confusing mode only. Adds the property of another random rare item 
     */
    private void addExtendedProperties(Item item) {
        if (item instanceof TheOneRing) {
            TheOneRing theOneRing = (TheOneRing)item;
            theOneRing.extendProperty(character);
        } else if (item instanceof Anduril) {
            Anduril anduril = (Anduril)item;
            anduril.extendProperty(character);
        }  else if (item instanceof TreeStump) {
            TreeStump treeStump = (TreeStump)item;
            treeStump.extendProperty(character);
        } 
    }

    /**
     * remove a card by its x, y coordinates
     * @param cardNodeX x index from 0 to width-1 of card to be removed
     * @param cardNodeY y index from 0 to height-1 of card to be removed
     * @param buildingNodeX x index from 0 to width-1 of building to be added
     * @param buildingNodeY y index from 0 to height-1 of building to be added
     */
    public Building convertCardToBuildingByCoordinates(int cardNodeX, int cardNodeY, int buildingNodeX, int buildingNodeY) {
        // start by getting card
        Card card = null;
        for (Card c: cardEntities){
            if ((c.getX() == cardNodeX) && (c.getY() == cardNodeY)){
                card = c;
                break;
            }
        }

        // Check if coordinates are already taken by another building
        boolean alrTaken = false;
        for (Building b : buildingEntities) {
            if(b.getX() == buildingNodeX && b.getY() == buildingNodeY) {
                alrTaken = true;
            }
        }

        // now spawn building
        Building newBuilding = null;
        if(!alrTaken) {
            Pair<Integer, Integer> pos = new Pair<Integer, Integer>(buildingNodeX, buildingNodeY);
            boolean checkAdjacent = checkIfAdjacentPathTile(buildingNodeX, buildingNodeY);
            if(card instanceof VampireCastleCard) {
                if(!orderedPath.contains(pos) && checkAdjacent) {
                    newBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof ZombiePitCard) {
                if(!orderedPath.contains(pos) && checkAdjacent) {
                    newBuilding = new ZombiePitBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof BarracksCard) {
                if(orderedPath.contains(pos)) {
                    newBuilding = new BarracksBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof TowerCard) {
                if(!orderedPath.contains(pos) && checkAdjacent) {
                    newBuilding = new TowerBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof VillageCard) {
                if(orderedPath.contains(pos)) {
                    newBuilding = new VillageBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof CampfireCard) {
                if(!orderedPath.contains(pos)) {
                    newBuilding = new CampfireBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            } else if(card instanceof TrapCard) {
                if(orderedPath.contains(pos)) {
                    newBuilding = new TrapBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                }
            }
        }

        if(newBuilding != null) {
            buildingEntities.add(newBuilding);

            // destroy the card
            card.destroy();
            cardEntities.remove(card);
            shiftCardsDownFromXCoordinate(cardNodeX);
        } else {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                System.out.println("Cannot place building there");
            }
        }

        return newBuilding;
    }

    public void addCycle() {
        cycle++;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public boolean isDoggieDefeated() {
        return doggieDefeated;
    }

    public void setDoggieDefeated(boolean doggieDefeated) {
        this.doggieDefeated = doggieDefeated;
    }

    public boolean isElanDefeated() {
        return elanDefeated;
    }

    public void setElanDefeated(boolean elanDefeated) {
        this.elanDefeated = elanDefeated;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public List<Building> getBuildings() {
        return buildingEntities;
    }

    public List<BasicEnemy> getEnemy() {
        return enemies;
    }

    public List<Card> getCards() {
        return cardEntities;
    }

    /**
     * Spawns zombie and vampires on the map from their respective buildings
     * @return list of spawned enemies that were spawned from either a zombie pit or vampire castle
     */
    public List<BasicEnemy> spawnEnemies() {
        List<BasicEnemy> spawnedEnemies = new ArrayList<>();
        for(Building b : buildingEntities) {
            if(b instanceof VampireCastleBuilding || b instanceof ZombiePitBuilding) {
                BasicEnemy temp = b.spawn(enemies, orderedPath, cycle);
                if(temp != null) {
                    spawnedEnemies.add(temp);
                }
            }
        }

        if (cycle % 20 == 0 && !doggieDefeated){
            BasicEnemy newDoggie = new Doggie(getBossSpawnPosition());
            enemies.add(newDoggie);
            spawnedEnemies.add(newDoggie);
        }

        if (cycle % 40 == 0 && !elanDefeated && exp >= 10000){
            BasicEnemy newElan = new Elan(getBossSpawnPosition());
            enemies.add(newElan);
            spawnedEnemies.add(newElan);
        }

        return spawnedEnemies;
    }

    /**
     * Spawns an ally when the hero passes over the barracks
     * @return the ally to be spawned
     */
    public Ally spawnAllies() {
        Ally newAlly = null;
        for(Building b : buildingEntities) {
            if(b instanceof BarracksBuilding) {
                newAlly = ((BarracksBuilding) b).spawn(character);
            }
        }

        return newAlly;
    }

    /**
     * Applies all the buffs to the character if within the radius/tile of buildings that give buffs
     */
    public void buffCharacter() {
        for(Building b : buildingEntities) {
            if(b instanceof CampfireBuilding || b instanceof VillageBuilding) {
                b.buff(character);
            }
        }
    }

    /**
     * Trap building damages the enemies and removes them from the game map
     */
    public void damageEnemy() {
        List<Building> buildingsToRemove = new ArrayList<>();
        for(Building b : buildingEntities) {
            if(b instanceof TrapBuilding) {
                Pair<BasicEnemy, Boolean> enemy = b.damage(enemies, buildingEntities);
                if(enemy.getValue0() != null) {
                    buildingsToRemove.add(b);
                }
                if(enemy.getValue1() == true) {
                    setGold(getGold() + enemy.getValue0().getGold());
                    setExp(getExp() + enemy.getValue0().getXp());
                }
            }
        }
        for(Building b : buildingsToRemove) {
            b.destroy();
            buildingEntities.remove(b);
        }
    }

    /**
     * Checks if there is a spawn position for the zombie/vampire and decides given by the implementation
     * @param x x coordinate of the building
     * @param y y coordinate of the building
     * @return true if there is a spawn position, false otherwise
     */
    public boolean checkIfAdjacentPathTile(int x, int y) {
        Pair<Integer, Integer> posLeft = new Pair<Integer, Integer>(x - 1, y);
        Pair<Integer, Integer> posRight = new Pair<Integer, Integer>(x + 1, y);
        Pair<Integer, Integer> posUp  = new Pair<Integer, Integer>(x, y + 1);
        Pair<Integer, Integer> posDown = new Pair<Integer, Integer>(x, y - 1);
        Pair<Integer, Integer> posLeftUp = new Pair<Integer, Integer>(x - 1, y + 1);
        Pair<Integer, Integer> posRightUp = new Pair<Integer, Integer>(x + 1, y + 1);
        Pair<Integer, Integer> posLeftDown = new Pair<Integer, Integer>(x - 1, y - 1);
        Pair<Integer, Integer> posRightDown = new Pair<Integer, Integer>(x + 1, y - 1);

        if(orderedPath.contains(posLeft) || orderedPath.contains(posRight) || orderedPath.contains(posUp) || orderedPath.contains(posDown) ||
            orderedPath.contains(posLeftUp) || orderedPath.contains(posRightUp) || orderedPath.contains(posLeftDown) || orderedPath.contains(posRightDown)) {
                return true;
            } else {
                return false;
            }

    }

    public int getCharacterX() {
        return character.getX();
    }

    public int getCharacterY() {
        return character.getY();
    }

    public int getHerosCastleX() {
        return castle.getX();
    }

    public HerosCastle getHerosCastle() {
        return castle;
    }

    public int getHerosCastleY() {
        return castle.getY();
    }

    public int getNumberOfAllies() {
        return character.getAllies().size();
    }

    public List<Item> getUnequippedInventoryItems() {
        return unequippedInventoryItems;
    }
    
    public void fluctuateDoggieCoin() {
        for(Item item: unequippedInventoryItems)
        {
            if(item instanceof DoggieCoin)
            {
                Random rand = new Random(System.currentTimeMillis());
                item.setValue(rand.nextInt(1000));
            }
        }
    }

    /**
     * Creates a random item based on rng
     * @return
     */
    public Item createRandomWeaponWithRare() {
        Random rand = new Random();
        int int_random = rand.nextInt(100);
        
        // If it passes the 1% chance spawn rare item
        if(int_random == 0)
        {
            int_random = rand.nextInt(3);
            switch(int_random){
                case 0:
                    return addUnequippedItem(ItemType.THE_ONE_RING);
                case 1:
                    return addUnequippedItem(ItemType.ANDURIL);
                case 2:
                    return addUnequippedItem(ItemType.TREE_STUMP);
            }
        }
        
        // No reward given if number is bigger than 10
        if (int_random >= 10)          
            return null;
        // Passes 10% chance
        else
        {
            return createRandomWeapon();
        }
    }

    
    /**
     * Creates a random item based on rng
     * @return
     */
    public Item createRandomWeapon() {
        Random rand = new Random();
            int int_random = rand.nextInt(7);
            switch(int_random){
                case 0:
                    return addUnequippedItem(ItemType.SWORD);
                case 1:
                    return addUnequippedItem(ItemType.STAKE);
                case 2:
                    return addUnequippedItem(ItemType.STAFF);
                case 3:
                    return addUnequippedItem(ItemType.ARMOUR);
                case 4:
                    return addUnequippedItem(ItemType.SHIELD);
                case 5:
                    return addUnequippedItem(ItemType.HELMET);
                case 6:
                    return addUnequippedItem(ItemType.HEALTH_POTION);
            }
        return null;
    }

    /**
     * Returns a card at equal chance
     * @return a pair of a card and an item
     */
    public Pair<Card, Item> randomCard() {
        Pair<Card, Item> cardItemPair = null;
        Random rand = new Random();
        int seed = rand.nextInt(7);
        if(seed == 0) {
            cardItemPair = loadCard(VampireCastleCard.class);
        } else if(seed == 1) {
            cardItemPair = loadCard(ZombiePitCard.class);
        } else if(seed == 2) {
            cardItemPair = loadCard(BarracksCard.class);
        } else if(seed == 3) {
            cardItemPair = loadCard(CampfireCard.class);
        } else if(seed == 4) {
            cardItemPair = loadCard(TowerCard.class);  
        } else if(seed == 5) {
            cardItemPair = loadCard(TrapCard.class);
        } else {
            cardItemPair = loadCard(VillageCard.class);
        }
        return cardItemPair;
    }

    /**
     * Restarts the game, resets all the data in the game world
     */
    public void restartGame() {
        character.setHealth(200);
        character.setMaxHealth(200);
        character.unsetBlocking();
        character.setDefense(0);
        character.setSpeed(8);
        character.setAttack(5);
        character.unsetRevive();
        character.setDoubleDamage(false);
        // Clear Allies
        for(Ally a : character.getAllies()) {
            a.destroy();
        }
        character.getAllies().clear();

        // Clear Equipped Items
        Map<Slot, Item> equipped = character.getMap();
        for(Map.Entry<Slot, Item> entry : equipped.entrySet()) {
            if(entry.getValue() != null) {
                entry.getValue().destroy();
            }
        }
        equipped.clear();

        // Clear buildings
        List<Building> removeBuildings = new ArrayList<>();
        for(Building b : buildingEntities) {
            if(!(b instanceof HerosCastle)) {
                b.destroy();
                removeBuildings.add(b);
            }
        }
        for(Building b : removeBuildings) {
            buildingEntities.remove(b);
        }

        // Clear Inventory
        for(Item i : unequippedInventoryItems) {
            i.destroy();
        }
        unequippedInventoryItems.clear();
        for(Card c : cardEntities) {
            c.destroy();
        }
        cardEntities.clear();

        // Clear all enemies
        for(BasicEnemy e : enemies) {
            e.destroy();
        }
        enemies.clear();

        // Clear all spawned potions
        for(HealthPotion p : healthPotionOnMap) {
            p.destroy();
        }
        healthPotionOnMap.clear();

        // Clear all spawned gold on map
        for(GoldSpawn g : goldOnMap) {
            g.destroy();
        }
        goldOnMap.clear();
        setGold(0);
        setExp(0);
        setCycle(0);
    }
}
