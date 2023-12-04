using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ItemTooltip : MonoBehaviour
{
    [SerializeField]
    private TextMeshProUGUI nameText;
    [SerializeField]
    private TextMeshProUGUI valueText;
    [SerializeField]
    private TextMeshProUGUI descriptionText;
    [SerializeField]
    private TextMeshProUGUI infoHeading;
    [SerializeField]
    private TextMeshProUGUI infoTextPrefab;
    [SerializeField]
    private GameObject infoTextGroup;
    [SerializeField]
    private GameObject parentCanvas;
    [SerializeField]
    private Camera uiCamera;

    private void Awake()
    {
        if (uiCamera == null)
            uiCamera = Camera.main;
        gameObject.SetActive(false);
    }

    public void UpdateUI(Item item)
    {
        ClearStats();
        nameText.text = item.Name;
        if (item.Amount > 1)
            nameText.text += " x" + item.Amount;
        valueText.text = "Value: " + item.Value.ToString();
        descriptionText.text = item.Description;
       
        if (item is Equipment equipment)
        {
            infoHeading.text = "Stat Modifiers";

            foreach (string key in equipment.StatModifiers.Keys)
            {
                TextMeshProUGUI newText = Instantiate(infoTextPrefab, infoTextGroup.transform, false).GetComponent<TextMeshProUGUI>();
                string formattedStat = equipment.StatModifiers[key] >= 0 ? "+" : "-";
                formattedStat += key.ToUpper()[0] + key.Substring(1);
                newText.text = formattedStat + ": " + equipment.StatModifiers[key].ToString();
            }
        }
        else if (item is Consumable consumable)
        {
            infoHeading.text = "Effects";

            foreach (Effect e in consumable.Effects)
            {
                TextMeshProUGUI newText = Instantiate(infoTextPrefab, infoTextGroup.transform, false).GetComponent<TextMeshProUGUI>();
                newText.text = e.ToString();
            }
        }
    }

    private void ClearStats()
    {
        var children = new List<GameObject>();
        foreach (Transform child in infoTextGroup.transform) children.Add(child.gameObject);
        children.ForEach(child => Destroy(child));
    }

    void Update()
    {
        Vector2 pos;

        RectTransformUtility.ScreenPointToLocalPointInRectangle(parentCanvas.transform as RectTransform, 
                                                                Input.mousePosition, uiCamera, out pos);

        transform.position = parentCanvas.transform.TransformPoint(pos);
    }
}
