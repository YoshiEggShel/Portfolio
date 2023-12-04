package unsw.loopmania;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LevelMenuController {

    @FXML 
    private Label goldValue1;

    @FXML 
    private Label xpValue1;

    @FXML
    private Label cycleValue1;

    @FXML
    private Label attackStat;

    @FXML
    private Label defenseStat;

    @FXML
    private Label speedStat;

    @FXML
    private Label currentHealthStat;

    @FXML
    private Label maxHealthStat;

    private MenuSwitcher itemSwitcher;

    private MenuSwitcher gameSwitcher;

    private LoopManiaWorldController mainController;

    public LevelMenuController(LoopManiaWorldController mainController) {
        this.mainController = mainController;
    }

    public void setGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSwitcher = gameSwitcher;
    }

    public void setItemSwitcher(MenuSwitcher itemSwitcher) {
        this.itemSwitcher = itemSwitcher;
    } 

    @FXML
    public void switchToGame() throws IOException {
        gameSwitcher.switchMenu();
    }

    @FXML
    public void switchToLevelShop() throws IOException {
        itemSwitcher.switchMenu();
    }

    @FXML
    public void handleAttack() {
        if(mainController.getXp() >= 1000) {
            mainController.getCharacter().modifyAttack(1);
            mainController.minusXp(1000);
            xpValue1.setText(mainController.getXpString());
            setAttackStat();
        }
    }

    @FXML
    public void handleSpeed() {
        if(mainController.getXp() >= 1000) {
            mainController.getCharacter().modifySpeed(1);
            mainController.minusXp(1000);
            xpValue1.setText(mainController.getXpString());
            setSpeedStat();
        }
    }

    @FXML
    public void handleDefense() {
        if(mainController.getXp() >= 1000) {
            mainController.getCharacter().modifyDefense(1);
            mainController.minusXp(1000);
            xpValue1.setText(mainController.getXpString());
            setDefenseStat();
        }
    }

    @FXML
    public void handleHealth() {
        if(mainController.getXp() >= 1000) {
            mainController.getCharacter().modifyMaxHealth(25);
            mainController.getCharacter().modifyHealth(25);
            mainController.minusXp(1000);
            xpValue1.setText(mainController.getXpString());
            setCurrHealthStat();
            setMaxHealthStat();
        }
    }

    public void setGoldValue() {
        this.goldValue1.setText(mainController.getGoldString());
    }

    public void setExpValue() {
        this.xpValue1.setText(mainController.getXpString());
    }

    public void setCycleValue() {
        this.cycleValue1.setText(mainController.getCycleString());
    }

    public void setAttackStat() {
        this.attackStat.setText(mainController.getAttackString());
    }

    public void setDefenseStat() {
        this.defenseStat.setText(mainController.getDefenseString());
    }

    public void setSpeedStat() {
        this.speedStat.setText(mainController.getSpeedString());
    }

    public void setCurrHealthStat() {
        this.currentHealthStat.setText(mainController.getCurrentHealthString());
    }

    public void setMaxHealthStat() {
        this.maxHealthStat.setText(mainController.getMaxHealthString());
    }
}
