using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.EventSystems;
using TMPro;
using UnityEngine.UI;

///<summary>Class in charge of the buttons generated for abilities.</summary>
public class TargetButton : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler, INavigationSelectable
{
    public Actor Actor { get; protected set; }
    [SerializeField]
    private TextMeshProUGUI text;

    public event Action<Actor> OnClick;
    public event Action<Actor> OnHover;
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

    ///<summary>Initialises class variables</summary>
    public void Initialise(Actor actor)
    {
        Actor = actor;
        text.text = actor.Character.Name;
        GetComponent<Button>().onClick.AddListener(ClickHandler);
    }

    public void ClickHandler()
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
        image.sprite = selectedSprite;
        OnHover?.Invoke(Actor);
    }

    public void OnSelectExit()
    {
        image.sprite = normalSprite;
        OnHoverExit?.Invoke();
    }

    public void OnSelect()
    {
        OnClick?.Invoke(Actor);
    }

    public GameObject GetGameObject()
    {
        return gameObject;
    }
}
