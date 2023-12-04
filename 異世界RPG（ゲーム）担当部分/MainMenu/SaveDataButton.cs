using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SaveDataButton : Button {

    public event Action<SaveData, int> OnSelected;
    private int saveIndex;
    private SaveData save;

    private Text saveNameText;
    private Text dateTimeText;

    public void Init()
    {
        saveNameText = transform.Find("SaveNameText").GetComponent<Text>();
        dateTimeText = transform.Find("DateTimeText").GetComponent<Text>();
    }

    public void AssignSave(int saveIndex, SaveData save)
    {
        this.saveIndex = saveIndex;
        onClick.RemoveAllListeners();
        onClick.AddListener(OnClick);

        if (save == null || save.dateTime == "")
        {
            dateTimeText.text = "";
            saveNameText.text = "New Save";
        }
        else
        {
            this.save = save;
            saveNameText.text = "Save " + saveIndex;
            dateTimeText.text = save.dateTime;
        }
    }

    public void OnClick()
    {
        OnSelected?.Invoke(save, saveIndex);
    }
}
