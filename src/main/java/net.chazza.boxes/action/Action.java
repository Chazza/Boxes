package net.chazza.boxes.action;

public class Action {

    private String prefix;
    private Enum<ActionType> type;
    public Action(String prefix){
        this.prefix = prefix;
        this.type = null;
    }

    public Action withType(Enum<ActionType> type){
        this.type = type;
        return this;
    }

    public Action compile(){
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Enum<ActionType> getType() {
        return type;
    }
}
