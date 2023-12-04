using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class QuestItemScreen : MonoBehaviour
{
    [SerializeField]
    private Transform itemScreenTransform;
    [SerializeField]
    private Transform charInfoGroup;
    [SerializeField]
    private QuestMapCharInfo charInfoPrefab;
    [SerializeField]
    private Transform inactivePosition;
    [SerializeField]
    private Transform activePosition;
    private List<QuestMapCharInfo> charInfo;
    [SerializeField]
    private CharacterSelectPanel characterSelectPanel;
    [SerializeField]
    private AllItem itemScreenController;

    public bool Active { get; private set; } = false;

    public void Initialise()
    {
        charInfo = new List<QuestMapCharInfo>();
        foreach (Character c in GameManager.instance.allyList)
        {
            QuestMapCharInfo newUi = Instantiate(charInfoPrefab.gameObject, charInfoGroup).GetComponent<QuestMapCharInfo>();
            newUi.Initialise(c);
            charInfo.Add(newUi);
        }

        characterSelectPanel.Initialise(GameManager.instance.allyList);
        characterSelectPanel.gameObject.SetActive(false);
        itemScreenController.OnItemUsed += OnItemUsed;
    }

    public void SetActive(bool active)
    {
        if (Active == active)
            return;

        if (!active && itemScreenController.CharacterSelectActive)
        {
            itemScreenController.DeactivateCharacterSelect();
        }

        if (active)
        {
            itemScreenTransform.position = activePosition.position;
            foreach(QuestMapCharInfo ui in charInfo)
            {
                ui.UpdateUI();
            }
            itemScreenController.ShowItems();
        }
        else
        {
            itemScreenTransform.position = inactivePosition.position;
            itemScreenController.HideItems();
        }
        Active = active;
    }

    private void OnItemUsed (Item usedItem, Character target)
    {
        foreach (QuestMapCharInfo ui in charInfo)
        {
            ui.UpdateUI();
        }
    }
}
