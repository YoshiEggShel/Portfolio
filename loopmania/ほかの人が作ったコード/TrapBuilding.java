package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;
import org.javatuples.Pair;

import java.util.List;

public class TrapBuilding extends Building {

    private final static int TRAP_DAMAGE = 100;

    public TrapBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    public Pair<BasicEnemy, Boolean> damage(List<BasicEnemy> enemies, List<Building> buildings) {
        BasicEnemy potentialDeadEnemy = null;
        boolean check = false;
        for(BasicEnemy e : enemies) {
            if(e.getX() == this.getX() && e.getY() == this.getY()) {
                e.damageHealth(TRAP_DAMAGE);
                potentialDeadEnemy = e;
                if(e.getHealth() <= 0) {
                    check = true;
                }
                break;
            }
        }
        // Remove all enemies killed off by traps
        if(check) {
            enemies.remove(potentialDeadEnemy);
            potentialDeadEnemy.destroy();
        }
        return new Pair<BasicEnemy, Boolean>(potentialDeadEnemy, check);
    }
}
