using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class GambitScreen : MonoBehaviour
{
    [SerializeField]
    private GambitSlotUI slotPrefab;
    [SerializeField]
    private Transform slotPanel;

    [SerializeField]
    private GambitCharacterSelectButton characterSelectButton;
    [SerializeField]
    private Transform characterSelectPanel;

    [SerializeField]
    private GambitActionSelectUI actionSelectUI;
    [SerializeField]
    private GameObject actionTypeSelectPanel;
    [SerializeField]
    private Button abilitySelectButton;
    [SerializeField]
    private Button itemSelectButton;

    private bool selectingAbilityAction;

    private GambitSlotUI slotToChangeAction;

    [SerializeField]
    private Image characterImage;
    [SerializeField]
    private Canvas parentCanvas;

    private Character currentCharacter;
    private List<GambitSlot> currentGambitSlots;

    void Start()
    {
        ClearSlotPanel();
        ClearCharacterPanel();
        CreateCharacterButtons();
        SelectCharacter(GameManager.instance.allyList[0]);

        abilitySelectButton.onClick.AddListener(SelectAbilities);
        itemSelectButton.onClick.AddListener(SelectItems);
        actionSelectUI.OnSelected += HandleSelectedAction;

        actionTypeSelectPanel.SetActive(false);
        actionSelectUI.gameObject.SetActive(false);
    }

    private void ClearSlotPanel()
    {
        List<Transform> children = new List<Transform>();
        foreach (Transform child in slotPanel)
        {
            children.Add(child);
        }

        children.ForEach(t => Destroy(t.gameObject));
    }

    private void CreateCharacterButtons()
    {
        List<Character> characters = GameManager.instance.allyList;
        foreach (Character c in characters)
        {
            GambitCharacterSelectButton newButton = Instantiate(characterSelectButton, characterSelectPanel).GetComponent<GambitCharacterSelectButton>();
            newButton.AssignCharacter(c);
            newButton.OnClick += SelectCharacter;
        }
    }

    //TODO: Think about this later, change the way the newIndex works
    private void HandleDrop(PointerEventData eventData, GambitSlotUI dropped)
    {
        int newIndex = -1;
        for (int i = 0; i < slotPanel.childCount; i++)
        {
            Vector2 slotPos = RectTransformUtility.WorldToScreenPoint(parentCanvas.worldCamera, slotPanel.GetChild(i).position);
            if (slotPos.y < eventData.position.y)
            {
                newIndex = i;
                break;
            }
        }
        dropped.transform.SetParent(slotPanel);
        if (newIndex != -1)
        {
            dropped.transform.SetSiblingIndex(newIndex);
            SwapSlots(currentGambitSlots.IndexOf(dropped.Slot), newIndex);
        }
        else
        {
            SwapSlots(currentGambitSlots.IndexOf(dropped.Slot), currentGambitSlots.Count-1);
        }

        GameManager.instance.SetCharacterGambitSlot(currentCharacter, currentGambitSlots);
        RenumberSlots();
    }

    private void SwapSlots(int i0, int i1)
    {
        GambitSlot temp = currentGambitSlots[i0];
        currentGambitSlots[i0] = currentGambitSlots[i1];
        currentGambitSlots[i1] = temp;
    }

    private void RenumberSlots()
    {
        for (int i = 0; i < slotPanel.childCount; i++)
        {
            GambitSlotUI currentSlot = slotPanel.GetChild(i).GetComponent<GambitSlotUI>();
            currentSlot.SetSlotNumberText(i + 1);
        }
    }
    
    private void SelectCharacter(Character c)
    {
        characterImage.sprite = c.FullBodySprite;
        ClearSlotPanel();
        List<GambitSlot> slots = GameManager.instance.GetCharacterGambitSlot(c);
        currentCharacter = c;
        currentGambitSlots = slots;
        ShowSlotList(slots);
    }

    private void ClearCharacterPanel()
    {
        List<Transform> children = new List<Transform>();
        foreach (Transform child in characterSelectPanel)
        {
            children.Add(child);
        }

        children.ForEach(t => Destroy(t.gameObject));
    }

    private void ShowSlotList(List<GambitSlot> slots) 
    {
        for (int i = 0; i < slots.Count; i++)
        {
            GambitSlotUI newSlot = Instantiate(slotPrefab.gameObject, slotPanel).GetComponent<GambitSlotUI>();
            newSlot.SetSlot(i+1, slots[i]);
            newSlot.SetParentCanvas(parentCanvas);
            newSlot.OnDropped += HandleDrop;
            newSlot.onActionChangeRequest += StartActionChange;
        }
    }

    private void StartActionChange(GambitSlotUI slotToChangeAction)
    {
        if (actionSelectUI.gameObject.activeSelf || actionTypeSelectPanel.activeSelf)
            return;

        this.slotToChangeAction = slotToChangeAction;
        actionTypeSelectPanel.SetActive(true);
        actionTypeSelectPanel.transform.position = slotToChangeAction.transform.position;
    }

    public void SelectAbilities()
    {
        actionTypeSelectPanel.SetActive(false);
        actionSelectUI.CreateGrid(currentCharacter.Abilities);
        actionSelectUI.gameObject.SetActive(true);
        selectingAbilityAction = true;
    }

    public void SelectItems()
    {
        actionTypeSelectPanel.SetActive(false);
        actionSelectUI.CreateGrid(ItemFactory.GetAllConsumables());
        actionSelectUI.gameObject.SetActive(true);
        selectingAbilityAction = false;
    }

    public void HandleSelectedAction(string action)
    {
        GambitSlot newSlot;
        if (selectingAbilityAction)
            newSlot = new AbilityGambitSlot(action, slotToChangeAction.Slot.GambitCondition);
        else
            newSlot = new ItemGambitSlot(action, slotToChangeAction.Slot.GambitCondition);

        slotToChangeAction.SetSlot(newSlot);

        actionSelectUI.gameObject.SetActive(false);
        currentGambitSlots[slotToChangeAction.Number - 1] = newSlot;
        GameManager.instance.SetCharacterGambitSlot(currentCharacter, currentGambitSlots);
    }

    public void ResetScreen()
    {
        actionTypeSelectPanel.SetActive(false);
        actionSelectUI.gameObject.SetActive(false);
    }
}
