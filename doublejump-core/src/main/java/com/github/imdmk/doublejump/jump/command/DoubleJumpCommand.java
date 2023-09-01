package com.github.imdmk.doublejump.jump.command;

import com.github.imdmk.doublejump.configuration.MessageConfiguration;
import com.github.imdmk.doublejump.jump.JumpConfiguration;
import com.github.imdmk.doublejump.jump.JumpPlayerManager;
import com.github.imdmk.doublejump.notification.Notification;
import com.github.imdmk.doublejump.notification.NotificationSender;
import com.github.imdmk.doublejump.region.RegionProvider;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Route(name = "doublejump")
public class DoubleJumpCommand {

    private final JumpConfiguration jumpConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final NotificationSender notificationSender;
    private final JumpPlayerManager jumpPlayerManager;
    private final RegionProvider regionProvider;

    public DoubleJumpCommand(JumpConfiguration jumpConfiguration, MessageConfiguration messageConfiguration, NotificationSender notificationSender, JumpPlayerManager jumpPlayerManager, RegionProvider regionProvider) {
        this.jumpConfiguration = jumpConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.notificationSender = notificationSender;
        this.jumpPlayerManager = jumpPlayerManager;
        this.regionProvider = regionProvider;
    }

    @Async
    @Execute(required = 0)
    void execute(Player player) {
        GameMode playerGameMode = player.getGameMode();
        String playerWorldName = player.getWorld().getName();

        if (this.regionProvider.isInRegion(player)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.targetInDisabledRegionNotification);
            return;
        }

        if (this.jumpConfiguration.restrictionsConfiguration.disabledGameModes.contains(playerGameMode)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.jumpModeDisabledGameModeNotification);
            return;
        }

        if (this.jumpConfiguration.restrictionsConfiguration.disabledWorlds.contains(playerWorldName)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.jumpModeDisabledWorldNotification);
            return;
        }

        if (this.jumpPlayerManager.isDoubleJumpMode(player)) {
            this.jumpPlayerManager.disable(player);
            this.notificationSender.sendMessage(player, this.messageConfiguration.jumpModeDisabledNotification);
        }
        else {
            this.jumpPlayerManager.enable(player, true);
            this.notificationSender.sendMessage(player, this.messageConfiguration.jumpModeEnabledNotification);
        }
    }

    @Async
    @Execute(route = "enable-for", required = 1)
    void enableFor(Player player, @Arg @Name("target") Player target) {
        GameMode targetGameMode = target.getGameMode();
        String targetWorldName = target.getWorld().getName();

        if (this.regionProvider.isInRegion(player)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.targetInDisabledRegionNotification);
            return;
        }

        if (this.jumpConfiguration.restrictionsConfiguration.disabledGameModes.contains(targetGameMode)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.targetHasDisabledGameModeNotification);
            return;
        }

        if (this.jumpConfiguration.restrictionsConfiguration.disabledWorlds.contains(targetWorldName)) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.targetInDisabledWorldNotification);
            return;
        }

        this.jumpPlayerManager.enable(player, true);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.jumpModeEnabledForNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendMessage(player, notification);
    }

    @Async
    @Execute(route = "disable-for", required = 1)
    void disableFor(Player player, @Arg @Name("target") Player target) {
        this.jumpPlayerManager.disable(target);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.jumpModeDisabledForNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendMessage(player, notification);
    }
}