using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class QuestSelectScreen : MonoBehaviour
{
    [SerializeField]
    private GameObject panel;
    [SerializeField]
    private Transform buttonGroup;
    [SerializeField]
    private QuestSelectButton prefab;
    public bool Active { get; private set; }

    public event Action<Quest> OnQuestSelected;

    void Awake()
    {
        Active = false;
    }

    private void ClearButtons()
    {
        List<Transform> buttons = new List<Transform>();
        foreach (Transform t in buttonGroup)
        {
            buttons.Add(t);
        }

        buttons.ForEach(t => Destroy(t.gameObject));
    }

    public void Close()
    {
        panel.SetActive(false);
        ClearButtons();
        Active = false;
    }

    public void ShowQuests(List<Quest> quests)
    {
        if (Active)
            return;

        Active = true;
        panel.SetActive(true);
        foreach (Quest quest in quests)
        {
            QuestSelectButton newButton = Instantiate(prefab.gameObject, buttonGroup).GetComponent<QuestSelectButton>();
            newButton.Init();
            newButton.AssignQuest(quest);
            newButton.OnQuestSelected += SelectQuest;
        }
    }

    private void SelectQuest(Quest quest)
    {
        OnQuestSelected?.Invoke(quest);
    }
}
