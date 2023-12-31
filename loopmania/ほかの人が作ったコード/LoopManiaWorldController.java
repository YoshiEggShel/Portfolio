package unsw.loopmania;

import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;

import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.listener.handle.ListenerHandles;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Slider;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.util.EnumMap;

import java.io.File;
import java.io.IOException;

import unsw.loopmania.ItemFactory.ItemType;
import unsw.loopmania.GameMode.Mode;
/**
 * the draggable types.
 * If you add more draggable types, add an enum value here.
 * This is so we can see what type is being dragged.
 */
enum DRAGGABLE_TYPE{
    CARD,
    ITEM
}

/**
 * A JavaFX controller for the world.
 * 
 * All event handlers and the timeline in JavaFX run on the JavaFX application thread:
 *     https://examples.javacodegeeks.com/desktop-java/javafx/javafx-concurrency-example/
 *     Note in https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Application.html under heading "Threading", it specifies animation timelines are run in the application thread.
 * This means that the starter code does not need locks (mutexes) for resources shared between the timeline KeyFrame, and all of the  event handlers (including between different event handlers).
 * This will make the game easier for you to implement. However, if you add time-consuming processes to this, the game may lag or become choppy.
 * 
 * If you need to implement time-consuming processes, we recommend:
 *     using Task https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Task.html by itself or within a Service https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Service.html
 * 
 *     Tasks ensure that any changes to public properties, change notifications for errors or cancellation, event handlers, and states occur on the JavaFX Application thread,
 *         so is a better alternative to using a basic Java Thread: https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm
 *     The Service class is used for executing/reusing tasks. You can run tasks without Service, however, if you don't need to reuse it.
 *
 * If you implement time-consuming processes in a Task or thread, you may need to implement locks on resources shared with the application thread (i.e. Timeline KeyFrame and drag Event handlers).
 * You can check whether code is running on the JavaFX application thread by running the helper method printThreadingNotes in this class.
 * 
 * NOTE: http://tutorials.jenkov.com/javafx/concurrency.html and https://www.developer.com/design/multithreading-in-javafx/#:~:text=JavaFX%20has%20a%20unique%20set,in%20the%20JavaFX%20Application%20Thread.
 * 
 * If you need to delay some code but it is not long-running, consider using Platform.runLater https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)
 *     This is run on the JavaFX application thread when it has enough time.
 */
public class LoopManiaWorldController {

    /**
     * squares gridpane includes path images, enemies, character, empty grass, buildings
     */
    @FXML
    private GridPane squares;

    /**
     * cards gridpane includes cards and the ground underneath the cards
     */
    @FXML
    private GridPane cards;

    /**
     * anchorPaneRoot is the "background". It is useful since anchorPaneRoot stretches over the entire game world,
     * so we can detect dragging of cards/items over this and accordingly update DragIcon coordinates
     */
    @FXML
    private AnchorPane anchorPaneRoot;

    /**
     * equippedItems gridpane is for equipped items (e.g. swords, shield, axe)
     */
    @FXML
    private GridPane equippedItems;

    @FXML
    private GridPane unequippedInventory;

    // all image views including tiles, character, enemies, cards... even though cards in separate gridpane...
    private List<ImageView> entityImages;

    /**
     * when we drag a card/item, the picture for whatever we're dragging is set here and we actually drag this node
     */
    private DragIcon draggedEntity;

    @FXML
    private GridPane stats;

    @FXML
    private Rectangle healthBar;

    @FXML
    private Label goldValue;

    @FXML
    private Label xpValue;

    @FXML
    private Label cycle;

    @FXML
    private GridPane allies;

    @FXML
    private Pane pausePane;

    @FXML
    private Pane cardHelpPane;

    @FXML 
    private Slider volumeSlider;

    @FXML
    private Label currentGold;

    @FXML
    private Label goalGold;

    @FXML
    private Label currentXp;

    @FXML
    private Label goalXp;

    @FXML
    private Label currentCycle;

    @FXML
    private Label goalCycle;

    private boolean isPaused;
    
    private LoopManiaWorld world;

    private int cycleCounter = 1;

    private int increment = 2;

    /**
     * runs the periodic game logic - second-by-second moving of character through maze, as well as enemies, and running of battles
     */
    private Timeline timeline;

    //BUILDINGS//
    private Image vampireCastleCardImage;
    private Image vampireCastleImage;
    private Image towerCardImage;
    private Image towerImage;
    private Image trapCardImage;
    private Image trapImage;
    private Image zombiePitCardImage;
    private Image zombiePitImage;
    private Image villageCardImage;
    private Image villageImage;
    private Image barracksCardImage;
    private Image barracksImage;
    private Image campfireCardImage;
    private Image campfireImage;
    private Image slugImage;
    private Image vampireImage;
    private Image zombieImage;
    private Image doggieImage;
    private Image elanImage;
    private Image allyImage;
    //ITEM//
    private Image swordImage;
    private Image stakeImage;
    private Image staffImage;
    private Image armourImage;
    private Image shieldImage;
    private Image helmetImage;
    private Image healthPotionImage;
    private Image theOneRingImage;
    private Image doggieCoin;
    private Image anduril;
    private Image treeStump;
    private Image heartImage;
    private Image goldImage;
    //MUSIC//
    private Media soundtrack;
    private MediaPlayer soundtrackMP;
    private Media gameover;
    private MediaPlayer gameoverMP;
    private Media victory;
    private MediaPlayer victoryMP;

