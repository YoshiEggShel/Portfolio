package unsw.loopmania;

public class GoalOR extends Goal {
    private Goal g1;
    private Goal g2;

    public GoalOR(Goal g1, Goal g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    //These two are for testing
    public Goal getG1() {
        return g1;
    }

    public Goal getG2() {
        return g2;
    }

    @Override
    public Boolean checkCompleted(LoopManiaWorld world){
        return (g1.checkCompleted(world) || g2.checkCompleted(world));
    }
}
