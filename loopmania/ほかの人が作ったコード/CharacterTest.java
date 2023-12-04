package unsw.loopmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;

public class CharacterTest {
    //Tests that the characters unarmed attack does the right amount of damage
    @Test
    public void TestUnarmedAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        dummyChar.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 45); 
    }

    //Tests that the characters sword attack does the right amount of damage
    @Test
    public void TestSwordAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        //Equip Sword
        Sword dummySword = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        dummyChar.equipItem(dummySword);

        dummyChar.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 30); 
    }

    //Tests that the characters stake attack does the right amount of damage to non vampires
    @Test
    public void TestStakeAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        //Equip Stake
        Stake dummyStake = new Stake(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        dummyChar.equipItem(dummyStake);

        dummyChar.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 38); 
    }

    //Tests that the characters stake attack does the right amount of damage to vampires 
    @Test
    public void TestStakeAttackVampire(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Vampire dummyVamp = new Vampire(dummyPos);
        Character dummyChar = new Character(dummyPos);

        //Equip Stake
        Stake dummyStake = new Stake(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        dummyChar.equipItem(dummyStake); //CHANGE ALL EQUIP ITEM CODE

        dummyChar.AttackTarget(dummyVamp, 0);

        assertTrue(dummyVamp.getHealth() == 158); 
    }

    //Tests that the characters staff attack does the right amount of damage
    @Test
    public void TestStaffAttack(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        //Equip Staff
        Staff dummyStaff = new Staff(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        dummyChar.equipItem(dummyStaff);

        dummyChar.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 40); 
    }

    @Test
    public void TestAddAlly(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Ally dummyAlly = new Ally(dummyPos);
        Character dummyChar = new Character(dummyPos);
        dummyChar.AddAlly(dummyAlly);

        assertTrue(dummyChar.getAllies().size() == 1);
    }

    //Tests that the characters attack is doubled when the doubledDamage is true
    @Test
    public void TestDoubleDamage(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        dummyChar.setDoubleDamage(true);
        dummyChar.AttackTarget(dummySlug, 0);

        assertTrue(dummySlug.getHealth() == 40); 
    }

    //Tests that the Character can block an attack when equipped with a shield
    @Test
    public void TestShieldBlock(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Slug dummySlug = new Slug(dummyPos);
        Character dummyChar = new Character(dummyPos);

        // Equip Shield
        Shield dummyShield = new Shield (new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        dummyChar.equipItem(dummyShield);

        dummySlug.AttackTarget(dummyChar, 1);

        assertTrue(dummyChar.getHealth() == 200);
    }
}
