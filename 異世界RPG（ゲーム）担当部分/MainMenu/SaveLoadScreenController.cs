using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SaveLoadScreenController : MonoBehaviour
{
    public Action<int> OnLoadSelected;
    public Action<int> OnSaveSelected;

    [SerializeField]
    private GameObject parent;
    [SerializeField]
    private SaveDataButton[] saveDataButtons;
    [SerializeField]
    private Button backButton;

    public bool Active { get; private set; }

    private void Awake()
    {
        for (int i = 0; i < 3; i++)
        {
            saveDataButtons[i].Init();
        }
        backButton.onClick.AddListener(Back);
        Active = false;
    }

    public void StartLoad()
    {
        parent.SetActive(true);
        for (int i = 0; i < 3; i++)
        {
            saveDataButtons[i].AssignSave(i, SaveDataManager.SaveDataCollection.GetSave(i));
            saveDataButtons[i].OnSelected += NotifyLoad;
        }
        Active = true;
    }

    public void NotifyLoad(SaveData saveData, int saveIndex)
    {
        if (saveData != null)
        {
            OnLoadSelected?.Invoke(saveIndex);
            Active = false;
        }
    }

    public void StartSave()
    {
        parent.SetActive(true);
        for (int i = 0; i < 3; i++)
        {
            saveDataButtons[i].AssignSave(i, SaveDataManager.SaveDataCollection.GetSave(i));
            saveDataButtons[i].OnSelected += NotifySave;
        }
        Active = true;
    }

    public void Back()
    {
        parent.SetActive(false);
        for (int i = 0; i < 3; i++)
        {
            saveDataButtons[i].OnSelected -= NotifySave;
            saveDataButtons[i].OnSelected -= NotifyLoad;
        }
        Active = false;
    }

    public void NotifySave(SaveData saveData, int saveIndex)
    {
        OnSaveSelected?.Invoke(saveIndex);
        Active = false;
    }
}
