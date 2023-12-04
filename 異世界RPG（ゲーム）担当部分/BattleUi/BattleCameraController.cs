using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BattleCameraController : MonoBehaviour
{
    private Vector3 originalPosition;
    private Quaternion originalRotation;

    public static BattleCameraController instance;

    [SerializeField]
    private Camera mainCamera;
    private Camera scanningCamera;

    private Queue<IState> stateQueue = new Queue<IState>();
    private IState currentState;
    private IState CurrentState
    {
        get
        {
            return currentState;
        }
        set
        {
            currentState = value;
            OnStateChanged?.Invoke();
        }
    }
    private IState defaultState;

    private bool active = false;
    public bool StateLocked { get; private set; } = false;

    public Action OnStateChanged;

    private Vector3 startingTranslationPosition = new Vector3(-0.5f, 0.6250f, -0.750f);

    void Awake() {
        instance = this;
        originalPosition = mainCamera.transform.position;
        originalRotation = mainCamera.transform.rotation;
        defaultState = new CameraMoveToState(mainCamera.transform, originalPosition, originalRotation, 5f, 5f);
        scanningCamera = Instantiate(mainCamera.gameObject).GetComponent<Camera>();
        AudioListener listenerCheck = scanningCamera.GetComponent<AudioListener>();
        if (listenerCheck != null)
            Destroy(listenerCheck);
        scanningCamera.gameObject.SetActive(false);
        BattleManager.instance.OnClassSwitchAnimationStart += OnClassAnimationStart;
        BattleManager.instance.OnClassSwitchAnimationComplete += OnClassAnimationEnd;
    }

    void Update()
    {
        if (!active)
            return;

        if (CurrentState == null)
            NextState();
        if (CurrentState == null)
            return;
        CurrentState.Execute();
    }

    public void SetActive(bool active)
    {
        this.active = active;
    }

    public void SetStateLock(bool active)
    {
        StateLocked = active;
    }
    private void ClearState()
    {
        if (StateLocked)
            return;

        stateQueue.Clear();
        if (CurrentState != null)
            CurrentState.Exit();
        CurrentState = null;
    }

    public void ResetPosition() {
        if (StateLocked)
            return;

        ClearState();
        CurrentState = defaultState;
    }
    private void StartZoomOut(float speed, Actor a) {
        if (StateLocked)
            return;

        ClearState();
        CurrentState = new CameraZoomOutState(mainCamera.transform, a.transform, speed);
        CurrentState.Enter();
    }

    public void StopAnimations() {
        StopAllCoroutines();
    }

    public void StartClassSwitchAnimation(Actor a) {
        if (StateLocked)
            return;

        StartZoomOut(0.3f, a);
    }

    // TODO: Extremely hacky solution, try to learn camera maths
    public void SetBattleCameraPosition(List<Actor> actors, Vector3 target, Vector3 facingDirection, bool directlySetPosition, Vector3 startingTranslationPosition)
    {
        Debug.Log(startingTranslationPosition);
        Debug.Log("Getting new battle camera position");
        scanningCamera.gameObject.SetActive(true);
        scanningCamera.transform.position = target;
        scanningCamera.transform.rotation = Quaternion.LookRotation(facingDirection);
        //scanningCamera.transform.Translate(new Vector3(-0.5f, 0.6250f, -0.750f));
        // MinY: 0.450
        // MaxY: 0.6250
        // Magnitude of x and z components should be approx 0.9
        scanningCamera.transform.Translate(startingTranslationPosition);
        scanningCamera.transform.LookAt(target);

        while (!AllActorsInPosition(scanningCamera, actors, 0.8f))
        {
            scanningCamera.transform.Translate(Vector3.back * 0.25f);
        }

        Actor xMin = null;
        Actor xMax = null;
        Actor yMin = null;
        Actor yMax = null;

        foreach (Actor a in actors)
        {
            if (xMin == null || xMax == null || yMin == null || yMax == null)
            {
                xMin = a;
                xMax = a;
                yMin = a;
                yMax = a;
                continue;
            }

            Vector3 screenCoords = scanningCamera.WorldToScreenPoint(a.StartingPos);
            Vector3 minXScreenCoords = scanningCamera.WorldToScreenPoint(xMin.StartingPos);
            Vector3 maxXScreenCoords = scanningCamera.WorldToScreenPoint(xMax.StartingPos);
            Vector3 minYScreenCoords = scanningCamera.WorldToScreenPoint(yMin.StartingPos);
            Vector3 maxYScreenCoords = scanningCamera.WorldToScreenPoint(yMax.StartingPos);

            if (screenCoords.x < minXScreenCoords.x)
            {
                xMin = a;
            }
            else if (screenCoords.x > maxXScreenCoords.x)
            {
                xMax = a;
            }

            if (screenCoords.y < minYScreenCoords.y)
            {
                yMin = a;
            }
            else if (screenCoords.y > maxYScreenCoords.y)
            {
                yMax = a;
            }
        }

        
        Vector3 minXCoords = scanningCamera.WorldToScreenPoint(xMin.StartingPos);
        Vector3 maxXCoords = scanningCamera.WorldToScreenPoint(xMax.StartingPos);
        Vector3 minYCoords = scanningCamera.WorldToScreenPoint(yMin.StartingPos);
        Vector3 maxYCoords = scanningCamera.WorldToScreenPoint(yMax.StartingPos);
        Vector3 directionX = minXCoords.x - (scanningCamera.pixelWidth - maxXCoords.x) > 0 ? Vector3.right : Vector3.left;
        Vector3 directionY = minYCoords.y - (scanningCamera.pixelHeight - maxYCoords.y) > 0 ? Vector3.up : Vector3.down;
        float lenienceX = 0.01f;
        float lenienceY = 0.01f;

        while (Mathf.Abs(minXCoords.x - (scanningCamera.pixelWidth - maxXCoords.x)) > scanningCamera.pixelWidth * lenienceX ||
            Mathf.Abs(minYCoords.y - (scanningCamera.pixelHeight - maxYCoords.y)) > scanningCamera.pixelHeight * lenienceY)
        {
            if (Mathf.Abs(minXCoords.x - (scanningCamera.pixelWidth - maxXCoords.x)) > scanningCamera.pixelWidth * lenienceX)
                scanningCamera.transform.Translate(directionX * 0.01f);
            if (Mathf.Abs(minYCoords.y - (scanningCamera.pixelHeight - maxYCoords.y)) > scanningCamera.pixelHeight * lenienceY)
                scanningCamera.transform.Translate(directionY * 0.01f);

            minXCoords = scanningCamera.WorldToScreenPoint(xMin.StartingPos);
            maxXCoords = scanningCamera.WorldToScreenPoint(xMax.StartingPos);
            minYCoords = scanningCamera.WorldToScreenPoint(yMin.StartingPos);
            maxYCoords = scanningCamera.WorldToScreenPoint(yMax.StartingPos);

            if (!(Mathf.Abs(minXCoords.x - (scanningCamera.pixelWidth - maxXCoords.x)) > scanningCamera.pixelWidth * lenienceX))
            {
                Vector3 newDirection = minXCoords.x - (scanningCamera.pixelWidth - maxXCoords.x) > 0 ? Vector3.right : Vector3.left;
                if (newDirection != directionX)
                {
                    Debug.Log("Had to be lenient");
                    lenienceX *= 2;
                }
                directionX = newDirection;
            }

            if (!(Mathf.Abs(minYCoords.y - (scanningCamera.pixelHeight - maxYCoords.y)) > scanningCamera.pixelHeight * lenienceY))
            {
                Vector3 newDirection = minYCoords.y - (scanningCamera.pixelHeight - maxYCoords.y) > 0 ? Vector3.up : Vector3.down;
                if (newDirection != directionY)
                {
                    Debug.Log("Had to be lenient");
                    lenienceY *= 2;
                }
                directionY = newDirection;
            }

            foreach (Actor a in actors)
            {
                if (xMin == null || xMax == null || yMin == null || yMax == null)
                {
                    xMin = a;
                    xMax = a;
                    yMin = a;
                    yMax = a;
                    continue;
                }

                Vector3 screenCoords = scanningCamera.WorldToScreenPoint(a.StartingPos);
                Vector3 minXScreenCoords = scanningCamera.WorldToScreenPoint(xMin.StartingPos);
                Vector3 maxXScreenCoords = scanningCamera.WorldToScreenPoint(xMax.StartingPos);
                Vector3 minYScreenCoords = scanningCamera.WorldToScreenPoint(yMin.StartingPos);
                Vector3 maxYScreenCoords = scanningCamera.WorldToScreenPoint(yMax.StartingPos);

                if (screenCoords.x < minXScreenCoords.x)
                {
                    xMin = a;
                }
                else if (screenCoords.x > maxXScreenCoords.x)
                {
                    xMax = a;
                }

                if (screenCoords.y < minYScreenCoords.y)
                {
                    yMin = a;
                }
                else if (screenCoords.y > maxYScreenCoords.y)
                {
                    yMax = a;
                }
            }
        }

        originalPosition = scanningCamera.transform.position;
        originalRotation = scanningCamera.transform.rotation;
        IState oldDefaultState = defaultState;
        defaultState = new CameraMoveToState(mainCamera.transform, originalPosition, originalRotation, 5f, 5f);
        if (directlySetPosition && !StateLocked)
        {
            Debug.Log("Directly set camera");
            mainCamera.transform.position = originalPosition;
            mainCamera.transform.rotation = originalRotation;
            CurrentState = defaultState;
        }
        if (oldDefaultState != null && CurrentState == oldDefaultState)
            CurrentState = defaultState;
        scanningCamera.gameObject.SetActive(false);
    }

    public bool AllActorsInPosition(Camera camera, List<Actor> actors, float screenPercentage)
    {
        float xMin = (camera.pixelWidth * (1-screenPercentage))/2;
        float yMin = (camera.pixelHeight * (1-screenPercentage))/2;
        float xMax = camera.pixelWidth - xMin;
        float yMax = camera.pixelHeight - yMin;

        foreach(Actor a in actors)
        {
            Vector3 screenPoint = camera.WorldToScreenPoint(a.StartingPos);
            if (screenPoint.x < xMin || screenPoint.x > xMax || screenPoint.y < yMin || screenPoint.y > yMax)
            {
                return false;
            }
        }

        return true;
    }

    private void NextState()
    {
        if (CurrentState != null)
            CurrentState.Exit();

        CurrentState = null;
        if (stateQueue.Count == 0) 
        {
            CurrentState = defaultState;
            CurrentState.Enter();
            return;
        }

        CurrentState = stateQueue.Dequeue();

        if (CurrentState != null)
            CurrentState.Enter();
    }

    public void SetOrbitTarget(Actor a)
    {
        if (StateLocked)
            return;

        ClearState();
        stateQueue.Enqueue(new CameraOrbitState(a.transform, mainCamera.transform, 2f, 2f, 5f, 5f, 10f));
        NextState();
    }

    public void SetOrbitTarget(Actor a, float height, float radius)
    {
        if (StateLocked)
            return;

        ClearState();
        stateQueue.Enqueue(new CameraOrbitState(a.CentreOfMass, mainCamera.transform, height, radius, 5f, 5f, 10f));
        NextState();
    }

    public void SetPerspectiveTarget(Actor a)
    {
        if (StateLocked)
            return;
        ClearState();
        Vector3 offsetPos = new Vector3(-0.926f, 1.315f, -1.406f);
        Vector3 targetRot = a.transform.rotation.eulerAngles + new Vector3(4.5f, 5.5f, 0);
        stateQueue.Enqueue(new CameraMoveToState(mainCamera.transform, a.transform.position + offsetPos, Quaternion.Euler(targetRot), 5f, 5f));
    }

    public void OnClassAnimationStart(Actor a)
    {
        SetStateLock(false);
        StartClassSwitchAnimation(a);
        SetStateLock(true);
    }

    public void OnClassAnimationEnd(Actor a)
    {
        SetStateLock(false);
        StopAnimations();
        ResetPosition();
    }
}