    /**
     * the image currently being dragged, if there is one, otherwise null.
     * Holding the ImageView being dragged allows us to spawn it again in the drop location if appropriate.
     */
    private ImageView currentlyDraggedImage;
    
    /**
     * null if nothing being dragged, or the type of item being dragged
     */
    private DRAGGABLE_TYPE currentlyDraggedType;

    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dropped over its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged over the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragOver;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dropped in the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged into the boundaries of its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragEntered;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged outside of the boundaries of its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragExited;

    /**
     * object handling switching to the main menu
     */
    private MenuSwitcher mainMenuSwitcher;

    private MenuSwitcher itemMenuSwitcher;

    private MenuSwitcher deathMenuSwitcher;

    private MenuSwitcher wonMenuSwitcher;

    /**
     * @param world world object loaded from file
     * @param initialEntities the initial JavaFX nodes (ImageViews) which should be loaded into the GUI
     */
    public LoopManiaWorldController(LoopManiaWorld world, List<ImageView> initialEntities) {
        this.world = world;
        entityImages = new ArrayList<>(initialEntities);
        vampireCastleCardImage = new Image((new File("src/images/vampire_castle_card.png")).toURI().toString());
        vampireCastleImage = new Image((new File("src/images/vampire_castle_building_transparent.png")).toURI().toString());
        towerCardImage = new Image((new File("src/images/tower_card.png")).toURI().toString());
        towerImage = new Image((new File("src/images/tower.png")).toURI().toString());
        trapCardImage = new Image((new File("src/images/trap_card.png")).toURI().toString());
        trapImage = new Image((new File("src/images/trap.png")).toURI().toString());
        villageCardImage = new Image((new File("src/images/village_card.png")).toURI().toString());
        villageImage = new Image((new File("src/images/village.png")).toURI().toString());
        zombiePitCardImage = new Image((new File("src/images/zombie_pit_card.png")).toURI().toString());
        zombiePitImage = new Image((new File("src/images/zombie_pit.png")).toURI().toString());
        barracksCardImage = new Image((new File("src/images/barracks_card.png")).toURI().toString());
        barracksImage = new Image((new File("src/images/barracks.png")).toURI().toString());
        campfireCardImage = new Image((new File("src/images/campfire_card.png")).toURI().toString());
        campfireImage = new Image((new File("src/images/campfire.png")).toURI().toString());
        slugImage = new Image((new File("src/images/slug.png")).toURI().toString());
        vampireImage = new Image((new File("src/images/vampire.png")).toURI().toString());
        zombieImage = new Image((new File("src/images/zombie.png")).toURI().toString());
        doggieImage = new Image((new File("src/images/doggie.png")).toURI().toString());
        elanImage = new Image((new File("src/images/ElanMuske.png")).toURI().toString());
        allyImage = new Image((new File("src/images/deep_elf_master_archer.png")).toURI().toString());
        //ITEM//
        swordImage = new Image((new File("src/images/basic_sword.png")).toURI().toString());
        stakeImage = new Image((new File("src/images/stake.png")).toURI().toString());
        staffImage = new Image((new File("src/images/staff.png")).toURI().toString());
        armourImage = new Image((new File("src/images/armour.png")).toURI().toString());
        shieldImage = new Image((new File("src/images/shield.png")).toURI().toString());
        helmetImage = new Image((new File("src/images/stake.png")).toURI().toString());
        healthPotionImage = new Image((new File("src/images/brilliant_blue_new.png")).toURI().toString());
        swordImage = new Image((new File("src/images/basic_sword.png")).toURI().toString());
        stakeImage = new Image((new File("src/images/stake.png")).toURI().toString());
        staffImage = new Image((new File("src/images/staff.png")).toURI().toString());
        armourImage = new Image((new File("src/images/armour.png")).toURI().toString());
        shieldImage = new Image((new File("src/images/shield.png")).toURI().toString());
        theOneRingImage = new Image((new File("src/images/the_one_ring.png")).toURI().toString());
        helmetImage = new Image((new File("src/images/helmet.png")).toURI().toString());
        goldImage = new Image((new File("src/images/gold_pile.png")).toURI().toString());
        doggieCoin = new Image((new File("src/images/doggieCoin.png")).toURI().toString());
        anduril = new Image((new File("src/images/anduril_flame_of_the_west.png")).toURI().toString());
        treeStump = new Image((new File("src/images/tree_stump.png")).toURI().toString());
        //ITEM//
        heartImage = new Image((new File("src/images/heart.png")).toURI().toString());
        currentlyDraggedImage = null;
        currentlyDraggedType = null;
        soundtrack = new Media((new File("src/music/dungeon.mp3")).toURI().toString());
        soundtrackMP = new MediaPlayer(soundtrack);
        gameover = new Media((new File("src/music/gameover.mp3")).toURI().toString());
        gameoverMP = new MediaPlayer(gameover);
        victory = new Media((new File("src/music/victory.mp3")).toURI().toString());
        victoryMP = new MediaPlayer(victory);

        // initialize them all...
        gridPaneSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragOver = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragEntered = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragExited = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
    }

