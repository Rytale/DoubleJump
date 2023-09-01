package com.github.imdmk.doublejump.jump.item.command;

import com.github.imdmk.doublejump.configuration.MessageConfiguration;
import com.github.imdmk.doublejump.jump.item.configuration.JumpItemConfiguration;
import com.github.imdmk.doublejump.notification.Notification;
import com.github.imdmk.doublejump.notification.NotificationSender;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Route(name = "doublejump")
public class DoubleJumpItemCommand {

    private final JumpItemConfiguration jumpItemConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final NotificationSender notificationSender;

    public DoubleJumpItemCommand(JumpItemConfiguration jumpItemConfiguration, MessageConfiguration messageConfiguration, NotificationSender notificationSender) {
        this.jumpItemConfiguration = jumpItemConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.notificationSender = notificationSender;
    }

    @Async
    @Execute(route = "item-give", required = 1)
    void giveItem(CommandSender sender, @Arg @Name("target") Player target) {
        if (!this.jumpItemConfiguration.enabled) {
            this.notificationSender.sendMessage(sender, this.messageConfiguration.jumpItemDisabledNotification);
            return;
        }

        ItemStack jumpItem = this.jumpItemConfiguration.item;
        Inventory targetInventory = target.getInventory();

        if (targetInventory.firstEmpty() == -1) {
            this.notificationSender.sendMessage(sender, this.messageConfiguration.targetFullInventoryNotification);
            return;
        }

        targetInventory.addItem(jumpItem);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.jumpItemAddedNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }

    @Async
    @Execute(route = "item-remove", required = 1)
    void removeItem(CommandSender sender, @Arg @Name("target") Player target) {
        ItemStack jumpItem = this.jumpItemConfiguration.item;

        Inventory targetInventory = target.getInventory();
        Inventory targetEnderChest = target.getEnderChest();

        if (!targetInventory.contains(jumpItem) && !targetEnderChest.contains(jumpItem)) {
            this.notificationSender.sendMessage(sender, this.messageConfiguration.targetHasNoJumpItemNotification);
            return;
        }

        targetInventory.remove(jumpItem);
        targetEnderChest.remove(jumpItem);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.jumpItemRemovedNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }
}