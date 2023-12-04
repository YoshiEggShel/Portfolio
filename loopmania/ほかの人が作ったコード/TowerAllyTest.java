package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class TowerAllyTest {
    @Test
    public void TestAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        TowerAlly dummyTower = new TowerAlly(dummyPos);
        Slug dummySlug = new Slug(dummyPos);
        
        dummyTower.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 40); 
    }
}
