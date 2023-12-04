package unsw.loopmania;

import java.util.Random;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.ItemFactory.ItemType;

/**
 * Defines the behaviour of The One Ring when used
 */
public class TheOneRing extends Item implements Consumable, RareItem {

    private ItemType extendedItem;
    
    public TheOneRing(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "The One Ring", Slot.SPECIAL);
        this.extendedItem = null;
        setValue(1);
    }

    public void useItem(Character target)
    {
        target.unsetRevive();
        destroy();
    }

    public void onEquip(Stats stats)
    {
        stats.setRevive();
    }

    public void onDeequip(Stats stats)
    {
        stats.unsetRevive();
        // This case would be for confusing mode. Need to also dequip the additional extended properties.
        if (extendedItem != null) {
            removeExtendedProperty(stats);
        }
    }

    // Randomly select another rare item and add its property
    public void extendProperty(Stats stats) {
        Random rand = new Random();
        int int_random = rand.nextInt(2);
        if (int_random == 0) {
            extendedItem = ItemType.ANDURIL;
            stats.modifyAttack(30);
        } else {
            extendedItem = ItemType.TREE_STUMP;
            stats.modifyDefense(30);
        }
        
    }

    public void removeExtendedProperty(Stats stats) {
        if (extendedItem.equals(ItemType.ANDURIL)) {
            stats.modifyAttack(-30);
        } else if (extendedItem.equals(ItemType.TREE_STUMP)) {
            stats.modifyDefense(-30);
        }
      
    }

 
}