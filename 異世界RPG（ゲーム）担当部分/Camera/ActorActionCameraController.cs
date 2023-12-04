using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ActorActionCameraController : MonoBehaviour
{
    private BattleManager battleManager;
    [SerializeField]
    private BattleCameraController cameraController;
    private Actor sourceActor;
    private Actor focusedActor;
    void Start()
    {
        battleManager = BattleManager.instance;
        battleManager.OnActionRequestSent += ActionListener;
        battleManager.OnWaveStart += ResetState;
        battleManager.OnVictory += ResetState;
        battleManager.OnDefeat += ResetState;
    }

    public void ActionListener(Actor actor, ActionRequest request)
    {
        if (actor.Character.Team != Team.Ally)
            return;
        if (cameraController.StateLocked)
            return;
        if (sourceActor != null || focusedActor != null)
            return;
        if (request.Target == null || !request.Target.Character.Alive)
            return;

        if (request is AbilityRequest abilityRequest)
        {
            sourceActor = actor;
            Actor target = abilityRequest.Target;
            focusedActor = target;
            cameraController.SetOrbitTarget(target, 1f, 4.5f);
            cameraController.SetStateLock(true);
            cameraController.OnStateChanged += UnsubscribeActors;
            actor.OnActionComplete += StartResetRoutine;
            actor.OnCharacterDeathEvent += StartResetRoutine;
            target.OnCharacterDeathEvent += StartResetRoutine;
        }
    }

    private void StartResetRoutine()
    {
        StopAllCoroutines();
        StartCoroutine(ResetRoutine());
    }

    // For subscribing to events
    private void StartResetRoutine(Actor a)
    {
        StartResetRoutine();
    }

    private IEnumerator ResetRoutine()
    {
        yield return new WaitForSeconds(1.5f);
        ResetCamera();
        UnsubscribeActors();
        yield break;
    }

    private void ResetCamera()
    {
        cameraController.SetStateLock(false);
        cameraController.ResetPosition();
    }

    public void UnsubscribeActors()
    {
        StopAllCoroutines();
        cameraController.OnStateChanged -= UnsubscribeActors;
        if (sourceActor == null)
            return;
        sourceActor.OnActionComplete -= StartResetRoutine;
        sourceActor.OnCharacterDeathEvent -= StartResetRoutine;
        sourceActor = null;

        if (focusedActor == null)
            return;
        focusedActor.OnCharacterDeathEvent -= StartResetRoutine;
        focusedActor = null;
    }
    
    private void ResetState()
    {
        UnsubscribeActors();
        cameraController.SetStateLock(false);
    }
}
