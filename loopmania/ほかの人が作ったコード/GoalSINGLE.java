package unsw.loopmania;

public class GoalSINGLE extends Goal{
    private String type;
    private int amount;

    public GoalSINGLE(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    //These two are for testing
    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public Boolean checkCompleted(LoopManiaWorld world){
        switch(type)
        {
            case "experience":
                if (world.getExp() >= amount){
                    return true;
                }
                return false;
            case "gold":
                if (world.getGold() >= amount){
                    return true;
                }
                return false;
            case "cycles":
                if (world.getCycle() >= amount){
                    return true;
                }
                return false;
            case "bosses":
                if (world.isDoggieDefeated() && world.isElanDefeated()){
                    return true;
                }
                return false;
            default:
                return false;
        }
        
    }
}
