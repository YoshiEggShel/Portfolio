using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraOrbitState : IState
{
    private Transform target;
    private Transform camera;

    private CameraMoveToState moveToState;
    private float orbitSpeed;

    private bool moved;

    public CameraOrbitState(Transform target, Transform camera, float height, float radius, float moveSpeed, float rotationSpeed, float orbitSpeed)
    {
        Vector3 offset = Vector3.Normalize(camera.position - target.position) * radius + Vector3.up * height;
        moveToState = new CameraMoveToState(camera, target, offset, moveSpeed, rotationSpeed);

        this.target = target;
        this.camera = camera;

        this.orbitSpeed = orbitSpeed;

        moved = false;
    }
    public void Enter() { }

    public bool Execute()
    {
        if (!moved)
        {
            moved = moveToState.Execute();
            return false;
        }

        camera.RotateAround(target.transform.position, Vector3.up, orbitSpeed * Time.deltaTime);
        camera.LookAt(target.transform.position);

        return false;
    }

    public void Exit() { }
}
