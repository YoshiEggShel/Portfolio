package unsw.loopmania;

public class Ally extends MovingEntity{
    //Used for turning allys into zombies upon critting
    private boolean zombified;

    public Ally(PathPosition position) {
        super(position);
        //Set stats
        setAttack(5);
        setDefense(0);
        setHealth(50);
        setSpeed(7);
        this.canBlock = false;
        this.canRevive = false;
        this.zombified = false;
        this.tranced = false;
        this.stunned = false;
    }
    
    //Attacks the specified target
    public void AttackTarget(MovingEntity target, int seed){
        int damage = this.getAttack() - target.getDefense();
        target.damageHealth(damage);
    }

    public boolean isZombified() {
        return zombified;
    }

    public void setZombified(boolean zombified) {
        this.zombified = zombified;
    }

}


