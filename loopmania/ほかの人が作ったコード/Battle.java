package unsw.loopmania;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.Random;

import unsw.loopmania.Item.Slot;

/**
 * Class which contains most of the battle logic
 * It is created whenever a battle occurs
 * It is removed after the battle ends, ending in either the heros death or rewards
 */
public class Battle {
    //Attributes
    private Character hero;
    private List<MovingEntity> allies;
    private List<MovingEntity> enemies;
    private List<MovingEntity> participants;
    private List<MovingEntity> defeated;

    public Battle(Character Hero, List<MovingEntity> Allies, List<MovingEntity> Enemies) {
        this.hero = Hero;
        this.allies = Allies;
        this.enemies = Enemies;
        //TODO: if Elan is in battle, move him to index 0, 
        //so that he can heal without being attacked during the boss fight
        participants = new ArrayList<MovingEntity>();
        defeated = new ArrayList<MovingEntity>();
        participants.addAll(Allies);
        participants.addAll(Enemies);
        participants.sort(Comparator.comparingInt(MovingEntity::getSpeed)); //Sorts by attack speed, lowest to highest
        Collections.reverse(participants); //reverses list to get highest to lowest
    }

    //Runs through the turn order, calling each entitys attack func, until battle is resolved
    public void Fight(){
        //Do actual combat work here
        int i = 0;
        //While there are enemies remaining, fight
        while (enemies.size() != 0){
            var attacker = participants.get((i % participants.size()));

            //Check that its still an active combatenant
            if (attacker.getHealth() <= 0){
                //If its dead, skip it
                i++;
                continue;
            }

            Random rand = new Random();
            int seed = rand.nextInt(21); //Random number between 1 and 20 inclusive

            //Its either an allied force
            if (allies.contains(attacker)){
                MovingEntity target = getTargetEnemy();
                attacker.AttackTarget(target, seed);

                //If the attack kills the enemy, defeat it
                if (target.getHealth() <= 0){
                    defeatEntity(target);
                }
            } 
            //Or an Enemy
            else if (enemies.contains(attacker)){
                MovingEntity target;

                //If enemy is tranced
                if (attacker.getTranced()){
                    target = getTargetEnemy();
                } else {
                    target = getTargetAlly();
                }

                attacker.AttackTarget(target, seed);

                //If the attack kills the ally, remove it from the heros ally list
                if (target.getHealth() <= 0){
                    if (target == hero){
                        heroDefeated(hero);
                        if (hero.getHealth() <= 0){
                            break; //Ends battle
                        }
                    } else if (allies.contains(target)){
                        //Ally dies, remove from hero's list
                        hero.getAllies().remove(target);
                        //Then check for zombification and react accordinly
                        Ally deadAlly = (Ally)target;
                        if (deadAlly.isZombified()){
                            //Remove target from rotation, as added zombie wil re balance numbers
                            participants.remove(target);
                            //Create and add zombie to both necessary lists
                            Zombie newZombie = new Zombie(target.getPosition());
                            enemies.add(newZombie);
                            participants.add(newZombie);
                            //Re sort list
                            participants.sort(Comparator.comparingInt(MovingEntity::getSpeed)); //Sorts by attack speed, lowest to highest
                            Collections.reverse(participants); //reverses list to get highest to lowest
                        }
                    } else {
                        //In this case, it is an tranced enemy killing another enemy
                        defeatEntity(target);
                    }
                }
            }

            //Increment to move onto next entity in turn order
            i++;
        }
    }

    public void heroDefeated(Character hero) {
        if (hero.canRevive){
            //Set heros hp back to max
            hero.setHealth(200);
            
            // In confusing mode, the 'revival item' can be any of the rare items. 
            Item reviveItem = getRevivalItem();

            hero.DeequipItem(reviveItem);
            reviveItem.destroy();
        }
        //else case now handled outside of func
    }

    public List<MovingEntity> getAllies() {
        return allies;
    }

    public List<MovingEntity> getEnemies() {
        return enemies;
    }

    public List<MovingEntity> getParticipants() {
        return participants;
    }

    public List<MovingEntity> getDefeated() {
        return defeated;
    }

    //Returns the defeated enemies as BasicEnemy list, as is required by the world funcs
    public List<BasicEnemy> getDefeatedEnemies(){
        List<BasicEnemy> result = new ArrayList<BasicEnemy>();
        for (MovingEntity e: getDefeated()) {
            result.add((BasicEnemy)e);
        }
        return result;
    }

    // In confusing mode, the 'revival item' can be any of the rare items. 
    // If the hero has all three rare items which each contain the one ring property, 
    // then the 'revival' will first come from the one ring, followed by anduril then treestump.  
    public Item getRevivalItem() {
        Item revivalItem = null;
        if (hero.getEquipment(Slot.SPECIAL) != null) {
            revivalItem = hero.getEquipment(Slot.SPECIAL); 
        } else if (hero.getEquipment(Slot.RIGHT_ARM) instanceof Anduril)  {
            revivalItem = hero.getEquipment(Slot.RIGHT_ARM); 
        } else if (hero.getEquipment(Slot.LEFT_ARM) instanceof TreeStump) {
            revivalItem = hero.getEquipment(Slot.LEFT_ARM); 
        }
        return revivalItem;
    }

    //Removes an enemy from participants and enemies and adds it to defeated
    //Ally gets handled differently, and hero leads to a game over, no need to resolve anything else
    public void defeatEntity(MovingEntity defeatedEnemy){
        //participants.remove(defeatedEnemy); 
        //does not remove from particpants to keep loop through turn order stable
        enemies.remove(defeatedEnemy);
        defeated.add(defeatedEnemy);
    }

    //Based on priority, returns an ally for an enemy to attack
    public MovingEntity getTargetAlly(){
        //Essentially its ally first, hero second
        //towers cannot be targeted

        //If the hero has no allies, return hero
        if (hero.getAllies().size() == 0){
            return hero;
        } 
        //Otherwise return the oldest ally
        else{
            return hero.getAllies().get(0);
        }
        
    }

    //Based on priority, returns an enemy for an ally to attack
    //In this case, priotity is simply the last element of the list
    //Since that means it was either supporting or is the main enemy if its last
    public MovingEntity getTargetEnemy(){
        if (enemies.size() == 0){
            return null;
        }
        return enemies.get(enemies.size() - 1);
    }
    
}
