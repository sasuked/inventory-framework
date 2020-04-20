package me.saiintbrisson.inventory;

import lombok.AllArgsConstructor;
import me.saiintbrisson.inventory.inv.InvHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class InventoryListener implements Listener {

    private Plugin plugin;

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        ((InvHolder) holder).getNode().handleClick(plugin, event);
    }

    @EventHandler
    public void handleOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        ((InvHolder) holder).getNode().handleOpen(plugin, event);
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        ((InvHolder) holder).getNode().handleClose(plugin, event);
    }

}