package com.cjoseph.boxes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import com.cjoseph.boxes.Boxes;
import com.cjoseph.boxes.api.Box;
import com.cjoseph.boxes.manager.BoxManager;
import com.cjoseph.boxes.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@CommandAlias("%mainCommand")
public class ListCommand extends BaseCommand {
    private Boxes core;

    public ListCommand(Boxes core) {
        this.core = core;
        core.getCommandManager().registerCommand(this, true);
    }

    @Subcommand("list")
    public void onCommand(CommandSender cs){
        if(cs.hasPermission("boxes.list")){
            core.sendCommandMsg(cs, "list-header");
            BoxManager.getBoxes().forEach((k, v) -> {
                core.getConfig().getStringList("message.list-format")
                        .forEach(msg -> cs.sendMessage(ColorUtil.translate(msg).replace("%type%", k)));
            });
            core.sendCommandMsg(cs, "list-footer");
        } else core.sendCommandMsg(cs, "no-permission");
    }
}
