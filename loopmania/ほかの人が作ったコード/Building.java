package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import org.javatuples.Pair;

public abstract class Building extends StaticEntity implements SpawnEnemyBuilding, BuffBuilding, DamageBuilding {

    public Building(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    public PathPosition possiblyGetSpawnPosition(List<Pair<Integer, Integer>> path) {
        // 8 path tiles possibly arround the building
        Pair<Integer, Integer> PosLeft = new Pair<Integer, Integer>(this.getX() - 1, this.getY());
        Pair<Integer, Integer> PosRight = new Pair<Integer, Integer>(this.getX() + 1, this.getY());
        Pair<Integer, Integer> PosUp  = new Pair<Integer, Integer>(this.getX(), this.getY() + 1);
        Pair<Integer, Integer> PosDown = new Pair<Integer, Integer>(this.getX(), this.getY() - 1);
        Pair<Integer, Integer> PosLeftUp = new Pair<Integer, Integer>(this.getX() - 1, this.getY() + 1);
        Pair<Integer, Integer> PosRightUp = new Pair<Integer, Integer>(this.getX() + 1, this.getY() + 1);
        Pair<Integer, Integer> PosLeftDown = new Pair<Integer, Integer>(this.getX() - 1, this.getY() - 1);
        Pair<Integer, Integer> PosRightDown = new Pair<Integer, Integer>(this.getX() + 1, this.getY() - 1);

        boolean PosLeftBool = false;
        boolean PosRightBool = false;
        boolean PosUpBool = false;
        boolean PosDownBool = false;
        boolean PosLeftUpBool = false;
        boolean PosRightUpBool = false;
        boolean PosLeftDownBool = false;
        boolean PosRightDownBool = false;

        if(path.contains(PosLeft)) {
            PosLeftBool = true;
        }
        if(path.contains(PosRight)) {
            PosRightBool = true;
        }
        if(path.contains(PosUp)) {
            PosUpBool = true;
        }
        if(path.contains(PosDown)) {
            PosDownBool = true;
        }
        if(path.contains(PosLeftUp)) {
            PosLeftUpBool = true;
        }
        if(path.contains(PosRightUp)) {
            PosRightUpBool = true;
        }
        if(path.contains(PosLeftDown)) {
            PosLeftDownBool = true;
        }
        if(path.contains(PosRightDown)) {
            PosRightDownBool = true;
        }
        PathPosition spawnPos = null;

        // Spawns from order left path tile and then preceeds clockwise 
        // If there are no immediate squares left, up, right or down, it will 
        // try spawn in the diagonals starting from left down and preceeds clockwise
        if(PosLeftBool) {
            spawnPos = new PathPosition(path.indexOf(PosLeft), path);
        } else if(PosUpBool) {
            spawnPos = new PathPosition(path.indexOf(PosUp), path);
        } else if(PosRightBool) {
            spawnPos = new PathPosition(path.indexOf(PosRight), path);
        } else if(PosDownBool) {
            spawnPos = new PathPosition(path.indexOf(PosDown), path);
        } else if(PosLeftDownBool) {
            spawnPos = new PathPosition(path.indexOf(PosLeftDown), path);
        } else if(PosLeftUpBool) {
            spawnPos = new PathPosition(path.indexOf(PosLeftUp), path);
        } else if(PosRightUpBool) {
            spawnPos = new PathPosition(path.indexOf(PosRightUp), path);
        } else if(PosRightDownBool) {
            spawnPos = new PathPosition(path.indexOf(PosRightDown), path);
        } else {
            throw new IllegalArgumentException("Building is not adjacent to a Path tile\n");
        }
        
        return spawnPos;
    }

    public void buff(Character character) {
        throw new UnsupportedOperationException();
    }

    public Pair<BasicEnemy, Boolean> damage(List<BasicEnemy> enemies, List<Building> building) {
        throw new UnsupportedOperationException();
    }

    public BasicEnemy spawn(List<BasicEnemy> enemies, List<Pair<Integer, Integer>> path, int cycle) {
        throw new UnsupportedOperationException();
    }
}
