package unsw.loopmania;

import java.util.Random;

import unsw.loopmania.Item.Slot;

public class Vampire extends BasicEnemy{
    //Unique Attributes
    private int frenzyTimer;
    private boolean direction = false;
    private int gold = 100;
    private int experience = 200;

    //Construct enemy at certain position, and set all attributes
    public Vampire(PathPosition position) {
        super(position);
        //Set radii
        this.setBattleRadius(2);
        this.setSupportRadius(3);
        //Set stats
        this.setAttack(20);
        this.setDefense(0);
        this.setHealth(200);
        this.setSpeed(10);
        //Set other
        this.tranced = false;
        this.canBlock = false;
        this.stunned = false;
        this.isBoss = false;
    }

    //Attacks the specified target
    public void AttackTarget(MovingEntity target, int seed){
        int damage = this.getAttack() - target.getDefense();

        //If the target entity (currently only main character) is wearing armor, halve damage
        if (target instanceof Character){
            Character hero = (Character)target;
            if (hero.getEquipment(Slot.CHEST) instanceof Armour){
                damage = damage/2;
            }
        }

        //If the target enityt (currently only main character) successfully blocks, reduce damage to 0
        if (target.tryBlock(seed)){
            damage = 0;
        }

        target.damageHealth(damage);

        //Critical
        if (seed == 20){
            //Check if hero is target and has shield
            if (target instanceof Character && target.canBlock){
                var rand = new Random();
                int seed2 = rand.nextInt(11);
                //If its below 7, the hero blocked the critical
                if (seed2 >= 7){
                    this.startFrenzy();
                }

            } 
            else {
                this.startFrenzy();
            }
        }

        //If frenzy buff has 0 turns left
        if (frenzyTimer == 0) {
            endFrenzy();
        }
        //Deincrement frenzy
        else {
            frenzyTimer--; 
        }

        //Handle tarnce
        if (getTranceTimer() == 0){
            setTranced(false);
        }
        else if (tranced){
            deincrementTranceTimer();
        }
    }

    //For when it scores a critical hit
    public void startFrenzy() {
        Random rand = new Random();
        frenzyTimer = rand.nextInt(4); //Generates a number between 1 and 3
        this.setAttack(this.getAttack() + rand.nextInt(7) + 4); //Increases attack by 5 to 10
    }

    //When critical hit buff expires
    public void endFrenzy() {
        this.setAttack(20); //Reset it to its base attack
    }

    //Returns how many tunrs a vamp has left to frenzy
    //If 0, the vamp is not in frenzy
    public int getFrenzyTimer() {
        return frenzyTimer;
    }

    //Set how many turns a vamp should frenzy for
    public void setFrenzyTimer(int frenzyTimer) {
        this.frenzyTimer = frenzyTimer;
    }

    /**
     * move a vampire (10% up path, 10% down path, 80% not moving)
     */
    public void move(){
        if(direction == false) {
            moveUpPath();
        } else {
            moveDownPath();
        }
    }

    /**
     * Gets the direction of the vampire. Initially, vampires will move
     * counter-clock wise and once in range of the campfire it will change
     * direction to clockwise and repeat
     */
    public boolean getDirection() {
        return direction;
    }

    /**
     * Changes the direction to the opposite way
     */
     public void changeDirection() {
        direction = !direction;
     }

     @Override
     public int getGold() {
         return gold;
     }

     @Override
     public int getXp() {
         return experience;
     }
}
