package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of a Staff when used
 */
public class Staff extends Item {
    public Staff(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Staff", Slot.RIGHT_ARM);
        setValue(5);
    }

    public void onEquip(Stats stats)
    {
        stats.modifyAttack(getValue());
    }

    public void onDeequip(Stats stats)
    {
        stats.modifyAttack(-getValue());
    }
}