using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class EffectText : MonoBehaviour
{
    [SerializeField]
    private TextMesh text;
    [SerializeField]
    private float textOffset;
    private Transform playerCamTransform;

    // Start is called before the first frame update
    void Start()
    {
        playerCamTransform = Camera.main.transform;
    }

    // Update is called once per frame
    void LateUpdate()
    {
        if(gameObject.activeSelf)
        transform.LookAt(this.transform.position + playerCamTransform.forward);
    }

    public void DisplayTxt(string textString, string colour, Vector3 actorPosition)
    {
        text.text = textString;
        if (colour == "red")
            text.color = Color.red;
        else if (colour == "green")
            text.color = Color.green;
        else if (colour == "yellow")
            text.color = Color.yellow;
        else if (colour == "magenta")
            text.color = Color.magenta;
        else if (colour == "purple")
            text.color = new Color(0.5f, 0, 1f);
        else if (colour == "blue")
            text.color = Color.blue;

        this.transform.position = actorPosition + new Vector3(0, textOffset, 0);
        
    }

}
