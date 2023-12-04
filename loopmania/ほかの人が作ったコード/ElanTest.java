package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class ElanTest {
    @Test
    public void TestHeal(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Elan dummyElan = new Elan(dummyPos);
        Zombie dummyZombie = new Zombie(dummyPos);

        dummyZombie.damageHealth(20);
        dummyElan.AttackTarget(dummyZombie, 12);

        assertTrue(dummyZombie.getHealth() == 92); 
    }

    @Test
    public void TestNoSelfHeal(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Elan dummyElan = new Elan(dummyPos);

        dummyElan.damageHealth(30);
        dummyElan.AttackTarget(dummyElan, 10);

        assertTrue(dummyElan.getHealth() == 720); 
    }
}
