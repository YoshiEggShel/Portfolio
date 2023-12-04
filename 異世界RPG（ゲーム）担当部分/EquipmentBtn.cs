using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using TMPro;

public class EquipmentBtn : MonoBehaviour, IPointerDownHandler, IPointerEnterHandler, IPointerExitHandler, INavigationSelectable
{
    public Equipment TrackedEquipment { get; protected set; }

    public event Action<EquipmentBtn> OnClick;
    public event Action<Equipment> OnHover;
    public event Action OnExit;
    private TextMeshProUGUI text;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void Initialise(Equipment equipment)
    {
        TrackedEquipment = equipment;
        this.gameObject.GetComponentInChildren<TextMeshProUGUI>().text = equipment.Name;
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
        OnHover?.Invoke(TrackedEquipment);
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
