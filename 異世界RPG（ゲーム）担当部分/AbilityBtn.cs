using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using TMPro;

///<summary>Class in charge of the buttons generated for abilities.</summary>
public class AbilityBtn : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler, IPointerClickHandler, INavigationSelectable
{
    private int abilityIndex;
    [SerializeField]
    private TextMeshProUGUI currText;

    public event Action<int> OnClickAbility;
    public event Action<int> OnHover;
    public event Action OnHoverExit;

    [SerializeField]
    private Sprite selectedSprite;
    private Sprite normalSprite;
    private Image image;

    void Awake()
    {
        image = GetComponent<Image>();
        normalSprite = image.sprite;
    }

    ///<summary>Sends ability index value upon click</summary>
    public GameObject GetGameObject()
    {
        return gameObject;
    }

    ///<summary>Initialises class variables</summary>
    public void Initialise(int index, string abiName)
    {
        abilityIndex = index;
        currText.text = abiName;
    }

    public void OnPointerClick(PointerEventData eventData)
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

    public void OnSelect()
    {
        OnClickAbility?.Invoke(abilityIndex);
        SoundManager.instance.PlayAudio("PressBtn", false, "system");
    }

    public void OnSelectEnter()
    {
        image.sprite = selectedSprite;
        OnHover?.Invoke(abilityIndex);
    }

    public void OnSelectExit()
    {
        image.sprite = normalSprite;
        OnHoverExit?.Invoke();
    }
}
