using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class CharacterStatusUi : MonoBehaviour
{
    [SerializeField]
    private Image charimage;
    [SerializeField]
    private List<TextMeshProUGUI> textLst;
    private Character refChar;
    [SerializeField]
    private List<EquipmentBtn> equipmentLst;
    [SerializeField]
    private List<CharacterSelectButton> characterSelectButtons;

    public enum StatType
    {
        NAME,
        LEVEL,
        HEALTH,
        MANA,
        ATTACK,
        DEFENCE,
        SPEED
    }

    public void UpdateUi(Character c)
    {
        refChar = c;
        this.gameObject.SetActive(true);
        charimage.sprite = refChar.FullBodySprite; 
        textLst[(int)StatType.NAME].text = refChar.Name;
        textLst[(int)StatType.LEVEL].text = "Level: " + refChar.Level.ToString();
        textLst[(int)StatType.HEALTH].text = "Health: " + refChar.Stats["health"].Value + "/" + refChar.Stats["health"].Max;
        textLst[(int)StatType.MANA].text = "Mana: " + refChar.Stats["mana"].Value + "/" + refChar.Stats["mana"].Max;
        textLst[(int)StatType.ATTACK].text = "Attack: " + refChar.Stats["attack"].Value;
        textLst[(int)StatType.DEFENCE].text = "Defence: " + refChar.Stats["defence"].Value;
        textLst[(int)StatType.SPEED].text = "Speed: " + refChar.Stats["speed"].Value;
        for(int i = 0; i < refChar.equipment.Length; i++)
        {
            if (refChar.equipment[i] != null)
            {
                equipmentLst[i].Initialise(refChar.equipment[i]);
                equipmentLst[i].gameObject.SetActive(true);
            }
            else
            {
                equipmentLst[i].gameObject.SetActive(false);
            }
        }
    }

    // Start is called before the first frame update
    void Start()
    {
        foreach (CharacterSelectButton button in characterSelectButtons)
        {
            button.OnCharacterSelected += UpdateUi;
        }

        foreach(EquipmentBtn equipment in equipmentLst)
        {
            equipment.OnClick += Unequip;
        }
    }

    private void Unequip(EquipmentBtn equipmentBtn)
    {
        GameManager.instance.Inventory.Unequip(refChar, equipmentBtn.TrackedEquipment.equipSlot);
        equipmentBtn.gameObject.SetActive(false);
        UpdateUi(refChar);
    }
}
