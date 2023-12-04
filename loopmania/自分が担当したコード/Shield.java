package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of a shield when used
 */
public class Shield extends Item {
    public Shield(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Shield", Slot.LEFT_ARM);
        setValue(5);
    }

    public void onEquip(Stats stats)
    {
        stats.modifyDefense(getValue());
        stats.setBlocking();
    }

    public void onDeequip(Stats stats)
    {
        stats.modifyDefense(-getValue());
        stats.unsetBlocking();
    }
}