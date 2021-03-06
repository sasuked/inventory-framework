package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ViewListener implements Listener {

    private final ViewFrame frame;

    public ViewListener(ViewFrame frame) {
        this.frame = frame;
    }

    private View getView(Inventory inventory) {
        // check for Player#getTopInventory
        if (inventory == null)
            return null;

        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof View))
            return null;

        View view = (View) holder;
        if (inventory.getType() != InventoryType.CHEST)
            throw new UnsupportedOperationException("Views is only supported on chest-type inventory.");

        return view;
    }

    @EventHandler
    public void onViewPluginDisable(PluginDisableEvent e) {
        if (frame.getListener() == null || !frame.getOwner().equals(e.getPlugin()))
            return;

        // if the plugin is disabled it will not be possible to handle events
        frame.unregister();
    }

    @EventHandler(ignoreCancelled = true)
    public void onViewClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        Inventory inv = e.getClickedInventory();
        if (inv == null)
            return; // clicked to the outside

        int clickedSlot = e.getSlot();
        if (clickedSlot >= inv.getSize())
            return;  // array index out of bounds: -999???!

        View view = getView(inv);
        if (view == null)
            return;

        Player player = (Player) e.getWhoClicked();
        ViewSlotContext context = new SynchronizedViewContext(view.getContext(player), clickedSlot, e.getCurrentItem());

        // moved to another inventory, not yet supported
        if (clickedSlot != e.getRawSlot()) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(view.isCancelOnClick());
        ViewItem item = view.getItem(clickedSlot);
        if (item == null) {
            item = context.getItem(clickedSlot);
            if (item == null) {
                view.onClick(context);
                e.setCancelled(context.isCancelled());
                return;
            }
        }

        if (item.getClickHandler() != null)
            item.getClickHandler().handle(context);

        e.setCancelled(item.isCancelOnClick());

        if (item.isCloseOnClick())
            player.closeInventory();
    }

    @EventHandler
    public void onViewClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;

        View view = getView(e.getInventory());
        if (view == null)
            return;

        Player player = (Player) e.getPlayer();
        view.onClose(new ViewContext(view, player, e.getInventory()));
        view.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItemOnView(PlayerPickupItemEvent e) {
        View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(true);
    }

}
