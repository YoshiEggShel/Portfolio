package unsw.loopmania;

import java.util.Random;

import unsw.loopmania.Item.Slot;

public class Slug extends BasicEnemy{

    private int gold = 20;
    private int experience = 30;

    //Construct enemy at certain position, and set all attributes
    public Slug(PathPosition position) {
        super(position);
        //Set radii
        this.setBattleRadius(1);
        this.setSupportRadius(1);
        //set stats
        this.setAttack(5);
        this.setDefense(0);
        this.setHealth(50);
        this.setSpeed(7);
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

        //Handle trance
        if (getTranceTimer() == 0){
            setTranced(false);
        }
        else if (tranced){
            deincrementTranceTimer();
        }
    }

    /**
     * move a slug (50% up path 50% down path)
     * slug will always move
     */
    public void move(){
        int directionChoice = (new Random()).nextInt(2);
        if (directionChoice == 0){
            moveUpPath();
        }
        else if (directionChoice == 1){
            moveDownPath();
        }
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
