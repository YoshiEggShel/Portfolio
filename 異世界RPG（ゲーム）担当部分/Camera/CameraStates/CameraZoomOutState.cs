using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraZoomOutState : IState
{
    private float zoomOutSpeed;
    private Transform target;
    private Transform camera;
    public CameraZoomOutState(Transform camera, Transform target, float zoomOutSpeed)
    {
        this.camera = camera;
        this.target = target;
        this.zoomOutSpeed = zoomOutSpeed;
    }
    public void Enter()
    {
        Vector3 positionOffset = target.forward * 2 + target.transform.right * 2 + new Vector3(0, 2f, 0);
        camera.position = target.transform.position + positionOffset;
        Vector3 lookTarget = target.transform.position + new Vector3(0, 1f, 0);
        camera.LookAt(lookTarget);
    }

    public bool Execute()
    {
        camera.Translate(new Vector3(0, 0, -zoomOutSpeed * Time.deltaTime));
        return false;
    }

    public void Exit() { }
}
