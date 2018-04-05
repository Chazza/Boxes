package com.cjoseph.boxes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.cjoseph.boxes.Boxes;
import com.cjoseph.boxes.api.Box;
import com.cjoseph.boxes.manager.BoxManager;
import com.cjoseph.boxes.util.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("%mainCommand")
public class ReloadCommand extends BaseCommand {
    private Boxes core;

    public ReloadCommand(Boxes core) {
        this.core = core;
        core.getCommandManager().registerCommand(this, true);
    }

    @Subcommand("reload")
    public void onCommand(CommandSender cs){
        if(cs.hasPermission("boxes.help")){
            core.handleReload();
            core.sendCommandMsg(cs, "reload");
        } else core.sendCommandMsg(cs, "no-permission");
    }
}
