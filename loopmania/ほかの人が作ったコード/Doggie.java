package unsw.loopmania;

import java.util.Random;

import unsw.loopmania.Item.Slot;

public class Doggie extends BasicEnemy{

    private int gold = 1000;
    private int experience = 2000;

    //Construct enemy at certain position, and set all attributes
    public Doggie(PathPosition position) {
        super(position);
        //Set radii
        this.setBattleRadius(1);
        this.setSupportRadius(1);
        //set stats
        this.setAttack(50);
        this.setDefense(0);
        this.setHealth(500);
        this.setSpeed(12);
        //Set other
        this.tranced = false;
        this.stunned = false;
        this.canBlock = false;
        this.isBoss = true;
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

        //If the target entity (currently only main character) is wearing wielding the tree stump, reduce damage by a further 10
        if (target instanceof Character){
            Character hero = (Character)target;
            if (hero.getEquipment(Slot.LEFT_ARM) instanceof TreeStump){
                damage = Math.max(0, damage - 10);
            }
        }

        //If the target enityt (currently only main character) successfully blocks, reduce damage to 0
        if (target.tryBlock(seed)){
            damage = 0;
        }

        target.damageHealth(damage);

        //Critical, so stun target
        if (seed == 20 && target instanceof Character){
            Character character = (Character)target;
            character.stunned = true; 
        }

        //Handle trance
        if (getTranceTimer() == 0){
            setTranced(false);
        }
        else if (tranced){
            deincrementTranceTimer();
        }
          
    }

    /**
     * move a Doggie (50% chance to chase character down, 50% chance to get distracted and do nothing)
     */
    public void move(){
        int directionChoice = (new Random()).nextInt(2);
        if (directionChoice == 1){
            moveUpPath();
            moveUpPath();
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
