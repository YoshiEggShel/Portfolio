using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class StatusIcon : MonoBehaviour
{
    public StatusClassification classification {get; protected set;}

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void Initialise(StatusClassification classific, Sprite sprite)
    {
        classification = classific;
        this.GetComponent<Image>().sprite = sprite;
    }

    public void DestoryIcon()
    {
        Destroy(this.gameObject);
    }
}
