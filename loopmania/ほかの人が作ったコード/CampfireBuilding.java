package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class CampfireBuilding extends Building {

    private int buffRadius;

    public CampfireBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        buffRadius = 2;
    }

    @Override
    public void buff(Character character) {
        if(Math.pow(this.getX() - character.getX(), 2) + Math.pow(this.getY() - character.getY(), 2) <= Math.pow(buffRadius, 2) &&
           character.getDoubleDamage() == false) {
                character.setDoubleDamage(true);
                character.modifyAttack(character.getAttack());
        } else if (Math.pow(this.getX() - character.getX(), 2) + Math.pow(this.getY() - character.getY(), 2) > Math.pow(buffRadius, 2) &&
                   character.getDoubleDamage() == true) {
                    character.setDoubleDamage(false);
                    character.setAttack(character.getAttack()/2);
        }
    }

    /**
     * The radius that keeps vampires away
     * @return radius that vampire cannot come into
     */
    public int getBuffRadius() {
        return buffRadius;
    }
}
