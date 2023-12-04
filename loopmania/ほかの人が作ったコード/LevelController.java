package unsw.loopmania;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import unsw.loopmania.GameMode.Mode;

/**
 * controller for the level selection.
 */
public class LevelController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;

    private Mode gameMode;

    @FXML
    private TextArea survivalText;

    @FXML
    private TextArea berserkerText;

    @FXML
    private TextArea confusingText;

    @FXML
    private TextArea standardText;

    public void setGameSwitcher(MenuSwitcher gameSwitcher){
        this.gameSwitcher = gameSwitcher;
    }

    /**
     * facilitates switching to main game upon button click
     * @throws IOException
     */
    @FXML
    private void switchToStandard() throws IOException {
        this.gameMode = Mode.STANDARD;
        gameSwitcher.switchMenu();
    }

    @FXML
    private void switchToSurvival() throws IOException {  
        this.gameMode = Mode.SURVIVAL;
        gameSwitcher.switchMenu();
    }

    @FXML
    private void switchToBerserker() throws IOException {
        this.gameMode = Mode.BERSERKER;
        gameSwitcher.switchMenu();     
    }

    @FXML
    private void switchToConfusing() throws IOException {
        this.gameMode = Mode.CONFUSING;
        gameSwitcher.switchMenu();     
    }

    @FXML
    public void enterStandard() {
        standardText.setVisible(true);
    }

    @FXML
    public void exitStandard() {
        standardText.setVisible(false);
    }

    @FXML
    public void enterSurvival() {
        survivalText.setVisible(true);
    }

    @FXML
    public void exitSurvival() {
        survivalText.setVisible(false);
    }

    @FXML
    public void enterBerserker() {
        berserkerText.setVisible(true);
    }

    @FXML
    public void exitBerserker() {
        berserkerText.setVisible(false);
    }

    @FXML
    public void enterConfusing() {
        confusingText.setVisible(true);
    }

    @FXML
    public void exitConfusing() {
        confusingText.setVisible(false);
    }


    public Mode getGameMode() {
        return gameMode;
    }
}
