package unsw.loopmania;

public interface Attack {
    public void AttackTarget (MovingEntity target, int seed); 
    //The seed is for attacks with a random effect,
        //eg trance, zombification or vampire frenzy
    //Currently takes a value of 1 to 10, inclusive, but this is easily adjusted
}
