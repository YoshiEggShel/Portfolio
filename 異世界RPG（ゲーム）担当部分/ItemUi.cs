using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class ItemUi : MonoBehaviour
{
    [SerializeField]
    private TextMeshProUGUI itemText;
    [SerializeField]
    private TextMeshProUGUI amountTxt;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void Initialise(string itemName, string amount)
    {
        itemText.text = itemName;
        amountTxt.text = "x " + amount;
    }
}
