using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class BattleTooltip : MonoBehaviour
{
    [SerializeField]
    private GameObject characterPanel;
    [SerializeField]
    private Text characterText;
    [SerializeField]
    private GameObject abilityPanel;
    [SerializeField]
    private Text abilityText;

    public void SetCharacter(Character character)
    {
        characterText.text = character.Name;
    }

    public void SetCharacterPanelActive(bool active)
    {
        characterPanel.SetActive(active);
    }

    public void SetAbility(Ability ability)
    {
        abilityText.text = ability.Name + " - " + ability.Description;
    }

    public void SetAbilityPanelActive(bool active)
    {
        abilityPanel.SetActive(active);
    }
}
