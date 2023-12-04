package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defines the behaviour of Doggie coin
 */
public class DoggieCoin extends Item {
    public DoggieCoin(SimpleIntegerProperty x, SimpleIntegerProperty y)
    {
        super(x,y, "Doggie Coin", null);
        setValue(50);
    }

    public void onEquip(Stats stats)
    {
        
    }

    public void onDeequip(Stats stats)
    {
        
    }
}