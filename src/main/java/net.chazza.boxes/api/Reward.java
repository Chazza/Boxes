package net.chazza.boxes.api;

import java.util.List;

public class Reward {
    private String id;
    private List<String> rewards;
    private Integer chance;

    public Reward(String id, List<String> rewards, int chance){
        this.id = id;
        this.rewards = rewards;
        this.chance = chance;
    }

    public String getId() {
        return id;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public Integer getChance() {
        return chance;
    }
}
