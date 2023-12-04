package unsw.loopmania;

import java.util.Random;


/**
 * a basic form of enemy in the world
 */
public abstract class BasicEnemy extends MovingEntity{
    //Attributes
    private int battleRadius;
    private int supportRadius;
    //Removed tranced timer, since its already in moving entity
    protected boolean isBoss;

    // TODO = modify this, and add additional forms of enemy
    public BasicEnemy(PathPosition position) {
        super(position);
    }

    /**
     * move the enemy
     */
    public void move(){
        // TODO = modify this, since this implementation doesn't provide the expected enemy behaviour
        // this basic enemy moves in a random direction... 25% chance up or down, 50% chance not at all...
        int directionChoice = (new Random()).nextInt(2);
        if (directionChoice == 0){
            moveUpPath();
        }
        else if (directionChoice == 1){
            moveDownPath();
        }
    }

    public int getBattleRadius() {
        return battleRadius;
    }

    public void setBattleRadius(int battleRadius) {
        this.battleRadius = battleRadius;
    }

    public int getSupportRadius() {
        return supportRadius;
    }

    public void setSupportRadius(int supportRadius) {
        this.supportRadius = supportRadius;
    }

    public int getTranceTimer() {
        return tranceTimer;
    }

    public void setTranceTimer(int tranceTimer) {
        this.tranceTimer = tranceTimer;
    }

    public boolean isBoss() {
        return isBoss;
    }
    
    public int getGold() {
        return 0;
    }

    public int getXp() {
        return 0;
    }
}
