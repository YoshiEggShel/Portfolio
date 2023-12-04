package unsw.loopmania;

/* Since all combatanants are Moving Entitys, 
we generate a class to stand in for towers in Battle
*/
public class TowerAlly extends MovingEntity{
    public TowerAlly(PathPosition position) {
        super(position);
        //Set stats
        setAttack(10);
        setDefense(9999); //Tower should never be attacked
        setHealth(9999);
        setSpeed(1);
        this.canBlock = false;
        this.canRevive = false;
        this.tranced = false;
        this.stunned = false;
    }
    
    //Attacks the specified target
    public void AttackTarget(MovingEntity target, int seed){
        int damage = this.getAttack() - target.getDefense();
        target.damageHealth(damage);
    }
}
