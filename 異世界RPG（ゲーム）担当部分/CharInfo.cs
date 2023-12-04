using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

///<summary>Class in charge of Ui elements of Character Stats.</summary>
public class CharInfo : MonoBehaviour
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
    private Slider actionGauge;
    [SerializeField]
    private Slider manaGauge;
    [SerializeField]
    public StatusManager statusManager {get; protected set;}
    [SerializeField]
    private float lerpSpeed;
    private int healthValue;
    private int manaValue;
    private bool is_charging = false;
    [SerializeField]
    private GaugeBar actionGaugeBarScript;
    [SerializeField]
    private SpriteRenderer charIcon;
    [SerializeField]
    private SpriteRenderer charIconBackground;

    public Actor trackedActor { get; protected set; }

    void Awake()
    {
        statusManager = GetComponentInChildren<StatusManager>();
    }

    // Update is called once per frame
    void Update()
    {
        // TODO: Events?...
        UpdateUI();
        UpdateGaugeGradually();
    }

    ///<summary>Assigns values to CharInfo Class variables.</summary>
    public void Initialise(Actor a)
    {
        trackedActor = a;

        a.OnChargeStart += OnChargeStart;
        a.DuringChargeEvent += UpdateCharge;
        a.OnEndAttack += OnChargeEnd;

        a.OnResolveEvent += OnResolve;
        a.OnUpdateGauge += OnUpdateTimeGauge;
        a.OnApplyCost += OnCostResolve;
        a.OnStatusRemovedEvent += OnStatusRemoved;

        healthGauge.minValue = 0;
        manaGauge.minValue = 0;
        actionGauge.minValue = 0;
        healthValue = trackedActor.Character.Stats["mana"].Value;
        manaValue = trackedActor.Character.Stats["health"].Value;
        charIcon.sprite = trackedActor.Character.Icon;
        charIconBackground.sortingOrder = -1;
        UpdateUI();

        foreach (Status s in a.Character.StatusEffects)
        {
            statusManager.AddStatusIcon(s.Classification);
        }
    }

    ///<summary>Updates values for the assigned Ui elements.</summary>
    public void UpdateUI()
    {
        nameTxt.text = trackedActor.Character.Name;
        manaValue = trackedActor.Character.Stats["mana"].Value;
        int manaCap = trackedActor.Character.Stats["mana"].Max;
        healthValue = trackedActor.Character.Stats["health"].Value;
        int healthCap = trackedActor.Character.Stats["health"].Max;

        manaGauge.maxValue = manaCap;
        manaTxt.text = "MP(" + manaValue.ToString() + "/" + manaCap.ToString() + ")";

        healthGauge.maxValue = healthCap;
        healthTxt.text = "HP(" + healthValue.ToString() + "/" + healthCap.ToString() + ")";

    }

    /// <summary>
    /// Called automatically when the tracked actor starts charging
    /// </summary>
    private void OnChargeStart(Actor a)
    {
        actionGaugeBarScript.ChangeColour();
        is_charging = true;
    }

    /// <summary>
    /// Called repeatedly while the tracked actor is charging
    /// </summary>
    private void UpdateCharge()
    {
        UpdateGauge();
    }

    /// <summary>
    /// Called when the tracked actor finishes charging and starts attack
    /// </summary>
    private void OnChargeEnd()
    {
        if (is_charging)
        {
            actionGaugeBarScript.RevertColour();
            is_charging = false;
        }
    }

    private void OnResolve(ResolveResult result, Actor target)
    {
        if (target.Character == trackedActor.Character)
        {
            UpdateUI();

            if (result is StatusEffectResult statResult)
            {
                OnStatusAdded(statResult.Status, target);
            }
        }
    }

    private void OnCostResolve(Cost c, Actor target)
    {
        if (target.Character == trackedActor.Character)
            UpdateUI();
    }
    private void OnStatusRemoved(Status s, Actor target)
    {
        if (target.Character == trackedActor.Character)
            statusManager.RemoveStatusIcon(s.Classification);
    }
    private void OnStatusAdded(Status s, Actor target)
    {
        if (target.Character == trackedActor.Character)
            statusManager.AddStatusIcon(s.Classification);
    }

    private void OnUpdateTimeGauge(Actor target)
    {
        if (target.Character == trackedActor.Character)
            UpdateGauge();
    }

    ///<summary>Updates the action gauge.</summary>
    public void UpdateGauge()
    {
        if(!is_charging)
        {
            actionGauge.value = trackedActor.TimeGaugeCurrent;
            actionGauge.maxValue = trackedActor.TimeGaugeMax;
        }
        else
        {
            actionGauge.value = trackedActor.ChargeGaugeCurrent;
            actionGauge.maxValue = trackedActor.ChargeGaugeMax;
        }
        
    }

    ///<summary>Lerps the health and mana gauge values.</summary>
    private void UpdateGaugeGradually()
    {
        if(manaGauge.value != manaValue)
        {
            manaGauge.value = Mathf.Lerp(manaGauge.value, manaValue, Time.deltaTime * lerpSpeed);
        }
        if(healthGauge.value != healthValue)
        {
            healthGauge.value = Mathf.Lerp(healthGauge.value, healthValue, Time.deltaTime * lerpSpeed);
        }
    }
}

