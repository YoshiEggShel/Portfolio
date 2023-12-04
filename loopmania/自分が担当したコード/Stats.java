package unsw.loopmania;

/**
 * An interface for methods to modify stats of a character
 */
public interface Stats {
    // Modifies health to 'current health'
    public void modifyHealth(int value);
    // Modifies value to character attack stat
    public void modifyAttack(int value);
    // Modifies value to character defense stat
    public void modifyDefense(int value);
    // Makes it possible for the character to block
    public void setBlocking();
    // Makes it impossible for the character to block
    public void unsetBlocking();
    // Makes it possible for the character to be revived
    public void setRevive();
    // Makes it impossible for the character to be revived
    public void unsetRevive();
    // Damages the character with the value
    public void damageHealth(int value);
}