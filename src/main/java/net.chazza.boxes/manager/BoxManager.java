package net.chazza.boxes.manager;

import net.chazza.boxes.Boxes;
import net.chazza.boxes.api.Box;
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
