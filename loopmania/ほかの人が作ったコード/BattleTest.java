package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.ItemFactory.ItemType;

import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class BattleTest {
    //A hero should always beat a slug, so the initial test will use the slug enemy
    //Currently fails because getTargetAlly is not yet implemented, because ally is not implemented
    @Test
    public void TestCombat(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummySlug);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        assertTrue(dummyBattle.getParticipants().size() == 2); //Check that both hero and slug are in the battle order

        dummyBattle.Fight();

        assertTrue(dummyBattle.getAllies().size() == 1); //Check that the hero is still alive
        assertTrue(dummyBattle.getDefeated().size() == 1); //Check that the slug has been defeated
    }

    //Tests that the participants list is sorted correctly
    @Test
    public void TestTurnOrder(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Vampire dummyVamp = new Vampire(dummyPos);
        Zombie dummyZombie = new Zombie(dummyPos);
        Character dummyChar = new Character(dummyPos);
        Ally dummyAlly = new Ally(dummyPos);
        TowerAlly dummyTower = new TowerAlly(dummyPos);

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        allies.add(dummyAlly);
        allies.add(dummyTower);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummySlug);
        enemies.add(dummyVamp);
        enemies.add(dummyZombie);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        assertTrue(dummyBattle.getParticipants().get(0) == dummyVamp);
        assertTrue(dummyBattle.getParticipants().get(1) == dummyChar);
        assertTrue(dummyBattle.getParticipants().get(2) == dummySlug);
        assertTrue(dummyBattle.getParticipants().get(3) == dummyAlly);
        assertTrue(dummyBattle.getParticipants().get(4) == dummyZombie);
        assertTrue(dummyBattle.getParticipants().get(5) == dummyTower);
    }

    //Tests that the get target ally function works as intended
    @Test
    public void TestGetTargetAlly(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Vampire dummyVamp = new Vampire(dummyPos);
        Zombie dummyZombie = new Zombie(dummyPos);
        Character dummyChar = new Character(dummyPos);
        Ally dummyAlly = new Ally(dummyPos);
        TowerAlly dummyTower = new TowerAlly(dummyPos);

        dummyChar.AddAlly(dummyAlly);

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        allies.add(dummyAlly);
        allies.add(dummyTower);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummySlug);
        enemies.add(dummyVamp);
        enemies.add(dummyZombie);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        //An ally exists, so it should be targeted first
        assertTrue(dummyBattle.getTargetAlly() == dummyAlly);
    }

    //Tests that the get target enemy function works as intended
    @Test
    public void TestGetTargetEnemy(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Vampire dummyVamp = new Vampire(dummyPos);
        Zombie dummyZombie = new Zombie(dummyPos);
        Character dummyChar = new Character(dummyPos);

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummyVamp);
        enemies.add(dummySlug);
        enemies.add(dummyZombie);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        //dummyZombie, being added last, is an ally of the main enemy, and thus should be targeted first
        assertTrue(dummyBattle.getTargetEnemy() == dummyZombie);
    }

    //Tests that the character revives if it is reduced to 0hp and can revive
    @Test
    public void TestHeroRevives(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);
        dummyChar.setHealth(1);
        ItemFactory itemfactory = new ItemFactory();
        dummyChar.equipItem(itemfactory.createItem(ItemType.THE_ONE_RING, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0)));

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummySlug);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        dummyBattle.Fight();

        //During the course of the fight, the character should die, revive, then win
        assertTrue(!dummyChar.canRevive);
        
    }

    //Tests that the characters dies if it is reduced to 0hp and cannot revive
    @Test
    public void TestHeroDies(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);
        dummyChar.setHealth(1);

        ArrayList<MovingEntity> allies = new ArrayList<MovingEntity>();
        allies.add(dummyChar);
        ArrayList<MovingEntity> enemies = new ArrayList<MovingEntity>();
        enemies.add(dummySlug);

        Battle dummyBattle = new Battle(dummyChar, allies, enemies);

        dummyBattle.Fight(); 

        //Battle should break when hero is 0hp or less

        assertTrue(dummyChar.getHealth() <= 0);
    }
}
