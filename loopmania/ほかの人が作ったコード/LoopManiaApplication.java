package unsw.loopmania;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * the main application
 * run main method from this class
 */
public class LoopManiaApplication extends Application {
    /**
     * the controller for the game. Stored as a field so can terminate it when click exit button
     */
    private LoopManiaWorldController mainController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // set title on top of window bar
        primaryStage.setTitle("Loop Mania");

        // prevent human player resizing game window (since otherwise would see white space)
        // alternatively, you could allow rescaling of the game (you'd have to program resizing of the JavaFX nodes)
        primaryStage.setResizable(false);

        // load the main game
        LoopManiaWorldControllerLoader loopManiaLoader = new LoopManiaWorldControllerLoader("world_with_twists_and_turns.json");
        mainController = loopManiaLoader.loadController();
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("LoopManiaView.fxml"));
        gameLoader.setController(mainController);
        Parent gameRoot = gameLoader.load();

        // load the main menu
        MainMenuController mainMenuController = new MainMenuController();
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("MainMenuView.fxml"));
        menuLoader.setController(mainMenuController);
        Parent mainMenuRoot = menuLoader.load();

        // load the level selection screen
        LevelController levelController = new LevelController();
        FXMLLoader levelLoader = new FXMLLoader(getClass().getResource("LevelView.fxml"));
        levelLoader.setController(levelController);
        Parent LevelRoot = levelLoader.load();
    
        // load the instructions screen
        InstructionsController instructionsController = new InstructionsController();
        FXMLLoader instructionsLoader = new FXMLLoader(getClass().getResource("InstructionsView.fxml"));
        instructionsLoader.setController(instructionsController);
        Parent instructionsRoot = instructionsLoader.load();
        
        // Load the item menu
        ItemMenuController itemMenuController = new ItemMenuController(mainController);
        FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("ItemMenuView.fxml"));
        itemLoader.setController(itemMenuController);
        Parent itemMenuRoot = itemLoader.load();

        // Load the level menu
        LevelMenuController levelMenuController = new LevelMenuController(mainController);
        FXMLLoader levelMenuLoader = new FXMLLoader(getClass().getResource("LevelMenuView.fxml"));
        levelMenuLoader.setController(levelMenuController);
        Parent levelMenuRoot = levelMenuLoader.load();

        // Load the death menu
        DeathMenuController deathMenuController = new DeathMenuController();
        FXMLLoader deathLoader = new FXMLLoader(getClass().getResource("DeathMenuView.fxml"));
        deathLoader.setController(deathMenuController);
        Parent deathMenuRoot = deathLoader.load();

        // Load the won menu
        WonMenuController wonMenuController = new WonMenuController();
        FXMLLoader wonLoader = new FXMLLoader(getClass().getResource("WonMenuView.fxml"));
        wonLoader.setController(wonMenuController);
        Parent wonMenuRoot = wonLoader.load();
        Scene wonScene = new Scene(wonMenuRoot);

        Scene deathScene = new Scene(deathMenuRoot);

        Scene itemScene = new Scene(itemMenuRoot);

        Scene levelScene = new Scene(levelMenuRoot);
        
        // create new scene with the main menu (so we start with the main menu)
        Scene scene = new Scene(mainMenuRoot);

        mainController.setItemMenuSwitcher(() -> {
            switchToRoot(itemScene, itemMenuRoot, primaryStage);
            itemMenuController.setGoldValue();
            itemMenuController.setCycleValue();
            itemMenuController.setExpValue();
            itemMenuController.setShopInventory();
        });
        itemMenuController.setGameSwitcher(() -> {
            itemMenuController.setUneqippedInventory();
            switchToRoot(scene, gameRoot, primaryStage);
            mainController.startTimer();
        });
        itemMenuController.setLevelSwitcher(() -> {
            switchToRoot(levelScene, levelMenuRoot, primaryStage);
            levelMenuController.setGoldValue();
            levelMenuController.setCycleValue();
            levelMenuController.setExpValue();
            levelMenuController.setAttackStat();
            levelMenuController.setDefenseStat();
            levelMenuController.setSpeedStat();
            levelMenuController.setCurrHealthStat();
            levelMenuController.setMaxHealthStat();
        });


        levelMenuController.setGameSwitcher(() -> {
            itemMenuController.setUneqippedInventory();
            switchToRoot(scene, gameRoot, primaryStage);
            mainController.startTimer();
        });
        levelMenuController.setItemSwitcher(() -> {
            switchToRoot(itemScene, itemMenuRoot, primaryStage);
            itemMenuController.setGoldValue();
            itemMenuController.setCycleValue();
            itemMenuController.setExpValue();
        });
        
        // set functions which are activated when button click to switch menu is pressed
        // e.g. from main menu to start the game, or from the game to return to main menu
        mainController.setMainMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
            mainController.resetGame();
        });
        mainMenuController.setLevelSwitcher(() -> {
            switchToRoot(scene, LevelRoot, primaryStage);
        });
        mainMenuController.setInstructionsSwitcher(() -> {
            switchToRoot(scene, instructionsRoot, primaryStage);
        });

        levelController.setGameSwitcher(() -> {
            switchToRoot(scene, gameRoot, primaryStage);  
            mainController.setGameMode(levelController.getGameMode());
            mainController.startTimer();
        });

        instructionsController.setGameSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);  
        });

        mainController.setDeathMenuSwitcher(() -> {
            switchToRoot(deathScene, deathMenuRoot, primaryStage);
        });
        deathMenuController.setGameSwitcher(() -> {
            switchToRoot(scene, gameRoot, primaryStage);
            mainController.resetGame();
            mainController.startTimer();
        });
        deathMenuController.setMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
            mainController.resetGame();
        });

        mainController.setWonMenuSwitcher(() -> {
            switchToRoot(wonScene, wonMenuRoot, primaryStage);
        });
        wonMenuController.setGameSwitcher(() -> {
            switchToRoot(scene, gameRoot, primaryStage);
            mainController.resetGame();
            mainController.startTimer();
        });
        wonMenuController.setMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
            mainController.resetGame();
        });

        // deploy the main onto the stage
        gameRoot.requestFocus();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        // wrap up activities when exit program
        mainController.terminate();
    }

    /**
     * switch to a different Root
     */
    private void switchToRoot(Scene scene, Parent root, Stage stage){
        scene.setRoot(root);
        root.requestFocus();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
