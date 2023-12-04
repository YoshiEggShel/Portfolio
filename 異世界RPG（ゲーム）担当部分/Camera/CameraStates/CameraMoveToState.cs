using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraMoveToState : IState
{
    private Transform camera;
    private Vector3 targetPosition;
    private Quaternion targetRotation;
    private Transform targetTransform;

    private float moveSpeed;
    private float rotationSpeed;

    public CameraMoveToState(Transform camera, Vector3 targetPosition, Quaternion targetRotation, float moveSpeed, float rotationSpeed)
    {
        this.camera = camera;
        this.targetPosition = targetPosition;
        this.targetRotation = targetRotation;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    // Creates a move to state that moves relative to a target
    public CameraMoveToState(Transform camera, Transform target, Vector3 targetRelativePosition, float moveSpeed, float rotationSpeed)
    {
        this.camera = camera;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;

        targetTransform = target;
        targetPosition = targetRelativePosition;
    }

    public void Enter() { }

    public bool Execute()
    {
        if (targetTransform == null)
        {
            camera.rotation = Quaternion.Slerp(camera.rotation, targetRotation, rotationSpeed * Time.deltaTime);
            camera.position = Vector3.Lerp(camera.position, targetPosition, moveSpeed * Time.deltaTime);
            if (Vector3.Distance(camera.position, targetPosition) < 0.005f && Quaternion.Angle(camera.rotation, targetRotation) < 0.5f)
                return true;
        }
        else
        {
            targetRotation = Quaternion.LookRotation(targetTransform.position - camera.position);
            camera.rotation = Quaternion.Slerp(camera.rotation, targetRotation, rotationSpeed * Time.deltaTime);
            camera.position = Vector3.Lerp(camera.position, targetTransform.position + targetPosition, moveSpeed * Time.deltaTime);
            if (Vector3.Distance(camera.position, targetTransform.position + targetPosition) < 0.05f)
                return true;
        }

        return false;
    }

    public void Exit() { }
}
