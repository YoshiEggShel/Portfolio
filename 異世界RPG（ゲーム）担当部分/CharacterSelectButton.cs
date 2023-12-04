using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class CharacterSelectButton : MonoBehaviour, IPointerDownHandler
{
    private Character referenceCharacter;
    public event Action<Character> OnCharacterSelected;

    public void Initialise(Character c)
    {
        referenceCharacter = c;
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        OnCharacterSelected?.Invoke(referenceCharacter);
        
    }
}
