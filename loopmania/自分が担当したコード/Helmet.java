package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of a Helmet when used
 */
public class Helmet extends Item {
    public Helmet(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Helmet", Slot.HEAD);
        setValue(5);
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