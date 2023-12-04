using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TargetPointer : MonoBehaviour
{
    [SerializeField]
    private Camera targetCamera;
    private Transform target;

    // Update is called once per frame
    void Update()
    {
        if (target == null)
            return;

        transform.position = new Vector3(target.position.x, transform.position.y, target.position.z);

        Vector3 lookTarget = targetCamera.transform.position;
        lookTarget.y = transform.position.y;
        transform.LookAt(lookTarget);
    }

    public void PointAt(Actor target)
    {
        this.target = target.transform;
        transform.position = target.transform.position;
        transform.Translate(Vector3.up * 3f);
        gameObject.SetActive(true);
    }

    public void Unpoint()
    {
        transform.parent = null;
        gameObject.SetActive(false);
    }
}
