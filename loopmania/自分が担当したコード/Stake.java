package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of a Stake when used
 */
public class Stake extends Item {
    public Stake(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Stake", Slot.RIGHT_ARM);
        setValue(7);
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