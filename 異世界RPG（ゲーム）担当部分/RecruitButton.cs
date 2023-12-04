using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class RecruitButton : MonoBehaviour
{
    private Character heldChar;

    public void AssignCharacter(string name, int level=1)
    {
        heldChar = CharacterFactory.GetCharacter(name, Team.Ally, level);
    }

    public void AssignCharacter(Character character)
    {
        heldChar = character;
    }

    public void RecruitCharacter()
    {
        GameManager.instance.allyPool.Add(heldChar);
    }

    private void OnMouseUp()
    {
        if (heldChar != null && GameManager.instance.Gold - heldChar.Value >= 0)
        {
            RecruitCharacter();
            GameManager.instance.ModifyGold(-heldChar.Value);
            Destroy(gameObject);
        }
    }
}
