package unsw.loopmania;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import unsw.loopmania.Item.Slot;

/**
 * represents the main character in the backend of the game world
 */
public class Character extends MovingEntity{

    private boolean doubleDamage;
    private List<Ally> allies;
    private Map <Slot, Item> equipment;

    public Character(PathPosition position) {
        super(position);
        //Set stats
        setAttack(5);
        setDefense(0);
        setHealth(200);
        setMaxHealth(200);
        setSpeed(8);
        this.canBlock = false;
        this.canRevive = false;
        this.doubleDamage = false;
        this.tranced = false;
        this.stunned = false;
        this.allies = new ArrayList<>();
        initialiseEquipment();
    }

    //Attacks the specified target
    public void AttackTarget(MovingEntity target, int seed){
        int damage = this.getAttack() - target.getDefense();

        //Attacking Vampire with a stake case
        if (this.getEquipment(Slot.RIGHT_ARM) instanceof Stake && target instanceof Vampire){
            damage+=30;
        }

        //Attacking Boss with Anduril case
        if (this.getEquipment(Slot.RIGHT_ARM) instanceof Anduril){
            BasicEnemy possibleBoss = (BasicEnemy)target;
            if (possibleBoss.isBoss()){
                damage = damage * 3;
            }
        }

        //Trancing an enemy case
        if (this.getEquipment(Slot.RIGHT_ARM) instanceof Staff && seed >= 20){
            //If it isnt a boss, trance it
            BasicEnemy enemy = (BasicEnemy)target;
            if (!enemy.isBoss){
                target.setTranced(true);
                target.setTranceTimer(3);
            }
        }

        //Near campfire damage adjustment
        if (doubleDamage){
            damage = damage * 2;
        }

        //If not stunned, attack, 
        if (!stunned){
            target.damageHealth(damage);
        } 
        //otherwise, deal no damage, but recover from stunned
        else {
            this.stunned = false;
        }

        
    }

    public void setDoubleDamage(boolean bool) {
        doubleDamage = bool;
    }

    public boolean getDoubleDamage() {
        return doubleDamage;
    }

    //TODO: Passing through barracks should make a new ally and call this
    //Adds an ally to the list of the charcters allies, if there is room
    public void AddAlly(Ally ally){
        //Should the ally be made here, or passed in?
        //Assuming passed in for now
        if (allies.size() < 3){
            allies.add(ally);
        }
    }

    //Returns list of allies. Should be contain 0 - 3 allies
    public List<Ally> getAllies() {
        return allies;
    }

    public Item getEquipment(Slot slot)
    {   
        return equipment.get(slot);
    }

    public Map<Slot, Item> getMap()
    {
        return equipment;
    }

    public Item DeequipItemByCoordinate(int x, int y) {
        for (Map.Entry<Slot, Item> e : equipment.entrySet()) {
            // Skip entry if null value
            if(e.getValue() == null)
                continue;
            // Get X and Y coordinate of item
            if ((e.getValue().getX() == x) && (e.getValue().getY() == y)) {
                Item item = e.getValue();
                DeequipItem(item);
                return item;
            }
        }
        return null;
    }

    public void DeequipItem(Item item) {
        equipment.get(item.getSlot()).onDeequip(this);
        equipment.put(item.getSlot(), null);

    }

    public void equipItem(Item item)
    {
        if(equipment.get(item.getSlot()) != null)
        {
            Item equippedItem = equipment.get(item.getSlot());
            DeequipItem(equippedItem);
            equippedItem.destroy();
        }
        equipment.put(item.getSlot(), item);
        equipment.get(item.getSlot()).onEquip(this);
    }
    
    private void initialiseEquipment(){
        equipment = new EnumMap<Slot, Item>(Slot.class);
        equipment.put(Slot.HEAD, null);
        equipment.put(Slot.CHEST, null);
        equipment.put(Slot.RIGHT_ARM, null);
        equipment.put(Slot.LEFT_ARM, null);
        equipment.put(Slot.POTION, null);
        equipment.put(Slot.SPECIAL, null);
    }
}
