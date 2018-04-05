package com.cjoseph.boxes.manager;

import com.cjoseph.boxes.Boxes;
import com.cjoseph.boxes.api.Box;
import com.google.common.collect.Maps;

import java.util.HashMap;

public class BoxManager {
    private static HashMap<String, Box> boxes;
    private Boxes core;

    public BoxManager(Boxes core){
        this.core = core;
        boxes = Maps.newHashMap();
    }

    public void addBox(Box newBox){
        boxes.put(newBox.getId(), newBox);
    }

    public Boxes getCore() {
        return core;
    }

    public static HashMap<String, Box> getBoxes() {
        return boxes;
    }
}
