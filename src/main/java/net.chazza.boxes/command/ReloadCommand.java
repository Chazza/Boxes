package net.chazza.boxes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import net.chazza.boxes.Boxes;
import net.chazza.boxes.api.Box;
import net.chazza.boxes.manager.BoxManager;
import net.chazza.boxes.util.ColorUtil;
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
