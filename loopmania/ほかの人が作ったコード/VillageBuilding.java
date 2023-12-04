package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class VillageBuilding extends Building {

    private final static int VILLAGE_HEAL = 60;

    public VillageBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public void buff(Character character) {
        if(character.getX() == this.getX() && character.getY() == this.getY()) {
            if(character.getHealth() < character.getMaxHealth() - VILLAGE_HEAL) {
                character.modifyHealth(VILLAGE_HEAL);
            } else {
                int maxHealth = character.getMaxHealth();
                character.setHealth(maxHealth);
            }
        }
    }
}
