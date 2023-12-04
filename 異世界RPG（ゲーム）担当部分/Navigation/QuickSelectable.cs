using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class QuickSelectable : MonoBehaviour, INavigationSelectable
{
    private Button button;
    private Image image;
    private Sprite normalSprite;
    [SerializeField]
    private Sprite selectedSprite;

    void Awake()
    {
        button = GetComponent<Button>();
        image = GetComponent<Image>();
        normalSprite = image.sprite;
    }
    public GameObject GetGameObject()
    {
        return gameObject;
    }

    public void OnSelect()
    {
        button.onClick?.Invoke();
    }

    public void OnSelectEnter()
    {
        image.sprite = selectedSprite;
    }

    public void OnSelectExit()
    {
        image.sprite = normalSprite;
    }
}
