package com.cjoseph.boxes.action;

import com.cjoseph.boxes.Boxes;
import com.cjoseph.boxes.util.ColorUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ActionManager {
    private Boxes core;
    private ArrayList<Action> actions;
    private String nmsVer;
    private boolean useOldMethods = false;

    public ActionManager(Boxes core){
        this.core = core;
        this.actions = new ArrayList<>();

        // Action Bar
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

        if (nmsVer.equalsIgnoreCase("v1_8_R1") || nmsVer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
    }

    public void addAction(Action action){
        actions.add(action);
    }
    public void removeAction(Action action){
        actions.remove(action);
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    private String getPlaceholders(String message, Player player){
        message = message.replace("%player%", player.getName());
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            message = PlaceholderAPI.setPlaceholders(player, message);
        return message;
    }


    // Action Bar
    private void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        if (nmsVer.startsWith("v1_12_")) {
            sendActionBarPost112(player, message);
        } else {
            sendActionBarPre112(player, message);
        }
    }

    private void sendActionBarPost112(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVer + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsVer + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsVer + ".Packet");
            Class<?> c2 = Class.forName("net.minecraft.server." + nmsVer + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + nmsVer + ".IChatBaseComponent");
            Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsVer + ".ChatMessageType");
            Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
            Object chatMessageType = null;
            for (Object obj : chatMessageTypes) {
                if (obj.toString().equals("GAME_INFO")) {
                    chatMessageType = obj;
                }
            }
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = c4.getConstructor(new Class<?>[]{c3, chatMessageTypeClass}).newInstance(o, chatMessageType);
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendActionBarPre112(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVer + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsVer + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsVer + ".Packet");
            if (useOldMethods) {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVer + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVer + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVer + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVer + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(core, duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(core, (long) duration);
        }
    }


    private Action getAction(String action){
        for(Action act : getActions()){
            if(act.getPrefix().equalsIgnoreCase(action))
                return act;
        }
        return null;
    }

    public void runActions(List<String> actionList, Player target){
        for(String action : actionList){
            String strAction, cmd;
            try {
                strAction = action.split(" ", 2)[0];
                cmd = action.split(" ", 2)[1];
            } catch (ArrayIndexOutOfBoundsException e){
                strAction = action;
                cmd = "";
            }
            cmd = getPlaceholders(cmd, target);

            Action actionObj = getAction(strAction);

            if(actionObj == null){
                core.getLogger().warning("Action for " + strAction + " has not been defined. Skipping!");
                return;
            }
            ActionType type = (ActionType) actionObj.getType();

            switch(type){
                case PLAYER_COMMAND:
                    target.performCommand(cmd);
                    break;
                case CONSOLE_COMMAND:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ColorUtil.translate(cmd));
                    break;
                case MESSAGE:
                    target.sendMessage(ColorUtil.translate(cmd));
                    break;
                case BROADCAST:
                    Bukkit.broadcastMessage(ColorUtil.translate(cmd));
                    break;
                case SOUND:
                    cmd = cmd.toUpperCase();
                    try {
                        Sound sound = Sound.valueOf(cmd);
                        target.playSound(target.getLocation(), sound, 5, 5);
                    } catch (IllegalArgumentException e){
                        core.getLogger().warning("Sound '" + cmd + "' is invalid. Skipping!");
                    }
                    break;
                case TITLE:
                    String title = cmd.contains(";") ? cmd.split(";")[0] : cmd;
                    String sub = cmd.contains(";") ? cmd.split(";")[1] : "";
                    target.sendTitle(ColorUtil.translate(title), ColorUtil.translate(sub));
                    break;
                case SUBTITLE:
                    String subtitle = cmd.contains(";") ? cmd.split(";")[0] : cmd;
                    target.sendTitle("", ColorUtil.translate(subtitle));
                    break;
                case ACTION_BAR:
                    String msg;
                    Integer ticks;
                    try {
                        String[] str = cmd.split(";", 2);
                        msg = str[0];
                        ticks = Integer.valueOf(str[1]);
                    } catch (ArrayIndexOutOfBoundsException e){
                        msg = cmd;
                        ticks = 60;
                    }
                    sendActionBar(target, ColorUtil.translate(msg), ticks);
                    break;
            }
        }
    }

}

