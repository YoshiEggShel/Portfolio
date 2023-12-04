using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MainMenuController : MonoBehaviour
{
    [SerializeField]
    private Button newGameButton;
    [SerializeField]
    private Button loadGameButton;
    [SerializeField]
    private Button exitGameButton;
    [SerializeField]
    private SaveLoadScreenController saveLoadScreen;
    private SaveDataManager saveDataManager;

    void Start()
    {
        newGameButton.onClick.AddListener(NewGame);
        loadGameButton.onClick.AddListener(LoadGame);
        exitGameButton.onClick.AddListener(ExitGame);
    }

    public void ExitGame()
    {
        Application.Quit();
    }

    public void LoadGame()
    {
        saveLoadScreen.StartLoad();
        saveLoadScreen.OnLoadSelected += LoadGameSelected;
    }

    public void LoadGameSelected(int saveIndex)
    {
        GameManager.instance.Init(saveIndex);
        GameManager.instance.LoadTownScene();
    }

    public void NewGame()
    {
        GameManager.instance.InitNewGame();
        GameManager.instance.LoadStoryCutscene();
    }
}
