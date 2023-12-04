using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ObjectFollower : MonoBehaviour
{
    public GameObject followedObject;

    // Update is called once per frame
    void Update()
    {
        if (followedObject != null)
        {
            transform.position = followedObject.transform.position;
        }
    }
}
