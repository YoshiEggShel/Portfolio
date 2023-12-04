using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class GambitActionSelectButton : MonoBehaviour, IPointerDownHandler
{
    public event Action<string> OnClick;
    private string action;
    [SerializeField]
    private Text nameText;

    public void Initialise(string action)
    {
        this.action = action;
        nameText.text = action;
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        OnClick?.Invoke(action);
    }
}
