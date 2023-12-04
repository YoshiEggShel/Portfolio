package unsw.loopmania;

import java.util.Random;

import unsw.loopmania.Item.Slot;

public class Zombie extends BasicEnemy{

    private int gold = 50;
    private int experience = 100;

    //Construct enemy at certain position, and set all attributes
    public Zombie(PathPosition position) {
        super(position);
        //Set radii
        this.setBattleRadius(2);
        this.setSupportRadius(2);
        //Set stats
        this.setAttack(10);
        this.setDefense(0);
        this.setHealth(100);
        this.setSpeed(5);
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

        //If the target entity (currently only main character) successfully blocks, reduce damage to 0
        if (target.tryBlock(seed)){
            damage = 0;
        }

        target.damageHealth(damage);

        //Critical
        if (seed == 20 && target instanceof Ally){
            Ally ally = (Ally)target;
            ally.setZombified(true);
            ally.setHealth(0); 
        }

        //Handle tarnce
        if (getTranceTimer() == 0){
            setTranced(false);
        }
        else if (tranced){
            deincrementTranceTimer();
        }
    }

    /**
     * move a zombie (20% up path, 20% down path, 60% not moving)
     */
    public void move(){
        int directionChoice = (new Random()).nextInt(5);
        if (directionChoice == 1){
            moveUpPath();
        }
        else if (directionChoice == 3){
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
