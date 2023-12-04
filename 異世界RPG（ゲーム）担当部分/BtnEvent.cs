using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class BtnEvent : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler, IPointerDownHandler, IPointerUpHandler
{
    [SerializeField]
    private Image image;
    [SerializeField]
    private Sprite idleImage;
    [SerializeField]
    private Sprite spriteClick;
    [SerializeField]
    private Sprite spriteHover;

    // Start is called before the first frame update
    void Start()
    {
        if (image != null)
            idleImage = image.sprite;
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void OnPointerEnter(PointerEventData pointerEventData)
    {
        SoundManager.instance.audioSource.PlayOneShot(SoundManager.instance.FindMatchingAudio("Hover", "system"));
        if (image != null)
            image.sprite = spriteHover;
    }

    public void OnPointerExit(PointerEventData pointerEventData)
    {
        if (image != null)
            image.sprite = idleImage;
    }

    public void OnPointerDown(PointerEventData pointerEventData)
    {
        if (image != null)
            image.sprite = spriteClick;
    }

    public void OnPointerUp(PointerEventData pointerEventData)
    {
        if (image != null)
            image.sprite = idleImage;
    }

}
