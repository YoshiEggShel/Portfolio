using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class GambitSlotUI : MonoBehaviour, IDragHandler, IDropHandler
{
    public GambitSlot Slot { get; protected set; }
    public int Number { get; protected set; }
    [SerializeField]
    private Text numberText;
    [SerializeField]
    private Text conditionText;
    [SerializeField]
    private Text actionText;
    [SerializeField]
    private Button actionButton;

    private Canvas parentCanvas;
    private GambitActiveToggle toggleButton;

    public event Action<GambitSlotUI> onActionChangeRequest;
    public event Action<PointerEventData, GambitSlotUI> OnDropped;

    private bool dragged = false;

    void Awake()
    {
        toggleButton = GetComponentInChildren<GambitActiveToggle>();
        actionButton.onClick.AddListener(NotifyActionChange);
    }

    void Update()
    {
        if (dragged)
        {
            Vector2 pos;

            RectTransformUtility.ScreenPointToLocalPointInRectangle(parentCanvas.transform as RectTransform,
                                                                    Input.mousePosition, Camera.main, out pos);

            transform.position = parentCanvas.transform.TransformPoint(pos);
        }
    }

    public void OnDrag(PointerEventData eventData)
    {
        Debug.Log("Detected drag");
        if (!dragged)
        {
            dragged = true;
            transform.SetParent(parentCanvas.transform);
        }
    }

    public void OnDrop(PointerEventData eventData)
    {
        dragged = false;
        OnDropped?.Invoke(eventData, this);
    }

    public void SetParentCanvas(Canvas parentCanvas)
    {
        this.parentCanvas = parentCanvas;
    }

    public void SetSlot(int i, GambitSlot slot)
    {
        Slot = slot;
        Number = i;
        numberText.text = i.ToString();
        conditionText.text = slot.GambitCondition.ToString();
        actionText.text = slot.ActionName;
        toggleButton.Initialise(slot.Active);
        toggleButton.OnToggle += UpdateSlotActive;
    }

    public void SetSlot(GambitSlot slot)
    {
        Slot = slot;
        conditionText.text = slot.GambitCondition.ToString();
        actionText.text = slot.ActionName;
        toggleButton.Initialise(slot.Active);
        toggleButton.OnToggle += UpdateSlotActive;
    }

    private void UpdateSlotActive(bool active)
    {
        Slot.SetActive(active);
    }

    public void SetSlotNumberText(int i)
    {
        numberText.text = i.ToString();
        Number = i;
    }

    public void NotifyActionChange()
    {
        onActionChangeRequest?.Invoke(this);
    }
}
