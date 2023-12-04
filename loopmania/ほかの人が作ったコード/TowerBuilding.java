package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class TowerBuilding extends Building {

    private int supportRadius;

    public TowerBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        supportRadius = 8;
    }

    public int getSupportRadius() {
        return supportRadius;
    }
}
