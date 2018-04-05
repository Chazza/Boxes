package com.cjoseph.boxes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.cjoseph.boxes.Boxes;
import org.bukkit.command.CommandSender;

public class MainCommand extends BaseCommand {
    private Boxes core;

    public MainCommand(Boxes core) {
        this.core = core;
        core.getCommandManager().registerCommand(this, true);
    }

    @CommandAlias("%mainCommand")
    public void onCommand(CommandSender cs){
        core.sendCommandMsg(cs, "root");
    }
}
