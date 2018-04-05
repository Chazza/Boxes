package com.cjoseph.boxes;

import co.aikar.commands.BukkitCommandManager;
import com.cjoseph.boxes.action.Action;
import com.cjoseph.boxes.action.ActionManager;
import com.cjoseph.boxes.action.ActionType;
import com.cjoseph.boxes.api.Box;
import com.cjoseph.boxes.api.BoxBuilder;
import com.cjoseph.boxes.api.BoxSettings;
import com.cjoseph.boxes.api.Reward;
import com.cjoseph.boxes.command.*;
import com.cjoseph.boxes.event.BoxInteractEvent;
import com.cjoseph.boxes.manager.BoxManager;
import com.cjoseph.boxes.util.ColorUtil;
import com.google.common.collect.Maps;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Boxes extends JavaPlugin {
    private ActionManager actionManager;
    public ActionManager getActionManager() {
        return actionManager;
    }

    private BukkitCommandManager commandManager;
    public BukkitCommandManager getCommandManager() {
        return commandManager;
    }

    private Map<UUID, Long> playerCooldowns;
    public Map<UUID, Long> getPlayerCooldowns() {
        return playerCooldowns;
    }

    public void sendCommandMsg(CommandSender cs, String cmd){
        getConfig().getStringList("message."+cmd).forEach(msg -> cs.sendMessage(ColorUtil.translate(msg)
                .replace("%player%", cs.getName()).replace("%version%", getDescription().getVersion())));
    }

    @Override
    public void onEnable(){
        saveDefaultConfig();
        setupBoxes(false);
        setupActions();

        playerCooldowns = Maps.newHashMap();

        commandManager = new BukkitCommandManager(this);
        commandManager.getCommandReplacements().addReplacement("mainCommand", "boxes|box|crate|crates");

        new MainCommand(this);
        new ReloadCommand(this);
        new GiveCommand(this);
        new HelpCommand(this);
        new ListCommand(this);

        new BoxInteractEvent(this);
    }

    public void handleReload(){
        reloadConfig();
        setupBoxes(true);
    }

    private void setupActions(){
        actionManager = new ActionManager(this);
        actionManager = new ActionManager(this);
        actionManager.addAction(new Action("[CONSOLE]").withType(ActionType.CONSOLE_COMMAND).compile());
        actionManager.addAction(new Action("[PLAYER]").withType(ActionType.PLAYER_COMMAND).compile());
        actionManager.addAction(new Action("[MESSAGE]").withType(ActionType.MESSAGE).compile());
        actionManager.addAction(new Action("[BROADCAST]").withType(ActionType.BROADCAST).compile());
        actionManager.addAction(new Action("[ACTIONBAR]").withType(ActionType.ACTION_BAR).compile());
        actionManager.addAction(new Action("[TITLE]").withType(ActionType.TITLE).compile());
        actionManager.addAction(new Action("[SUBTITLE]").withType(ActionType.SUBTITLE).compile());
        actionManager.addAction(new Action("[SOUND]").withType(ActionType.SOUND).compile());
    }

    private void setupBoxes(boolean silence){
        BoxManager boxManager = new BoxManager(this);

        Box box;
        BoxBuilder boxBuilder;
        for(String boxStr : getConfig().getConfigurationSection("box").getKeys(false)){
            if(!silence)
                getLogger().info("BOX: " + boxStr + " is being created.");

            box = new Box(boxStr);
            box.setUseRewards(getConfig().getStringList("box."+boxStr+".pre-rewards"));
            box.setBreakRewards(getConfig().getStringList("box."+boxStr+".break-rewards"));
            box.withSettings(new BoxSettings()
                    .setBreakTime(getConfig().getInt("box."+boxStr+".setting.time"))
                    .requiresPerm(getConfig().getBoolean("box."+boxStr+".setting.permission"))
                    .minRewards(getConfig().getInt("box."+boxStr+".setting.min-rewards"))
                    .maxRewards(getConfig().getInt("box."+boxStr+".setting.max-rewards"))
            );

            List<Reward> boxRewards = new ArrayList<>();
            getConfig().getConfigurationSection("box."+boxStr+".rewards").getKeys(false)
                    .forEach(boxReward -> {
                        List<String> rewards = getConfig().getStringList("box."+boxStr+".rewards."+boxReward+".reward");
                        Integer chance = getConfig().getInt("box."+boxStr+".rewards."+boxReward+".chance");
                        boxRewards.add(new Reward(boxReward, rewards, chance));
                    });
            box.setChanceRewards(boxRewards);

            boxBuilder = new BoxBuilder(boxStr)
                    .withName(getConfig().getString("box."+boxStr+".item.name"))
                    .withLore(getConfig().getStringList("box."+boxStr+".item.lore"))
                    .withType(getConfig().getString("box."+boxStr+".item.type"));
            box.setBoxItem(boxBuilder.getItemStack());
            boxManager.addBox(box);
        }
    }

    @Override
    public void onDisable(){

    }
}