    @FXML
    public void initialize() {
        
        Image pathTilesImage = new Image((new File("src/images/32x32GrassAndDirtPath.png")).toURI().toString());
        Image inventorySlotImage = new Image((new File("src/images/empty_slot.png")).toURI().toString());
        Rectangle2D imagePart = new Rectangle2D(0, 0, 32, 32);

        // Add the ground first so it is below all other entities (inculding all the twists and turns)
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                ImageView groundView = new ImageView(pathTilesImage);
                groundView.setViewport(imagePart);
                squares.add(groundView, x, y);
            }
        }


        // load entities loaded from the file in the loader into the squares gridpane
        for (ImageView entity : entityImages){
            squares.getChildren().add(entity);
        }
        
        // add the ground underneath the cards
        for (int x=0; x<world.getWidth(); x++){
            ImageView groundView = new ImageView(pathTilesImage);
            groundView.setViewport(imagePart);
            cards.add(groundView, x, 0);
        }

        // add the empty slot images for the unequipped inventory
        for (int x=0; x<LoopManiaWorld.unequippedInventoryWidth; x++){
            for (int y=0; y<LoopManiaWorld.unequippedInventoryHeight; y++){
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                unequippedInventory.add(emptySlotView, x, y);
            }
        }

        // add the empty slot images for the equipped inventory
        for (int x=0; x<LoopManiaWorld.equippedInventoryWidth; x++){
            for (int y=0; y<LoopManiaWorld.equippedInventoryHeight; y++){
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                equippedItems.add(emptySlotView, x, y);
            }
        }

        // Set up the character stats 
        ImageView heartImageView = new ImageView(heartImage);
        ImageView goldImageView = new ImageView(goldImage);
        stats.add(heartImageView, 1, 0);
        stats.add(goldImageView, 1, 1);

        // create the draggable icon
        draggedEntity = new DragIcon();
        draggedEntity.setVisible(false);
        draggedEntity.setOpacity(0.7);
        anchorPaneRoot.getChildren().add(draggedEntity);

        pausePane.setVisible(false);

        cardHelpPane.setStyle("-fx-background-color: white;");
        cardHelpPane.setVisible(false);

        // Loop the music (even though it's 12 min long)
        soundtrackMP.setOnEndOfMedia(new Runnable() {
            public void run() {
                soundtrackMP.seek(Duration.ZERO);
            }
        });
        // Initialise the soundtracks
        soundtrackMP.play();
        gameoverMP.stop();
        victoryMP.stop();

        // Volume Slider
        volumeSlider.setValue(soundtrackMP.getVolume()*100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                soundtrackMP.setVolume(volumeSlider.getValue()/100);
            }
        });
    }

    /**
     * create and run the timer
     */
    public void startTimer(){
        System.out.println("starting timer");
        isPaused = false;
        // trigger adding code to process main game logic to queue. JavaFX will target framerate of 0.3 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.3), event -> {
            world.runTickMoves();
            switchToMenu();
            loadGoldAndPotion();
            loadTrapDamage();
            world.buffCharacter();
            setHealth();
            showAllies();

            // Battle enemies
            List<BasicEnemy> defeatedEnemies = world.runBattles();

            if(world.getCharacter().getHealth() <= 0) {
                switchToDeathMenu();
            }
            world.GainBattleRewards(defeatedEnemies);
            for (BasicEnemy e: defeatedEnemies){
                reactToEnemyDefeat(e);
            }

            //If a goal has been set
            if (world.getGoal() != null){
                //Check if goals have been reached
                 if (world.getGoal().checkCompleted(world)){
                    //Game has been won
                    switchToWonMenu();
                }
            }    
            // Spawn gold or potions
            Pair<List<HealthPotion>, List<GoldSpawn>> goldOrPotion = world.possiblySpawnItem();

            // // Spawn Gold
            // List<GoldSpawn> newGold = world.possiblySpawnGold();
            for(GoldSpawn gold: goldOrPotion.getValue1()) {
                onLoad(gold);
            }

            // // Spawn Potions
            // List<HealthPotion> newPotion = world.possiblySpawnPotion();
            for(HealthPotion potion: goldOrPotion.getValue0()) {
                onLoad(potion);
            }

            // Spawn Enemies
            List<BasicEnemy> newEnemies = world.possiblySpawnEnemies();
            List<BasicEnemy> newZombiesVampires = new ArrayList<>();
            if(world.getCharacterX() == world.getHerosCastleX() && world.getCharacterY() == world.getHerosCastleY()) {
                setCycle();
                newZombiesVampires = world.spawnEnemies();
            }
            for (BasicEnemy newEnemy: newEnemies){
                onLoad(newEnemy);
            }
            for (BasicEnemy enemy : newZombiesVampires) {
                onLoad(enemy);
            }
            printThreadingNotes("HANDLED TIMER");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * pause the execution of the game loop
     * the human player can still drag and drop items during the game pause
     */
    public void pause(){
        isPaused = true;
        System.out.println("pausing");
        timeline.stop();
    }

    public void terminate(){
        pause();
    }

    public void setGameMode(Mode mode) {
        world.setGameMode(mode);
    }

    public Mode getGameMode() {
        return world.getGameMode();
    }

    /**
     * pair the entity an view so that the view copies the movements of the entity.
     * add view to list of entity images
     * @param entity backend entity to be paired with view
     * @param view frontend imageview to be paired with backend entity
     */
    private void addEntity(Entity entity, ImageView view) {
        
        trackPosition(entity, view);
        entityImages.add(view);
    }

    /**
     * load a card from the world, and pair it with an image in the GUI
     */
    private void loadCard() {
        Pair<Card, Item> card = world.randomCard();
        onLoad(card.getValue0());
        if(card.getValue1() != null) {
            Item item = card.getValue1();
            onLoad(item);
        }
    }

    /**
     * load an item from the world, and pair it with an image in the GUI
     */
    public void loadItem(ItemType itemType){
        // start by getting first available coordinates
        Item item;
        if(itemType == null)
        {
            item = world.createRandomWeaponWithRare();
            if(item == null)
                return;
        }
        else
            item = world.addUnequippedItem(itemType);
        onLoad(item);
    }
    
    /**
     * Load the picked up gold/potion into the display
     */
    public void loadGoldAndPotion() {
        world.pickUpGold();
        setGold();
        Item item = world.pickUpPotion();
        if(item != null) {
            onLoad(item);
        }
    }

    /**
     * Load the gold and exp from trap kills
     */
    public void loadTrapDamage() {
        world.damageEnemy();
        setGold();
        setXP();
    }
    
    /**
     * run GUI events after an enemy is defeated, such as spawning items/experience/gold
     * @param enemy defeated enemy for which we should react to the death of
     */
    private void reactToEnemyDefeat(BasicEnemy enemy){
        // react to character defeating an enemy
        // in starter code, spawning extra card/weapon...
        setXP();
        setGold();
        setHealth();
        if(enemy instanceof Doggie)
            loadItem(ItemType.DOGGIE_COIN);
        if(enemy instanceof Elan)
            fluctuateDoggieCoin();
        loadItem(null);
        loadCard();
    }

    public void potionTrigger() {
        if (world.usingPotion()) { 
            double newBarLevel = 100; 
            healthBar.setWidth(newBarLevel);
        }
    }

    public void minusGold(int gold) {
        Integer newGold = world.getGold() - gold;
        world.setGold(newGold);
        goldValue.setText(newGold.toString());
    }

    public void addGold(int gold) {
        Integer newGold = world.getGold() + gold;
        world.setGold(newGold);
        goldValue.setText(newGold.toString());
    }

    public void minusXp(int xp) {
        Integer newXp = world.getExp() - xp;
        world.setExp(newXp);
        xpValue.setText(newXp.toString());
    }

    public int getXp() {
        return world.getExp();
    }

    public void setHealth() {
        int newHealth = world.getCharacter().getHealth();
        int maxHealth = world.getCharacter().getMaxHealth();
        double ratio = maxHealth/100;
        healthBar.setWidth((double)newHealth/ratio);
    }

    public void setGold() {
        Integer newGold = world.getGold();
        goldValue.setText(newGold.toString());
    }

    public void setXP() {
        Integer newXP = world.getExp();
        xpValue.setText(newXP.toString());
    }

    public void setCycle() {
        Integer newCycle = world.getCycle();
        cycle.setText(newCycle.toString());
    }

    public void setGoalGold() {
        Integer currGold = world.getGold();
        currentGold.setText(currGold.toString());

        Integer goal = recurse(world.getGoal(), 0, "gold");
        goalGold.setText(goal.toString());
    }

    public void setGoalXp() {
        Integer currXp = world.getExp();
        currentXp.setText(currXp.toString());

        Integer goal = recurse(world.getGoal(), 0, "experience");
        goalXp.setText(goal.toString());
    }

    public void setGoalCycle() {
        Integer currCycle = world.getCycle();
        currentCycle.setText(currCycle.toString());

        Integer goal = recurse(world.getGoal(), 0, "cycle");
        goalCycle.setText(goal.toString());
    }

    /**
     * Recursive function to get the goals of experience, cycles and gold
     * @param goal the goal
     * @param amount the amount of the goal
     * @param type type of the goal
     * @return the amount of the goal
     */
    public int recurse(Goal goal, int amount, String type) {
        if(goal instanceof GoalSINGLE) {
           if(((GoalSINGLE) goal).getType().equals(type)) {
               amount = ((GoalSINGLE) goal).getAmount();
               return amount;
           }
        } else {
            if(goal instanceof GoalAND) {
                if(recurse(((GoalAND) goal).getG1(), amount, type) == 0) {
                    return recurse(((GoalAND) goal).getG2(), amount, type);
                } else {
                    return recurse(((GoalAND) goal).getG1(), amount, type);
                }
            } else {
                if(recurse(((GoalOR) goal).getG1(), amount, type) == 0) {
                    return recurse(((GoalOR) goal).getG2(), amount, type);
                } else {
                    return recurse(((GoalOR) goal).getG1(), amount, type);
                }
            }
        }
        return 0;
    }

    public int getGold() {
        return world.getGold();
    }

    public String getGoldString() {
        Integer tempGold = world.getGold();
        return tempGold.toString();
    }

    public String getXpString() {
        Integer tempXp = world.getExp();
        return tempXp.toString();
    }

    public String getCycleString() {
        Integer tempCycle = world.getCycle();
        return tempCycle.toString();
    }

    public String getAttackString() {
        Character character = world.getCharacter();
        Integer tempAttack = character.getAttack();
        return tempAttack.toString();
    }

    public String getDefenseString() {
        Character character = world.getCharacter();
        Integer tempDefense = character.getDefense();
        return tempDefense.toString();
    }

    public String getSpeedString() {
        Character character = world.getCharacter();
        Integer tempSpeed = character.getSpeed();
        return tempSpeed.toString();
    }

    public String getCurrentHealthString() {
        Character character = world.getCharacter();
        Integer tempCurrHealth = character.getHealth();
        return tempCurrHealth.toString();
    }

    public String getMaxHealthString() {
        Character character = world.getCharacter();
        Integer tempMaxHealth = character.getMaxHealth();
        return tempMaxHealth.toString();
    }

    public GridPane getUneqippedInventory() {
        return unequippedInventory;
    }

    public List<Item> getWorldUnequippedInventory() {
        return world.getUnequippedInventoryItems();
    }

    public Character getCharacter() {
        return world.getCharacter();
    }
    /**
     * load a vampire castle card into the GUI.
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the cards GridPane.
     * @param vampireCastleCard
     */
    private void onLoad(Card card) {
        ImageView view = null;
        if(card instanceof VampireCastleCard) {
            view = new ImageView(vampireCastleCardImage);
            addEntity(((VampireCastleCard) card), view);
        } else if(card instanceof ZombiePitCard) {
            view = new ImageView(zombiePitCardImage);
            addEntity(((ZombiePitCard) card), view);
        } else if(card instanceof BarracksCard) {
            view = new ImageView(barracksCardImage);
            addEntity(((BarracksCard) card), view);
        } else if(card instanceof VillageCard) {
            view = new ImageView(villageCardImage);
            addEntity(((VillageCard) card), view);
        } else if(card instanceof CampfireCard) {
            view = new ImageView(campfireCardImage);
            addEntity(((CampfireCard) card), view);
        } else if(card instanceof TrapCard) {
            view = new ImageView(trapCardImage);
            addEntity(((TrapCard) card), view);
        } else {
            view = new ImageView(towerCardImage);
            addEntity(((TowerCard) card), view);
        }
        if(view != null) {
            addDragEventHandlers(view, DRAGGABLE_TYPE.CARD, cards, squares);
            cards.getChildren().add(view);
        }
    }

    /**
     * load an item into the GUI.
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the unequippedInventory GridPane.
     * @param sword
     */
    private void onLoad(Item item){
        String itemName = item.getName();
        ImageView view = null;
        switch(itemName){
            case "Sword":
                 view = new ImageView(swordImage);
                 break;
            case "Stake":
                view = new ImageView(stakeImage);
                break;
            case "Staff":
                view = new ImageView(staffImage);
                break;
            case "Armour":
                view = new ImageView(armourImage);
                break;
            case "Shield":
                view = new ImageView(shieldImage);
                break;
            case "Helmet":
                view = new ImageView(helmetImage);
                break;
            case "Health Potion":
                view = new ImageView(healthPotionImage);
                break;
            case "Doggie Coin":
                view = new ImageView(doggieCoin);
                break;
            case "The One Ring":
                view = new ImageView(theOneRingImage);
                break;
            case "Anduril, Flame of the West":
                view = new ImageView(anduril);
                break;
            case "Tree Stump":
                view = new ImageView(treeStump);
                break;
            default:
                view = null;
                break;
        }
        if(view != null) {
            addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, unequippedInventory, equippedItems);
            addEntity(item, view);
            unequippedInventory.getChildren().add(view);
        }
    }

    /**
     * Loads gold onto path tiles
     * @param gold the gold to be loaded
     */
    private void onLoad(GoldSpawn gold) {
        ImageView view = new ImageView(goldImage);
        addEntity(gold, view);
        squares.getChildren().add(view);
    }

    /**
     * Loads the potion onto path tiles
     * @param potion potion to be loaded
     */
    private void onLoad(HealthPotion potion) {
        ImageView view = new ImageView(healthPotionImage);
        addEntity(potion, view);
        squares.getChildren().add(view);
    }
      
    /**
     * load an enemy into the GUI
     * @param enemy
     */
    private void onLoad(BasicEnemy enemy) {
        ImageView view = null;
        if(enemy instanceof Slug) {
            view = new ImageView(slugImage);
            addEntity(((Slug) enemy), view);
        } else if(enemy instanceof Vampire) {
            view = new ImageView(vampireImage);
            addEntity(((Vampire) enemy), view);
        } else if(enemy instanceof Zombie) {
            view = new ImageView(zombieImage);
            addEntity(((Zombie) enemy), view);
        } else if (enemy instanceof Doggie){
            view = new ImageView(doggieImage);
            addEntity(((Doggie) enemy), view);
        } else {
            view = new ImageView(elanImage);
            addEntity(((Elan) enemy), view);
        }
        squares.getChildren().add(view);
    }

    /**
     * load a building into the GUI
     * @param building
     */
    private void onLoad(Building building) {
        ImageView view = null;
        if(building instanceof VampireCastleBuilding) {
            view = new ImageView(vampireCastleImage);
            addEntity(((VampireCastleBuilding) building), view);
        } else if(building instanceof ZombiePitBuilding) {
            view = new ImageView(zombiePitImage);
            addEntity(((ZombiePitBuilding) building), view);
        } else if(building instanceof TowerBuilding) {
            view = new ImageView(towerImage);
            addEntity(((TowerBuilding) building), view);
        } else if(building instanceof VillageBuilding) {
            view = new ImageView(villageImage);
            addEntity(((VillageBuilding) building), view);
        } else if(building instanceof BarracksBuilding) {
            view = new ImageView(barracksImage);
            addEntity(((BarracksBuilding) building), view);
        } else if(building instanceof TrapBuilding) {
            view = new ImageView(trapImage);
            addEntity(((TrapBuilding) building), view);
        } else if(building instanceof CampfireBuilding) {
            view = new ImageView(campfireImage);
            addEntity(((CampfireBuilding) building), view);
        }
        if(view != null) {
            squares.getChildren().add(view);
        }
    }

    /**
     * add drag event handlers for dropping into gridpanes, dragging over the background, dropping over the background.
     * These are not attached to invidual items such as swords/cards.
     * @param draggableType the type being dragged - card or item
     * @param sourceGridPane the gridpane being dragged from
     * @param targetGridPane the gridpane the human player should be dragging to (but we of course cannot guarantee they will do so)
     */
    private void buildNonEntityDragHandlers(DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        // for example, in the specification, villages can only be dropped on path, whilst vampire castles cannot go on the path
        gridPaneSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /*
                 *you might want to design the application so dropping at an invalid location drops at the most recent valid location hovered over,
                 * or simply allow the card/item to return to its slot (the latter is easier, as you won't have to store the last valid drop location!)
                 */
                Building newBuilding = null;
                Item item = null;
                if (currentlyDraggedType == draggableType){
                    // problem = event is drop completed is false when should be true...
                    // https://bugs.openjdk.java.net/browse/JDK-8117019
                    // putting drop completed at start not making complete on VLAB...

                    //Data dropped
                    //If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if(node != targetGridPane && db.hasImage()){
                        Integer cIndex = GridPane.getColumnIndex(node);
                        Integer rIndex = GridPane.getRowIndex(node);
                        int x = cIndex == null ? 0 : cIndex; // Drop location
                        int y = rIndex == null ? 0 : rIndex;
                        //Places at 0,0 - will need to take coordinates once that is implemented
                        ImageView view = new ImageView(db.getImage());

                        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);

                        switch (draggableType){
                            case CARD:
                                newBuilding = convertCardToBuildingByCoordinates(nodeX, nodeY, x, y);
                                if(newBuilding != null) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    onLoad(newBuilding);
                                }
                                break;
                            case ITEM: 
                                removeDraggableDragEventHandlers(draggableType, targetGridPane);                      
                                // Equip item
                                if (sourceGridPane.equals(unequippedInventory) && targetGridPane.equals(equippedItems)) {
                                    item = world.moveFromUnequippedToEquipped(nodeX, nodeY, x, y);
                                    addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, equippedItems, unequippedInventory);
                                    addEntity(item, view);
                                    equippedItems.add(view, item.getX(), item.getY());
                                }
                                // Dequip item
                                else if (sourceGridPane.equals(equippedItems) && targetGridPane.equals(unequippedInventory)) {
                                    item = world.moveFromEquippedToUnequipped(nodeX, nodeY, x, y);
                                    addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, unequippedInventory, equippedItems);
                                    addEntity(item, view);
                                    unequippedInventory.add(view, item.getX(), item.getY());
                                }
                                break;
                            default:
                                break;
                        }
                        if(newBuilding != null || item != null) {
                            draggedEntity.setVisible(false);
                            draggedEntity.setMouseTransparent(false);
                            // remove drag event handlers before setting currently dragged image to null
                            currentlyDraggedImage = null;
                            currentlyDraggedType = null;
                        }
                        printThreadingNotes("DRAG DROPPED ON GRIDPANE HANDLED");
                    }
                }
                if(newBuilding != null || item != null) {
                    event.setDropCompleted(true);
                // consuming prevents the propagation of the event to the anchorPaneRoot (as a sub-node of anchorPaneRoot, GridPane is prioritized)
                // https://openjfx.io/javadoc/11/javafx.base/javafx/event/Event.html#consume()
                // to understand this in full detail, ask your tutor or read https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm
                    event.consume();
                }
            }
        });

        // this doesn't fire when we drag over GridPane because in the event handler for dragging over GridPanes, we consume the event
        anchorPaneRootSetOnDragOver.put(draggableType, new EventHandler<DragEvent>(){
            // https://github.com/joelgraff/java_fx_node_link_demo/blob/master/Draggable_Node/DraggableNodeDemo/src/application/RootLayout.java#L110
            @Override
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    if(event.getGestureSource() != anchorPaneRoot && event.getDragboard().hasImage()){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                if (currentlyDraggedType != null){
                    draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                event.consume();
            }
        });

        // this doesn't fire when we drop over GridPane because in the event handler for dropping over GridPanes, we consume the event
        anchorPaneRootSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    //Data dropped
                    //If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if(node != anchorPaneRoot && db.hasImage()){
                        //Places at 0,0 - will need to take coordinates once that is implemented
                        currentlyDraggedImage.setVisible(true);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        // remove drag event handlers before setting currently dragged image to null
                        removeDraggableDragEventHandlers(draggableType, targetGridPane);
                        
                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;
                    }
                }
                //let the source know whether the image was successfully transferred and used
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * remove the card from the world, and spawn and return a building instead where the card was dropped
     * @param cardNodeX the x coordinate of the card which was dragged, from 0 to width-1
     * @param cardNodeY the y coordinate of the card which was dragged (in starter code this is 0 as only 1 row of cards)
     * @param buildingNodeX the x coordinate of the drop location for the card, where the building will spawn, from 0 to width-1
     * @param buildingNodeY the y coordinate of the drop location for the card, where the building will spawn, from 0 to height-1
     * @return building entity returned from the world
     */
    private Building convertCardToBuildingByCoordinates(int cardNodeX, int cardNodeY, int buildingNodeX, int buildingNodeY) {
        return world.convertCardToBuildingByCoordinates(cardNodeX, cardNodeY, buildingNodeX, buildingNodeY);
    }

    /**
     * remove an item from the unequipped inventory by its x and y coordinates in the unequipped inventory gridpane
     * @param nodeX x coordinate from 0 to unequippedInventoryWidth-1
     * @param nodeY y coordinate from 0 to unequippedInventoryHeight-1
     */
    public void removeItemByCoordinates(int nodeX, int nodeY) {
        world.removeUnequippedInventoryItemByCoordinates(nodeX, nodeY);
    }

    // Calls method that fluctuates the value of Doggie coins the player has
    private void fluctuateDoggieCoin() {
        world.fluctuateDoggieCoin();
    }

    /*
    private void removeInventoryItemByCoordinates(int nodeX, int nodeY) {
        world.removeEquippedInventoryItemByCoordinates(nodeX, nodeY);
        
    } */

    /**
     * add drag event handlers to an ImageView
     * @param view the view to attach drag event handlers to
     * @param draggableType the type of item being dragged - card or item
     * @param sourceGridPane the relevant gridpane from which the entity would be dragged
     * @param targetGridPane the relevant gridpane to which the entity would be dragged to
     */
    private void addDragEventHandlers(ImageView view, DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                currentlyDraggedImage = view; // set image currently being dragged, so squares setOnDragEntered can detect it...
                currentlyDraggedType = draggableType;
                //Drag was detected, start drap-and-drop gesture
                //Allow any transfer node
                Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                
                //Put ImageView on dragboard
                ClipboardContent cbContent = new ClipboardContent();
                cbContent.putImage(view.getImage());
                db.setContent(cbContent);
                view.setVisible(false);

                buildNonEntityDragHandlers(draggableType, sourceGridPane, targetGridPane);
                //unequippedInventory.getChildren().remove(view); Commented out so item returns to original position
                if (sourceGridPane.equals(unequippedInventory) && targetGridPane.equals(equippedItems)) {
                    view.getX();
                }

                draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                draggedEntity.setImage(view.getImage());
                /* switch (draggableType){
                    case CARD:
                        draggedEntity.setImage(vampireCastleCardImage);
                        break;
                    case ITEM:
                        draggedEntity.setImage(view.getImage());
                        break;
                    default:
                        break;
                } */
                
                draggedEntity.setVisible(true);
                draggedEntity.setMouseTransparent(true);
                draggedEntity.toFront();

                // IMPORTANT!!!
                // to be able to remove event handlers, need to use addEventHandler
                // https://stackoverflow.com/a/67283792
                targetGridPane.addEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

                for (Node n: targetGridPane.getChildren()){
                    // events for entering and exiting are attached to squares children because that impacts opacity change
                    // these do not affect visibility of original image...
                    // https://stackoverflow.com/questions/41088095/javafx-drag-and-drop-to-gridpane
                    gridPaneNodeSetOnDragEntered.put(draggableType, new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType){
                            //The drag-and-drop gesture entered the target
                            //show the user that it is an actual gesture target
                                if(event.getGestureSource() != n && event.getDragboard().hasImage() && event.isDropCompleted()){
                                    n.setOpacity(0.7);
                                }
                            }
                            event.consume();
                        }
                    });
                    gridPaneNodeSetOnDragExited.put(draggableType, new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType){
                                
                                n.setOpacity(1);
                            }
                
                            event.consume();
                        }
                    });
                    n.addEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
                    n.addEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
                }
                event.consume();
            }
            
        });
    }

    /**
     * remove drag event handlers so that we don't process redundant events
     * this is particularly important for slower machines such as over VLAB.
     * @param draggableType either cards, or items in unequipped inventory
     * @param targetGridPane the gridpane to remove the drag event handlers from
     */
    public void removeDraggableDragEventHandlers(DRAGGABLE_TYPE draggableType, GridPane targetGridPane){
        // remove event handlers from nodes in children squares, from anchorPaneRoot, and squares
        targetGridPane.removeEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));

        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

        for (Node n: targetGridPane.getChildren()){
            n.removeEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
            n.removeEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
        }
    }

    /**
     * handle the pressing of keyboard keys.
     * Specifically, we should pause when pressing SPACE
     * @param event some keyboard key press
     */
    @FXML
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
        case SPACE:
            if (isPaused){
                pausePane.setVisible(false);
                changeOpacity(1.0);
                startTimer();
                soundtrackMP.play();
            }
            else{
                pausePane.setVisible(true);
                changeOpacity(0.5);
                pause();
                soundtrackMP.pause();
                setGoalGold();
                setGoalXp();
                setGoalCycle();
            }
            break;
        case P:
            potionTrigger(); 
        default:
            break;
        }
    }
    
    // Change opacity
    public void changeOpacity(double opacity){
        squares.setOpacity(opacity);
        cards.setOpacity(opacity);
        equippedItems.setOpacity(opacity);
        unequippedInventory.setOpacity(opacity);
        allies.setOpacity(opacity);
        stats.setOpacity(opacity);
    }

    public void showCardHelp() {
        cardHelpPane.setVisible(true);
        changeOpacity(0.5);
        pause();
    }

    public void hideCardHelp() {
        cardHelpPane.setVisible(false);
        changeOpacity(1.0);
        startTimer();
    }

    public void setMainMenuSwitcher(MenuSwitcher mainMenuSwitcher){
        this.mainMenuSwitcher = mainMenuSwitcher;
    }

    public void setItemMenuSwitcher(MenuSwitcher itemMenuSwitcher) {
        this.itemMenuSwitcher = itemMenuSwitcher;
    }

    public void setDeathMenuSwitcher(MenuSwitcher deathMenuSwitcher) {
        this.deathMenuSwitcher = deathMenuSwitcher;
    }

    public void setWonMenuSwitcher(MenuSwitcher wonMenuSwitcher) {
        this.wonMenuSwitcher = wonMenuSwitcher;
    }

    /**
     * this method is triggered when click button to go to main menu in FXML
     * @throws IOException
     */
    @FXML
    private void switchToMainMenu() throws IOException {
        pause();
        soundtrackMP.stop();
        soundtrackMP.play();
        gameoverMP.stop();
        victoryMP.stop();
        mainMenuSwitcher.switchMenu();
    }

    /**
     * Helper function to switch menu, pauses the game state
     * @throws IOException
     */
    public void switchToItemMenu() throws IOException {
        pause();
        itemMenuSwitcher.switchMenu();
    }

    /**
     * Switches to the item menu on cycles 1, 3, 6, and so on
     */
    public void switchToMenu() {
        if(cycleCounter == world.getCycle()) {
            try {
                switchToItemMenu();
                incrCycleCounter();
            } catch (IOException e) {
                System.out.println("Nothing was done");
            } 
        }
    }

    /**
     * Increments the cycle counts to enter the shop menu in the hero's castle
     */
    public void incrCycleCounter() {
        cycleCounter += increment;
        increment++;
    }

    /**
     * Switch to death menu once health <= 0
     */
    public void switchToDeathMenu() {
        timeline.stop();
        soundtrackMP.stop();
        gameoverMP.play();
        deathMenuSwitcher.switchMenu();
    }

    /**
     * Switch to the won menu once completed the goals
     */
    public void switchToWonMenu() {
        timeline.stop();
        soundtrackMP.stop();
        victoryMP.play();
        wonMenuSwitcher.switchMenu();
    }

    /**
     * Restarts the data in the game upon death/winning the game
     * Loops back the character to starting position (Hero's Castle)
     */
    public void resetGame() {
        while(true) {
            if(world.getCharacterX() == world.getHerosCastleX() && world.getCharacterY() == world.getHerosCastleY()) {
                break;
            } else {
                world.runTickMoves();
            }
        }
        cycleCounter = 1;
        increment = 2;
        world.restartGame();
        setCycle();
        setGold();
        setXP();
        setHealth();
        soundtrackMP.play();
        gameoverMP.stop();
        victoryMP.stop();
    }

    /**
     * Function to spawn and show the allies on the game screen
     */
    public void showAllies() {
        world.spawnAllies();
        allies.getChildren().clear();
        int numberOfAllies = world.getNumberOfAllies();
        for(int i = 0; i < numberOfAllies; i++) {
            ImageView view = new ImageView(allyImage);
            allies.add(view, i, 0);
        }
    }

    /**
     * Set a node in a GridPane to have its position track the position of an
     * entity in the world.
     *
     * By connecting the model with the view in this way, the model requires no
     * knowledge of the view and changes to the position of entities in the
     * model will automatically be reflected in the view.
     * 
     * note that this is put in the controller rather than the loader because we need to track positions of spawned entities such as enemy
     * or items which might need to be removed should be tracked here
     * 
     * NOTE teardown functions setup here also remove nodes from their GridPane. So it is vital this is handled in this Controller class
     * @param entity
     * @param node
     */
    public void trackPosition(Entity entity, Node node) {
        GridPane.setColumnIndex(node, entity.getX());
        GridPane.setRowIndex(node, entity.getY());

        ChangeListener<Number> xListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setColumnIndex(node, newValue.intValue());
            }
        };
        ChangeListener<Number> yListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setRowIndex(node, newValue.intValue());
            }
        };

        // if need to remove items from the equipped inventory, add code to remove from equipped inventory gridpane in the .onDetach part
        ListenerHandle handleX = ListenerHandles.createFor(entity.x(), node)
                                               .onAttach((o, l) -> o.addListener(xListener))
                                               .onDetach((o, l) -> {
                                                    o.removeListener(xListener);
                                                    entityImages.remove(node);
                                                    squares.getChildren().remove(node);
                                                    cards.getChildren().remove(node);
                                                    equippedItems.getChildren().remove(node);
                                                    unequippedInventory.getChildren().remove(node);
                                                })
                                               .buildAttached();
        ListenerHandle handleY = ListenerHandles.createFor(entity.y(), node)
                                               .onAttach((o, l) -> o.addListener(yListener))
                                               .onDetach((o, l) -> {
                                                   o.removeListener(yListener);
                                                   entityImages.remove(node);
                                                   squares.getChildren().remove(node);
                                                   cards.getChildren().remove(node);
                                                   equippedItems.getChildren().remove(node);
                                                   unequippedInventory.getChildren().remove(node);
                                                })
                                               .buildAttached();
        handleX.attach();
        handleY.attach();

        // this means that if we change boolean property in an entity tracked from here, position will stop being tracked
        // this wont work on character/path entities loaded from loader classes
        entity.shouldExist().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> obervable, Boolean oldValue, Boolean newValue) {
                handleX.detach();
                handleY.detach();
            }
        });
    }

    /**
     * we added this method to help with debugging so you could check your code is running on the application thread.
     * By running everything on the application thread, you will not need to worry about implementing locks, which is outside the scope of the course.
     * Always writing code running on the application thread will make the project easier, as long as you are not running time-consuming tasks.
     * We recommend only running code on the application thread, by using Timelines when you want to run multiple processes at once.
     * EventHandlers will run on the application thread.
     */
    
    private void printThreadingNotes(String currentMethodLabel){
        System.out.println("\n###########################################");
        System.out.println("current method = "+currentMethodLabel);
        System.out.println("In application thread? = "+Platform.isFxApplicationThread());
        System.out.println("Current system time = "+java.time.LocalDateTime.now().toString().replace('T', ' '));
    }

}
