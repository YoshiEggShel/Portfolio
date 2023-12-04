using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GambitActionSelectUI : MonoBehaviour
{
    [SerializeField]
    private GambitActionSelectButton buttonPrefab;
    [SerializeField]
    private Transform gridGroup;

    public event Action<string> OnSelected;

    public void ClearGrid()
    {
        List<Transform> children = new List<Transform>();
        foreach (Transform child in gridGroup)
        {
            children.Add(child);
        }

        children.ForEach(t => Destroy(t.gameObject));
    }

    public void CreateGrid(ICollection<Consumable> consumables)
    {
        ClearGrid();
        foreach (Consumable consumable in consumables)
        {
            GambitActionSelectButton newButton = Instantiate(buttonPrefab.gameObject, gridGroup).GetComponent<GambitActionSelectButton>();
            newButton.Initialise(consumable.Name);
            newButton.OnClick += NotifyActionSelect;
        }
    }

    public void CreateGrid(ICollection<Ability> abilities)
    {
        ClearGrid();
        foreach (Ability ability in abilities)
        {
            GambitActionSelectButton newButton = Instantiate(buttonPrefab.gameObject, gridGroup).GetComponent<GambitActionSelectButton>();
            newButton.Initialise(ability.Name);
            newButton.OnClick += NotifyActionSelect;
        }
    }

    public void NotifyActionSelect(string action)
    {
        OnSelected?.Invoke(action);
    }
}
