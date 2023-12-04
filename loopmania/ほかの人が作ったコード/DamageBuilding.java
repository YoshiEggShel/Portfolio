package unsw.loopmania;

import java.util.List;

import org.javatuples.Pair;

public interface DamageBuilding {
    public Pair<BasicEnemy, Boolean> damage(List<BasicEnemy> enemies, List<Building> building);
}
