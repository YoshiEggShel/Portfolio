package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import org.javatuples.Pair;

/**
 * a basic form of building in the world
 */
public class VampireCastleBuilding extends Building {

    public VampireCastleBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public BasicEnemy spawn(List<BasicEnemy> enemies, List<Pair<Integer, Integer>> path, int cycle) {
        Vampire newVamp = null;
        if(cycle%5 == 0) {
            PathPosition pos = possiblyGetSpawnPosition(path);
            if(pos != null) {
                newVamp = new Vampire(pos);
                enemies.add(newVamp);
            }
        }
        return newVamp;
    }
}
