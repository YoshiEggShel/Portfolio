using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class NavigationPanel : MonoBehaviour
{
    private List<INavigationSelectable> selectables = new List<INavigationSelectable>();

    private INavigationSelectable currentSelectable;
    private int currentSelectableIndex = 0;

    public void AddSelectable(INavigationSelectable selectable)
    {
        selectables.Add(selectable);
        GameObject go = selectable.GetGameObject();
        go.transform.SetParent(transform, false);
    }

    private void EnterSelectable(int i)
    {
        ClearCurrentSelectable();
        if (selectables.Count == 0)
            return;
        currentSelectable = selectables[i];
        currentSelectable.OnSelectEnter();
    }

    public void GoUp()
    {
        if (selectables.Count == 0)
            return;
        if (currentSelectableIndex <= 0)
            return;
        currentSelectableIndex--;
        EnterSelectable(currentSelectableIndex);
    }

    public void GoDown()
    {
        if (selectables.Count == 0)
            return;
        if (currentSelectableIndex >= selectables.Count - 1)
            return;
        currentSelectableIndex++;
        EnterSelectable(currentSelectableIndex);
    }

    public void Select()
    {
        if (currentSelectable == null)
            return;
        currentSelectable.OnSelect();
    }

    public void Clear()
    {
        ClearCurrentSelectable();
        selectables.ForEach(s => Destroy(s.GetGameObject()));
        selectables.Clear();
        currentSelectableIndex = 0;
    }

    public void ClearWithoutDestroying()
    {
        ClearCurrentSelectable();
        selectables.Clear();
        currentSelectableIndex = 0;
    }

    private void ClearCurrentSelectable()
    {
        if (currentSelectable != null)
        {
            currentSelectable.OnSelectExit();
            currentSelectable = null;
        }
    }

    public void RemoveSelectable(INavigationSelectable selectable)
    {
        if (!selectables.Contains(selectable))
            return;
        if (selectable == currentSelectable)
            ClearCurrentSelectable();
        selectables.Remove(selectable);
        Destroy(selectable.GetGameObject());
        EnterSelectable(currentSelectableIndex);
    }

    public void EnterPanel()
    {
        currentSelectableIndex = 0;
        if (selectables.Count > 0)
            EnterSelectable(currentSelectableIndex);
    }

    public void DynamicInitialise()
    {
        foreach(Transform t in transform)
        {
            QuickSelectable check = t.GetComponent<QuickSelectable>();
            if (check == null)
                continue;
            selectables.Add(check);
        }
        currentSelectableIndex = 0;
    }
}
