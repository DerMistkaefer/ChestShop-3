package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class EmptyShopDeleter implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() != TransactionEvent.TransactionType.BUY) {
            return;
        }

        Inventory ownerInventory = event.getOwnerInventory();
        Sign sign = event.getSign();
        Container connectedContainer = uBlock.findConnectedContainer(sign);

        if (!shopShouldBeRemoved(ownerInventory, event.getStock())) {
            return;
        }

        if (!isInRemoveWorld(sign)) {
            return;
        }

        ShopDestroyedEvent destroyedEvent = new ShopDestroyedEvent(null, event.getSign(), connectedContainer);
        ChestShop.callEvent(destroyedEvent);

        sign.getBlock().setType(Material.AIR);

        if (Properties.REMOVE_EMPTY_CHESTS && !ChestShopSign.isAdminShop(ownerInventory) && InventoryUtil.isEmpty(ownerInventory)) {
            connectedContainer.getBlock().setType(Material.AIR);
        } else {
            ownerInventory.addItem(new ItemStack(Material.SIGN, 1));
        }
    }

    private static boolean shopShouldBeRemoved(Inventory inventory, ItemStack[] stock) {
        return Properties.REMOVE_EMPTY_SHOPS && !ChestShopSign.isAdminShop(inventory) && !InventoryUtil.hasItems(stock, inventory);
    }

    private static boolean isInRemoveWorld(Sign sign) {
        return Properties.REMOVE_EMPTY_WORLDS.isEmpty() || Properties.REMOVE_EMPTY_WORLDS.contains(sign.getWorld().getName());
    }
}
