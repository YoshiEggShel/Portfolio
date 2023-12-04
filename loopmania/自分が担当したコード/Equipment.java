package unsw.loopmania;

/**
 * An interface for methods unique to item type 
 */
public interface Equipment {
    public void onEquip(Stats stats);
    public void onDeequip(Stats stats);
}