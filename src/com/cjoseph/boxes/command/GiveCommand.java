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
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@CommandAlias("%mainCommand")
public class GiveCommand extends BaseCommand {
    private Boxes core;

    public GiveCommand(Boxes core) {
        this.core = core;
        core.getCommandManager().registerCommand(this, true);
    }

    @Subcommand("give")
    public void onCommand(CommandSender cs, OnlinePlayer target, String id, @Default("1") @Optional Integer amount){
        if(cs.hasPermission("boxes.give")){
            java.util.Optional<Map.Entry<String, Box>> boxFilter = BoxManager.getBoxes().entrySet().stream()
                    .filter(box -> box.getKey().equalsIgnoreCase(id)).findFirst();

            if(!boxFilter.isPresent()){
                core.sendCommandMsg(cs, "unknown");
                return;
            }

            boxFilter.ifPresent(stringBoxEntry -> {
                ItemStack boxItem = stringBoxEntry.getValue().getBoxItem();
                boxItem.setAmount(amount);

                target.getPlayer().getInventory().addItem(boxItem);

                core.getConfig().getStringList("message.given").forEach(msg -> {
                    msg = msg.replace("%player%", target.getPlayer().getName());
                    msg = msg.replace("%amount%", amount + "");
                    msg = msg.replace("%type%", stringBoxEntry.getKey());
                    cs.sendMessage(ColorUtil.translate(msg));
                });


                if(cs != target.getPlayer())
                    core.getConfig().getStringList("message.received").forEach(msg -> {
                        msg = msg.replace("%amount%", amount+"");
                        msg = msg.replace("%type%", stringBoxEntry.getKey());
                        target.getPlayer().sendMessage(ColorUtil.translate(msg));
                    });
            });
        } else core.sendCommandMsg(cs, "no-permission");
    }
}
