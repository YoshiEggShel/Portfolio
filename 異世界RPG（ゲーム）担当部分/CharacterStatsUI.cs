using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CharacterStatsUI : MonoBehaviour
{
    [SerializeField]
    private Image icon;
    [SerializeField]
    private Text nameText;

    private Character refCharacter;

    public void AssignCharacter(Character c)
    {
        refCharacter = c;
        icon.sprite = refCharacter.Icon;
        nameText.text = refCharacter.Name;
    }
}
