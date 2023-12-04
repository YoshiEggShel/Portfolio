package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * represents an item in the backend world
 */
public abstract class Item extends StaticEntity implements Equipment{
    private String name;
    private Slot slot;
    // This could be used for multiple purpose
    private int value;

    /**
     * An enum that will be allocated to every item to restrict item behaviour
     */
    public enum Slot{
        HEAD,
        CHEST,
        LEFT_ARM,
        RIGHT_ARM,
        SPECIAL,
        POTION
    }

    public Item(SimpleIntegerProperty x, SimpleIntegerProperty y, String name, Slot slot) {
        super(x, y);
        this.name = name;
        this.slot = slot;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Slot getSlot()
    {
        return slot;
    }

    public void setSlot(Slot slot)
    {
        this.slot = slot;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public void setX(int x) {
        x().set(x);
    }

    public void setY(int y) {
        y().set(y);
    }
}
