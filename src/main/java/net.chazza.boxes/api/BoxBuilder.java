package net.chazza.boxes.api;

import net.chazza.boxes.Boxes;
import net.chazza.boxes.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BoxBuilder {
    private String name;
    private List<String> lore;
    private Material type;
    private int data;
    private String id;

    private Boxes core;

    public BoxBuilder(String id){
        this.name = id + " Box";
        this.lore = new ArrayList<>();
        this.id = id;
        this.core = JavaPlugin.getPlugin(Boxes.class);
    }

    public BoxBuilder withType(String type){
        String[] str = type.split(";", 2);
        Material mat;
        try {
            mat = Material.valueOf(str[0]);
        } catch (NullPointerException e){
            core.getLogger().warning("Material '" + type + "' for '" + id + "' box is invalid. Defaulting to Chest!");
            mat = Material.CHEST;
        }
        this.type = mat;
        this.data = Integer.valueOf(str[1]);
        return this;
    }

    public BoxBuilder withName(String name){
        if(name != null)
            this.name = ColorUtil.translate(name);
        return this;
    }

    public BoxBuilder withLore(List<String> lore){
        if(lore != null)
            this.lore = ColorUtil.translate(lore);
        return this;
    }

    public ItemStack getItemStack(){
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setDurability((short) data);
        item.setItemMeta(meta);
        return item;
    }
}
