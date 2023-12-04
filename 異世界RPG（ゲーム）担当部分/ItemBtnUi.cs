using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using TMPro;

public class ItemBtnUi : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler, IPointerDownHandler , INavigationSelectable
{
    public Item TrackedItem { get; protected set; }

    public event Action<ItemBtnUi> OnClick;
    public event Action<Item> OnHover;
    public event Action OnExit;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void Initialise(Item item)
    {
        TrackedItem = item;
        gameObject.GetComponentInChildren<TextMeshProUGUI>().text = item.Name;
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        OnSelect();
    }

    public void OnPointerEnter(PointerEventData eventData)
    {
        OnSelectEnter();
    }

    public void OnPointerExit(PointerEventData eventData)
    {
        OnSelectExit();
    }

    public void OnSelectEnter()
    {
        OnHover?.Invoke(TrackedItem);
    }

    public void OnSelectExit()
    {
        OnExit?.Invoke();
    }

    public void OnSelect()
    {
        OnClick?.Invoke(this);
    }

    public GameObject GetGameObject()
    {
        return gameObject;
    }
}
