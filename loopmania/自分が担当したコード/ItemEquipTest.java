package unsw.loopmania;

import org.junit.Test;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Item.Slot;
import unsw.loopmania.ItemFactory.ItemType;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.javatuples.Pair;
import java.util.Arrays;
import java.util.Map;

public class ItemEquipTest {
    private ItemFactory itemFactory = new ItemFactory();
    @Test
    public void TestNaked(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        
        for(Map.Entry<Slot, Item> entry : dummyChar.getMap().entrySet())
        {
            assertTrue(entry.getValue() == null);
        }
    }

    @Test
    public void TestEquipSword(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.SWORD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preAttack = dummyChar.getAttack();
        dummyChar.equipItem(dummyItem);
        int afterAttack = dummyChar.getAttack();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterAttack - preAttack == dummyItem.getValue());
    }

    @Test
    public void TestEquipStake(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.STAKE, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preAttack = dummyChar.getAttack();
        dummyChar.equipItem(dummyItem);
        int afterAttack = dummyChar.getAttack();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterAttack - preAttack == dummyItem.getValue());
    }

    @Test
    public void TestEquipStaff(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.STAFF, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preAttack = dummyChar.getAttack();
        dummyChar.equipItem(dummyItem);
        int afterAttack = dummyChar.getAttack();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterAttack - preAttack == dummyItem.getValue());
    }

    @Test
    public void TestEquipArmour(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.ARMOUR, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preDefense = dummyChar.getDefense();
        dummyChar.equipItem(dummyItem);
        int afterDefense = dummyChar.getDefense();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterDefense - preDefense == dummyItem.getValue());
    }

    @Test
    public void TestEquipShield(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.SHIELD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preDefense = dummyChar.getDefense();
        assertTrue(!dummyChar.getBlockingStatus());
        dummyChar.equipItem(dummyItem);
        assertTrue(dummyChar.getBlockingStatus());
        int afterDefense = dummyChar.getDefense();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterDefense - preDefense == dummyItem.getValue());
    }
    
    @Test
    public void TestEquipHelmet(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.HELMET, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int preDefense = dummyChar.getDefense();
        dummyChar.equipItem(dummyItem);
        int afterDefense = dummyChar.getDefense();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterDefense - preDefense == dummyItem.getValue());
    }

    @Test
    public void TestEquipHealthPotion(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.HEALTH_POTION, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        dummyChar.equipItem(dummyItem);
        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
    }

    @Test
    public void TestEquipTheOneRing(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.THE_ONE_RING, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        assertTrue(!dummyChar.getReviveStatus());
        dummyChar.equipItem(dummyItem);
        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(dummyChar.getReviveStatus());
    }

    @Test
    public void TestEquipAnduril(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.ANDURIL, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        assertTrue(dummyItem instanceof Sword);
        
        int preAttack = dummyChar.getAttack();
        dummyChar.equipItem(dummyItem);
        int afterAttack = dummyChar.getAttack();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterAttack - preAttack == dummyItem.getValue());
    }
    
    @Test
    public void TestEquipTreeStump(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.TREE_STUMP, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        assertTrue(dummyItem instanceof Shield);
        int preDefense = dummyChar.getDefense();
        assertTrue(!dummyChar.getBlockingStatus());
        dummyChar.equipItem(dummyItem);
        assertTrue(dummyChar.getBlockingStatus());
        int afterDefense = dummyChar.getDefense();

        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()).equals(dummyItem));
        assertTrue(afterDefense - preDefense == dummyItem.getValue());
    }

    @Test
    public void TestDeequipItem(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummyItem = itemFactory.createItem(ItemType.SWORD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        dummyChar.equipItem(dummyItem);
        Item deequippedItem = dummyChar.DeequipItemByCoordinate(dummyItem.getX(), dummyItem.getY());
        
        assertTrue(dummyItem == deequippedItem);
        assertTrue(dummyChar.getEquipment(dummyItem.getSlot()) == null);
    }
    

    @Test
    public void TestOverwritingEquipment(){
        ArrayList<Pair<Integer, Integer>> dummyPath = new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,1), new Pair<>(0,2), new Pair<>(1,2),
                                                                new Pair<>(2,2), new Pair<>(2,1), new Pair<>(2,0), new Pair<>(1,0)));
        PathPosition dummyPos = new PathPosition(0, dummyPath);
        Character dummyChar = new Character(dummyPos);
        Item dummySword = itemFactory.createItem(ItemType.SWORD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        Item dummyStake = itemFactory.createItem(ItemType.STAKE, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        Item dummyStaff = itemFactory.createItem(ItemType.STAFF, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        int initialState = dummyChar.getAttack();
        dummyChar.equipItem(dummySword);
        dummyChar.equipItem(dummyStake);
        int equippedStake = dummyChar.getAttack();
        assertTrue(dummyChar.getEquipment(dummyStake.getSlot()).equals(dummyStake));
        assertTrue(equippedStake - initialState == dummyStake.getValue());
        dummyChar.equipItem(dummyStaff);
        int equippedStaff = dummyChar.getAttack();
        assertTrue(dummyChar.getEquipment(dummyStaff.getSlot()).equals(dummyStaff));
        assertTrue(equippedStaff - initialState == dummyStaff.getValue());
    }
}