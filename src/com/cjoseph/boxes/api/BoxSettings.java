package com.cjoseph.boxes.api;

public class BoxSettings {
    private int breakTime;
    private boolean permission;
    private int minRewards;
    private int maxRewards;

    public BoxSettings(){}

    public BoxSettings setBreakTime(int breakTime){
        this.breakTime = breakTime;
        return this;
    }

    public BoxSettings requiresPerm(boolean permission){
        this.permission = permission;
        return this;
    }

    public BoxSettings minRewards(int minRewards){
        this.minRewards = minRewards;
        return this;
    }

    public BoxSettings maxRewards(int maxRewards){
        this.maxRewards = maxRewards;
        return this;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public boolean requiresPerm() {
        return permission;
    }

    public int getMinRewards() {
        return minRewards;
    }

    public int getMaxRewards() {
        return maxRewards;
    }
}
