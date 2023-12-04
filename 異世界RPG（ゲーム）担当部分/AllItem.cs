using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class AllItem : MonoBehaviour
{
    [SerializeField]
    private Transform layout;
    [SerializeField]
    private GameObject btnPrefab;
    [SerializeField]
    private List<GameObject> itemLst;
    [SerializeField]
    private ItemTooltip tooltip;
    [SerializeField]
    private GameObject selectPanel;

    private bool subscribed = false;
    private ItemBtnUi selectedButton;

    public bool CharacterSelectActive { get; private set; }

    public event Action<Item, Character> OnItemUsed;

    // Start is called before the first frame update
    void Start()
    {
        CharacterSelectActive = false;
    }

    public void ShowItems()
    {
        foreach (Item item in GameManager.instance.Inventory.GetConsumables())
        {
            GameObject prefab = Instantiate(btnPrefab, Vector3.zero, Quaternion.identity);
            ItemBtnUi prefabScript = prefab.AddComponent<ItemBtnUi>();
            prefabScript.Initialise(item);
            prefab.gameObject.GetComponentInChildren<TextMeshProUGUI>().text = item.Name;
            prefab.transform.SetParent(layout, false);
            itemLst.Add(prefab);

            prefabScript.OnHover += ActivateTooltip;
            prefabScript.OnExit += DeactivateTooltip;
            prefabScript.OnClick += SelectItem;
        }
    }

    public void HideItems()
    {
        foreach (GameObject btn in itemLst)
        {
            Destroy(btn);
        }
        itemLst.Clear();
        if (subscribed)
            DeactivateCharacterSelect();
    }

    private void SelectCharacter(Character selected)
    {
        DeactivateCharacterSelect();
        if (selectedButton == null)
        {
            Debug.LogError("We selected a character without selecting an item!");
            return;
        }
        selectedButton.GetComponent<Image>().color = Color.white;
        int amount = selectedButton.TrackedItem.Amount;
        bool success = GameManager.instance.Inventory.UseConsumable((Consumable)selectedButton.TrackedItem, selected, selected);
        if (success)
        {
            OnItemUsed?.Invoke(selectedButton.TrackedItem, selected);
            if (amount == 1)
                Destroy(selectedButton.gameObject);
        }
    }

    private void SelectItem(ItemBtnUi selected)
    {
        Debug.Log("Selected");
        if (selectedButton != null)
        {
            selectedButton.GetComponent<Image>().color = Color.white;
        }
        selectedButton = selected;
        selectedButton.GetComponent<Image>().color = Color.red;

        if (!subscribed)
            ActivateCharacterSelect();

        RectTransform btnRect = selected.transform as RectTransform;
        Vector3[] corners = new Vector3[4];
        btnRect.GetWorldCorners(corners);
        selectPanel.transform.position = corners[2];
    }

    public void ActivateCharacterSelect()
    {
        selectPanel.SetActive(true);
        foreach (Transform child in selectPanel.transform)
        {
            child.GetComponent<CharacterSelectButton>().OnCharacterSelected += SelectCharacter;
        }
        subscribed = true;
        CharacterSelectActive = true;
    }
    public void DeactivateCharacterSelect()
    {
        selectPanel.SetActive(false);
        foreach (Transform child in selectPanel.transform)
        {
            child.GetComponent<CharacterSelectButton>().OnCharacterSelected -= SelectCharacter;
        }
        subscribed = false;
        CharacterSelectActive = false;
    }
    private void ActivateTooltip(Item e)
    {
        tooltip.gameObject.SetActive(true);
        tooltip.UpdateUI(e);
    }
    private void DeactivateTooltip()
    {
        tooltip.gameObject.SetActive(false);
    }
}
