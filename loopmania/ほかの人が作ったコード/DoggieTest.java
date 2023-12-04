package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class DoggieTest {
    @Test
    public void TestAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Doggie dummyDog = new Doggie(dummyPos);
        Character dummyChar = new Character(dummyPos);

        dummyDog.AttackTarget(dummyChar, 0);

        assertTrue(dummyChar.getHealth() == 150); 
    }

    @Test
    public void TestStun(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Doggie dummyDog = new Doggie(dummyPos);
        Character dummyChar = new Character(dummyPos);

        dummyDog.AttackTarget(dummyChar, 20);

        assertTrue(dummyChar.stunned);
    }

    @Test
    public void TestDoggieCoinSpawn(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Doggie dummyDog = new Doggie(dummyPos);
        Character dummyChar = new Character(dummyPos);

        dummyDog.AttackTarget(dummyChar, 20);

        assertTrue(dummyChar.stunned);
    }
}
