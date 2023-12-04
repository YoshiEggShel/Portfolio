package unsw.loopmania;

public abstract class Goal {
    //Goal utilises the Startegy pattern via the Check completed function:
    //All goals are essentially defined by whether they have been met or not,
    //but the each type has different criteria for checking completed,
    //which is delegated to the subclasses
    public Boolean checkCompleted(LoopManiaWorld world){
        return true; //This should never be used. Should be done by subclasses instead
    }
    //This abstract class forms the chassis for the goals composite design
    //All goals are either AND, OR or SINGLE, with SINGLE goals forming the leaves
}
