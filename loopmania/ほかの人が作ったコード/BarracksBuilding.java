package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class BarracksBuilding extends Building {

    private final static int MAX_ALLIES = 3;

    public BarracksBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    public Ally spawn(Character character) {
        Ally newAlly = null;
        if(character.getX() == this.getX() && character.getY() == this.getY() && character.getAllies().size() < MAX_ALLIES) {
            newAlly = new Ally(null); //Path position is unimportant to an ally
            character.AddAlly(newAlly);
        }

        return newAlly;
    }
}
