using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class GambitActiveToggle : MonoBehaviour, IPointerDownHandler
{
    public event Action<bool> OnToggle;
    private bool active;
    private Text activeText;

    void Awake()
    {
        activeText = GetComponentInChildren<Text>();
    }

    public void Initialise(bool active)
    {
        this.active = active;
        UpdateText();
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        active = !active;
        UpdateText();
        OnToggle?.Invoke(active);
    }

    private void UpdateText()
    {
        if (active)
            activeText.text = "ON";
        else
            activeText.text = "OFF";
    }
}
