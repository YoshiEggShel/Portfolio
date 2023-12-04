package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of an Armour when used
 */
public class Armour extends Item {
    public Armour(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Armour", Slot.CHEST);
        setValue(10);
    }

    public void onEquip(Stats stats)
    {
        stats.modifyDefense(getValue());
    }

    public void onDeequip(Stats stats)
    {
        stats.modifyDefense(-getValue());
    }
}