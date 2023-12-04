package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of Health Potion when used
 */
public class HealthPotion extends Item implements Consumable{
    public HealthPotion(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Health Potion", Slot.POTION);
        setValue(50);
    }

    public void useItem(Character target)
    { 
        target.setHealth(target.getMaxHealth());
    }

    public void onEquip(Stats stats)
    {
        return;
    }

    public void onDeequip(Stats stats)
    {
        return;
    }
}