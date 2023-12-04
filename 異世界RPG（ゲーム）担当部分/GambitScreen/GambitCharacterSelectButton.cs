using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class GambitCharacterSelectButton : MonoBehaviour, IPointerDownHandler
{
    private Character character;
    private Button myButton;
    public event Action<Character> OnClick;

    void Awake()
    {
        myButton = GetComponent<Button>();
    }

    public void AssignCharacter(Character character)
    {
        this.character = character;
        myButton.image.sprite = character.Icon;
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        OnClick?.Invoke(character);
    }
}
