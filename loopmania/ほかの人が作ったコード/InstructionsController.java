package unsw.loopmania;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * controller for the level selection.
 */
public class InstructionsController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;


    public void setGameSwitcher(MenuSwitcher gameSwitcher){
        this.gameSwitcher = gameSwitcher;
    }

    /**
     * facilitates switching to main game upon button click
     * @throws IOException
     */
    @FXML
    private void switchToMainMenu() throws IOException {
        gameSwitcher.switchMenu();
    }

}
