package com.github.imdmk.doublejump.jump.placeholder;

import com.github.imdmk.doublejump.jump.JumpPlayer;
import com.github.imdmk.doublejump.jump.JumpPlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class JumpPlayerStreakPlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;
    private final JumpPlayerManager jumpPlayerManager;

    public JumpPlayerStreakPlaceholder(PluginDescriptionFile pluginDescriptionFile, JumpPlayerManager jumpPlayerManager) {
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.jumpPlayerManager = jumpPlayerManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "jump-player-streak";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.pluginDescriptionFile.getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.pluginDescriptionFile.getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        Optional<JumpPlayer> jumpPlayerOptional = this.jumpPlayerManager.getJumpPlayer(player.getUniqueId());

        return jumpPlayerOptional
                .map(JumpPlayer::getStreak)
                .map(String::valueOf)
                .orElse(null);
    }
}
