using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class AllEquipment : MonoBehaviour
{
    [SerializeField]
    private Transform layout;
    [SerializeField]
    private GameObject btnPrefab;
    [SerializeField]
    private List<GameObject> equipmentLst;
    [SerializeField]
    private ItemTooltip tooltip;
    [SerializeField]
    private GameObject selectPanel;

    private bool subscribed = false;

    private EquipmentBtn selectedButton;
    // Start is called before the first frame update
    void Start()
    {
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    void OnEnable()
    {
        tooltip.gameObject.SetActive(false);
        foreach (Equipment equipment in GameManager.instance.Inventory.GetEquipment())
        {
            GameObject prefab = Instantiate(btnPrefab, Vector3.zero, Quaternion.identity);
            EquipmentBtn prefabScript = prefab.AddComponent<EquipmentBtn>();
            prefabScript.Initialise(equipment);
            prefab.transform.SetParent(layout, false);
            equipmentLst.Add(prefab);

            prefabScript.OnHover += ActivateTooltip;
            prefabScript.OnExit += DeactivateTooltip;
            prefabScript.OnClick += SelectItem;
        }
    }

    void OnDisable()
    {
        tooltip.gameObject.SetActive(false);
        foreach (GameObject btn in equipmentLst)
        {
            Destroy(btn);
        }
        equipmentLst.Clear();
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
        int amount = selectedButton.TrackedEquipment.Amount;
        Debug.Log(amount);
        bool success = GameManager.instance.Inventory.EquipEquipment(selectedButton.TrackedEquipment, selected);
        if (success && amount == 1)
        {
            Destroy(selectedButton.gameObject);
        }
    }

    private void SelectItem(EquipmentBtn selected)
    {
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

    private void ActivateCharacterSelect()
    {
        selectPanel.SetActive(true);
        foreach (Transform child in selectPanel.transform)
        {
            child.GetComponent<CharacterSelectButton>().OnCharacterSelected += SelectCharacter;
        }
        subscribed = true;
    }
    private void DeactivateCharacterSelect()
    {
        selectPanel.SetActive(false);
        foreach (Transform child in selectPanel.transform)
        {
            child.GetComponent<CharacterSelectButton>().OnCharacterSelected -= SelectCharacter;
        }
        subscribed = false;
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
