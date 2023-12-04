package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import org.javatuples.Pair;

public class ZombiePitBuilding extends Building {
    public ZombiePitBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public BasicEnemy spawn(List<BasicEnemy> enemies, List<Pair<Integer, Integer>> path, int cycle) {
        Zombie newZomb = null;
        PathPosition pos = possiblyGetSpawnPosition(path);
        if(pos != null) {
            newZomb = new Zombie(pos);
            enemies.add(newZomb);
        }
        return newZomb;
    }
}
