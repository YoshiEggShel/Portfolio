package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.ItemFactory.ItemType;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.javatuples.Pair;

public class LoopManiaWorldTest {
    @Test
    public void TestGatherAllies(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);
        Ally dummyAlly = new Ally(dummyPos);
        dummyChar.AddAlly(dummyAlly);
        Card towerCard = d.loadCard(TowerCard.class).getValue0();
        d.convertCardToBuildingByCoordinates(towerCard.getX(), towerCard.getY(), 1, 1);

        assertTrue(d.gatherAllies().size() == 3);
    }

    @Test
    public void TestGatherEnemies(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        //Vampire within support range
        PathPosition dummyPos2 = new PathPosition(3, dummyPath);
        Vampire dummyVamp = new Vampire(dummyPos2);
        d.addEnemy(dummyVamp);
        //Zombie within battle range
        PathPosition dummyPos3 = new PathPosition(1, dummyPath);
        Zombie dummyZombie = new Zombie(dummyPos3);
        d.addEnemy(dummyZombie);
        //Slug too far away
        PathPosition dummyPos4 = new PathPosition(5, dummyPath);
        Slug dummySlug = new Slug(dummyPos4);
        d.addEnemy(dummySlug);

        assertTrue(d.gatherEnemies().size() == 2);
    }

    @Test
    public void TestRunBattles(){
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        //Slug
        Slug dummySlug = new Slug(dummyPos);
        d.addEnemy(dummySlug);

        //Run battles
        List<BasicEnemy> defeated = d.runBattles();

        //Hero should always beat a slug, so check that there is 1 defeated enemy
        assertTrue(defeated.size() == 1);
        //The only memeber of the list should be the dummy slug
        assertTrue(defeated.get(0) == dummySlug);
    }

    @Test
    public void TestBattleRewards(){
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        //Slug
        Slug dummySlug = new Slug(dummyPos);
        d.addEnemy(dummySlug);

        //Run battles
        List<BasicEnemy> defeated = d.runBattles();

        //Rewards
        d.GainBattleRewards(defeated);

        //Check that numbers have gone up
        assertTrue(d.getExp() == 50);
        assertTrue(d.getGold() == 50);

    }

    @Test
    public void TestResetWorld() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        //Slug
        Slug dummySlug = new Slug(dummyPos);
        d.addEnemy(dummySlug);

        d.addUnequippedItem(ItemType.SWORD);
        d.addUnequippedItem(ItemType.SWORD);
        d.addUnequippedItem(ItemType.SWORD);
        d.addUnequippedItem(ItemType.SWORD);

        Card towerCard = d.loadCard(TowerCard.class).getValue0();
        d.convertCardToBuildingByCoordinates(towerCard.getX(), towerCard.getY(), 1, 1);
        d.loadCard(TowerCard.class).getValue0();
        d.loadCard(TowerCard.class).getValue0();
        d.loadCard(TowerCard.class).getValue0();

        assertEquals(2, d.getBuildings().size());
        assertEquals(3, d.getCards().size());
        assertEquals(1, d.getEnemy().size());
        assertEquals(4, d.getUnequippedInventoryItems().size());

        d.restartGame();

        assertEquals(1, d.getBuildings().size());
        assertEquals(0, d.getCards().size());
        assertEquals(0, d.getEnemy().size());
        assertEquals(0, d.getUnequippedInventoryItems().size());

    }


    @Test
    public void TestGoldSpawn() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnGold();
        }

        assertTrue(d.getGoldOnMap().size() > 0);
        
    }

    @Test
    public void TestPickGoldSpawn() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(1, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnGold();
        }

        for(int i = 0; i < 9; i++) {
            d.runTickMoves();
            d.pickUpGold();
        }
        assertTrue(d.getGold() > 0);
    }

    @Test
    public void TestPossibleEnemyPosition() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
        new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnEnemies();
        }

        assertTrue(d.getEnemy().size() > 0);
    }

    @Test
    public void TestPossiblePotionSpawn() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnPotion();
        }

        assertTrue(d.getPotionOnMap().size() > 0);
    }

    @Test
    public void TestPickUpPotion() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(1, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnPotion();
        }

        for(int i = 0; i < 9; i++) {
            d.runTickMoves();
            d.pickUpPotion();
        }
        assertTrue(d.getUnequippedInventoryItems().size() > 0);
    }

    @Test
    public void TestItemSpawn() {
        //Path
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));

        //World
        LoopManiaWorld d = new LoopManiaWorld(3, 3, dummyPath);
        HerosCastle c = new HerosCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        d.setCastle(c);

        //Character
        PathPosition dummyPos = new PathPosition(1, dummyPath);
        Character dummyChar = new Character(dummyPos);
        d.setCharacter(dummyChar);

        for(int i = 0; i < 200; i++) {
            d.possiblySpawnItem();
        }

        for(int i = 0; i < 9; i++) {
            d.runTickMoves();
            d.pickUpPotion();
            d.pickUpGold();
        }
        assertTrue(d.getGold() > 0);
        assertTrue(d.getUnequippedInventoryItems().size() > 0);
    }
}
