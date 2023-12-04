using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ActorQueue
{
    public event Action<Actor> OnCurrentActorChanged;
    private Actor currentActor;
    private Actor CurrentActor
    {
        get
        {
            return currentActor;
        }
        set
        {
            bool actualChange = currentActor != value;
            currentActor = value;
            if (actualChange)
                OnCurrentActorChanged?.Invoke(value);
        }
    }
    private Queue<Actor> actorQueue;

    public ActorQueue(IEnumerable<Actor> actors)
    {
        actorQueue = new Queue<Actor>();

        foreach (Actor actor in actors)
        {
            actor.OnActorReady += OnActorReady;
            actor.OnActorStunned += OnActorUnavailable;
            actor.OnCharacterDeathEvent += OnActorUnavailable;
        }
    }

    public void OnActorReady(Actor actor)
    {
        if (!actorQueue.Contains(actor))
        {
            actorQueue.Enqueue(actor);
        }
        if (actorQueue.Count == 1)
        {
            CurrentActor = actor;
        }
    }

    /*
    public void PassRequest(ActionRequest action)
    {
        currentActor.SetAction(action);
        if (actorQueue.Peek() == CurrentActor)
        {
            actorQueue.Dequeue();
            GetNextActor();
        }
        else
        {
            Debug.LogError("Actor queue not updated to current actor!");
        }
    }
    */

    public void CycleActor()
    {
        if (actorQueue.Peek() == CurrentActor)
        {
            actorQueue.Dequeue();
            GetNextActor();
        }
        else
        {
            Debug.LogError("Actor queue not updated to current actor!");
        }
    }

    // When an actor is made unavailable due to death/stun, we must remove it from the queue
    private void OnActorUnavailable(Actor actor)
    {
        if (!actorQueue.Contains(actor))
            return;

        List<Actor> listRep = new List<Actor>(actorQueue);
        listRep.Remove(actor);

        actorQueue = new Queue<Actor>(listRep);
        if (CurrentActor == actor)
        {
            GetNextActor();
        }
    }

    private void GetNextActor()
    {
        if (actorQueue.Count > 0)
            CurrentActor = actorQueue.Peek();
        else
            CurrentActor = null;
    }

    public override string ToString()
    {
        string s = "[";
        foreach (Actor a in actorQueue)
        {
            s += a.name + ", ";
        }
        s += "]";
        return s;
    }
}
