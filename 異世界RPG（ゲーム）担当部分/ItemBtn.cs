using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

///<summary>Class in charge of the buttons generated for abilities.</summary>
public class ItemBtn : MonoBehaviour, INavigationSelectable
{
    public Item TrackedItem { get; protected set; }
    [SerializeField]
    private TextMeshProUGUI nameText;
    [SerializeField]
    private TextMeshProUGUI amountText;
    [SerializeField]
    private Sprite selectedSprite;
    private Sprite normalSprite;
    private Image image;

    public event Action<Item> OnClickItem;

    void Awake()
    {
        image = GetComponent<Image>();
        normalSprite = image.sprite;
    }

    ///<summary>Sends ability index value upon click</summary>
    public void Click()
    {
        OnSelect();
    }

    ///<summary>Update ui to display tracked item's stats</summary>
    public void Initialise(Item item)
    {
        TrackedItem = item;
        UpdateUI();
    }

    public void UpdateUI()
    {
        if (TrackedItem != null)
        {
            nameText.text = TrackedItem.Name;
            amountText.text = TrackedItem.Amount.ToString();
        }
    }

    public void OnSelectEnter()
    {
        image.sprite = selectedSprite;
    }

    public void OnSelectExit()
    {
        image.sprite = normalSprite;
    }

    public void OnSelect()
    {
        OnClickItem?.Invoke(TrackedItem);
        SoundManager.instance.PlayAudio("PressBtn", false, "system");
    }

    public GameObject GetGameObject()
    {
        return gameObject;
    }
}
