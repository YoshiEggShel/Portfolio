using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.UI;

public class ExpScreen : MonoBehaviour
{
    private float gainedExperience;
    [SerializeField]
    private TMP_Text totalExpText;
    [SerializeField]
    private ExpInfo expPrefab;
    [SerializeField]
    private Transform expLayout;
    [SerializeField]
    private Button exitButton;

    public event Action OnAllExperienceGiven;
    private List<Character> characters = new List<Character>();
    AudioSource audioSource;
    AudioClip expTick;

    public void Awake()
    {
        audioSource = gameObject.GetComponent<AudioSource>();
        if (audioSource == null)
            audioSource = gameObject.AddComponent<AudioSource>();
        expTick = SoundManager.instance.FindMatchingAudio("ExpTick", "sfx");
    }

    public void OnEnable()
    {
        Clear();
        GameManager.instance.allyList.ForEach(c => AddCharacter(c));
        exitButton.gameObject.SetActive(false);
    }

    public void AddCharacter(Character c)
    {
        ExpInfo newInfo = Instantiate(expPrefab.gameObject, expLayout).GetComponent<ExpInfo>();
        newInfo.Initialise(c);
        characters.Add(c);
    }

    public void Clear()
    {
        List<GameObject> toDelete = new List<GameObject>();
        foreach(Transform t in expLayout)
        {
            toDelete.Add(t.gameObject);
        }
        toDelete.ForEach(g => Destroy(g));

        characters.Clear();
    }

    public void SetExp(float experience)
    {
        gainedExperience = experience;
        totalExpText.text = Mathf.RoundToInt(gainedExperience).ToString();
    }

    public void GiveExp()
    {
        StartCoroutine(GainExpRoutine());
    }

    private IEnumerator GainExpRoutine()
    {
        while (gainedExperience > 0)
        {
            yield return new WaitForSeconds(0.02f);
            if (gainedExperience > 1)
            {
                foreach (Character c in characters)
                {
                    c.AddExperience(1);
                }
                gainedExperience -= 1;
            }
            else
            {
                foreach (Character c in characters)
                {
                    c.AddExperience(gainedExperience);
                }
                gainedExperience = 0;
            }
            PlayExpTick();
            totalExpText.text = Mathf.RoundToInt(gainedExperience).ToString();
        }
        exitButton.gameObject.SetActive(true);
        OnAllExperienceGiven?.Invoke();
        yield break;
    }

    public void PlayExpTick()
    {
        audioSource.PlayOneShot(expTick);
    }
}
