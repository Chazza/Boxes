package net.chazza.boxes.api;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private String id;
    private Integer minRewards;
    private Integer maxRewards;
    private ItemStack boxItem;
    private List<String> useRewards;
    private List<String> breakRewards;
    private List<Reward> chanceRewards;
    private BoxSettings boxSettings;

    public Box(String id){
        this.id = id;
        this.minRewards = 1;
        this.maxRewards = 1;
        this.useRewards = new ArrayList<>();
        this.breakRewards = new ArrayList<>();
        this.chanceRewards = new ArrayList<>();
        this.boxSettings = new BoxSettings();
    }

    public void setBoxItem(ItemStack boxItem) {
        this.boxItem = boxItem;
    }

    public void setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
    }

    public void setMinRewards(int minRewards) {
        this.minRewards = minRewards;
    }

    public void withSettings(BoxSettings boxSettings){
        this.boxSettings = boxSettings;
    }

    public void setUseRewards(List<String> useRewards) {
        this.useRewards = useRewards;
    }

    public void setBreakRewards(List<String> breakRewards) {
        this.breakRewards = breakRewards;
    }

    public void setChanceRewards(List<Reward> chanceRewards) {
        this.chanceRewards = chanceRewards;
    }

    public List<String> getUseRewards() {
        return useRewards;
    }

    public List<String> getBreakRewards() {
        return breakRewards;
    }

    public List<Reward> getChanceRewards() {
        return chanceRewards;
    }

    public BoxSettings getBoxSettings() {
        return boxSettings;
    }

    public ItemStack getBoxItem() {
        return boxItem;
    }

    public String getId() {
        return id;
    }
}
