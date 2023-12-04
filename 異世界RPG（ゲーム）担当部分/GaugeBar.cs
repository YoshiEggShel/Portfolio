using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

///<summary>Class in charge of the gradient of a Ui gauge.</summary>
public class GaugeBar : MonoBehaviour
{
    [SerializeField]
    private Slider slider;
    [SerializeField]
    private Gradient gradient;
    [SerializeField]
    private Image fill;
    private Color originalColour;
    [SerializeField]
    private Color colourChange;

    void Start()
    {
        originalColour = fill.color;
    }

    ///<summary>Changes the colour of the fill colour as specified in gradient value.</summary>
    public void OnValueChanged()
    {
        fill.color = gradient.Evaluate(slider.normalizedValue);
    }

    public void ChangeColour()
    {
        fill.color = colourChange;
    }

    public void RevertColour()
    {
        fill.color = originalColour;
    }
}
