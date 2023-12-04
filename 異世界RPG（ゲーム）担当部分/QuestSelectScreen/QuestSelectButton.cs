using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class QuestSelectButton : Button
{
    private Quest quest;
    private Text questNameText;
    private Text questDescriptionText;

    public event Action<Quest> OnQuestSelected;

    public void Init()
    {
        questNameText = transform.Find("QuestNameText").GetComponent<Text>();
        questDescriptionText = transform.Find("QuestDescriptionText").GetComponent<Text>();
        onClick.AddListener(OnClick);
    }

    public void AssignQuest(Quest quest)
    {
        this.quest = quest;
        questNameText.text = quest.QuestName;
        questDescriptionText.text = quest.QuestDescription;
    }

    public void OnClick()
    {
        OnQuestSelected?.Invoke(quest);
    }
}
