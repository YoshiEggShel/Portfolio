package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class EnemyMoveTest {
    @Test
    public void TestSlugMove(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);

        assertTrue(dummySlug.getX() == 0);
        assertTrue(dummySlug.getY() == 0);

        dummySlug.moveUpPath();
        assertTrue(dummySlug.getX() == 1);
        assertTrue(dummySlug.getY() == 0);

        dummySlug.moveUpPath();
        dummySlug.moveUpPath();
        assertTrue(dummySlug.getX() == 2);
        assertTrue(dummySlug.getY() == 1);

        dummySlug.moveDownPath();
        dummySlug.moveDownPath();
        dummySlug.moveDownPath();
        dummySlug.moveDownPath();
        assertTrue(dummySlug.getX() == 0);
        assertTrue(dummySlug.getY() == 1);
    }

    @Test
    public void TestZombieMove(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Zombie dummyZombie = new Zombie(dummyPos);

        dummyZombie.moveDownPath();
        dummyZombie.moveDownPath();
        assertTrue(dummyZombie.getX() == 0);
        assertTrue(dummyZombie.getY() == 2);

        dummyZombie.moveUpPath();
        dummyZombie.moveUpPath();
        assertTrue(dummyZombie.getX() == 0);
        assertTrue(dummyZombie.getY() == 0);

        dummyZombie.moveDownPath();
        dummyZombie.moveUpPath();
        dummyZombie.moveUpPath();
        assertTrue(dummyZombie.getX() == 1);
        assertTrue(dummyZombie.getY() == 0);
    }

    @Test
    public void TestVampireMove(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Vampire dummyVampire = new Vampire(dummyPos);

        dummyVampire.moveDownPath();
        assertTrue(dummyVampire.getX() == 0);
        assertTrue(dummyVampire.getY() == 1);

        dummyVampire.moveDownPath();
        dummyVampire.moveUpPath();
        dummyVampire.moveUpPath();
        dummyVampire.moveUpPath();
        assertTrue(dummyVampire.getX() == 1);
        assertTrue(dummyVampire.getY() == 0);

        dummyVampire.moveDownPath();
        assertTrue(dummyVampire.getX() == 0);
        assertTrue(dummyVampire.getY() == 0);
    }
}