using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class QuestMapCharInfo : MonoBehaviour
{

    [SerializeField]
    private TextMeshProUGUI nameTxt;
    [SerializeField]
    private TextMeshProUGUI manaTxt;
    [SerializeField]
    private TextMeshProUGUI healthTxt;
    [SerializeField]
    private Slider healthGauge;
    [SerializeField]
    private Slider manaGauge;
    [SerializeField]
    public StatusManager statusManager { get; protected set; }
    [SerializeField]
    private float lerpSpeed;
    private int healthValue;
    private int manaValue;
    private bool is_charging = false;
    [SerializeField]
    private Image charIcon;

    public Character trackedCharacter;

    // Start is called before the first frame update
    void Awake()
    {
        statusManager = GetComponentInChildren<StatusManager>();
    }

    // Update is called once per frame
    void Update()
    {
        UpdateGaugeGradually();
    }

    ///<summary>Assigns values to CharInfo Class variables.</summary>
    public void Initialise(Character c)
    {
        trackedCharacter = c;

        c.OnStatusAddedEvent += OnStatusAdded;
        c.OnStatusRemovedEvent += OnStatusRemoved;
        //c.OnResolveEvent += OnResolve; NOT WORKING FOR SOME REASON

        healthGauge.minValue = 0;
        manaGauge.minValue = 0;
        healthValue = trackedCharacter.Stats["mana"].Value;
        manaValue = trackedCharacter.Stats["health"].Value;
        charIcon.sprite = trackedCharacter.Icon;
        UpdateUI();

        foreach (Status s in c.StatusEffects)
        {
            statusManager.AddStatusIcon(s.Classification);
        }
    }

    ///<summary>Updates values for the assigned Ui elements.</summary>
    public void UpdateUI()
    {
        //Debug.Log("Updated Quest Map UI");
        nameTxt.text = trackedCharacter.Name;
        manaValue = trackedCharacter.Stats["mana"].Value;
        int manaCap = trackedCharacter.Stats["mana"].Max;
        healthValue = trackedCharacter.Stats["health"].Value;
        int healthCap = trackedCharacter.Stats["health"].Max;

        manaGauge.maxValue = manaCap;
        manaTxt.text = "MP(" + manaValue.ToString() + "/" + manaCap.ToString() + ")";

        healthGauge.maxValue = healthCap;
        healthTxt.text = "HP(" + healthValue.ToString() + "/" + healthCap.ToString() + ")";
    }

    ///<summary>Lerps the health and mana gauge values.</summary>
    private void UpdateGaugeGradually()
    {
        if (manaGauge.value != manaValue)
        {
            manaGauge.value = Mathf.Lerp(manaGauge.value, manaValue, Time.deltaTime * lerpSpeed);
        }
        if (healthGauge.value != healthValue)
        {
            healthGauge.value = Mathf.Lerp(healthGauge.value, healthValue, Time.deltaTime * lerpSpeed);
        }
    }

    public void OnResolve(ResolveResult result)
    {
        UpdateUI();
    }

    private void OnStatusAdded(Status s)
    {
        if (trackedCharacter.NumberOfStatusOfType(s.Classification) == 1)
        {
            statusManager.AddStatusIcon(s.Classification);
        }
    }

    private void OnStatusRemoved(Status s)
    {
        if (trackedCharacter.NumberOfStatusOfType(s.Classification) == 0)
        {
            statusManager.RemoveStatusIcon(s.Classification);
        }
    }
}
