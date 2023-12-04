using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public interface INavigationSelectable
{
    void OnSelectEnter();
    void OnSelectExit();
    void OnSelect();

    GameObject GetGameObject();
}
