package unsw.loopmania;

import java.io.IOException;

import javafx.fxml.FXML;

public class DeathMenuController {

    MenuSwitcher gameSwitcher;

    MenuSwitcher menuSwitcher;

    public void setGameSwitcher(MenuSwitcher gameSwitcher){
        this.gameSwitcher = gameSwitcher;
    }

    public void setMenuSwitcher(MenuSwitcher menuSwitcher) {
        this.menuSwitcher = menuSwitcher;
    }

    @FXML
    public void restartGame() throws IOException {
        gameSwitcher.switchMenu();
    }

    @FXML
    public void exitGame() {
        menuSwitcher.switchMenu();
    }
}
