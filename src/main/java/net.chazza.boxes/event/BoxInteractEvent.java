package net.chazza.boxes.event;

import net.chazza.boxes.Boxes;
import net.chazza.boxes.api.Box;
import net.chazza.boxes.manager.BoxManager;
import net.chazza.boxes.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BoxInteractEvent implements Listener {
    private Boxes core;

    public BoxInteractEvent(Boxes core){
        this.core = core;
        core.getServer().getPluginManager().registerEvents(this, core);
    }

    private int millisToSeconds(Long mills){
        return (int) TimeUnit.MILLISECONDS.toSeconds(mills - System.currentTimeMillis());
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack handStack = player.getItemInHand();
        if (!Bukkit.getVersion().contains("1.8")) {
            if (e.getHand() == EquipmentSlot.OFF_HAND) {
                return; // off hand packet, ignore.
            }
        }

        Optional<Map.Entry<String, Box>> boxFilter = BoxManager.getBoxes().entrySet().stream()
                .filter(box -> box.getValue().getBoxItem().isSimilar(handStack)).findFirst();

        if(boxFilter.isPresent()){
            e.setCancelled(true);
            Box box = boxFilter.get().getValue();

            if(core.getPlayerCooldowns().containsKey(player.getUniqueId())){
                Integer timeLeft = millisToSeconds(core.getPlayerCooldowns().get(player.getUniqueId()));

                if(timeLeft > 0){
                    core.getConfig().getStringList("message.cooldown").forEach(msg ->
                            player.sendMessage(ColorUtil.translate(msg).replace("%time%", timeLeft+"")));
                    return;
                }
            }
            if(box.getBoxSettings().requiresPerm() && !player.hasPermission("boxes.use."+box.getId().toLowerCase())){
                core.sendCommandMsg(player, "no-permission");
                return;
            }

            core.getActionManager().runActions(box.getUseRewards(), player);

            if(handStack.getAmount() > 1)
                player.getItemInHand().setAmount(handStack.getAmount() - 1);
            else
                player.setItemInHand(new ItemStack(Material.AIR));

            core.getPlayerCooldowns().put(player.getUniqueId(), System.currentTimeMillis()
                    + (TimeUnit.SECONDS.toMillis(box.getBoxSettings().getBreakTime() + 1)));
            new BukkitRunnable(){
                @Override
                public void run() {
                    core.getActionManager().runActions(box.getBreakRewards(), player);

                    AtomicBoolean hasReward = new AtomicBoolean(false);
                    AtomicInteger rewardCount = new AtomicInteger(0);
                    ThreadLocalRandom rand = ThreadLocalRandom.current();

                    while(!hasReward.get() && box.getBoxSettings().getMinRewards() > rewardCount.get()) {

                        box.getChanceRewards().forEach(reward -> {
                            if (rand.nextInt(100)+1 <= reward.getChance() && !hasReward.get()) {
                                rewardCount.incrementAndGet();
                                core.getActionManager().runActions(reward.getRewards(), player);

                                if(rewardCount.get() == box.getBoxSettings().getMaxRewards()){
                                    hasReward.set(true);
                                }
                            }
                        });
                    }
                }
            }.runTaskLater(core, 20*box.getBoxSettings().getBreakTime());
        }
    }
}
