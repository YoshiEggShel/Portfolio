package unsw.loopmania;

import org.junit.Test;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Item.Slot;
import unsw.loopmania.ItemFactory.ItemType;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemFactoryTest {
    private ItemFactory itemFactory = new ItemFactory();
      //Tests that the characters unarmed attack does the right amount of damage
    @Test
    public void TestCreateSword(){
      Item dummyItem = itemFactory.createItem(ItemType.SWORD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
         assertTrue(dummyItem.getClass() == Sword.class);
         assertTrue(dummyItem.getName().equals("Sword"));
         assertTrue(dummyItem.getSlot().equals(Slot.RIGHT_ARM));
         assertTrue(dummyItem.getValue() == 15);
    }
    @Test
    public void TestCreateStake(){
         Item dummyItem = itemFactory.createItem(ItemType.STAKE, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
         assertTrue(dummyItem.getClass() == Stake.class);
         assertTrue(dummyItem.getName().equals("Stake"));
         assertTrue(dummyItem.getSlot().equals(Slot.RIGHT_ARM));
         assertTrue(dummyItem.getValue() == 7);
    }

    @Test
    public void TestCreateStaff(){
         Item dummyItem = itemFactory.createItem(ItemType.STAFF, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
         assertTrue(dummyItem.getClass() == Staff.class);
         assertTrue(dummyItem.getName().equals("Staff"));
         assertTrue(dummyItem.getSlot().equals(Slot.RIGHT_ARM));
         assertTrue(dummyItem.getValue() == 5);
    }

    @Test
    public void TestCreateArmour(){
         Item dummyItem = itemFactory.createItem(ItemType.ARMOUR, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
         assertTrue(dummyItem.getClass() == Armour.class);
         assertTrue(dummyItem.getName().equals("Armour"));
         assertTrue(dummyItem.getSlot().equals(Slot.CHEST));
         assertTrue(dummyItem.getValue() == 10);
    }

    @Test
    public void TestCreateShield(){
          Item dummyItem = itemFactory.createItem(ItemType.SHIELD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == Shield.class);
          assertTrue(dummyItem.getName().equals("Shield"));
          assertTrue(dummyItem.getSlot().equals(Slot.LEFT_ARM));
          assertTrue(dummyItem.getValue() == 5);
    }
    
    @Test
    public void TestCreateHelmet(){
          Item dummyItem = itemFactory.createItem(ItemType.HELMET, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == Helmet.class);
          assertTrue(dummyItem.getName().equals("Helmet"));
          assertTrue(dummyItem.getSlot().equals(Slot.HEAD));
          assertTrue(dummyItem.getValue() == 5);
    }
    /*
    @Test
    public void TestCreateGold(){
          Item test = itemFactory.createItem(ItemType.GOLD, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0), 100);
          assertTrue(test.getClass() == Gold.class);
          assertTrue(test.getName().equals("Gold"));
          assertTrue(test.getValue() == 100);
    }
    */
    @Test
    public void TestCreateHealthPotion(){
          Item dummyItem = itemFactory.createItem(ItemType.HEALTH_POTION, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == HealthPotion.class);
          assertTrue(dummyItem.getName().equals("Health Potion"));
          assertTrue(dummyItem.getSlot().equals(Slot.POTION));
          assertTrue(dummyItem.getValue() == 50);
    }

    @Test
    public void TestCreateTheOneRing(){
          Item dummyItem = itemFactory.createItem(ItemType.THE_ONE_RING, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == TheOneRing.class);
          assertTrue(dummyItem.getName().equals("The One Ring"));
          assertTrue(dummyItem.getSlot().equals(Slot.SPECIAL));
          assertTrue(dummyItem.getValue() == 1);
    }

    @Test
    public void TestCreateAnduril(){
          Item dummyItem = itemFactory.createItem(ItemType.ANDURIL, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == Anduril.class);
          assertTrue(dummyItem.getName().equals("Anduril, Flame of the West"));
          assertTrue(dummyItem.getSlot().equals(Slot.RIGHT_ARM));
          assertTrue(dummyItem.getValue() == 30);
    }

    @Test
    public void TestCreateTreeStump(){
          Item dummyItem = itemFactory.createItem(ItemType.TREE_STUMP, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == TreeStump.class);
          assertTrue(dummyItem.getName().equals("Tree Stump"));
          assertTrue(dummyItem.getSlot().equals(Slot.LEFT_ARM));
          assertTrue(dummyItem.getValue() == 30);
    }

    @Test
    public void TestCreateDoggieCoin(){
          Item dummyItem = itemFactory.createItem(ItemType.DOGGIE_COIN, new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
          assertTrue(dummyItem.getClass() == DoggieCoin.class);
          assertTrue(dummyItem.getName().equals("Doggie Coin"));
          assertTrue(dummyItem.getValue() == 0);
    }
}
