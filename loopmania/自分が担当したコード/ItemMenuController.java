package unsw.loopmania;

import java.io.IOException;

import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import unsw.loopmania.GameMode.Mode;
import javafx.scene.layout.Pane;
import unsw.loopmania.ItemFactory.ItemType;

public class ItemMenuController {
    @FXML 
    private GridPane itemsGridPane;

    @FXML 
    private Button helmetButton;

    @FXML 
    private Button potionButton;

    @FXML 
    private Button swordButton;

    @FXML 
    private Button shieldButton;

    @FXML 
    private Button stakeButton;

    @FXML 
    private Button staffButton;

    @FXML 
    private Button armorButton;

    @FXML 
    private Button continueGameButton;

    @FXML 
    private Label goldValue;

    @FXML 
    private Label xpValue;

    @FXML
    private Label cycleValue;

    @FXML
    private GridPane inventoryGridPane;

    @FXML
    private Pane potionPane;
    @FXML
    private Pane shieldPane;
    @FXML
    private Pane armourPane;
    @FXML
    private Pane helmetPane;
    @FXML
    private Pane stakePane;
    @FXML
    private Pane swordPane;
    @FXML
    private Pane staffPane;
    @FXML
    private Pane sellPane;

    private MenuSwitcher levelSwitcher;

    private MenuSwitcher gameSwitcher;

    private LoopManiaWorldController mainController;

    List<ItemType> purchasedItems = new ArrayList<>();

    public ItemMenuController (LoopManiaWorldController mainController) {
        this.mainController = mainController;
    }

    public void setGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSwitcher = gameSwitcher;
    }

    public void setLevelSwitcher(MenuSwitcher levelSwitcher) {
        this.levelSwitcher = levelSwitcher;
    } 

    @FXML
    public void initialize() {
        potionPane.setVisible(false);
        shieldPane.setVisible(false);
        armourPane.setVisible(false);
        helmetPane.setVisible(false);
        stakePane.setVisible(false);
        swordPane.setVisible(false);
        staffPane.setVisible(false);
        sellPane.setVisible(false);
    }

    @FXML
    public void switchToGame() throws IOException {
        gameSwitcher.switchMenu();
        purchasedItems.clear();
    }

    @FXML
    public void switchToLevelShop() throws IOException {
        levelSwitcher.switchMenu();
    }

    @FXML 
    public void handlePotion() throws IOException {
        // Check if the player has enough money
        int currentGold = mainController.getGold();
        if(currentGold >= 50 && mainController.getWorldUnequippedInventory().size() < 16 && canPurchaseInMode(ItemType.HEALTH_POTION)) {
            mainController.loadItem(ItemType.HEALTH_POTION);
            purchasedItems.add(ItemType.HEALTH_POTION);
            setShopInventory();
            mainController.minusGold(50);
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void handleHelmet() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16 && canPurchaseInMode(ItemType.HELMET)) {
            mainController.loadItem(ItemType.HELMET);
            purchasedItems.add(ItemType.HELMET);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void handleSword() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16) {
            mainController.loadItem(ItemType.SWORD);
            purchasedItems.add(ItemType.SWORD);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void handleShield() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16 && canPurchaseInMode(ItemType.SHIELD)) {
            mainController.loadItem(ItemType.SHIELD);
            purchasedItems.add(ItemType.SHIELD);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void handleStake() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16) {
            mainController.loadItem(ItemType.STAKE);
            purchasedItems.add(ItemType.STAKE);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void handleStaff() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16) {
            mainController.loadItem(ItemType.STAFF);
            purchasedItems.add(ItemType.STAFF);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML 
    public void handleArmor() throws IOException {
        int currentGold = mainController.getGold();
        if(currentGold >= 300 && mainController.getWorldUnequippedInventory().size() < 16 && canPurchaseInMode(ItemType.ARMOUR)) {
            mainController.loadItem(ItemType.ARMOUR);
            purchasedItems.add(ItemType.ARMOUR);
            mainController.minusGold(300);
            setShopInventory();
            goldValue.setText(mainController.getGoldString());
        }
    }

    @FXML
    public void sellAllItems() throws IOException {
        setUneqippedInventory();
        List<Item> removedItems = new ArrayList<>();
        for(Item i : mainController.getWorldUnequippedInventory()) {
            removedItems.add(i);
        }
        for(Item i : removedItems) {
            if(i instanceof DoggieCoin)
                mainController.addGold(i.getValue());
            else
                mainController.addGold(50);
            mainController.removeItemByCoordinates(i.getX(), i.getY());
            goldValue.setText(mainController.getGoldString());
        }
        setShopInventory();
    }

    @FXML
    public void enterPotion() {
        potionPane.setVisible(true);
    }

    @FXML
    public void exitPotion() {
        potionPane.setVisible(false);
    }

    @FXML
    public void enterShield() {
        shieldPane.setVisible(true);
    }

    @FXML
    public void exitShield() {
        shieldPane.setVisible(false);
    }

    @FXML
    public void enterArmour() {
        armourPane.setVisible(true);
    }

    @FXML
    public void exitArmour() {
        armourPane.setVisible(false);
    }

    @FXML
    public void enterHelmet() {
        helmetPane.setVisible(true);
    }

    @FXML
    public void exitHelmet() {
        helmetPane.setVisible(false);
    }

    @FXML
    public void enterStake() {
        stakePane.setVisible(true);
    }

    @FXML
    public void exitStake() {
        stakePane.setVisible(false);
    }

    @FXML
    public void enterSword() {
        swordPane.setVisible(true);
    }

    @FXML
    public void exitSword() {
        swordPane.setVisible(false);
    }

    @FXML
    public void enterStaff() {
        staffPane.setVisible(true);
    }

    @FXML
    public void exitStaff() {
        staffPane.setVisible(false);
    }

    @FXML
    public void enterSell() {
        sellPane.setVisible(true);
    }

    @FXML
    public void exitSell() {
        sellPane.setVisible(false);
    }


    public void setGoldValue() {
        this.goldValue.setText(mainController.getGoldString());
    }

    public void setExpValue() {
        this.xpValue.setText(mainController.getXpString());
    }

    public void setCycleValue() {
        this.cycleValue.setText(mainController.getCycleString());
    }

    // Check if the player is able to purchase the item in the current game mode
    public Boolean canPurchaseInMode(ItemType item) {
        if  (mainController.getGameMode().equals(Mode.STANDARD)) {
            return true;
        } else if (mainController.getGameMode().equals(Mode.SURVIVAL)) {
            if (purchasedItems.contains(ItemType.HEALTH_POTION) && item.equals(ItemType.HEALTH_POTION)) {
                return false;
            } 
        } else if (mainController.getGameMode().equals(Mode.BERSERKER) 
                   && (item.equals(ItemType.ARMOUR)) || (item.equals(ItemType.HELMET)) || (item.equals(ItemType.SHIELD))) {
            if (purchasedItems.contains(ItemType.ARMOUR) || purchasedItems.contains(ItemType.HELMET) 
            || purchasedItems.contains(ItemType.SHIELD)) {
                return false;
            }
        }

        return true;
    }

    public void setShopInventory() {
        GridPane uneqippedItems = mainController.getUneqippedInventory();
        ObservableList<Node> items = uneqippedItems.getChildren();
        inventoryGridPane.getChildren().addAll(items);
    }

    public void setUneqippedInventory() {
        GridPane unequippedItems = mainController.getUneqippedInventory();
        ObservableList<Node> items = inventoryGridPane.getChildren();
        unequippedItems.getChildren().addAll(items);
    }

    public GridPane getInventory() {
        return inventoryGridPane;
    }
} 
