using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ExpInfo : MonoBehaviour
{
    [SerializeField]
    private TextMeshProUGUI nameText;
    [SerializeField]
    private TextMeshProUGUI levelText;
    [SerializeField]
    private TextMeshProUGUI expText;
    [SerializeField]
    private Slider expGauge;
    [SerializeField]
    private float lerpSpeed;
    private int expValue;
    [SerializeField]
    private Image charIcon;

    public Character trackedCharacter;

    // Update is called once per frame
    void Update()
    {
        if (trackedCharacter == null)
            return;

        UpdateUI();
        UpdateGaugeGradually();
    }

    ///<summary>Assigns values to CharInfo Class variables.</summary>
    public void Initialise(Character c)
    {
        trackedCharacter = c;
        c.OnLevelUp += OnLevelUp;

        expGauge.minValue = 0;
        expValue = Mathf.FloorToInt(trackedCharacter.Experience);
        levelText.text = "Level: " + trackedCharacter.Level;
        charIcon.sprite = trackedCharacter.Icon;
        UpdateUI();
    }

    ///<summary>Updates values for the assigned Ui elements.</summary>
    public void UpdateUI()
    {
        //Debug.Log("Updated Quest Map UI");
        nameText.text = trackedCharacter.Name;
        expValue = Mathf.FloorToInt(trackedCharacter.Experience);
        int expCap = Mathf.RoundToInt(trackedCharacter.ExperienceToNextLevel);
        levelText.text = "Level: " + trackedCharacter.Level;

        expGauge.maxValue = expCap;
        expText.text = "EXP(" + expValue.ToString() + "/" + expCap.ToString() + ")";
    }

    ///<summary>Lerps the health and mana gauge values.</summary>
    private void UpdateGaugeGradually()
    {
        if (expGauge.value != expValue)
        {
            expGauge.value = Mathf.Lerp(expGauge.value, expValue, Time.deltaTime * lerpSpeed);
        }
    }

    private void OnLevelUp(Character c)
    {
        expGauge.value = 0;
    }
}
