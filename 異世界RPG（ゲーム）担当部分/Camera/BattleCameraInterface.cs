using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BattleCameraInterface : MonoBehaviour
{
    private Actor actor;
    private void Awake()
    {
        actor = GetComponent<Actor>();
    }

    public void OrbitMe()
    {
        BattleCameraController.instance.SetOrbitTarget(actor);
    }

    public void ResetCamera()
    {
        BattleCameraController.instance.ResetPosition();
    }
}
