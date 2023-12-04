package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class GoalTest {
    @Test
    public void TestGoalSINGLE(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        GoalSINGLE goal = new GoalSINGLE("gold", 1000);
        d.setGoal(goal);
        d.setGold(999);
        assertTrue(!goal.checkCompleted(d));
        d.setGold(1000);
        assertTrue(goal.checkCompleted(d));

    }

    @Test
    public void TestGoalSINGLEBosses(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        GoalSINGLE goal = new GoalSINGLE("bosses", 1000);
        d.setGoal(goal);
        d.setDoggieDefeated(true);
        assertTrue(!goal.checkCompleted(d));
        d.setElanDefeated(true);
        assertTrue(goal.checkCompleted(d));

    }

    @Test
    public void TestGoalAND(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        GoalSINGLE g1 = new GoalSINGLE("gold", 1000);
        GoalSINGLE g2 = new GoalSINGLE("experience", 2000);
        GoalAND goal = new GoalAND(g1, g2);
        d.setGoal(goal);
        d.setGold(1000);
        d.setExp(1000);
        assertTrue(!goal.checkCompleted(d));
        d.setExp(2000);
        assertTrue(goal.checkCompleted(d));
        
    }

    @Test
    public void TestGoalOR(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        GoalSINGLE g1 = new GoalSINGLE("gold", 1000);
        GoalSINGLE g2 = new GoalSINGLE("experience", 2000);
        GoalOR goal = new GoalOR(g1, g2);
        d.setGoal(goal);
        d.setGold(1000);
        d.setExp(1000);
        assertTrue(goal.checkCompleted(d));
    }
}
