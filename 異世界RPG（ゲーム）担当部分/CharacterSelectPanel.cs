using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class CharacterSelectPanel : MonoBehaviour
{
    [SerializeField]
    private Button characterSelectButtonPrefab;
    [SerializeField]
    private Transform layoutGroup;

    public void Initialise(List<Character> characters)
    {
        foreach (Character character in characters)
        {
            GameObject selectPrefab = Instantiate(characterSelectButtonPrefab.gameObject, Vector3.zero, Quaternion.identity);
            selectPrefab.GetComponentInChildren<TextMeshProUGUI>().text = character.Name;
            selectPrefab.AddComponent<CharacterSelectButton>().Initialise(character);
            selectPrefab.transform.SetParent(layoutGroup.transform, false);
        }

    }
}
